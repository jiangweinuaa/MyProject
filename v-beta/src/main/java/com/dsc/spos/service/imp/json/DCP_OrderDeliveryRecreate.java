package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderDeliveryRecreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderDeliveryRecreateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.kdniao.kdnQGService;
import com.dsc.spos.waimai.kdniao.kdnTCService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_OrderDeliveryRecreate extends SPosAdvanceService<DCP_OrderDeliveryRecreateReq, DCP_OrderDeliveryRecreateRes> {
    @Override
    protected void processDUID(DCP_OrderDeliveryRecreateReq req, DCP_OrderDeliveryRecreateRes res) throws Exception {
        String eId = req.getRequest().geteId();
        if(eId==null||eId.isEmpty())
        {
            eId = req.geteId();
        }
        if (eId==null||eId.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        String langType=req.getLangType();
        if(langType==null||langType.isEmpty()){
        	langType="zh_CN";
        }
        req.seteId(eId);
        req.getRequest().seteId(eId);
        String orderNo = req.getRequest().getOrderNo();
        String sql = "select * from DCP_order where EID='" + eId + "' and orderno='" + orderNo + "'";
        this.Log(
                "【调用DCP_OrderDeliveryRecreate接口，物流重下单】查询开始：单号OrderNO=" + orderNo + " 传入的参数eid=" + eId + "  查询语句：" + sql);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        if (getQDataDetail == null || getQDataDetail.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("该订单不存在");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】查询完成：该订单不存在！ 单号OrderNO=" + orderNo);
            return;
        }
        Map<String, Object> map_order = getQDataDetail.get(0);
        String orderStatus = map_order.get("STATUS").toString();
        if ("3".equals(orderStatus)||"11".equals(orderStatus)||"12".equals(orderStatus))
        {
            StringBuilder statusTypeName = new StringBuilder("");
            String statusName = HelpTools.GetOrderStatusName("1", orderStatus, statusTypeName);
            res.setSuccess(false);
            res.setServiceDescription("该订单状态是" + statusName + "，不能重新发物流！");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单状态是" + statusName + "，不能重发物流！单号OrderNO=" + orderNo +", 订单状态status=" + orderStatus);
            return;
        }
        String deliveryStatus = map_order.getOrDefault("DELIVERYSTATUS", "").toString();//配送状态
        if (deliveryStatus.isEmpty()||"4".equals(deliveryStatus)||"5".equals(deliveryStatus))
        {

        }
        else
        {
            StringBuilder statusTypeName = new StringBuilder("");
            String statusName = HelpTools.GetOrderStatusName("2", deliveryStatus, statusTypeName);
            res.setSuccess(false);
            res.setServiceDescription("该订单配送状态是" + statusName + "，不能重新发物流！");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单配送状态是" + statusName + "，不能重发物流！单号OrderNO=" + orderNo +", 订单配送状态deliveryStatus=" + deliveryStatus);
            return;
        }
        String order_deliveryType = getQDataDetail.get(0).getOrDefault("DELIVERYTYPE", "").toString();
        if (!"KDN".equals(order_deliveryType))
        {
            res.setSuccess(false);
            res.setServiceDescription("该订单物流类型非快递鸟,不能重新发物流(目前仅支持快递鸟物流上门取件类型重发物流)！");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单物流类型是deliveryType=" + order_deliveryType + "，不能重发物流！单号OrderNO=" + orderNo);
            return;
        }
        String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

        String shipType = map_order.get("SHIPTYPE").toString();//配送方式( 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送)
        String shipMode = "";//kdn 物流产品类型 0全国快递，1同城快递
        if ("2".equals(shipType))
        {
            shipMode = "0";
        }
        else if ("6".equals(shipType))
        {
            shipMode = "1";
        }
        else
        {
            res.setSuccess(false);
            res.setServiceDescription("该订单配送方式不对,不能重新发物流(目前仅支持全国快递/同城配送重发物流)！");
            HelpTools.writelog_waimai("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单物流类型是deliveryType=" + order_deliveryType + "，不能重发物流！单号OrderNO=" + orderNo);
            return;
        }
        String channelId=map_order.get("CHANNELID").toString();
        String machShopId=map_order.get("MACHSHOP").toString();
        String machShopName=map_order.get("MACHSHOPNAME").toString();
        String shippingId=map_order.get("SHIPPINGSHOP").toString();
        
        //是否使用新的配送门店
        boolean isChangeShopId=false;
        String newShopId=req.getRequest().getNewShopId();
        //新的配送门店
        if(newShopId!=null&&newShopId.trim().length()>0&&!newShopId.equals(shippingId)){
        	isChangeShopId=true;
        	shippingId=newShopId;
        }
        
        String shopId=map_order.get("SHOP").toString();
        String shopName=map_order.get("SHOPNAME").toString();

        /***************查询下必传参数*********************/
        String sql_shippingShop = " select b.ORG_NAME,a.* from dcp_org a left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno"
        		+ " where a.eid='"+eId+"' and a.organizationno='"+shippingId+"' and a.lang_type='"+langType+"'";
        this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】开始调用kdn快递鸟物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
        List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
        if(getShippingShopData==null||getShippingShopData.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceDescription("该订单的配送门店查询组织信息为空！");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单的配送门店查询组织信息为空！单号OrderNO=" + orderNo+",配送门店shippingshop="+shippingId);
            return;
        }
        /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
        Map<String, Object> outSalesetMap=this.getKDNDeliverySetByShippingShop(eId,order_deliveryType,shippingId,shipMode);
        if(outSalesetMap==null||outSalesetMap.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceDescription("该订单的配送门店对应的物流配置参数为空！");
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】该订单的配送门店对应的物流配置参数为空！单号OrderNO=" + orderNo+",配送门店shippingshop="+shippingId);
            return;
        }


        boolean isInvokeKDN = true;
        StringBuffer isInvokeDadaMess = new StringBuffer("");
        Map<String, Object> shippingShopInfo = getShippingShopData.get(0);
        String shippingName=shippingShopInfo.get("ORG_SHOPNAME").toString();
        String PROVINCE = shippingShopInfo.getOrDefault("PROVINCE", "").toString();//寄件人省名称
        String CITY = shippingShopInfo.getOrDefault("CITY", "").toString();//寄件人市名称
        String ADDRESS = shippingShopInfo.getOrDefault("ADDRESS", "").toString();//寄件人详细地址
        String PHONE = shippingShopInfo.getOrDefault("PHONE", "").toString();//寄件人联系电话

        String messStr_send = "";

        if(PROVINCE.isEmpty())
        {
            isInvokeKDN = false;
            messStr_send += "省份未维护,";
        }
        if(CITY.isEmpty())
        {
            isInvokeKDN = false;
            messStr_send += "城市未维护,";
        }
        if(ADDRESS.isEmpty())
        {
            isInvokeKDN = false;
            messStr_send += "详细地址未维护,";
        }
        if(PHONE.isEmpty())
        {
            isInvokeKDN = false;
            messStr_send += "联系电话未维护";
        }

        if(isInvokeKDN==false)
        {
            messStr_send = "<br>配送门店("+shippingId+")信息异常:"+messStr_send;
                                    /*isInvokeDadaMess = new StringBuffer("");
                                    isInvokeDadaMess.append(messStr);*/
            isInvokeDadaMess.append(messStr_send);
        }

        /*******************判断下收件人信息***********************/

        String orderProvinceName = map_order.getOrDefault("PROVINCE","").toString();
        String orderCityName = map_order.getOrDefault("CITY","").toString();
        String orderExpAreaName = map_order.getOrDefault("COUNTY","").toString();
        String orderAddress = map_order.getOrDefault("ADDRESS","").toString();
        String recipientName = map_order.getOrDefault("GETMAN", "").toString();
        if (recipientName.isEmpty())
        {
            recipientName = map_order.getOrDefault("CONTMAN", "").toString();
        }
        String recipientMobile = map_order.getOrDefault("GETMANTEL", "").toString().trim();
        if (recipientMobile.isEmpty())
        {
            recipientMobile = map_order.getOrDefault("CONTTEL", "").toString().trim();
        }

        String messStr_recipien = "";
        if(orderProvinceName.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件省份为空,";
        }
        if(orderCityName.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件市为空,";
        }
        if(orderExpAreaName.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件区/县为空,";
        }
        if(orderAddress.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件详细地址为空,";
        }
        if(recipientName.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件人姓名为空,";
        }
        if(recipientMobile.isEmpty())
        {
            isInvokeKDN = false;
            messStr_recipien += "收件人联系电话为空,";
        }
        if (isInvokeKDN == false)
        {
            if (!messStr_recipien.isEmpty())
            {
                messStr_recipien = "<br>收件人信息异常:"+messStr_recipien;
                isInvokeDadaMess.append(messStr_recipien);
            }

        }


        if(!isInvokeKDN)
        {
            //dada.Log("自动发快递ExpressOrderCreate开始调用dada物流,"+isInvokeDadaMess.toString()+"***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"无需配送************");
            //写订单日志
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

            orderStatusLog oslog=new orderStatusLog();
            oslog.setCallback_status("N");
            oslog.setChannelId(channelId);
            oslog.setDisplay("1");
            oslog.seteId(eId);
            oslog.setLoadDocType(orderLoadDoctype);
            oslog.setMachShopName(machShopName);
            oslog.setMachShopNo(machShopId);
            oslog.setMemo("收寄方信息不完整"+isInvokeDadaMess.toString());

            oslog.setNeed_callback("N");
            oslog.setNeed_notify("N");
            oslog.setNotify_status("N");
            oslog.setOpName(req.getOpName());
            oslog.setOpNo(req.getOpNO());
            oslog.setOrderNo(orderNo);
            oslog.setShippingShopName(shippingName);
            oslog.setShippingShopNo(shippingId);
            oslog.setShopName(shopName);
            oslog.setShopNo(shopId);
            //

            String statusType = "99";// 其他状态
            String updateStaus = "99";// 订单修改

            oslog.setStatusType(statusType);
            oslog.setStatus(updateStaus);

            String statusName = "物流下单失败";
            String statusTypeName = "手动重发物流";

            oslog.setStatusName(statusName);
            oslog.setStatusType(statusType);
            oslog.setStatusTypeName(statusTypeName.toString());
            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            orderStatusLogList.add(oslog);
            StringBuilder errorMessage = new StringBuilder();
            boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

            res.setSuccess(false);
            res.setServiceDescription("收寄方信息不完整"+isInvokeDadaMess.toString());
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】开始调用kdn快递鸟物流,"+isInvokeDadaMess.toString()+",订单orderNo="+orderNo+"，收寄方信息不完整无法配送************");
            return;
        }


        String sqlOrderDetail="select * from dcp_order_detail where (PACKAGETYPE='1' or PACKAGETYPE='2' or PACKAGETYPE is null) and  eid='"+eId+"' and orderno='"+orderNo+"' ";
        List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

        String resKDN= "";
        //String ref_deliveryNo = PosPub.getGUID(false).substring(0,30);//传入的物流单号,最多30位，这个先这样吧一般不会重复
        long timestamp = System.currentTimeMillis()/1000;
        String ref_deliveryNo = orderNo+"-"+timestamp;//传入的物流单号,最多50位，这个先这样吧一般不会重复
        if ("0".equals(shipMode))
        {
            //全国快递，商家订单号最多30
            if (ref_deliveryNo.length()>30)
            {
                ref_deliveryNo = PosPub.getGUID(false).substring(0,30);//传入的物流单号,最多30位，这个先这样吧一般不会重复
            }
        }
        else
        {
            //同城快递，商家订单号最多50
            if (ref_deliveryNo.length()>50)
            {
                ref_deliveryNo = PosPub.getGUID(false);
            }
        }

        StringBuffer apiErrorMessage = new StringBuffer("");
        boolean resultSuccess = false;
        String out_deliveryNo = "";//KDN返回的物流单号，不是真正的运单号
        if ("0".equals(shipMode))
        {
            kdnQGService kdn = null;
            String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
            if ("350238".equals(EBusinessID))
            {
                kdn = new kdnQGService(true);
            }
            else
            {
                kdn = new kdnQGService();
            }
            //先调用超区校验接口
            resKDN = kdn.checkDeliveryRange(outSalesetMap,map_order,shippingShopInfo,apiErrorMessage);
            if (resKDN==null||resKDN.isEmpty())
            {
                //后面统一写异常的订单历程

            }
            else
            {
                JSONObject jsonRes1 = JSONObject.parseObject(resKDN);
                String Success1 = jsonRes1.get("Success").toString();
                String Reason1 = jsonRes1.getOrDefault("Reason","")==null?"":jsonRes1.getOrDefault("Reason","").toString();
                if (!"true".equalsIgnoreCase(Success1))
                {
                    //后面统一写异常的订单历程
                    apiErrorMessage.append("超区校验接口返回异常:"+Reason1);
                }
                else
                {
                    //调用下单接口
                    resKDN = "";
                    
                    cancelKDNDelivery(req, isChangeShopId, shipMode,kdn, null,orderNo, map_order, outSalesetMap, shippingShopInfo);
                    resKDN = kdn.kdnOrderCreate(ref_deliveryNo,outSalesetMap, map_order, shippingShopInfo, getDetailDatas,apiErrorMessage);
                    if (resKDN==null||resKDN.isEmpty())
                    {
                        //后面统一写异常的订单历程
                    }
                    else
                    {
                        JSONObject jsonRes2 = JSONObject.parseObject(resKDN);
                        String Success2 = jsonRes2.get("Success").toString();
                        String Reason2 = jsonRes2.getOrDefault("Reason","")==null?"":jsonRes2.getOrDefault("Reason","").toString();
                        if ("true".equalsIgnoreCase(Success2))
                        {
                            resultSuccess = true;
                            String kdnOrderStr = jsonRes2.getOrDefault("Order","")==null?"":jsonRes2.getOrDefault("Order","").toString();
                            try {
                                JSONObject kdnOrderJson = JSONObject.parseObject(kdnOrderStr);
                                out_deliveryNo = kdnOrderJson.getOrDefault("KDNOrderCode","")==null?"":kdnOrderJson.getOrDefault("KDNOrderCode","").toString();
                            }
                            catch (Exception e)
                            {

                            }
                        }
                        else
                        {
                            //后面统一写异常的订单历程
                            apiErrorMessage.append("下单接口返回异常:"+Reason2);
                        }
                    }
                }


            }

        }
        else
        {
            kdnTCService kdn = null;
            String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
            if ("1663339".equals(EBusinessID))
            {
                kdn = new kdnTCService(true);
            }
            else
            {
                kdn = new kdnTCService();
            }

            //调用下单接口
            resKDN = "";
            cancelKDNDelivery(req, isChangeShopId, shipMode,null,kdn, orderNo, map_order, outSalesetMap, shippingShopInfo);
            resKDN = kdn.kdnOrderCreate(ref_deliveryNo,outSalesetMap, map_order, shippingShopInfo, getDetailDatas,apiErrorMessage);
            if (resKDN==null||resKDN.isEmpty())
            {
                //后面统一写异常的订单历程
            }
            else
            {
                JSONObject jsonRes2 = JSONObject.parseObject(resKDN);
                String resultCode = jsonRes2.get("resultCode").toString();
                String reason = jsonRes2.getOrDefault("reason","")==null?"":jsonRes2.getOrDefault("reason","").toString();
                if ("100".equalsIgnoreCase(resultCode))
                {
                    resultSuccess = true;
                    JSONObject dataJson =  jsonRes2.getJSONObject("data");
                    out_deliveryNo = dataJson.getOrDefault("kdnOrderCode","")==null?"":dataJson.getOrDefault("kdnOrderCode","").toString();

                }
                else
                {
                    //后面统一写异常的订单历程
                    apiErrorMessage.append("下单接口返回异常:"+reason);
                }
            }
        }

        if(resultSuccess)
        {
            //执行
            List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
            UptBean ubecOrder=new UptBean("dcp_order");
            ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            if(!ref_deliveryNo.isEmpty())
            {
                ubecOrder.addUpdateValue("REF_DELIVERYNO", new DataValue(ref_deliveryNo, Types.VARCHAR));

                //插入订单对应物流表
                String[] columns_delivery = { "EID", "ORDERNO", "REF_DELIVERYNO", "OUT_DELIVERYNO",
                        "DELIVERYTYPE","LOADDOCTYPE","CHANNELID","SHIPPINGSHOP", "SHIPPERCODE", "LOGISTICSNO", "STATE", "DELIVERYSTATUS"};

                DataValue[] insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(ref_deliveryNo, Types.VARCHAR),
                                new DataValue(out_deliveryNo, Types.VARCHAR),
                                new DataValue(order_deliveryType, Types.VARCHAR),
                                new DataValue(orderLoadDoctype, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(shippingId, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),//SHIPPERCODE
                                new DataValue("", Types.VARCHAR),//LOGISTICSNO
                                new DataValue("", Types.VARCHAR),
                                new DataValue("7", Types.VARCHAR) //-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                        };


                InsBean ib1 = new InsBean("dcp_order_delivery",columns_delivery);
                ib1.addValues(insValue1);
                lstData.add(new DataProcessBean(ib1));
            }
            if(!out_deliveryNo.isEmpty())
            {
                ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(out_deliveryNo, Types.VARCHAR));//暂时先给快递鸟单号，后续回调更新运单单号
                ubecOrder.addUpdateValue("OUTDOCORDERNO", new DataValue(out_deliveryNo, Types.VARCHAR));//使用才哥搞的 商有云字段
            }
            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("7", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            lstData.add(new DataProcessBean(ubecOrder));


            StaticInfo.dao.useTransactionProcessData(lstData);


            //写订单日志
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            String LogStatus="7";
            orderStatusLog oslog=new orderStatusLog();
            oslog.setCallback_status("N");
            oslog.setChannelId(channelId);
            oslog.setDisplay("1");
            oslog.seteId(eId);
            oslog.setLoadDocType(orderLoadDoctype);
            oslog.setMachShopName(machShopName);
            oslog.setMachShopNo(machShopId);
            oslog.setMemo("物流重新下单");
            if (!ref_deliveryNo.isEmpty())
            {
                oslog.setMemo("已上传物流(手动重新下单)<br>商家物流单号:"+ref_deliveryNo+"<br>快递鸟订单号:"+out_deliveryNo);
            }
            oslog.setNeed_callback("N");
            oslog.setNeed_notify("N");
            oslog.setNotify_status("N");
            oslog.setOpName(req.getOpName());
            oslog.setOpNo(req.getOpNO());
            oslog.setOrderNo(orderNo);
            oslog.setShippingShopName(shippingName);
            oslog.setShippingShopNo(shippingId);
            oslog.setShopName(shopName);
            oslog.setShopNo(shopId);
            oslog.setStatus(LogStatus);
            //
            String statusType="2";
            StringBuilder statusTypeName=new StringBuilder();
            String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
            oslog.setStatusName(statusName);
            oslog.setStatusType(statusType);
            oslog.setStatusTypeName(statusTypeName.toString());
            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            orderStatusLogList.add(oslog);
            StringBuilder errorMessage = new StringBuilder();
            boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
            res.setSuccess(true);
            res.setServiceDescription("服务执行成功");
            return;
        }
        else
        {
            //写订单日志
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

            orderStatusLog oslog=new orderStatusLog();
            oslog.setCallback_status("N");
            oslog.setChannelId(channelId);
            oslog.setDisplay("1");
            oslog.seteId(eId);
            oslog.setLoadDocType(orderLoadDoctype);
            oslog.setMachShopName(machShopName);
            oslog.setMachShopNo(machShopId);
            oslog.setMemo("手动重新呼叫物流异常:"+apiErrorMessage.toString());

            oslog.setNeed_callback("N");
            oslog.setNeed_notify("N");
            oslog.setNotify_status("N");
            oslog.setOpName(req.getOpName());
            oslog.setOpNo(req.getOpNO());
            oslog.setOrderNo(orderNo);
            oslog.setShippingShopName(shippingName);
            oslog.setShippingShopNo(shippingId);
            oslog.setShopName(shopName);
            oslog.setShopNo(shopId);
            //

            String statusType = "99";// 其他状态
            String updateStaus = "99";// 订单修改

            oslog.setStatusType(statusType);
            oslog.setStatus(updateStaus);

            String statusName = "物流下单失败";
            String statusTypeName = "手动重发物流";

            oslog.setStatusName(statusName);
            oslog.setStatusType(statusType);
            oslog.setStatusTypeName(statusTypeName.toString());
            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            orderStatusLogList.add(oslog);
            StringBuilder errorMessage = new StringBuilder();
            boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
            res.setSuccess(false);
            res.setServiceDescription("呼叫物流异常:"+apiErrorMessage.toString());
            return;
        }


    }
    
    public void cancelKDNDelivery(DCP_OrderDeliveryRecreateReq req,boolean isChangeShopId,
    		String shipMode,
    		kdnQGService kdn,
    		kdnTCService kdntc,
    		String orderNo,Map<String, Object> map_order,Map<String, Object> outSalesetMap,Map<String, Object> shippingShopInfo)throws Exception {
    	if(isChangeShopId){
    		StringBuffer apiErrorMessage = new StringBuffer("");
        	String oldRefDeliveryNo= map_order.get("REF_DELIVERYNO")==null?"":map_order.get("REF_DELIVERYNO").toString();
        	String resKDN=null;
        	
        	if ("0".equals(shipMode)){
        		//全国快递
        		resKDN=kdn.kdnOrderCancel(oldRefDeliveryNo, outSalesetMap, orderNo, "11", "修改配送机构,取消物流", apiErrorMessage);
        	}else{
        		//同城配送
        		resKDN=kdntc.kdnOrderCancel(oldRefDeliveryNo, outSalesetMap, orderNo, "11", "修改配送机构,取消物流", apiErrorMessage);
        	}
        		
        	
        	this.Log("【调用DCP_OrderDeliveryRecreate接口，物流取消】调用kdn快递鸟物流返回信息,"+resKDN+"************");
            
        	boolean isSuccess=false;
        	String memo="";
        	
        	
        	if("0".equals(shipMode)){
        		//全国快递
        		JSONObject jsonRes1 = JSONObject.parseObject(resKDN);
        		String Success1 = jsonRes1.get("Success").toString();
        		String Reason1 = jsonRes1.getOrDefault("Reason","")==null?"":jsonRes1.getOrDefault("Reason","").toString();
        		
        		if ("true".equalsIgnoreCase(Success1)) {
        			//成功
        			isSuccess=true;
        			memo="快递鸟全国快递物流取消失败成功";
        		}else{
        			//失败
        			isSuccess=false;
        			memo="快递鸟全国快递物流取消失败:"+Reason1;
        		}
        	}else{
        		//同城配送
        		JSONObject jsonRes = JSONObject.parseObject(resKDN);
                String resultCode = jsonRes.get("resultCode").toString();
                String reason = jsonRes.getOrDefault("reason","")==null?"":jsonRes.getOrDefault("reason","").toString();
                if (!"100".equals(resultCode))
                {
                	//失败
        			isSuccess=false;
        			memo="快递鸟同城配送物流取消失败:"+reason;
                }
                else
                {
                	//成功
        			isSuccess=true;
        			memo="快递鸟同城配送物流取消失败成功";
                }
        	}
            
            {
            	String eId = req.getRequest().geteId();
                if(eId==null||eId.isEmpty())
                {
                    eId = req.geteId();
                }
            	String channelId=map_order.get("CHANNELID").toString();
            	String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();
            	String machShopId=map_order.get("MACHSHOP").toString();
                String machShopName=map_order.get("MACHSHOPNAME").toString();
                String shippingName=shippingShopInfo.get("ORG_SHOPNAME").toString();
                String shippingId=shippingShopInfo.get("ORGANIZATIONNO").toString();
                String shopId=map_order.get("SHOP").toString();
                String shopName=map_order.get("SHOPNAME").toString();
                
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

                orderStatusLog oslog=new orderStatusLog();
                oslog.setCallback_status("N");
                oslog.setChannelId(channelId);
                oslog.setDisplay("1");
                oslog.seteId(eId);
                oslog.setLoadDocType(orderLoadDoctype);
                oslog.setMachShopName(machShopName);
                oslog.setMachShopNo(machShopId);
                oslog.setMemo(memo);

                oslog.setNeed_callback("N");
                oslog.setNeed_notify("N");
                oslog.setNotify_status("N");
                oslog.setOpName(req.getOpName());
                oslog.setOpNo(req.getOpNO());
                oslog.setOrderNo(orderNo);
                oslog.setShippingShopName(shippingName);
                oslog.setShippingShopNo(shippingId);
                oslog.setShopName(shopName);
                oslog.setShopNo(shopId);
                //

                String statusType = "99";// 其他状态
                String updateStaus = "99";// 订单修改

                oslog.setStatusType(statusType);
                oslog.setStatus(updateStaus);

                String statusName = "物流取消";
                String statusTypeName = "物流取消";

                oslog.setStatusName(statusName);
                oslog.setStatusType(statusType);
                oslog.setStatusTypeName(statusTypeName.toString());
                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                orderStatusLogList.add(oslog);
                StringBuilder errorMessage = new StringBuilder();
                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
            }
            
            if(!isSuccess){
            	//物流取消失败报错
            	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, memo);
            }
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderDeliveryRecreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderDeliveryRecreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderDeliveryRecreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderDeliveryRecreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String eId = req.getRequest().geteId();

        if(Check.Null(eId)){
			/*errMsg.append("企业编码不能为空值 ");
			isFail = true;*/

        }

        if(Check.Null(req.getRequest().getOrderNo())){
            errMsg.append("订单号不能为空值 ");
            isFail = true;

        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderDeliveryRecreateReq> getRequestType() {
        return new TypeToken<DCP_OrderDeliveryRecreateReq>(){};
    }

    @Override
    protected DCP_OrderDeliveryRecreateRes getResponseType() {
        return new DCP_OrderDeliveryRecreateRes();
    }

    public void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "DCP_OrderDeliveryRecreate");
        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    /**
     * kdn快递鸟物流参数(全国快递和同城配送不一致)
     * @param eId
     * @param deliveryType
     * @param shopId
     * @param shipMode
     * @return
     * @throws Exception
     */
    public Map<String, Object> getKDNDeliverySetByShippingShop(String eId,String deliveryType,String shopId,String shipMode) throws  Exception
    {
        Map<String, Object> resultMap = null;
        if (!"1".equals(shipMode))
        {
            shipMode = "0";//0全国快递，1同城快递
        }
        try {
            StringBuffer sqlBuffer = new StringBuffer("");
            sqlBuffer.append(" select * from (");
            //先查询适用于指定门店的参数
            sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
                    + " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2'  AND B.SHOPID='" + shopId + "' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(" UNION ALL ");
            //查询下适用全部门店
            sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(") A ORDER BY A.IDX DESC");
            String sql = sqlBuffer.toString();
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】。【快递鸟物流类型】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】。【快递鸟物流类型】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】。【快递鸟物流类型】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            }
        }

        catch (Exception e)
        {
            this.Log("【调用DCP_OrderDeliveryRecreate接口，物流重下单】。【快递鸟物流类型】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;
    }
}
