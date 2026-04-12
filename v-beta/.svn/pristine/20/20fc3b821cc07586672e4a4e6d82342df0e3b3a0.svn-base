package com.dsc.spos.service.imp.json;

import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import org.apache.cxf.common.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.foreign.erp.request.HolidayOrderCreateRequest;
import com.dsc.spos.json.cust.req.DCP_OrderModifyReq;
import com.dsc.spos.json.cust.req.DCP_OrderModifyReq.level1ELM;
import com.dsc.spos.json.cust.req.DCP_OrderModifyReq.level2ELM;
import com.dsc.spos.json.cust.res.DCP_OrderModifyRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.scheduler.job.HolidayShoporderCreate_V3.Wrapper;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ConvertUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;


public class DCP_OrderModify extends SPosAdvanceService<DCP_OrderModifyReq, DCP_OrderModifyRes> {

    @Override
    protected void processDUID(DCP_OrderModifyReq req, DCP_OrderModifyRes res) throws Exception
    {
        String eId = req.getRequest().geteId();
        if(eId==null||eId.isEmpty())
        {
            eId = req.geteId();
        }
        if (eId==null||eId.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        req.seteId(eId);
        req.getRequest().seteId(eId);
        String orderNo = req.getRequest().getOrderNo();
        String loadDocType = req.getRequest().getLoadDocType();
        String langType = req.getLangType();
        if(langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_order where EID='" + eId + "'");
        sqlbuf.append(" and orderno='" + orderNo + "'");
        if (loadDocType!=null&&loadDocType.isEmpty()==false)
        {
            sqlbuf.append(" and LOADDOCTYPE='" + loadDocType + "'");
        }

        String sql = sqlbuf.toString();
        String orderStatus = "";// 订单状态
        String orderShop = "";// 数据库里面下单门店
        boolean nResult = false;
        StringBuilder meassgeInfo = new StringBuilder();
        HelpTools.writelog_waimai(
                "【调用DCP_OrderModify接口，订单信息修改】查询开始：单号OrderNO=" + orderNo + " 传入的参数eid=" + eId + "  查询语句：" + sql);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        if (getQDataDetail == null || getQDataDetail.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("该订单不存在");
            HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】查询完成：该订单不存在！ 单号OrderNO=" + orderNo);
            return;
        }
        loadDocType = getQDataDetail.get(0).get("LOADDOCTYPE").toString();
        String channelId = getQDataDetail.get(0).get("CHANNELID").toString();
        String loadDocBillType = getQDataDetail.get(0).get("LOADDOCBILLTYPE").toString();
        String loadDocOrderNo = getQDataDetail.get(0).get("LOADDOCORDERNO").toString();
        orderStatus = getQDataDetail.get(0).get("STATUS").toString();

        orderShop = getQDataDetail.get(0).get("SHOP").toString();

        String orderShipType = getQDataDetail.get(0).get("SHIPTYPE").toString();
        String orderContMan = getQDataDetail.get(0).get("CONTMAN").toString();
        String orderContTel = getQDataDetail.get(0).get("CONTTEL").toString();
        String orderAdress = getQDataDetail.get(0).get("ADDRESS").toString();
        String orderGetMan = getQDataDetail.get(0).get("GETMAN").toString();
        String orderGetManTel = getQDataDetail.get(0).get("GETMANTEL").toString();
        String orderShipDate = getQDataDetail.get(0).get("SHIPDATE").toString();
        String orderShipStartTime = getQDataDetail.get(0).get("SHIPSTARTTIME").toString();
        String orderShipEndTime = getQDataDetail.get(0).get("SHIPENDTIME").toString();
        String orderShipShopNo = getQDataDetail.get(0).get("SHIPPINGSHOP").toString();
        String orderShipShopName = getQDataDetail.get(0).get("SHIPPINGSHOPNAME").toString();
        String orderMachShopNo = getQDataDetail.get(0).get("MACHSHOP").toString();
        String orderMachShopName = getQDataDetail.get(0).get("MACHSHOPNAME").toString();
        String orderMemo = getQDataDetail.get(0).get("MEMO").toString();
        String orderProMemo = getQDataDetail.get(0).get("PROMEMO").toString();
        String orderDelMemo = getQDataDetail.get(0).get("DELMEMO").toString();
        String deliverStatus = getQDataDetail.get(0).getOrDefault("DELIVERYSTATUS", "").toString();//配送状态
        String productStatus = getQDataDetail.get(0).getOrDefault("PRODUCTSTATUS", "").toString();//生产状态
        String isShipcompany_db = getQDataDetail.get(0).getOrDefault("ISSHIPCOMPANY", "").toString();//是否总部生产
        String isDelete_db = getQDataDetail.get(0).getOrDefault("ISDELETE", "").toString();

        String order_province = getQDataDetail.get(0).getOrDefault("PROVINCE", "").toString();
        String order_city = getQDataDetail.get(0).getOrDefault("CITY", "").toString();
        String order_county = getQDataDetail.get(0).getOrDefault("COUNTY", "").toString();
        String order_street = getQDataDetail.get(0).getOrDefault("STREET", "").toString();

        String order_deliveryBusinessType = getQDataDetail.get(0).getOrDefault("DELIVERYBUSINESSTYPE", "").toString();
        String order_isUrgentOrder = getQDataDetail.get(0).getOrDefault("ISURGENTORDER", "").toString();
        String order_deliveryType = getQDataDetail.get(0).getOrDefault("DELIVERYTYPE", "").toString();
        String order_longitude = getQDataDetail.get(0).getOrDefault("LONGITUDE", "").toString();
        String order_latitude = getQDataDetail.get(0).getOrDefault("LATITUDE", "").toString();

        String delId = getQDataDetail.get(0).getOrDefault("DELID", "").toString();
        String delName = getQDataDetail.get(0).getOrDefault("DELNAME", "").toString();
        String delTelephone = getQDataDetail.get(0).getOrDefault("DELTELEPHONE", "").toString();
        String packerId = getQDataDetail.get(0).getOrDefault("PACKERID", "").toString();
        String packerName = getQDataDetail.get(0).getOrDefault("PACKERNAME", "").toString();
        String packerTelephone = getQDataDetail.get(0).getOrDefault("PACKERTELEPHONE", "").toString();
        String canModify = getQDataDetail.get(0).getOrDefault("CANMODIFY", "").toString();
        String operationtype = getQDataDetail.get(0).getOrDefault("OPERATIONTYPE", "").toString();

        //【ID1037561】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单下订增加配送费，超区费，加急费同时支持费用补录---服务 by jinzma 20231206
        String deliveryMoney = getQDataDetail.get(0).get("DELIVERYMONEY").toString();       //配送费
        String superZoneMoney = getQDataDetail.get(0).get("SUPERZONEMONEY").toString();     //超区费
        String urgentMoney = getQDataDetail.get(0).get("URGENTMONEY").toString();           //加急费

        //【ID1037555】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单打印贺卡及配送路线选择---中台修改
        String isHaveCard = getQDataDetail.get(0).get("ISHAVECARD").toString();         //是否含贺卡  Y/N
        String isCardPrint = getQDataDetail.get(0).get("ISCARDPRINT").toString();       //贺卡是否已打印  Y/N
        String lineNo = getQDataDetail.get(0).get("LINENO").toString();                 //路线编码
        String lineName = getQDataDetail.get(0).get("LINENAME").toString();             //路线名称


        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】查询完成：单号OrderNo=" + orderNo + " 数据库中下单门店=" + orderShop
                + " 订单状态status=" + orderStatus);

        if (orderStatus.equals("0") || orderStatus.equals("1"))
        {
            //订单状态status为：0待审核或1订单开立；才可进行订单编辑，其他订单状态不允许编辑（后端需对订单状态校验）
        }
        else
        {

            StringBuilder statusTypeName = new StringBuilder("");
            String statusType = "1";
            String statusName = HelpTools.GetOrderStatusName(statusType, orderStatus, statusTypeName);
            res.setSuccess(false);
            res.setServiceDescription("该订单状态是" + statusName + "，不能修改！");
            HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】该订单状态是" + statusName + "，不能修改信息！单号OrderNO=" + orderNo
                    + " 数据库中下单门店=" + orderShop + " 订单状态status=" + orderStatus);
            return;
        }
        //增加菲尔雪逻辑
        //是否需要生产控制” ControlProduction    Y/N 默认值N；
        String ControlProduction = PosPub.getPARA_SMS(dao,eId,"","ControlProduction");
        //操作门店不为空，并且参数是Y
        if ("Y".equals(ControlProduction)&&!Check.Null(req.getRequest().getShopId()))
        {
            //// operationType枚举值：0或者空 表示不可修改订单不可退订 1表示可修改订单，2表示可退单
            if ("1".equals(operationtype)==false)
            {
                //生产状态(4生产接单；5生产拒单；6完工入库；7内部调拨）
                if ("4".equals(productStatus)||"6".equals(productStatus)||"7".equals(productStatus))
                {
                    //1下单机构，2生产机构，3配送机构  默认值2；
                    String typeOfOrg = PosPub.getPARA_SMS(dao,eId,"","TypeOfOrganization");
                    if (StrUtil.isEmpty(typeOfOrg))
                    {
                        typeOfOrg = "2";
                    }
                    String str_error = "需要生产机构同意，请联系该门店";
                    boolean isCheckOrg = true;
                    String shop_create = getQDataDetail.get(0).get("SHOP").toString();
                    String shopName_create = getQDataDetail.get(0).get("SHOPNAME").toString();
                    String shop_mach = getQDataDetail.get(0).get("MACHSHOP").toString();
                    String shopName_mach = getQDataDetail.get(0).get("MACHSHOPNAME").toString();
                    String shop_shipping = getQDataDetail.get(0).get("SHIPPINGSHOP").toString();
                    String shopName_shipping = getQDataDetail.get(0).get("SHIPPINGSHOPNAME").toString();
                    if ("1".equals(typeOfOrg))
                    {
                        if (shop_create!=null&&shop_create.trim().isEmpty()==false)
                        {
                            str_error = "需要下单机构("+shop_create+shopName_create+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }
                    else if ("3".equals(typeOfOrg))
                    {
                        if (shop_shipping!=null&&shop_shipping.trim().isEmpty()==false)
                        {
                            str_error = "需要配送机构("+shop_shipping+shopName_shipping+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }
                    else
                    {
                        if (shop_mach!=null&&shop_mach.trim().isEmpty()==false)
                        {
                            str_error = "需要生产机构("+shop_mach+shopName_mach+")同意，请联系该门店!";
                            isCheckOrg = false;
                        }
                    }

                    if (!isCheckOrg)
                    {
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        res.setServiceDescription(str_error);
                        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】"+str_error+"，不能修改信息！单号OrderNO=" + orderNo
                                + " 数据库中下单门店=" + orderShop + " 订单状态status=" + orderStatus);
                        return;
                    }
                }

            }

        }


        boolean isCanModify_deliverStatus = false;//是否可以修改配送相关
        if(deliverStatus==null||deliverStatus.isEmpty()||deliverStatus.equals("4")||deliverStatus.equals("5"))
        {
            //配送状态为4或5或空时才可修改配送机构
            isCanModify_deliverStatus = true;
        }
        StringBuilder statusTypeName = new StringBuilder("");
        String deliverStatusName = HelpTools.GetOrderStatusName("2", deliverStatus, statusTypeName);


        boolean isCanModify_productStatus = false;//是否可以修改生产相关
        if(productStatus==null||productStatus.isEmpty()||productStatus.equals("5"))
        {
            //生产状态为5.生产拒单或空时才可修改生产机构
            isCanModify_productStatus = true;
        }
        statusTypeName = new StringBuilder("");
        String productStatusName = HelpTools.GetOrderStatusName("4", productStatus, statusTypeName);


        // 防止没有传更新的节点，那么就不用执行语句
        boolean isNeedUpdate = false;
        StringBuffer logmemo = new StringBuffer("");
        UptBean ub1 = null;
        ub1 = new UptBean("DCP_ORDER");

        // condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));
        ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));

        boolean isUpdateCanModify = false;//是不是配送补录修改

        if (req.getRequest().getShipType() != null && req.getRequest().getShipType().trim().length() > 0)
        {
            if (req.getRequest().getShipType().length() > 1)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipType节点值太大，长度不能超过1，");
            }
            if(req.getRequest().getShipType().equals(orderShipType)==false)
            {
                if(!isCanModify_deliverStatus)
                {
                    String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送方式！";
                    HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                            + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
                }

                ub1.addUpdateValue("shiptype", new DataValue(req.getRequest().getShipType(), Types.VARCHAR));

                // 修改配送方式的时候，同时清空配送状态
                if (!orderStatus.equals("12"))
                {
                    // 为12时，不接受配送状态变更的信息，否则会影响到之前的配送状态
                    ub1.addUpdateValue("DeliveryStatus", new DataValue("", Types.VARCHAR));
                }

                isNeedUpdate = true;

                String bname = "";

                if (orderShipType.equals("1"))
                {
                    bname = "订单来源渠道";
                } else if (orderShipType.equals("2"))
                {
                    bname = "全国配送";
                } else if (orderShipType.equals("3"))
                {
                    bname = "顾客自提";
                } else if (orderShipType.equals("5"))
                {
                    bname = "总部配送";
                } else if (orderShipType.equals("6"))
                {
                    bname = "同城配送";
                } else
                {
                    bname = "其他未知";
                }

                String ename = "";
                if (req.getRequest().getShipType().equals("1"))
                {
                    ename = "订单来源渠道";
                } else if (req.getRequest().getShipType().equals("2"))
                {
                    ename = "全国配送";
                } else if (req.getRequest().getShipType().equals("3"))
                {
                    ename = "顾客自提";
                } else if (req.getRequest().getShipType().equals("5"))
                {
                    ename = "总部配送";
                } else if (req.getRequest().getShipType().equals("6"))
                {
                    ename = "同城配送";
                } else
                {
                    ename = "其他未知";
                }

                logmemo .append( "配送方式：" +orderShipType+"("+ bname +")"+ "-->" +req.getRequest().getShipType()+"("+ ename +")" + "<br>");
            }

        }

        if (req.getRequest().getShippingShopNo() != null)
        {
            if (req.getRequest().getShippingShopNo().length() > 20)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shippingShopNo节点值太大，长度不能超过20，");
            }
            if(req.getRequest().getShippingShopNo().equals(orderShipShopNo)==false)
            {

                if(!isCanModify_deliverStatus)
                {
                    String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送门店！";
                    HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                            + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
                }


                ub1.addUpdateValue("SHIPPINGSHOP", new DataValue(req.getRequest().getShippingShopNo(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送门店：" + orderShipShopNo + "-->" + req.getRequest().getShippingShopNo()+"<br>");

                //更新商品单身对应的仓库
                try
                {
                    String shippingShopNo_update = req.getRequest().getShippingShopNo();
                    HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】【修改了配送门店】【获取配送门店的仓库】配送门店shippingShopNo="+shippingShopNo_update+"， 单号orderNo="+orderNo);
                    String sql_warehouse = "select A.OUT_COST_WAREHOUSE,AL.WAREHOUSE_NAME from dcp_org  A "
                            + " left join dcp_warehouse_lang AL on A.EID=AL.EID  AND   A.ORGANIZATIONNO=AL.ORGANIZATIONNO AND A.OUT_COST_WAREHOUSE = AL.WAREHOUSE AND AL.LANG_TYPE='"+langType+"' "
                            + " where  A.EID='"+eId+"' and A.ORGANIZATIONNO ='"+shippingShopNo_update+"' ";

                    HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】【修改了配送门店】【获取配送门店的仓库】查询配送门店仓库sql="+sql_warehouse+",配送门店shippingShopNo="+shippingShopNo_update+"， 单号orderNo="+orderNo);

                    List<Map<String, Object>> getShippingshopWarehouseInfo = StaticInfo.dao.executeQuerySQL(sql_warehouse, null);

                    if(getShippingshopWarehouseInfo==null||getShippingshopWarehouseInfo.isEmpty())
                    {
                        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】【修改了配送门店】【获取配送门店的仓库】，配送门店shippingShopNo="+ shippingShopNo_update+"查无资料,单号orderNo="+orderNo);
                    }
                    else
                    {

                        String warehouseNo = getShippingshopWarehouseInfo.get(0).getOrDefault("OUT_COST_WAREHOUSE","").toString();
                        String warehouseName = getShippingshopWarehouseInfo.get(0).getOrDefault("WAREHOUSE_NAME","").toString();
                        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】【修改了配送门店】【更新DCP_ORDER_DETAIL】更新WAREHOUSE="+warehouseNo+",更新WAREHOUSENAME="+warehouseName+"， 单号orderNo="+orderNo);
                        //需要更新下商品单身的仓库
                        UptBean up2 = new UptBean("DCP_ORDER_DETAIL");
                        up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        up2.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));


                        up2.addUpdateValue("WAREHOUSE", new DataValue(warehouseNo, Types.VARCHAR));
                        up2.addUpdateValue("WAREHOUSENAME", new DataValue(warehouseName, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up2));

                    }

                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }



            }

        }

        if (req.getRequest().getShippingShopName() != null)
        {
            String shippingShopName = req.getRequest().getShippingShopName() == null ? ""
                    : req.getRequest().getShippingShopName();
            if(shippingShopName.equals(orderShipShopName)==false)
            {
                if (shippingShopName.length() > 80)
                {
                    shippingShopName = shippingShopName.substring(0, 80);
                }
                ub1.addUpdateValue("SHIPPINGSHOPNAME", new DataValue(shippingShopName, Types.VARCHAR));
                isNeedUpdate = true;
                logmemo .append( "配送门店名称：" +  orderShipShopName + "-->"+ req.getRequest().getShippingShopName() + "<br>");
            }
        }

        if (req.getRequest().getMachShopNo() != null)
        {
            if (req.getRequest().getMachShopNo().length() > 20)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的machShopNo节点值太大，长度不能超过20，");
            }
            if(req.getRequest().getMachShopNo().equals(orderMachShopNo)==false)
            {

                if(!isCanModify_productStatus)
                {
                    String errorLog = "该订单生产是"+productStatusName+"，不能修改生产门店！";
                    HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                            + " 数据库中 生产状态productStatus=" + productStatus);
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
                }

                ub1.addUpdateValue("MACHSHOP", new DataValue(req.getRequest().getMachShopNo(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "生产门店：" + orderMachShopNo +  "-->" + req.getRequest().getMachShopNo() + "<br>");


                //这里查询下修改得门店是不是，总部
                String sql_shop = "select * from dcp_org where eid='"+eId+"' and ORGANIZATIONNO='"+req.getRequest().getMachShopNo()+"' ";
                List<Map<String, Object>> getShopData = this.doQueryData(sql_shop, null);
                if(getShopData!=null&&getShopData.isEmpty()==false)
                {
                    String isShipcompany_req = "N";
                    String org_form = getShopData.get(0).getOrDefault("ORG_FORM", "").toString();
                    if(org_form.equals("0"))
                    {
                        isShipcompany_req = "Y";
                    }

                    if(isShipcompany_db.equals(isShipcompany_req)==false)
                    {
                        ub1.addUpdateValue("ISSHIPCOMPANY", new DataValue(isShipcompany_req, Types.VARCHAR));
                        logmemo .append( "是否总部生产：" + isShipcompany_db + "-->" + isShipcompany_req+ "<br>");
                    }
                }

              /*  //需要更新下商品单身的shopQty
                UptBean up2 = new UptBean("DCP_ORDER_DETAIL");
                up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                up2.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));

                up2.addUpdateValue("SHOPQTY", new DataValue("0", Types.FLOAT));
                this.addProcessData(new DataProcessBean(up2));*/


            }

        }

        if (req.getRequest().getMachShopName() != null)
        {
            String machShopName = req.getRequest().getMachShopName() == null ? ""
                    : req.getRequest().getMachShopName();
            if(machShopName.equals(orderMachShopName)==false)
            {
                if (machShopName.length() > 80)
                {
                    machShopName = machShopName.substring(0, 80);
                }
                ub1.addUpdateValue("MACHSHOPNAME", new DataValue(machShopName, Types.VARCHAR));
                isNeedUpdate = true;
                logmemo .append( "生产门店名称：" +  orderMachShopName + "-->"+ req.getRequest().getMachShopName() + "<br>");
            }

        }


        if (req.getRequest().getShipDate() != null)
        {
            if (req.getRequest().getShipDate().length() > 8)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过8，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送日期！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getShipDate().equals(orderShipDate)==false)
            {
                ub1.addUpdateValue("SHIPDATE", new DataValue(req.getRequest().getShipDate(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送日期：" + orderShipDate + "-->" + req.getRequest().getShipDate() + "<br>");
            }

        }

        if (req.getRequest().getShipStartTime() != null)
        {
            if (req.getRequest().getShipStartTime().length() > 50)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipStartTTime节点值太大，长度不能超过50，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送时间！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getShipStartTime().equals(orderShipStartTime)==false)
            {
                ub1.addUpdateValue("SHIPSTARTTIME", new DataValue(req.getRequest().getShipStartTime(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送开始时间：" + orderShipStartTime + "-->" + req.getRequest().getShipStartTime() + "<br>");
            }

        }

        if (req.getRequest().getShipEndTime() != null)
        {
            if (req.getRequest().getShipEndTime().length() > 50)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipEndTime节点值太大，长度不能超过50，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送时间！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getShipEndTime().equals(orderShipEndTime)==false)
            {
                ub1.addUpdateValue("SHIPENDTIME", new DataValue(req.getRequest().getShipEndTime(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送结束时间：" + orderShipEndTime + "-->" + req.getRequest().getShipEndTime() + "<br>");
            }

        }

        if (req.getRequest().getDeliveryType() != null)
        {
            if (req.getRequest().getDeliveryType().length() > 6)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的deliveryType节点值太大，长度不能超过6，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改物流类型！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getDeliveryType().equals(order_deliveryType)==false)
            {
                ub1.addUpdateValue("DELIVERYTYPE", new DataValue(req.getRequest().getDeliveryType(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "物流类型：" + order_deliveryType + "-->" + req.getRequest().getDeliveryType() + "<br>");
            }

        }

        if (req.getRequest().getAddress() != null)
        {
            if (req.getRequest().getAddress().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的address节点值太大，长度不能超过100，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getAddress().equals(orderAdress)==false)
            {
                ub1.addUpdateValue("address", new DataValue(req.getRequest().getAddress(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "收货地址：" + orderAdress + "-->" + req.getRequest().getAddress() + "<br>");
            }

        }

        if (req.getRequest().getProvince() != null)
        {
            if (req.getRequest().getProvince().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送省份！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getProvince().equals(order_province)==false)
            {
                ub1.addUpdateValue("PROVINCE", new DataValue(req.getRequest().getProvince(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送省份：" + order_province + "-->" + req.getRequest().getProvince() + "<br>");
            }

        }

        if (req.getRequest().getCity() != null)
        {
            if (req.getRequest().getCity().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送城市！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getCity().equals(order_city)==false)
            {
                ub1.addUpdateValue("CITY", new DataValue(req.getRequest().getCity(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送城市：" + order_city + "-->" + req.getRequest().getCity() + "<br>");
            }

        }

        if (req.getRequest().getCounty() != null)
        {
            if (req.getRequest().getCounty().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送区县！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getCounty().equals(order_county)==false)
            {
                ub1.addUpdateValue("COUNTY", new DataValue(req.getRequest().getCounty(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送区县：" + order_county + "-->" + req.getRequest().getCounty() + "<br>");
            }

        }

        if (req.getRequest().getStreet() != null)
        {
            if (req.getRequest().getStreet().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送街道！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getStreet().equals(order_street)==false)
            {
                ub1.addUpdateValue("STREET", new DataValue(req.getRequest().getStreet(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送街道：" + order_street + "-->" + req.getRequest().getStreet() + "<br>");
            }

        }

        if (req.getRequest().getLongitude() != null&&!req.getRequest().getLongitude().isEmpty())
        {
            try
            {
                Double.parseDouble(req.getRequest().getLongitude());
            } catch (Exception e)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的longitude节点值必须是数字类型，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址的经度！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getLongitude().equals(order_longitude)==false)
            {
                ub1.addUpdateValue("LONGITUDE", new DataValue(req.getRequest().getLongitude(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送地址经度：" + order_longitude + "-->" + req.getRequest().getLongitude() + "<br>");
            }

        }

        if (req.getRequest().getLatitude() != null&&!req.getRequest().getLatitude().isEmpty())
        {
            try
            {
                Double.parseDouble(req.getRequest().getLatitude());
            } catch (Exception e)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的longitude节点值必须是数字类型，");
            }
            if(!isCanModify_deliverStatus)
            {
                String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址的维度！";
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】" + errorLog + "，单号OrderNO=" + orderNo
                        + " 数据库中 配送状态deliverStatus=" + deliverStatus);
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
            }
            if(req.getRequest().getLatitude().equals(order_latitude)==false)
            {
                ub1.addUpdateValue("LATITUDE", new DataValue(req.getRequest().getLatitude(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送地址维度：" + order_latitude + "-->" + req.getRequest().getLatitude() + "<br>");
            }

        }

        if (req.getRequest().getContMan() != null)
        {
            if (req.getRequest().getContMan().length() > 40)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的contMan节点值太大，长度不能超过40，");
            }
            if(req.getRequest().getContMan().equals(orderContMan)==false)
            {
                ub1.addUpdateValue("CONTMAN", new DataValue(req.getRequest().getContMan(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "订货人：" + orderContMan + "-->" + req.getRequest().getContMan() + "<br>");
            }

        }

        if (req.getRequest().getContTel() != null)
        {
            if (req.getRequest().getContTel().length() > 40)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getManTel节点值太大，长度不能超过40，");
            }
            if(req.getRequest().getContTel().equals(orderContTel)==false)
            {
                ub1.addUpdateValue("CONTTEL", new DataValue(req.getRequest().getContTel(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "订货人电话：" + orderContTel + "-->" + req.getRequest().getContTel() + "<br>");

            }

        }


        if (req.getRequest().getGetMan() != null)
        {
            if (req.getRequest().getGetMan().length() > 40)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getMan节点值太大，长度不能超过40，");
            }
            if(req.getRequest().getGetMan().equals(orderGetMan)==false)
            {
                ub1.addUpdateValue("getman", new DataValue(req.getRequest().getGetMan(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "收货人：" + orderGetMan + "-->" + req.getRequest().getGetMan() + "<br>");
            }

        }

        if (req.getRequest().getGetManTel() != null)
        {
            if (req.getRequest().getGetManTel().length() > 40)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getManTel节点值太大，长度不能超过40，");
            }
            if(req.getRequest().getGetManTel().equals(orderGetManTel)==false)
            {
                ub1.addUpdateValue("GETMANTEL", new DataValue(req.getRequest().getGetManTel(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "收货人电话：" + orderGetManTel + "-->" + req.getRequest().getGetManTel() + "<br>");

            }

        }

        if (req.getRequest().getDeliveryBusinessType() != null)
        {
            if (req.getRequest().getDeliveryBusinessType().length() > 32)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的deliveryBusinessType节点值太大，长度不能超过32，");
            }
            if(req.getRequest().getDeliveryBusinessType().equals(order_deliveryBusinessType)==false)
            {
                ub1.addUpdateValue("DELIVERYBUSINESSTYPE",
                        new DataValue(req.getRequest().getDeliveryBusinessType(), Types.VARCHAR));

                isNeedUpdate = true;

                String bname = "";

                if (order_deliveryBusinessType.equals("1"))
                {
                    bname = "随车";
                } else if (order_deliveryBusinessType.equals("2"))
                {
                    bname = "代发";
                } else if (order_deliveryBusinessType.equals("3"))
                {
                    bname = "总部派车";
                } else
                {
                    bname = "其他未知";
                }
                String ename = "";
                if (req.getRequest().getDeliveryBusinessType().equals("1"))
                {
                    ename = "随车";
                } else if (req.getRequest().getDeliveryBusinessType().equals("2"))
                {
                    ename = "代发";
                } else if (req.getRequest().getDeliveryBusinessType().equals("3"))
                {
                    ename = "总部派车";
                } else
                {
                    ename = "其他未知";
                }

                logmemo.append("配送业务类型：" + order_deliveryBusinessType + "(" + bname + ")" + "-->"
                        + req.getRequest().getDeliveryBusinessType() + "(" + ename + ")" + "<br>");
            }

        }

        if (req.getRequest().getIsUrgentOrder() != null)
        {
            if (req.getRequest().getIsUrgentOrder().length() > 1)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的isUrgentOrder节点值太大，长度不能超过1，");
            }
            if(req.getRequest().getIsUrgentOrder().equals(order_isUrgentOrder)==false)
            {
                ub1.addUpdateValue("ISURGENTORDER", new DataValue(req.getRequest().getIsUrgentOrder(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "是否紧急订单：" + order_isUrgentOrder + "-->" + req.getRequest().getIsUrgentOrder() + "<br>");

            }

        }

        if (req.getRequest().getMemo() != null)
        {
            if (req.getRequest().getMemo().length() > 255)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入memo节点值太大，长度不能超过255，");
            }
            if(req.getRequest().getMemo().equals(orderMemo)==false)
            {
                ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "订单备注：" + orderMemo + "-->" + req.getRequest().getMemo() + "<br>");
            }

        }

        if (req.getRequest().getProMemo() != null)
        {
            if (req.getRequest().getProMemo().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入proMemo节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getProMemo().equals(orderProMemo)==false)
            {
                ub1.addUpdateValue("PROMEMO", new DataValue(req.getRequest().getProMemo(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "生产备注：" + orderProMemo + "-->" + req.getRequest().getProMemo() + "<br>");
            }

        }

        if (req.getRequest().getDelMemo() != null)
        {
            if (req.getRequest().getDelMemo().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入delMemo节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getDelMemo().equals(orderDelMemo)==false)
            {
                ub1.addUpdateValue("DELMEMO", new DataValue(req.getRequest().getDelMemo(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送备注：" + orderDelMemo + "-->" + req.getRequest().getDelMemo() + "<br>");
            }

        }

        if(req.getRequest().getIsDelete()!=null)
        {
            if (req.getRequest().getIsDelete().length() > 1)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入isDelete节点值太大，长度不能超过1，");
            }
            if(req.getRequest().getIsDelete().equals(isDelete_db)==false)
            {
                ub1.addUpdateValue("ISDELETE", new DataValue(req.getRequest().getIsDelete(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "ISDELETE：" + isDelete_db + "-->" + req.getRequest().getIsDelete() + "<br>");
            }
        }

        if (req.getRequest().getPackerId() != null)
        {
            if (req.getRequest().getPackerId().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerId节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getPackerId().equals(packerId)==false)
            {
                ub1.addUpdateValue("PACKERID", new DataValue(req.getRequest().getPackerId(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "打包人ID：" + packerId + "-->" + req.getRequest().getPackerId() + "<br>");
            }

        }
        if (req.getRequest().getPackerName() != null)
        {
            if (req.getRequest().getPackerName().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerName节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getPackerName().equals(packerName)==false)
            {
                ub1.addUpdateValue("PACKERNAME", new DataValue(req.getRequest().getPackerName(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "打包人：" + packerName + "-->" + req.getRequest().getPackerName() + "<br>");
            }

        }
        if (req.getRequest().getPackerTelephone() != null)
        {
            if (req.getRequest().getPackerTelephone().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerTelephone节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getPackerTelephone().equals(packerTelephone)==false)
            {
                ub1.addUpdateValue("PACKERTELEPHONE", new DataValue(req.getRequest().getPackerTelephone(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "打包人电话：" + packerTelephone + "-->" + req.getRequest().getPackerTelephone() + "<br>");
            }

        }

        if (req.getRequest().getDelId() != null)
        {
            if (req.getRequest().getDelId().length() > 10)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delId节点值太大，长度不能超过10，");
            }
            if(req.getRequest().getDelId().equals(delId)==false)
            {
                ub1.addUpdateValue("DELID", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送人ID：" + delId + "-->" + req.getRequest().getDelId() + "<br>");
            }

        }
        if (req.getRequest().getDelName() != null)
        {
            if (req.getRequest().getDelName().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delName节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getDelName().equals(delName)==false)
            {
                ub1.addUpdateValue("DELNAME", new DataValue(req.getRequest().getDelName(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送人：" + delName + "-->" + req.getRequest().getDelName() + "<br>");
            }

        }
        if (req.getRequest().getDelTelephone() != null)
        {
            if (req.getRequest().getDelTelephone().length() > 100)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delTelephone节点值太大，长度不能超过100，");
            }
            if(req.getRequest().getDelTelephone().equals(delTelephone)==false)
            {
                ub1.addUpdateValue("DELTELEPHONE", new DataValue(req.getRequest().getDelTelephone(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "配送人电话：" + delTelephone + "-->" + req.getRequest().getDelTelephone() + "<br>");
            }

        }

        if (req.getRequest().getCanModify() != null)
        {
            if (req.getRequest().getCanModify().length() > 1)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的canModify节点值太大，长度不能超过1，");
            }
            if(req.getRequest().getCanModify().equals(canModify)==false)
            {
                ub1.addUpdateValue("CANMODIFY", new DataValue(req.getRequest().getCanModify(), Types.VARCHAR));

                isNeedUpdate = true;
                logmemo .append( "是否允许补录：" + canModify + "-->" + req.getRequest().getCanModify() + "<br>");
                //把配送补录有Y改成N，Y-->N
                if ("N".equals(req.getRequest().getCanModify()))
                {
                    isUpdateCanModify = true;
                }
            }

        }

        //【ID1037561】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单下订增加配送费，超区费，加急费同时支持费用补录---服务 by jinzma 20231206
        if (PosPub.isNumericType(req.getRequest().getDeliveryMoney())){
            if(!req.getRequest().getDeliveryMoney().equals(deliveryMoney)) {
                ub1.addUpdateValue("DELIVERYMONEY", new DataValue(req.getRequest().getDeliveryMoney(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "配送费：" + deliveryMoney + "-->" + req.getRequest().getDeliveryMoney() + "<br>");
            }
        }

        if (PosPub.isNumericType(req.getRequest().getSuperZoneMoney())){
            if(!req.getRequest().getSuperZoneMoney().equals(superZoneMoney)) {
                ub1.addUpdateValue("SUPERZONEMONEY", new DataValue(req.getRequest().getSuperZoneMoney(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "超区费：" + superZoneMoney + "-->" + req.getRequest().getSuperZoneMoney() + "<br>");
            }
        }

        if (PosPub.isNumericType(req.getRequest().getUrgentMoney())){
            if(!req.getRequest().getUrgentMoney().equals(urgentMoney)) {
                ub1.addUpdateValue("URGENTMONEY", new DataValue(req.getRequest().getUrgentMoney(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "加急费：" + urgentMoney + "-->" + req.getRequest().getUrgentMoney() + "<br>");
            }
        }

        //【ID1037555】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单打印贺卡及配送路线选择---中台修改   by jinzma 20231222
        if (req.getRequest().getIsHaveCard() != null){
            if(!req.getRequest().getIsHaveCard().equals(isHaveCard)) {
                ub1.addUpdateValue("ISHAVECARD", new DataValue(req.getRequest().getIsHaveCard(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "是否含贺卡：" + isHaveCard + "-->" + req.getRequest().getIsHaveCard() + "<br>");
            }
        }

        if (req.getRequest().getIsCardPrint() != null){
            if(!req.getRequest().getIsCardPrint().equals(isCardPrint)) {
                ub1.addUpdateValue("ISCARDPRINT", new DataValue(req.getRequest().getIsCardPrint(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "贺卡是否已打印：" + isCardPrint + "-->" + req.getRequest().getIsCardPrint() + "<br>");
            }
        }

        if (req.getRequest().getLineNo() != null){
            if(!req.getRequest().getLineNo().equals(lineNo)) {
                ub1.addUpdateValue("LINENO", new DataValue(req.getRequest().getLineNo(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "路线编码：" + lineNo + "-->" + req.getRequest().getLineNo() + "<br>");
            }
        }

        if (req.getRequest().getLineName() != null){
            if(!req.getRequest().getLineName().equals(lineName)) {
                ub1.addUpdateValue("LINENAME", new DataValue(req.getRequest().getLineName(), Types.VARCHAR));
                isNeedUpdate = true;
                logmemo.append( "路线名称：" + lineName + "-->" + req.getRequest().getLineName() + "<br>");
            }
        }


        //商品备注修改
        List<level1ELM> goodsList = req.getRequest().getGoodsList();

        if(goodsList!=null&&goodsList.isEmpty()==false)
        {
            for (level1ELM oneLv1 : goodsList)
            {
                String item = oneLv1.getItem();
                List<level2ELM> messageList = oneLv1.getMessages();
                if(item==null||item.isEmpty()||messageList==null||messageList.isEmpty())
                {
                    continue;
                }
                isNeedUpdate = true;

                //需要更新下商品单身的isMemo
                UptBean up2 = new UptBean("DCP_ORDER_DETAIL");
                up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                up2.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));
                up2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                up2.addUpdateValue("ISMEMO", new DataValue("Y", Types.VARCHAR));
                this.addProcessData(new DataProcessBean(up2));

                DelBean del = new DelBean("DCP_ORDER_DETAIL_MEMO");
                del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));
                del.addCondition("OITEM", new DataValue(item, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(del));

                String[] columns_goodsMemo =
                        { "eid", "orderno", "SHOPID", "OITEM", "ITEM", "MEMONAME","MEMOTYPE","MEMO"};

                int goodsMemoItem = 0;
                StringBuffer goodsMemo = new StringBuffer("");
                for (level2ELM  goodsMessage : messageList)
                {
                    goodsMemoItem++;
                    goodsMemo .append( goodsMessage.getMessage()+",");
                    DataValue[] insValue_goodsMemo = new DataValue[] {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(orderShop, Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(goodsMemoItem, Types.VARCHAR),
                            new DataValue(goodsMessage.getMsgName(), Types.VARCHAR),
                            new DataValue(goodsMessage.getMsgType(), Types.VARCHAR),
                            new DataValue(goodsMessage.getMessage(), Types.VARCHAR),
                    };

                    InsBean ib_goodsMemo = new InsBean("DCP_ORDER_DETAIL_MEMO", columns_goodsMemo);
                    ib_goodsMemo.addValues(insValue_goodsMemo);
                    this.addProcessData(new DataProcessBean(ib_goodsMemo));
                }

                logmemo .append( "商品项次："+item+",修改备注：" + goodsMemo.toString()+  "<br>");

            }
        }

        if (isNeedUpdate == false)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请传入需要修改的节点！");
        }
        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
        //防止时间差，在查询一次
        boolean isNeedRequestErp = isUploadErp(eId, orderNo);
        if(isNeedRequestErp)
        {

            String str = this.getRequestERPJson(req, eId, orderNo);
            HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】调用ERP接口 holidayorder.create请求："+str);
            String resbody="";
            try
            {
                String updateDatetime_log = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());//提前给写日志主键赋值，后面写可能会重复
                resbody=HttpSend.Send(str, "holidayorder.create", eId, orderShop,orderShop,orderNo);

                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】ERP接口 holidayorder.create返回："+resbody);
                if(resbody!=null&& resbody.isEmpty()==false )
                {
                    JSONObject jsonres = new JSONObject(resbody);
                    JSONObject std_data_res = jsonres.getJSONObject("std_data");
                    JSONObject execution_res = std_data_res.getJSONObject("execution");

                    String code = execution_res.getString("code");
                    String description ="";

                    if (!execution_res.isNull("description") )
                    {
                        description = execution_res.getString("description");
                    }

                    if (code.equals("0"))
                    {

                        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】 holidayorder.create返回：成功");


                        orderStatusLog onelv_erp = new orderStatusLog();

                        onelv_erp.setLoadDocType(loadDocType);
                        onelv_erp.setChannelId(channelId);

                        onelv_erp.setNeed_callback("N");
                        onelv_erp.setNeed_notify("N");

                        onelv_erp.seteId(eId);

                        String opNO = req.getRequest().getOpId() == null ? "" : req.getRequest().getOpId();

                        String o_opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();

                        onelv_erp.setOpNo(opNO);
                        onelv_erp.setOpName(o_opName);
                        onelv_erp.setOrderNo(orderNo);
                        onelv_erp.setLoadDocBillType(loadDocBillType);
                        onelv_erp.setLoadDocOrderNo(loadDocOrderNo);

                        String statusType_log = "99";// 其他状态
                        String updateStaus_log = "99";// 订单修改

                        onelv_erp.setStatusType(statusType_log);
                        onelv_erp.setStatus(updateStaus_log);
                        String statusName_log = "已请求ERP";
                        String statusTypeName_log = "其他状态";
                        onelv_erp.setStatusTypeName(statusTypeName_log);
                        onelv_erp.setStatusName(statusName_log);

                        StringBuffer memo = new StringBuffer("");
                        memo .append( statusTypeName_log + "-->" + statusName_log + "(订单修改)<br>");
                        memo .append( logmemo.toString());
                        onelv_erp.setMemo(memo.toString());
                        onelv_erp.setUpdate_time(updateDatetime_log);
                        orderStatusLogList.add(onelv_erp);


                    }
                    else
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用ERP接口返回异常:"+description);

                    }

                }
                else
                {
                    HelpTools.writelog_waimai("门店订单修改调用ERP接口 holidayorder.create返回为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店订单修改调用ERP接口holidayorder.create返回为空！");
                }


            }
            catch (Exception e)
            {
                HelpTools.writelog_waimai("门店订单修改调用ERP接口 holidayorder.create返回异常："+e.getMessage());
                // region 写下日志
                try
                {
                    List<orderStatusLog> orderStatusLogErrorList = new ArrayList<orderStatusLog>();

                    orderStatusLog onelv1 = new orderStatusLog();

                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);

                    onelv1.setNeed_callback("N");
                    onelv1.setNeed_notify("N");

                    onelv1.seteId(eId);

                    String opNO = req.getRequest().getOpId() == null ? "" : req.getRequest().getOpId();
                    if (opNO.isEmpty())
                    {
                        opNO = req.getOpNO()==null?"":req.getOpNO();
                    }

                    String o_opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();
                    if (o_opName.isEmpty())
                    {
                        o_opName = req.getOpName()==null?"":req.getOpName();
                    }

                    onelv1.setOpNo(opNO);
                    onelv1.setOpName(o_opName);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setLoadDocBillType(loadDocBillType);
                    onelv1.setLoadDocOrderNo(loadDocOrderNo);

                    String statusType_log = "99";// 其他状态
                    String updateStaus_log = "99";// 订单修改

                    onelv1.setStatusType(statusType_log);
                    onelv1.setStatus(updateStaus_log);
                    String statusName_log = "订单修改调用ERP接口失败";
                    String statusTypeName_log = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName_log);
                    onelv1.setStatusName(statusName_log);

                    StringBuffer memo = new StringBuffer("");
                    memo.append( statusTypeName_log + "-->" + statusName_log + "<br>");
                    memo.append(e.getMessage());
                    onelv1.setMemo(memo.toString());
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    orderStatusLogErrorList.add(onelv1);

                    StringBuilder errorMessage = new StringBuilder();
                    HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogErrorList, errorMessage);

                } catch (Exception e1)
                {
                    HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e1.toString() + " 订单号orderNO:" + orderNo);
                }
                // endregion
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
            }


        }


        ub1.addUpdateValue("OPERATIONTYPE", new DataValue("0", Types.VARCHAR));
        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));
        this.doExecuteDataToDB();
        HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改】修改成功，单号OrderNO=" + orderNo);

        // 订单修改完成后 同步【加工任务档]信息
        this.updateProcessTaskInfo(req);

        String memo_machShop_redis = "";
        if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
        {
            if (isUpdateCanModify)
            {
                HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改成功后】配送补录订单变成正式订单，判断下异店生产订单是否需要写缓存，单号OrderNO=" + orderNo);
                try
                {
                    //查询下订单信息，写缓存
                    com.dsc.spos.waimai.entity.order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao,eId,loadDocType,orderNo);
                    if (dcpOrder!=null)
                    {
                        //订单生产门店不为空，且不等于下单门店
                       if (dcpOrder.getMachShopNo()!=null&&!dcpOrder.getMachShopNo().trim().isEmpty()&&!dcpOrder.getMachShopNo().equals(dcpOrder.getShopNo()))
                       {
                           //且不是总部生产
                           if ("Y".equals(dcpOrder.getIsShipCompany()))
                           {
                               HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改成功后】配送补录订单变成正式订单，生产门店="+dcpOrder.getMachShopNo()+",是总部生产，无需写缓存,单号OrderNO=" + orderNo);
                           }
                           else
                           {
                               ParseJson pj = new ParseJson();
                               String Response_json = pj.beanToJson(dcpOrder);
                               RedisPosPub redis = new RedisPosPub();
                               String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + dcpOrder.getMachShopNo();
                               String hash_key = orderNo;
                               HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改成功后】配送补录订单变成正式订单，生产门店="+dcpOrder.getMachShopNo()+",写缓存开始,单号OrderNO=" + orderNo);
                               boolean nRet_setRedis = redis.setHashMap(redis_key, hash_key, Response_json);
                               if (nRet_setRedis)
                               {
                                   memo_machShop_redis = "生产门店("+dcpOrder.getMachShopNo()+")写缓存成功";
                                   HelpTools.writelog_waimai(
                                           "【调用DCP_OrderModify接口，订单信息修改成功后】【写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                               } else
                               {
                                   memo_machShop_redis = "生产门店("+dcpOrder.getMachShopNo()+")写缓存失败";
                                   HelpTools.writelog_waimai(
                                           "调用DCP_OrderModify接口，订单信息修改成功后】【写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                               }
                           }

                       }
                       else
                       {
                           HelpTools.writelog_waimai("【调用DCP_OrderModify接口，订单信息修改成功后】配送补录订单变成正式订单，生产门店="+dcpOrder.getMachShopNo()+",下单门店="+dcpOrder.getShopNo()+",生产门店为空或者生产门店与下单门店一致，无需写缓存,单号OrderNO=" + orderNo);
                       }
                    }
                }
                catch (Exception e)
                {

                }


            }

        }

        // region 写下日志
        try
        {

            orderStatusLog onelv1 = new orderStatusLog();

            onelv1.setLoadDocType(loadDocType);
            onelv1.setChannelId(channelId);

            onelv1.setNeed_callback("N");
            onelv1.setNeed_notify("N");

            onelv1.seteId(eId);

            String opNO = req.getRequest().getOpId() == null ? "" : req.getRequest().getOpId();
            if (opNO.isEmpty())
            {
                opNO = req.getOpNO()==null?"":req.getOpNO();
            }

            String o_opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();
            if (o_opName.isEmpty())
            {
                o_opName = req.getOpName()==null?"":req.getOpName();
            }

            onelv1.setOpNo(opNO);
            onelv1.setOpName(o_opName);
            onelv1.setOrderNo(orderNo);
            onelv1.setLoadDocBillType(loadDocBillType);
            onelv1.setLoadDocOrderNo(loadDocOrderNo);

            String statusType_log = "99";// 其他状态
            String updateStaus_log = "99";// 订单修改

            onelv1.setStatusType(statusType_log);
            onelv1.setStatus(updateStaus_log);
            String statusName_log = "订单修改";
            String statusTypeName_log = "其他状态";
            onelv1.setStatusTypeName(statusTypeName_log);
            onelv1.setStatusName(statusName_log);

            StringBuffer memo = new StringBuffer("");
            memo.append( statusTypeName_log + "-->" + statusName_log + "<br>");
            memo.append( logmemo.toString());
            memo.append(memo_machShop_redis);
            onelv1.setMemo(memo.toString());
            String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            onelv1.setUpdate_time(updateDatetime);
            orderStatusLogList.add(onelv1);

            StringBuilder errorMessage = new StringBuilder();
            boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
            if (nRet)
            {
                HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
            } else
            {
                HelpTools.writelog_waimai(
                        "【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
            }
            this.pData.clear();

        } catch (Exception e)
        {
            HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
        }
        // endregion

        //【ID1030282】【大万3.0】车销业务场景下系统改造评估----绩效算到下单门店 by jinzma 20221229
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderModifyReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderModifyReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderModifyReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderModifyReq req) throws Exception
    {
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

        if(Check.Null(req.getRequest().getLoadDocType())){
			/*errMsg.append("订单渠道类型不能为空值 ");
			isFail = true;*/

        }



        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }


    /**
     * 判断下，是否已上传ERP
     * @param eId
     * @param orderNo
     * @return
     * @throws Exception
     */
    private boolean isUploadErp(String eId ,String orderNo) throws Exception
    {
        String sqlSourceno = "select PROCESS_STATUS from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
        try
        {
            List<Map<String, Object>> data_Source = this.doQueryData(sqlSourceno, null);

            if (data_Source!=null && data_Source.size()>0)
            {
                if (data_Source.get(0).get("PROCESS_STATUS")!=null && data_Source.get(0).get("PROCESS_STATUS").toString().equals("Y"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (Exception e)
        {

        }
        return false;
    }

    private String getRequestERPJson(DCP_OrderModifyReq req,String eId ,String orderNo) throws Exception
    {
        // 1、提取数据
        List<Wrapper<HolidayOrderCreateRequest.RequestBean>> orders = fetchTop20HolidayOrders(eId,orderNo);
        if(orders==null||orders.isEmpty())
        {
            return "";
        }

        HolidayOrderCreateRequest.RequestBean request_order = orders.get(0).getObj();
        //修改的变更
        request_order.setOperation_type("1");//修改


        if (req.getRequest().getShipType() != null && req.getRequest().getShipType().trim().length() > 0)
        {
            request_order.setShiptype(req.getRequest().getShipType());
        }

        if (req.getRequest().getShippingShopNo() != null)
        {
            request_order.setShippingshop(req.getRequest().getShippingShopNo());
        }

        if (req.getRequest().getMachShopNo() != null)
        {
            if (!req.getRequest().getMachShopNo().equals(request_order.getProductionshop()))
            {
                //这里查询下修改得门店是不是，总部
                String sql_shop = "select * from dcp_org where eid='"+eId+"' and ORGANIZATIONNO='"+req.getRequest().getMachShopNo()+"' ";
                List<Map<String, Object>> getShopData = this.doQueryData(sql_shop, null);
                if(getShopData!=null&&getShopData.isEmpty()==false)
                {
                    String isShipcompany_req = "N";
                    String org_form = getShopData.get(0).getOrDefault("ORG_FORM", "").toString();
                    if(org_form.equals("0"))
                    {
                        isShipcompany_req = "Y";
                    }
                    String type = isShipcompany_req.equals("Y") ? "2" :"1";//1.门店生产2.总部生产
                    request_order.setType(type);
                }
            }
            //先判断上面，再赋值下面
            request_order.setProductionshop(req.getRequest().getMachShopNo());
        }

        if (req.getRequest().getShipDate() != null)
        {
            String SHIPDATE = req.getRequest().getShipDate();
            //这里判断一下需求日期是否为空
            if (SHIPDATE != null && !SHIPDATE.isEmpty()) {
                SHIPDATE = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SHIPDATE));
            }
            request_order.setAgreee_date(SHIPDATE);

        }

        if (req.getRequest().getShipStartTime() != null)
        {
            String shipStartTime = req.getRequest().getShipStartTime();
            request_order.setShipStartTime(shipStartTime);
        }

        if (req.getRequest().getShipEndTime() != null)
        {
            String shipEndTime = req.getRequest().getShipEndTime();
            request_order.setShipEndTime(shipEndTime);

        }

        if (req.getRequest().getAddress() != null)
        {
            String address = req.getRequest().getAddress();
            request_order.setAddress(address);
        }

        if (req.getRequest().getGetMan() != null)
        {
            request_order.setGetman(req.getRequest().getGetMan());
        }

        if (req.getRequest().getGetManTel() != null)
        {
            request_order.setGetmantel(req.getRequest().getGetManTel());

        }

        if (req.getRequest().getMemo() != null)
        {
            request_order.setMemo(req.getRequest().getMemo());
        }

        if (req.getRequest().getProMemo() != null)
        {
            request_order.setProMemo(req.getRequest().getProMemo());
        }

        if (req.getRequest().getDelMemo() != null)
        {
            request_order.setDelMemo(req.getRequest().getDelMemo());
        }

        if(req.getRequest().getContMan()!=null)
        {
            request_order.setInvuser(req.getRequest().getContMan());
        }
        if(req.getRequest().getContTel()!=null)
        {
            request_order.setPhone(req.getRequest().getContTel());
        }
        if (req.getRequest().getDeliveryType() != null)
        {
            request_order.setDeliveryType(req.getRequest().getDeliveryType());

        }
        if(req.getRequest().getProvince()!=null)
        {
            request_order.setProvince(req.getRequest().getProvince());
        }
        if(req.getRequest().getCity()!=null)
        {
            request_order.setCity(req.getRequest().getCity());
        }
        if(req.getRequest().getCounty()!=null)
        {
            request_order.setCounty(req.getRequest().getCounty());
        }
        if(req.getRequest().getStreet()!=null)
        {
            request_order.setStreet(req.getRequest().getStreet());
        }
        if(req.getRequest().getDeliveryBusinessType()!=null)
        {
            request_order.setDeliveryBusinessType(req.getRequest().getDeliveryBusinessType());
        }
        if(req.getRequest().getIsUrgentOrder()!=null)
        {
            request_order.setIsUrgentOrder(req.getRequest().getIsUrgentOrder());
        }
        if(req.getRequest().getLongitude()!=null&&!req.getRequest().getLongitude().isEmpty())
        {
            request_order.setLongitude(req.getRequest().getLongitude());
        }
        if(req.getRequest().getLatitude()!=null&&!req.getRequest().getLatitude().isEmpty())
        {
            request_order.setLatitude(req.getRequest().getLatitude());
        }

        // 自定义payload中的json结构
        JSONObject payload = new JSONObject();
        JSONObject std_data = new JSONObject();
        JSONObject parameter = new JSONObject();
        JSONArray request = new JSONArray();

        ParseJson pj = new ParseJson();
        String req_str= pj.beanToJson(request_order);
        JSONObject header = new JSONObject(req_str);
        request.put(header);

        parameter.put("request", request);
        std_data.put("parameter", parameter);
        payload.put("std_data", std_data);
        String str = payload.toString();// 将json对象转换为字符串

        return str;
    }

    private List<Wrapper<HolidayOrderCreateRequest.RequestBean>> fetchTop20HolidayOrders(String eId,String orderNo) throws Exception
    {
        List<Wrapper<HolidayOrderCreateRequest.RequestBean>> orderList = new ArrayList<>();
        List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> detailList = new ArrayList<>();
        List<Wrapper<HolidayOrderCreateRequest.MessagesBean>> messageList = new ArrayList<>();
        List<Wrapper<HolidayOrderCreateRequest.AgioInfo>> agioList = new ArrayList<>();

        // 单头
        //String sql = " select * from (select A.*,row_number()over(order by create_Datetime desc) rn from DCP_ORDER A where  A.process_status='N' )where rn<=20 ";

        //增加订单上传ERP的白名单管控  BY JZMA 20201203
        String sql = " select * from  DCP_ORDER  where eid='"+eId+"' and orderno='"+orderNo+"' ";
        String[] conditionValues100_01 = {};
        List<Map<String, Object>> data = this.doQueryData(sql, conditionValues100_01);
        for (Map<String, Object> map : data)
        {
            HolidayOrderCreateRequest.RequestBean b = ConvertUtils.mapToBean(map, HolidayOrderCreateRequest.RequestBean.class);
            String isIntention = map.getOrDefault("ISINTENTION","N").toString();
            if (!"Y".equals(isIntention))
            {
                b.setIsIntention("N");
            }
            b.setVersion("3.0");
            b.setOperation_type("0");
            b.setOrder_type(map.get("BILLTYPE").toString());
            b.setFront_no(map.get("ORDERNO").toString());
            b.setCustomer_no(map.get("SELLNO").toString());
            b.setAtmvirtualaccount(map.get("VIRTUALACCOUNTCODE").toString());
            b.setShippingshop(map.get("SHIPPINGSHOP").toString());
            b.setShippingshopname(map.get("SHIPPINGSHOPNAME").toString());
            b.setProductionshop(map.get("MACHSHOP").toString());
            b.setPickupway("");
            b.setBuyerguino(map.get("BUYERGUINO").toString());
            b.setShiptype(map.get("SHIPTYPE").toString());
            b.setGetman(map.get("GETMAN").toString());
            b.setGetmantel(map.get("GETMANTEL").toString());
            b.setShiptime(ConvertUtils.toStr(map.get("SHIPTIME")));
            String SHIPTYPE = map.get("SHIPTYPE").toString();
            String ISSHIPCOMPANY = map.get("ISSHIPCOMPANY").toString();
            //POS总部生产填的ISSHIPCOMPANY 为Y
            b.setType(ISSHIPCOMPANY.equals("Y") ? "2" :"1");
            b.setSite_no(map.get("SHOP").toString());
            //是否是需求日期
            String SHIPDATE = map.get("SHIPDATE").toString();
            //这里判断一下需求日期是否为空
            if (SHIPDATE != null && !SHIPDATE.isEmpty()) {
                SHIPDATE = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SHIPDATE));
            }
            b.setAgreee_date(SHIPDATE);
            b.setSid(map.get("MANUALNO").toString());
            b.setInvuser(map.get("CONTMAN").toString());
            b.setPhone(map.get("CONTTEL").toString());
            b.setAddress(map.get("ADDRESS").toString());
            b.setCreator("");

            String CREATE_DATETIME = map.get("CREATE_DATETIME").toString();
            CREATE_DATETIME = CREATE_DATETIME.substring(0, 14);
            CREATE_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(CREATE_DATETIME));
            b.setCreate_datetime(CREATE_DATETIME);

            // todo 需要确认 CREATE_DATETIME => yyyy-MM-dd
			/* String SDATE = map.get("SDATE").toString();
		            SDATE = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SDATE));
		            b.setOrder_date(SDATE);*/
            // 日期格式yyyy-MM-dd   截取订单创建时间create_datetime字段里的日期  BY JZMA 20201123
            Date day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CREATE_DATETIME);
            String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(day);
            b.setOrder_date(orderDate);

            b.setModify_no("");
            b.setModify_datetime("");
            b.setApprove_no("");
            b.setApprove_datetime("");
            b.setMemo(map.get("MEMO").toString());
            String ISORGORDER = map.get("ISORGORDER").toString();
            if (ISORGORDER == null || ISORGORDER.isEmpty()) {
                ISORGORDER = "N";
            }
            b.setIsholidayorder(ISORGORDER);
            b.setRequest_detail(new ArrayList<>());
            //超商取货
            String subtype_name = "";
            String DeliveryType = map.get("DELIVERYTYPE").toString();
            if (SHIPTYPE.equals("6")) {
                if (DeliveryType.equals("16")) {
                    subtype_name = "綠界7-11";
                }
                if (DeliveryType.equals("17")) {
                    subtype_name = "綠界全家";
                }
                if (DeliveryType.equals("18")) {
                    subtype_name = "綠界萊爾富";
                }
            }
            b.setSubtype_name(subtype_name);
            b.setInvoiceType(map.get("INVOPERATETYPE").toString());
            b.setInvNo(map.get("INVNO").toString());
            //【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
            b.setTot_amt_merReceive(map.get("TOT_AMT_MERRECEIVE").toString());
            b.setTot_amt_custPayReal(map.get("TOT_AMT_CUSTPAYREAL").toString());
            b.setTot_disc_merReceive(map.get("TOT_DISC_MERRECEIVE").toString());
            b.setTot_disc_custPayReal(map.get("TOT_DISC_CUSTPAYREAL").toString());
            b.setDepartNo(map.getOrDefault("DEPARTNO", "").toString());

            //发票试算单头数据
            String sql_INVOICEPRE="select * from DCP_INVOICEPRE where SALETYPE='Order' and SALENO='"+map.get("ORDERNO").toString()+"' ";
            List<Map<String, Object>> data_INVOICEPRE = this.doQueryData(sql_INVOICEPRE, null);
            if (data_INVOICEPRE!=null && data_INVOICEPRE.size()>0)
            {
                b.setTaxAbleUamt(data_INVOICEPRE.get(0).get("TAXABLEUAMT").toString());
                b.setZeroTaxAmt(data_INVOICEPRE.get(0).get("ZEROTAXAMT").toString());
                b.setFreeTaxAmt(data_INVOICEPRE.get(0).get("FREETAXAMT").toString());
                b.setTaxAbleAmt(data_INVOICEPRE.get(0).get("TAXABLEAMT").toString());
                b.setTaxAbleTax(data_INVOICEPRE.get(0).get("TAXABLETAX").toString());
                b.setUntaxPayAmt(data_INVOICEPRE.get(0).get("UNTAXPAYAMT").toString());
                b.setUntaxPayTax(data_INVOICEPRE.get(0).get("UNTAXPAYTAX").toString());
                b.setTaxRate(data_INVOICEPRE.get(0).get("TAXRATE").toString());
                b.setInvTotAmt(data_INVOICEPRE.get(0).get("INVTOTAMT").toString());
                b.setInvAmt(data_INVOICEPRE.get(0).get("INVAMT").toString());
                b.setInvUamt(data_INVOICEPRE.get(0).get("INVUAMT").toString());
                b.setInvTax(data_INVOICEPRE.get(0).get("INVTAX").toString());
                b.setGftInvAmt(data_INVOICEPRE.get(0).get("GFTINVAMT").toString());
                b.setGftInvTax(data_INVOICEPRE.get(0).get("GFTINVTAX").toString());
                b.setAccAmt(data_INVOICEPRE.get(0).get("ACCUMAMT").toString());
                b.setAccTax(data_INVOICEPRE.get(0).get("ACCUMTAX").toString());
                b.setExtraCpAmt(data_INVOICEPRE.get(0).get("EXTRACPAMT").toString());
                b.setExtraCpTax(data_INVOICEPRE.get(0).get("EXTRACPTAX").toString());
            }

            //【ID1013581】订单配送地址增加省市区，优先处理，今天能把webservice改好，这样ERP才能改，否正周一达不到切换条件 BY JZMA 20201207
            b.setProvince(map.get("PROVINCE").toString());
            b.setCity(map.get("CITY").toString());
            b.setCounty(map.get("COUNTY").toString());
            b.setStreet(map.get("STREET").toString());
            b.setStatus(map.get("STATUS").toString());
            b.setPayStatus(map.get("PAYSTATUS").toString());


            Wrapper<HolidayOrderCreateRequest.RequestBean> w = new Wrapper<HolidayOrderCreateRequest.RequestBean>(
                    map.get("EID").toString(), map.get("ORDERNO").toString(),  b);
            orderList.add(w);
        }

        if (!orderList.isEmpty())
        {
            String inSql = "(" + orderList.stream().map(Wrapper::getInKey).collect(Collectors.joining(",")) + ")";
            // 单身
            sql = "select a.* from DCP_ORDER_DETAIL a where a.QTY>=0 and (a.EID, a.ORDERNO) in " + inSql;
            data = this.doQueryData(sql, new String[]{});
            data.forEach(mapdetail->
            {
                HolidayOrderCreateRequest.RequestDetailBean d = ConvertUtils.mapToBean(mapdetail, HolidayOrderCreateRequest.RequestDetailBean.class);
                d.setSeq(mapdetail.get("ITEM").toString());
                d.setItem_no(mapdetail.get("PLUNO").toString());
                d.setsUnit(mapdetail.get("SUNIT").toString());
                d.setQty(mapdetail.get("QTY").toString());
                d.setOriprice(mapdetail.get("OLDPRICE").toString());
                d.setQrcode_key(ConvertUtils.toStr(mapdetail.get("SOURCECODE_DETAIL")));

                String PICKQTY=mapdetail.get("PICKQTY").toString();
                String SHOPQTY=mapdetail.get("SHOPQTY").toString();
                if (PICKQTY == null || PICKQTY.isEmpty()) {
                    PICKQTY = "0";
                }
                if (SHOPQTY == null || SHOPQTY.isEmpty()) {
                    SHOPQTY = "0";
                }
                double shopqtytemp=Double.parseDouble(PICKQTY)+Double.parseDouble(SHOPQTY);
                d.setShopqty(PosPub.GetdoubleScale(shopqtytemp,2)+"" );
                //这里可以计算一下实际价格
                double AMT=Double.parseDouble(mapdetail.get("AMT").toString());
                double QTY=Double.parseDouble(mapdetail.get("QTY").toString());
                d.setPrice(PosPub.GetdoubleScale(AMT/QTY, 2)+"");
                d.setAmount(mapdetail.get("AMT").toString());
                d.setInvItem(mapdetail.get("INVITEM").toString());
                d.setInvSplitType(mapdetail.get("INVSPLITTYPE").toString());
                //【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
                d.setDisc_merReceive(mapdetail.get("DISC_MERRECEIVE").toString());
                d.setAmt_merReceive(mapdetail.get("AMT_MERRECEIVE").toString());
                d.setDisc_custPayReal(mapdetail.get("DISC_CUSTPAYREAL").toString());
                d.setAmt_custPayReal(mapdetail.get("AMT_CUSTPAYREAL").toString());


                //处理==绑定变量SQL的写法
                List<DataValue> lstDV=new ArrayList<>();
                DataValue dv=null;

                //发票试算商品
                String sql_INVOICEPRE_GOODS="select * from DCP_INVOICEPRE_GOODS where SALETYPE='Order' and SALENO=? and ITEM=? ";

                //?问号参数赋值处理
                dv=new DataValue(mapdetail.get("ORDERNO").toString(),Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(mapdetail.get("ITEM").toString(),Types.INTEGER);
                lstDV.add(dv);

                List<Map<String, Object>> data_INVOICEPRE_GOODS=null;
                try
                {
                    data_INVOICEPRE_GOODS = this.executeQuerySQL_BindSQL(sql_INVOICEPRE_GOODS, lstDV);
                }
                catch (Exception e)
                {
                    //log("订单新建/修改HolidayShoporderCreate_V3发票试算商品查询异常:" +e.getMessage());
                }
                if (data_INVOICEPRE_GOODS!=null && data_INVOICEPRE_GOODS.size()>0)
                {
                    d.setGftInvAmt(data_INVOICEPRE_GOODS.get(0).get("GFTINVAMT").toString());
                    d.setGftInvTax(data_INVOICEPRE_GOODS.get(0).get("GFTINVTAX").toString());
                    d.setTaxAbleUamt(data_INVOICEPRE_GOODS.get(0).get("TAXABLEUAMT").toString());
                    d.setZeroTaxAmt(data_INVOICEPRE_GOODS.get(0).get("ZEROTAXAMT").toString());
                    d.setFreeTaxAmt(data_INVOICEPRE_GOODS.get(0).get("FREETAXAMT").toString());
                    d.setTaxAbleAmt(data_INVOICEPRE_GOODS.get(0).get("TAXABLEAMT").toString());
                    d.setOldAmt(data_INVOICEPRE_GOODS.get(0).get("AMT").toString());
                    d.setUntaxPayAmt(data_INVOICEPRE_GOODS.get(0).get("UNTAXPAYAMT").toString());
                    d.setUntaxPayTax(data_INVOICEPRE_GOODS.get(0).get("UNTAXPAYTAX").toString());
                    d.setInvTotAmt(data_INVOICEPRE_GOODS.get(0).get("INVTOTAMT").toString());
                    d.setInvAmt(data_INVOICEPRE_GOODS.get(0).get("INVAMT").toString());
                    d.setInvUamt(data_INVOICEPRE_GOODS.get(0).get("INVUAMT").toString());
                    d.setInvTax(data_INVOICEPRE_GOODS.get(0).get("INVTAX").toString());
                    d.setAccAmt(data_INVOICEPRE_GOODS.get(0).get("ACCUMAMT").toString());
                    d.setAccTax(data_INVOICEPRE_GOODS.get(0).get("ACCUMTAX").toString());
                    d.setExtraCpAmt(data_INVOICEPRE_GOODS.get(0).get("EXTRACPAMT").toString());
                    d.setExtraCpTax(data_INVOICEPRE_GOODS.get(0).get("EXTRACPTAX").toString());
                }


                Wrapper<HolidayOrderCreateRequest.RequestDetailBean> wrapper = new Wrapper<HolidayOrderCreateRequest.RequestDetailBean>(mapdetail.get("EID").toString(),
                        mapdetail.get("ORDERNO").toString(), mapdetail.get("ITEM").toString(), d);
                detailList.add(wrapper);
            });

            // 消息
            sql = "select a.* from DCP_ORDER_DETAIL_MEMO a where (a.EID, a.ORDERNO) in " + inSql;
            data = this.doQueryData(sql, new String[]{});
            data.forEach(map->{
                HolidayOrderCreateRequest.MessagesBean b = ConvertUtils.mapToBean(map, HolidayOrderCreateRequest.MessagesBean.class);
                b.setMsgtype(map.get("MEMOTYPE").toString());
                b.setMsgname(map.get("MEMONAME").toString());
                b.setMessage(map.get("MEMO").toString());
                Wrapper<HolidayOrderCreateRequest.MessagesBean> wrapper = new Wrapper<HolidayOrderCreateRequest.MessagesBean>(map.get("EID").toString(),
                        map.get("ORDERNO").toString(),  map.get("OITEM").toString(), b);
                messageList.add(wrapper);
            });

            // 优惠
            sql = "select a.* from DCP_ORDER_DETAIL_AGIO a where (a.EID, a.ORDERNO) in " + inSql;
            data = this.doQueryData(sql, new String[]{});
            data.forEach(m->{
                HolidayOrderCreateRequest.AgioInfo b = ConvertUtils.mapToBean(m, HolidayOrderCreateRequest.AgioInfo.class);
                b.setItem(m.get("ITEM").toString());
                b.setQty(m.get("QTY").toString());
                b.setAmt(m.get("AMT").toString());
                b.setInputDisc(m.get("INPUTDISC").toString());
                b.setRealDisc(m.get("REALDISC").toString());
                b.setDisc(m.get("DISC").toString());
                b.setDcType(m.get("DCTYPE").toString());
                b.setDcTypeName(m.get("DCTYPENAME").toString());
                b.setPmtNo(m.get("PMTNO").toString());
                b.setBsNo(m.get("BSNO").toString());
                //【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
                b.setDisc_merReceive(m.get("DISC_MERRECEIVE").toString());
                b.setDisc_custPayReal(m.get("DISC_CUSTPAYREAL").toString());
                Wrapper<HolidayOrderCreateRequest.AgioInfo> wrapper = new Wrapper<HolidayOrderCreateRequest.AgioInfo>(m.get("EID").toString(),
                        m.get("ORDERNO").toString(),  m.get("MITEM").toString(), b);
                agioList.add(wrapper);
            });

            // 组装数据
            Map<String, Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> deailMap = detailList.stream().collect(Collectors.toMap(Wrapper::getItemKey, v -> v));
            Map<String, List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>>> deailMap2 = detailList.stream().collect(Collectors.groupingBy(Wrapper::getOrderKey));
            orderList.forEach(it->{
                List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> w = deailMap2.get(it.getOrderKey());
                if(w != null){
                    it.getObj().setRequest_detail(w.stream().map(Wrapper::getObj).collect(Collectors.toList()));
                }
            });

            // 循环message
            messageList.forEach(it->{
                Wrapper<HolidayOrderCreateRequest.RequestDetailBean> w = deailMap.get(it.getItemKey());
                if(w != null){
                    w.getObj().getMessages().add(it.getObj());
                }
            });

            agioList.forEach(it->{
                Wrapper<HolidayOrderCreateRequest.RequestDetailBean> w = deailMap.get(it.getItemKey());
                if(w != null){
                    w.getObj().getAgio_info().add(it.getObj());
                }
            });
        }

        return orderList;
    }


    @Override
    protected TypeToken<DCP_OrderModifyReq> getRequestType()
    {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderModifyReq>(){};
    }

    @Override
    protected DCP_OrderModifyRes getResponseType()
    {
        // TODO Auto-generated method stub
        return new DCP_OrderModifyRes();
    }

    /**
     * 订单修改完成后，同步加工任务档 信息 修改 ISMODIFYSHIP=N
     * @param req
     */
    private void updateProcessTaskInfo(DCP_OrderModifyReq req){
        try {
            DCP_OrderModifyReq.levelRequest request = req.getRequest();
            String eId = request.geteId();
            String orderNo = request.getOrderNo();
            String address = request.getAddress();    // 配送地址
            String memo = request.getMemo();          // 订单备注
            String shipDate = request.getShipDate();  // 配送日期
            String getMan = request.getGetMan();      // 取货人
            String getManTel = request.getGetManTel();// 取货人电话
            String shipStartTime = request.getShipStartTime(); // 配送开始时间
            String shipEndTime = request.getShipEndTime();     // 配送截止时间

            String modifyBy = req.getRequest().getOpId();
            if (modifyBy==null||modifyBy.isEmpty())
            {
                modifyBy = req.getOpNO();
            }
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());

            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append(" SELECT * FROM DCP_PROCESSTASK " +
                    " WHERE EID = '"+eId+"' and OFNO = '"+orderNo+"'");
            String sql = sqlbuf.toString();
            List<Map<String, Object>> getProd = this.doQueryData(sql, null);

            if(!CollectionUtils.isEmpty(getProd)){
                // 如果查询有值 则同步加工任务档信息 修改 ISMODIFYSHIP = N

                UptBean ub1 = null;
                ub1 = new UptBean("DCP_PROCESSTASK");
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("ADDRESS", new DataValue(address, Types.VARCHAR));
                ub1.addUpdateValue("SHIPDATE", new DataValue(shipDate, Types.VARCHAR));
                ub1.addUpdateValue("SHIPSTARTTIME", new DataValue(shipStartTime, Types.VARCHAR));
                ub1.addUpdateValue("SHIPENDTIME", new DataValue(shipEndTime, Types.VARCHAR));
                ub1.addUpdateValue("GETMAN", new DataValue(getMan, Types.VARCHAR));
                ub1.addUpdateValue("GETMANTEL", new DataValue(getManTel, Types.VARCHAR));
                ub1.addUpdateValue("ISMODIFYSHIP", new DataValue("N", Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
                HelpTools.writelog_waimai("【订单修改成功,更新加工任务档 DCP_ProcessTask 成功】" + " 订单号orderNO:" + orderNo);
            }

        } catch (Exception e) {

        }
    }
}



