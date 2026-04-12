package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderRefundAgreeOrRejectReq;
import com.dsc.spos.json.cust.req.DCP_StockUnlock_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderRefundAgreeOrRejectRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMDYOrderProcess;
import com.dsc.spos.waimai.WMELMOrderProcess;
import com.dsc.spos.waimai.WMJBPOrderProcess;
import com.dsc.spos.waimai.WMMTOrderProcess;
import com.dsc.spos.waimai.WMSGOrderProcess;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.convert.Convert;

public class DCP_OrderRefundAgreeOrReject extends SPosAdvanceService<DCP_OrderRefundAgreeOrRejectReq, DCP_OrderRefundAgreeOrRejectRes>
{

    @Override
    protected void processDUID(DCP_OrderRefundAgreeOrRejectReq req, DCP_OrderRefundAgreeOrRejectRes res) throws Exception
    {
        // TODO Auto-generated method stub
        /*************必传的节点******************/
        String eId_para = req.getRequest().geteId();//请求传入的eId
        //操作类型 1:同意 2：拒绝
        String opType = req.getRequest().getOpType();
        //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
        String refundType = req.getRequest().getRefundType();
        String orderNo = req.getRequest().getOrderNo();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();
        String refundBdate = req.getRequest().getRefundBdate();
        String refundDatetime = req.getRequest().getRefundDatetime();
        if(refundDatetime==null||refundDatetime.isEmpty())
        {
            refundDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        String tot_amt = req.getRequest().getTot_amt();// req.getRequest().getTot_amt();
        //已提货商品退单处理  0：退钱且退货,1：退钱不退货
        //0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单） 1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
        String pickGoodsRefundType = req.getRequest().getPickGoodsRefundType();
        String refundReason = req.getRequest().getRefundReason();
        String refundReasonNo = req.getRequest().getRefundReasonNo();
        String refundReasonName = req.getRequest().getRefundReasonName();
        String invOperateType = req.getRequest().getInvOperateType();
        /*************非必传的节点******************/
        String shopId_para = req.getRequest().getShopId();//操作门店ID
        String opNo = req.getRequest().getOpNo();
        String opName = req.getRequest().getOpName();

        if(opType.equals("1")||opType.equals("2"))
        {

        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型opType="+opType+"暂不支持");
        }

        //检查下单据可不可以退
        StringBuffer errorInfo = new StringBuffer();




        String sql = "select * from dcp_order where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】单号orderNo="+orderNo+"  查询语句："+sql);
        List<Map<String, Object>> getQHead = this.doQueryData(sql, null);
        if(getQHead==null||getQHead.isEmpty())
        {
            errorInfo.append("该订单不存在！");
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询完成,单号orderNo="+orderNo+"该订单不存在！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        String status = getQHead.get(0).get("STATUS").toString();
        String refundStatus = getQHead.get(0).get("REFUNDSTATUS").toString();
        if (tot_amt==null||tot_amt.trim().isEmpty()){
        	tot_amt = getQHead.get(0).get("TOT_AMT").toString();
        }
        	
        String payAmt = getQHead.get(0).get("PAYAMT").toString();
        loadDocType = getQHead.get(0).get("LOADDOCTYPE").toString();
        channelId = getQHead.get(0).get("CHANNELID").toString();
        String shopId_db = getQHead.get(0).get("SHOP").toString();
        String loadDocBillType = getQHead.get(0).get("LOADDOCBILLTYPE").toString();
        String loadDocOrderNo = getQHead.get(0).get("LOADDOCORDERNO").toString();
        String shippingShopNo = getQHead.get(0).get("SHIPPINGSHOP").toString();
        String headOrderNo = getQHead.get(0).get("HEADORDERNO")==null?"":getQHead.get(0).get("HEADORDERNO").toString();
        String INVOPERATETYPE = "";
        String app_poi_code = getQHead.get(0).getOrDefault("ORDERSHOP", "").toString();
        String isRefundDeliverAmt=req.getRequest().getIsRefundDeliverAmt();
        Map<String,Object> recalculateMap=new HashMap<String,Object>();
        {
        	DCP_OrderRefund orderRefund = new DCP_OrderRefund();
        	recalculateMap=orderRefund.doRecalculateReq(getQHead.get(0), eId_para, orderNo, refundType, pickGoodsRefundType,req.getRequest().getTot_amt(),isRefundDeliverAmt, req.getRequest().getGoods());
        	if(recalculateMap!=null){
        		String newrefundType=recalculateMap.get("refundType")==null?"":recalculateMap.get("refundType").toString();
        		if(newrefundType.length()>0){
        			req.getRequest().setRefundType(newrefundType);
        			refundType=newrefundType;
        		}
        	}
        }
        
        try
        {
            INVOPERATETYPE = getQHead.get(0).get("INVOPERATETYPE").toString();//发票记录类型：0-开立 1-作废 2-折让

        } catch (Exception e)
        {
            // TODO: handle exception
        }
        //抖音外卖同意退单用到。
        String after_sale_id = getQHead.get(0).getOrDefault("AFTER_SALE_ID","").toString();
        boolean isCallPosInvoiceRefund = false;//是否调用POS发票作废/折让接口

        ParseJson pj = new ParseJson();
        String redis_shop = "";//操作缓存的门店
        if(shopId_para!=null&&shopId_para.isEmpty())
        {
            redis_shop = shopId_para;//传入的操作门店不为空，就取操作门店
        }
        else
        {
            redis_shop = shippingShopNo;//为空取配送门店
        }

        //new个对象，赋值必须的，后续用于更新缓存
        order dcpOrder = new order();
        dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
        dcpOrder.setPay(new ArrayList<orderPay>());
        dcpOrder.setOrderNo(orderNo);
        dcpOrder.setLoadDocType(loadDocType);
        dcpOrder.setChannelId(channelId);
        dcpOrder.setLoadDocBillType(loadDocBillType);
        dcpOrder.setLoadDocOrderNo(loadDocOrderNo);
        dcpOrder.setStatus(status);
        dcpOrder.setRefundStatus(refundStatus);
        dcpOrder.setShopNo(shopId_db);
        dcpOrder.setShippingShopNo(shippingShopNo);

        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询完成,单号orderNo="+orderNo+" 状态status="+status);
        if(status.equals("3"))
        {
            errorInfo.append("该订单状态为已取消！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        if(status.equals("12"))
        {
            errorInfo.append("订单状态为已退单！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
        }
        
        
        
        
        //小程序订单退单申请审核逻辑  
        if(orderLoadDocType.MINI.equals(loadDocType)){
        	if (isHasPartOrderToSale(eId_para,orderNo))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
            }
        	
        	
        	boolean isContinue=false;
        	String statusType = "3";//退单状态
        	String updateStaus = "";
        	String memo="";
        	String des="";
        	
        	String crmStatusType="4";//4= 退单状态变更  调用OrderStatusUpdate时的传参
        	String crmStatus="";//4= 退单状态变更  调用OrderStatusUpdate时的传参
        	String crmDescription="";//4= 退单状态变更  调用OrderStatusUpdate时的传参
        	
        	
        	//REFUNDSTATUS退单状态: 1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功,7.用户申请部分退款 8.拒绝部分退款 9.部分退款失败 10.部分退款成功
            String refundstatus=getQHead.get(0).get("REFUNDSTATUS")==null?"":getQHead.get(0).get("REFUNDSTATUS").toString();
            //STATUS 单据状态: 0.待审核1.订单开立 2.已接单 3. 已拒单 8.待提货 9.待发货 10.已发货 11.已完成 12.整单已退单
            //0：退钱且退货  & refundstatus=21(顾客小程序申请退单) & status不等于1(1-订单开立状态直接退款，不需要走下面部分)
            
            if("1".equals(opType)){
            	if("0".equals(pickGoodsRefundType)&&"21".equals(refundstatus)&&!"1".equals(status)){
            		//同意
            		UptBean up1 = new UptBean("DCP_ORDER");
            		//退货且退款  + 退单状态为21时，更新 退单状态为 22
            		up1.addUpdateValue("REFUNDSTATUS", new DataValue("22", Types.VARCHAR));
            		up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
            		up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
            		this.addProcessData(new DataProcessBean(up1));
            		
            		des="退货申请审核通过,等待顾客上传物流单号!";
            		memo="退货申请审核通过,等待顾客上传物流单号等";
            		updateStaus = "2";//用户申请退单
            		crmStatus="31"; //31.同意退单
            		crmDescription="同意退单申请";
            		isContinue=true;
            		
            		//全退
            		if(Check.Null(refundType)||"0".equals(refundType)){
            			ExecBean ex1=new ExecBean("UPDATE CRM_ORDERGOODS SET AMOUNT_RETURN=AMOUNT,QTY_RETURN=QUANTITY "
                				+ " WHERE EID='"+eId_para+"' AND BILLNO='"+orderNo+"'");
            			this.addProcessData(new DataProcessBean(ex1));
            		}
            		//部分退
            		else if("1".equals(refundType)){
            			List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods=req.getRequest().getGoods();
                    	if(goods!=null&&goods.size()>0){
                    		for(DCP_OrderRefundAgreeOrRejectReq.levelGoods good:goods){
                    			//更新CRM_ORDERGOODS
                    			UptBean ub1 = new UptBean("CRM_ORDERGOODS");
                    			ub1.addUpdateValue("QTY_RETURN", new DataValue(good.getQty(), Types.VARCHAR));
                    			ub1.addUpdateValue("AMOUNT_RETURN", new DataValue(good.getAmt(), Types.VARCHAR));
                    			ub1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
                    			ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
                    			ub1.addCondition("SERIALNO", new DataValue(good.getItem(), Types.VARCHAR));
                    			this.addProcessData(new DataProcessBean(ub1));
                    		}
                    	}
            		}
            		
            		UptBean ub1 = new UptBean("CRM_ORDER");
        			ub1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
        			ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
        			if(req.getRequest().getTot_amt()!=null&&req.getRequest().getTot_amt().trim().length()>0){
        				ub1.addUpdateValue("AMOUNT_RETURN", new DataValue(req.getRequest().getTot_amt(), Types.VARCHAR));
        			}
        			//退单类型 0退货退款,1仅退款
        			ub1.addUpdateValue("PICKGOODSREFUNDTYPE", new DataValue(pickGoodsRefundType, Types.VARCHAR));
        			//退货范围：0整单退1部分退
        			ub1.addUpdateValue("REFUNDTYPE", new DataValue(refundType, Types.VARCHAR));
        			ub1.addUpdateValue("ISREFUNDDELIVERAMT", new DataValue(isRefundDeliverAmt, Types.VARCHAR));
        			
        			this.addProcessData(new DataProcessBean(ub1));
        			
        			ub1 = new UptBean("DCP_ORDER");
        			ub1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
        			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
        			if(req.getRequest().getTot_amt()!=null&&req.getRequest().getTot_amt().trim().length()>0){
        				ub1.addUpdateValue("AMOUNT_RETURN", new DataValue(req.getRequest().getTot_amt(), Types.VARCHAR));
        			}
        			//退单类型 0退货退款,1仅退款
        			ub1.addUpdateValue("PICKGOODSREFUNDTYPE", new DataValue(pickGoodsRefundType, Types.VARCHAR));
        			//退货范围：0整单退1部分退
        			ub1.addUpdateValue("REFUNDTYPE", new DataValue(refundType, Types.VARCHAR));
        			ub1.addUpdateValue("ISREFUNDDELIVERAMT", new DataValue(isRefundDeliverAmt, Types.VARCHAR));
        			this.addProcessData(new DataProcessBean(ub1));
        			
            	}
            	
            	//是否允许退货退款分开returnAndRefund
            	String returnAndRefund=PosPub.getPARA_SMS(this.dao, eId_para, null, "returnAndRefund");
            	//是否允许退货退款分开returnAndRefund=0 CRM发起退单申请，不需要退货审核，流程上保留原有逻辑；
            	if("0".equals(returnAndRefund)){
            		this.pData.clear();
            		isContinue=false;
            	}
            	
            }
            //拒绝
            else if("2".equals(opType)){
        		memo="拒绝退货申请";
        		updateStaus = "3";//已拒绝
            	des="已拒绝退单申请!";
            	crmStatus="3"; //3.拒绝退单
            	crmDescription=refundReason; //拒绝退单原因
            	isContinue=true;
        	}
        	
        	
        	
            if(isContinue){
            	
            	//调用OrderStatusUpdate接口，同步状态给CRM 并写CRM订单历程
            	{

            		try
                    {
                        //不做任何操作 ，只是通知微商城做流传记录
                        String updateTime_wechat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        JSONObject objReq = new JSONObject();
                        objReq.put("orderNo", orderNo);
                        objReq.put("statusType", crmStatusType);
                        objReq.put("status", crmStatus);
                        objReq.put("pickGoodsRefundType", pickGoodsRefundType);
                        objReq.put("refundType", refundType);
                        objReq.put("description", crmDescription);
                        objReq.put("oprId", "");
                        objReq.put("terminalId", "");
                        objReq.put("orgType", "2");
                        objReq.put("orgId", "");
                        objReq.put("orgId", updateTime_wechat);
                        objReq.put("opType", opType);

                        String request = objReq.toString();
                        String microMarkServiceName = "OrderStatusUpdate";

                        //接口都默认返回ture

                        String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);

                    }
                    catch (Exception e)
                    {

                    }
            	}
            	
            	//写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId_para);
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopId_db);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表dcp_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }

            	this.doExecuteDataToDB();
            	this.pData.clear();
            	res.setSuccess(true);
                res.setServiceDescription(des);
                res.setServiceStatus("000");
                return;
            }
        }
        

        //1-同意
        if(opType.equals("1"))
        {
            List<Map<String, Object>> getData_Order_detail=new ArrayList<>();
            boolean isWechatOrder = false;//是否手机商城渠道类型订单
            //全退
            if(refundType.equals("0"))
            {
                if(pickGoodsRefundType==null||pickGoodsRefundType.isEmpty())
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点pickGoodsRefundType不可为空！");
                }


                if(INVOPERATETYPE!=null&&INVOPERATETYPE.equals("0"))
                {
                    //查询下参数AreaType
                    String AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId_para, "", "AreaType");
                    if(AreaType!=null&&AreaType.equals("TW")==true)
                    {
                        boolean checkInvoiceRefund = false;
                        if (invOperateType==null||invOperateType.trim().isEmpty())
                        {
                            errorInfo.append("退单发票操作类型invOperateType不能为空，");
                            checkInvoiceRefund = true;
                        }
                        if (refundReasonNo==null||refundReasonNo.trim().isEmpty())
                        {
                            errorInfo.append("退单理由码refundReasonNo不能为空，");
                            checkInvoiceRefund = true;
                        }
                        if (refundReasonName==null||refundReasonName.trim().isEmpty())
                        {
                            errorInfo.append("退单理由码名称refundReasonName不能为空，");
                            checkInvoiceRefund = true;
                        }
                        if (refundReason==null||refundReason.trim().isEmpty())
                        {
                            errorInfo.append("退单原因描述refundReason不能为空，");
                            checkInvoiceRefund = true;
                        }

                        if(checkInvoiceRefund)
                        {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorInfo.toString());
                        }

                        isCallPosInvoiceRefund = true;

                    }
                }




                UptBean up1 = new UptBean("DCP_ORDER");
                up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

                //更新updatetime
                up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                up1.addUpdateValue("LASTREFUNDTIME", new DataValue(refundDatetime, Types.VARCHAR));

                boolean otherChannelRes = false;//退款调用相应渠道接口，成功标识
                StringBuilder otherChannelError = new StringBuilder("");


                if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))//商城的需要调用退款
                {
                    isWechatOrder = true;
                    if (isHasPartOrderToSale(eId_para,orderNo))
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
                    }
                    
                    try
                    {

                        String refundReasonCode_wechat = "4";//退单原因  不传默认0-未知、1-未付款自主取消、2-超时未支付、3-商家拒单、4-用户申请退单
                        String refundReasonDesc = refundReason;//退单原因描述
                        if(refundReasonDesc==null||refundReasonDesc.trim().isEmpty())
                        {
                            refundReasonDesc = "";
                        }
                        JSONObject objReq = new JSONObject();
                        objReq.put("orderNo", orderNo);
                        objReq.put("refund", 1);
                        objReq.put("refundReason", refundReasonCode_wechat);
                        objReq.put("refundReasonDesc", refundReasonDesc);
                        objReq.put("tot_amt", tot_amt);
                        objReq.put("isRefundDeliverAmt", isRefundDeliverAmt);

                        String request = objReq.toString();
                        String microMarkServiceName = "OrderRefund";

                        String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);
                        JSONObject json = new JSONObject(result);
                        try
                        {

                            String success = json.get("success").toString();
                            String serviceDescription = json.get("serviceDescription").toString();
                            if(success.equals("true")|| serviceDescription.equals("訂單異常或已退款")||serviceDescription.equals("订单异常或已退款"))
                            {
                                otherChannelRes = true;
                            }
                            else
                            {
                                otherChannelRes = false;
                                otherChannelError.append(serviceDescription);
                            }
                        }
                        catch (Exception e)
                        {
                            otherChannelRes = false;
                            otherChannelError.append(e.getMessage());
                        }
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }
                }
                else if (loadDocType.equals(orderLoadDocType.ELEME)) //饿了么订单
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db,
                                                                                loadDocType,app_poi_code);
                    if (map != null)
                    {
                        String elmAPPKey = map.get("APPKEY").toString();
                        String elmAPPSecret = map.get("APPSECRET").toString();
                        String elmAPPName = map.get("APPNAME").toString();
                        String elmIsTest = map.get("ISTEST").toString();
                        boolean elmIsSandbox = false;
                        if (elmIsTest != null && elmIsTest.equals("Y"))
                        {
                            elmIsSandbox = true;
                        }
                        String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                        String userId = map.getOrDefault("USERID","").toString();
                        if ("Y".equals(isJBP))
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(userId,elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }
                        else
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }

                    }
                    else
                    {
                        otherChannelRes = WMELMOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }



                }
                else if (loadDocType.equals(orderLoadDocType.MEITUAN)) //美团订单
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    String isJbp = map.get("ISJBP").toString();

                    if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                    {
                        otherChannelRes = WMJBPOrderProcess.orderRefundAgree(eId_para, shopId_db, orderNo, "", otherChannelError);
                    }
                    else
                    {
                        otherChannelRes = WMMTOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }
                }
                else if (loadDocType.equals(orderLoadDocType.MTSG))
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    otherChannelRes = WMSGOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                }
                else if (loadDocType.equals(orderLoadDocType.DYWM))
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                    }

                    String clientKey = map.get("APPKEY").toString();
                    String clientSecret = map.get("APPSECRET").toString();
                    //String elmAPPName = map.get("APPNAME").toString();
                    String isTest = map.get("ISTEST").toString();
                    boolean isSandbox = false;
                    if (isTest != null && isTest.equals("Y"))
                    {
                        isSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    //String after_sale_id = "";//退单ID
                    otherChannelRes = WMDYOrderProcess.orderRefundAgree(isSandbox,clientKey,clientSecret,orderNo,after_sale_id,"","",otherChannelError);

                }
                else if (loadDocType.equals(orderLoadDocType.JDDJ)) //京东到家订单
                {
                    if (opNo == null || opNo.trim().isEmpty())
                    {
                        opNo = "pos";
                    }
                    otherChannelRes = HelpJDDJHttpUtil.orderCancelOperate(orderNo, true, opNo,"同意退单", otherChannelError);
                }
                else if (loadDocType.equals(orderLoadDocType.POS)) //winpos门店订单
                {
                    otherChannelRes = true;
                }
                else if (loadDocType.equals(orderLoadDocType.YOUZAN)) //有赞订单
                {
                    YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
                    try{
                        Map<String, Object> otherMap = new HashMap<String, Object>();
                        JsonBasicRes thisRes=new JsonBasicRes();
                        thisRes=ycb.refundAgree(eId_para, orderNo, shopId_db,otherMap,null);
                        if(!thisRes.isSuccess()){
                            otherChannelRes = false;
                            otherChannelError.append(thisRes.getServiceDescription());
                        }
                        else
                        {
                            otherChannelRes = true;
                        }
                    }catch (Exception e) {
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }

                }
                else if(loadDocType.equals(orderLoadDocType.WAIMAI))
                {
                    String ReturnReviewMode = PosPub.getPARA_SMS(this.dao,eId_para,"","ReturnReviewMode");// 0：仅门店POS退   1：仅订单中台退  2：门店POS和订单中台都可以退货
                    if (ReturnReviewMode==null||ReturnReviewMode.isEmpty())
                    {
                        ReturnReviewMode = "0";//默认0
                    }
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，对应参数小程序外卖单退单审核模式ReturnReviewMode="+ReturnReviewMode+"(0：仅门店POS退，1：仅订单中台退，2：门店POS和订单中台都可以退货),订单号orderNo="+orderNo);
                    String Yc_Key = "";
                    String Yc_Sign_Key = "";
                    String Yc_Url = "";
                    String crmPayUrl = "";
                    if (req.getApiUser()==null||Check.Null(req.getApiUser().getUserCode())||Check.Null(req.getApiUser().getUserKey()))
                    {
                        //HelpTools.writelog_waimai("该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号),订单号orderNo="+orderNo);
                        //HelpTools.writelog_fileName("该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号),订单号orderNo="+orderNo, "ScanPayAddLog");
                        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"必须使用接口账号来调用(网页端没有接口账号)！"+otherChannelError.toString());
                        if ("0".equals(ReturnReviewMode))
                        {
                            HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退,订单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退,订单号orderNo="+orderNo, "ScanPayAddLog");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=0)设置仅门店POS退！"+otherChannelError.toString());
                        }
                        //获取下接口账号
                        String sql_channel = "select A.APPID,A.APPNO, A.APPTYPE,B.USERCODE,B.USERKEY from "
                                + " crm_channel a inner join crm_apiuser b on a.eid=b.eid and a.channelid=b.channelid and a.appno=b.apptype"
                                + " where a.eid='"+eId_para+"' and a.channelid='"+channelId+"' and a.appno='"+loadDocType+"'";
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+",在中台退单，获取渠道ID="+channelId+",对应的接口账号查询sql:"+sql_channel+",订单号orderNo="+orderNo);
                        List<Map<String, Object>> getQDataChannel=this.doQueryData(sql_channel, null);
                        if (getQDataChannel==null||getQDataChannel.isEmpty())
                        {
                            HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，在中台退单，获取渠道ID="+channelId+",对应的接口账号查询为空,订单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，在中台退单，获取渠道ID="+channelId+",对应的接口账号查询为空,订单号orderNo="+orderNo, "ScanPayAddLog");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，在中台退单，获取渠道编码("+channelId+")对应的接口账号为空！"+otherChannelError.toString());
                        }
                        Yc_Key = getQDataChannel.get(0).get("USERCODE").toString();
                        Yc_Sign_Key = getQDataChannel.get(0).get("USERKEY").toString();

                    }
                    else
                    {
                        if ("1".equals(ReturnReviewMode))
                        {
                            HelpTools.writelog_waimai("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退,订单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退,订单号orderNo="+orderNo, "ScanPayAddLog");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道类型"+loadDocType+"，退单审核参数(ReturnReviewMode=1)设置仅网页中台退！"+otherChannelError.toString());
                        }
                        Yc_Key = req.getApiUser().getUserCode();
                        Yc_Sign_Key = req.getApiUser().getUserKey();
                    }
                    Yc_Url = PosPub.getCRM_INNER_URL(eId_para);
                    crmPayUrl = PosPub.getPAY_INNER_URL(eId_para);
                    if (Yc_Url==null||Yc_Url.trim().isEmpty()||crmPayUrl==null||crmPayUrl.trim().isEmpty()) {
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"退单CrmUrl、Mobile_Url移动支付接口参数未设置,单号orderNo="+orderNo);
                        HelpTools.writelog_fileName("该渠道类型"+loadDocType+"退单CrmUrl、Mobile_Url移动支付接口参数未设置,单号orderNo="+orderNo, "ScanPayAddLog");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "Mobile_Url移动支付接口参数未设置!");
                    }

                    //查询下付款
                    String sql_pay = " select * from DCP_ORDER_PAY_DETAIL where SOURCEBILLTYPE='Order' and EID='"+eId_para+"' and LOADDOCTYPE='"+loadDocType+"' and SOURCEBILLNO='"+orderNo+"' ";
                    HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】sql语句:"+sql_pay+",单号orderNo="+orderNo);
                    List<Map<String, Object>> getPaylist = this.doQueryData(sql_pay, null);
                    if(getPaylist==null||getPaylist.isEmpty())
                    {
                        otherChannelRes = true;
                        errorInfo.append("该订单没有需要退款的支付方式！");
                        HelpTools.writelog_waimai("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！");
                        HelpTools.writelog_fileName("该渠道类型"+loadDocType+"订单，【查询付款明细】完成,单号orderNo="+orderNo+",没有付款明细数据！", "ScanPayAddLog");
                        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorInfo.toString());
                    }
                    else
                    {
                        //尾款处理,这个只是记录付款
                        com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
                        //这里才会扣款
                        com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
                        //券列表
                        com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();

                        String partnerMember = "";//req.getRequest().getPartnerMember();
                        // 用与企迈会员退款
                        String cardno = getQHead.get(0).get("CARDNO").toString();
                        String memberPayNo_origin = "";//原退款单号

                        boolean isMobilePay = false;//移动支付退款
                        String refundPayReqStr = "";
                        for (Map<String, Object> payMap : getPaylist)
                        {
                            boolean isMemberPay = false;
                            String pay = payMap.getOrDefault("PAY","0").toString();
                            String changed = payMap.getOrDefault("CHANGED","0").toString();
                            String extra = payMap.getOrDefault("EXTRA","0").toString();
                            BigDecimal p_pay=new BigDecimal(pay);
                            BigDecimal p_changed=new BigDecimal(changed);
                            BigDecimal p_extra=new BigDecimal(extra);

                            //pay-changed-extra累加起来
                            BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);
                            p_realpay=p_realpay.setScale(2, RoundingMode.HALF_UP);

                            //券面额
                            BigDecimal faceAmt=p_pay;//.add(p_extra);
                            faceAmt=faceAmt.setScale(2,RoundingMode.HALF_UP);

                            String funcNo = payMap.getOrDefault("FUNCNO","").toString();
                            String payCode = payMap.getOrDefault("PAYCODE","").toString();
                            String payName = payMap.getOrDefault("PAYNAME","").toString();
                            String cardNo = payMap.getOrDefault("CARDNO","").toString();
                            String descore_str = payMap.getOrDefault("DESCORE","0").toString();
                            double descore = new BigDecimal(descore_str).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

                            String couponQty_str = payMap.getOrDefault("COUPONQTY","0").toString();
                            double couponQty = new BigDecimal(couponQty_str).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                            String paysernum = payMap.getOrDefault("PAYSERNUM","").toString();

                            //****会员卡扣款****
                            if (funcNo.equals("301"))
                            {
                                if (!paysernum.isEmpty())
                                {
                                    memberPayNo_origin = paysernum;
                                }
                                isMemberPay = true;
                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",payCode);//收款方式代号
                                tempPay.put("payName",payName);//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",cardNo);//卡号
                                tempPay.put("isCardPay",1);//
                                payslistArray.add(tempPay);

                                //
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",cardNo);
                                tempCard.put("amount",p_realpay);//0只处理积分
                                tempCard.put("getPoint","0");//0只处理积分
                                cardlistArray.add(tempCard);
                                continue;
                            }
                            else if (funcNo.equals("302"))//积分扣减
                            {
                                if (!paysernum.isEmpty())
                                {
                                    memberPayNo_origin = paysernum;
                                }
                                isMemberPay = true;
                                if("qimai".equals(partnerMember)){
                                    com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                    tempPay.put("payType",payCode);//收款方式代号
                                    tempPay.put("payName",payName);//收款方式名称
                                    tempPay.put("payAmount",p_realpay);//付款金额
                                    tempPay.put("noCode",cardNo);//卡号
                                    tempPay.put("isCardPay",0);//
                                    payslistArray.add(tempPay);
                                }

                                //
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",cardNo);
                                tempCard.put("usePoint",descore);//积分扣减
                                tempCard.put("amount","0");//0只处理积分
                                tempCard.put("getPoint","0");//0只处理积分
                                cardlistArray.add(tempCard);
                                continue;
                            }
                            else if (funcNo.equals("304") || funcNo.equals("305")|| funcNo.equals("307"))//现金券/折扣券
                            {
                                if (!paysernum.isEmpty())
                                {
                                    memberPayNo_origin = paysernum;
                                }
                                isMemberPay = true;
                                if("qimai".equals(partnerMember)){
                                    com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                    tempPay.put("payType",payCode);//收款方式代号
                                    tempPay.put("payName",payName);//收款方式名称
                                    tempPay.put("payAmount",p_realpay);//付款金额
                                    tempPay.put("noCode",cardNo);//卡号
                                    tempPay.put("isCardPay",0);//
                                    payslistArray.add(tempPay);

                                    com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                    tempCard.put("cardNo",cardno);
                                    tempCard.put("amount","0");//0
                                    tempCard.put("usePoint","0");//积分扣减
                                    tempCard.put("getPoint","0");//
                                    cardlistArray.add(tempCard);
                                }

                                //
                                com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCoupon.put("couponCode",cardNo);//券号
                                tempCoupon.put("couponType","");//券类型
                                tempCoupon.put("quantity",couponQty);//使用张数
                                tempCoupon.put("faceAmount",faceAmt);//总面额
                                tempCoupon.put("buyAmount",p_pay);//抵账金额
                                couponlistArray.add(tempCoupon);
                                continue;
                            }

                            //上面的请求一定要加continue，或者判断标记，因为有历史数据，会员劵支付、积分抵现payType也是#P1
                            if (!isMemberPay)
                            {
                                String payType = payMap.getOrDefault("PAYTYPE","").toString();
                                String order_id = payMap.getOrDefault("REFNO","").toString();
                                String trade_no = payMap.getOrDefault("PAYSERNUM","").toString();
                                double pay_amt = new BigDecimal(pay).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

                                if ("#P1".equals(payType)||"#P2".equals(payType))
                                {
                                    String shop_code = shopId_db;
                                    String pos_code = "999";
                                    String ret_order_id = PosPub.getGUID(false);//退款单号
                                    isMobilePay = true;

                                    com.alibaba.fastjson.JSONObject RefundPayReq = new com.alibaba.fastjson.JSONObject();
                                    RefundPayReq.put("serviceId", "Refund");

                                    com.alibaba.fastjson.JSONObject payReq = new com.alibaba.fastjson.JSONObject();
                                    payReq.put("pay_type", payType);
                                    payReq.put("shop_code", shop_code);
                                    payReq.put("pos_code", pos_code);
                                    payReq.put("order_id", order_id);
                                    payReq.put("trade_no", trade_no);
                                    payReq.put("ret_order_id", ret_order_id);
                                    payReq.put("pay_amt", pay_amt);
                                    payReq.put("return_amount", pay_amt);

                                    RefundPayReq.put("request", payReq);

                                    String reqStr = payReq.toString();
                                    String sign = PosPub.encodeMD5(reqStr + Yc_Sign_Key);

                                    com.alibaba.fastjson.JSONObject signJson = new com.alibaba.fastjson.JSONObject();
                                    signJson.put("sign", sign);
                                    signJson.put("key", Yc_Key);

                                    RefundPayReq.put("sign", signJson);
                                    refundPayReqStr = RefundPayReq.toString();
                                    HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，【组装】请求req：" + refundPayReqStr
                                                                      + ",单号orderNo="+orderNo+",原支付单号order_id="+order_id);
                                    HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，【组装】请求req：" + refundPayReqStr
                                                                        + ",单号orderNo="+orderNo+",原支付单号order_id="+order_id, "ScanPayAddLog");
                                }
                            }

                        }

                        //先退移动支付
                        if (isMobilePay&&!Check.Null(refundPayReqStr))
                        {

                            String payResStr = HttpSend.Sendcom(refundPayReqStr, crmPayUrl);
                            HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，返回res:" + payResStr + ",单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，返回res:" + payResStr + ",单号orderNo="+orderNo, "ScanPayAddLog");

                            com.alibaba.fastjson.JSONObject payResJson = new com.alibaba.fastjson.JSONObject();
                            payResJson = JSON.parseObject(payResStr);//String转json
                            String paySuccess = payResJson.getOrDefault("success","").toString(); // TRUE 或 FALSE
                            //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                            String payServiceStatus = payResJson.getOrDefault("serviceStatus","").toString();
                            String payServiceDescription = payResJson.getOrDefault("serviceDescription","").toString();
                            if (!"true".equalsIgnoreCase(paySuccess))
                            {
                                HelpTools.writelog_fileName("存在移动支付,退款调用Refund接口，返回失败"+payServiceDescription+",单号orderNo="+orderNo, "ScanPayAddLog");
                                HelpTools.writelog_waimai("存在移动支付,退款调用Refund接口，返回失败:"+payServiceDescription+",单号orderNo="+orderNo);
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付退款失败:"+payServiceDescription);
                            }
                            otherChannelRes = true;
                        }

                        //成功之后，退会员支付相关
                        if (!Check.Null(memberPayNo_origin) || cardlistArray.size()>0 || couponlistArray.size()>0)
                        {
                            com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                            com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
                            reqheader.put("orderNo", memberPayNo_origin);//需唯一
                            reqheader.put("refundOrderNo", "RE"+orderNo);//新的退款单号
                            reqheader.put("orderAmount", tot_amt);//
                            reqheader.put("pointAmount", tot_amt);//
                            //digiwin  鼎捷    qimai企迈   空为鼎捷
                            reqheader.put("partnerMember", partnerMember);
                            // 0.退订单 1.退销售单                企迈场景使用   固定为0
                            reqheader.put("refundType", "0");

                            reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道

                            //2021-06-03 消费单CRM_CONSUME 表记录 消费机构不对。
                            // 3.0 退订时退款原路返回， 消费门店也是原来收款门店，  所以这里 orgId 不能写 操作门店，跟不是下订门店和配送门店， 而是实际消费门店（可能是下订门店付款，也可能是配送门店付款）
                            //reqheader.put("orgId", shippingShopNo);//

                            if(!Check.Null(shopId_db)){
                                reqheader.put("orgId", shopId_db);//
                            }
                            else{
                                reqheader.put("orgId", shopId_db);// shippingShopNo 配送机构， 作为默认值用，其实这么写是不对的，
                            }

                            reqheader.put("oprId", opNo);//
                            reqheader.put("goodsdetail", goodslistArray);
                            reqheader.put("cards", cardlistArray);
                            reqheader.put("coupons", couponlistArray);
                            reqheader.put("payDetail", payslistArray);

                            //
                            String req_sign=reqheader.toString() + Yc_Sign_Key;

                            req_sign= DigestUtils.md5Hex(req_sign);

                            //
                            signheader.put("key", Yc_Key);//
                            signheader.put("sign", req_sign);//md5

                            payReq.put("serviceId", "MemberPayRefund");

                            payReq.put("request", reqheader);
                            payReq.put("sign", signheader);


                            String str = payReq.toString();

                            HelpTools.writelog_waimai("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo,"ScanPayAddLog");
                            String	resbody = "";

                            //编码处理
                            str= URLEncoder.encode(str,"UTF-8");

                            resbody=HttpSend.Sendcom(str, Yc_Url);

                            HelpTools.writelog_waimai("会员退款接口MemberPayRefund返回："+resbody +",单号orderNo="+orderNo);
                            HelpTools.writelog_fileName("会员退款接口MemberPayRefund返回："+resbody +",单号orderNo="+orderNo,"ScanPayAddLog");
                            //获得积分
                            BigDecimal getPoint=new BigDecimal(0);

                            if (resbody.equals("")==false)
                            {
                                com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);

                                String serviceDescription=jsonres.get("serviceDescription").toString();
                                String serviceStatus=jsonres.get("serviceStatus").toString();

                                //单号重复,直接查询积分
                                if (serviceStatus.equals("900"))
                                {
                                    reqheader.clear();
                                    signheader.clear();
                                    payReq.clear();

                                    reqheader.put("refundOrderNo", "RE"+orderNo);
                                    req_sign=reqheader.toString() + Yc_Sign_Key;
                                    req_sign=DigestUtils.md5Hex(req_sign);

                                    //
                                    signheader.put("key", Yc_Key);//
                                    signheader.put("sign", req_sign);//md5

                                    payReq.put("serviceId", "MemberPayRefundQuery");

                                    payReq.put("request", reqheader);
                                    payReq.put("sign", signheader);

                                    str = payReq.toString();

                                    HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery请求内容："+str+",单号orderNo="+orderNo);

                                    //编码处理
                                    str=URLEncoder.encode(str,"UTF-8");

                                    resbody=HttpSend.Sendcom(str, Yc_Url);

                                    HelpTools.writelog_waimai("会员退款接口MemberPayRefundQuery返回："+resbody+",单号orderNo="+orderNo);

                                    if (resbody.equals("")==false)
                                    {
                                        jsonres = JSON.parseObject(resbody);

                                        serviceDescription=jsonres.get("serviceDescription").toString();
                                        serviceStatus=jsonres.get("serviceStatus").toString();
                                        if (jsonres.get("success").toString().equals("true"))
                                        {
                                            otherChannelRes = true;
                                            //res.getDatas().getVipDatas().setCardsInfo(new ArrayList<DCP_OrderRefundRes.Card>());
                                            com.alibaba.fastjson.JSONObject datas=jsonres.getJSONObject("datas");
                                            String JrefundStatus=datas.getString("refundStatus");//0:未退款 1:已转入退款
                                            if (JrefundStatus.equals("0"))
                                            {
                                                otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:退款状态refundStatus=0");
                                                otherChannelRes=false;
                                            }
                                            //res.getDatas().setBillNo("RE"+orderNo);
                                        }
                                        else
                                        {
                                            otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:" +serviceDescription );
                                            otherChannelRes=false;
                                        }
                                    }
                                    else
                                    {
                                        otherChannelError.append("调用会员退款查询接口MemberPayRefundQuery失败:");
                                        otherChannelRes=false;
                                    }
                                }
                                else
                                {
                                    if (jsonres.get("success").toString().equals("true"))
                                    {
                                        otherChannelRes = true;
                                        /*res.getDatas().getVipDatas().setCardsInfo(new ArrayList<DCP_OrderRefundRes.Card>());

                                        com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                        for (int pi = 0; pi < cardsList.size(); pi++)
                                        {
                                            //多张卡累加积分
                                            getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));

                                            DCP_OrderRefundRes.Card card=res.new Card();
                                            card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
                                            card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
                                            card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
                                            card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
                                            card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
                                            card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
                                            card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
                                            card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
                                            card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
                                            card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
                                            card.setGetPoint(getPoint.toPlainString());
                                            card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
                                            card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));

                                            res.getDatas().getVipDatas().getCardsInfo().add(card);

                                            //
                                            res.getDatas().getVipDatas().setAmount(card.getAmount_after());
                                            res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
                                            res.getDatas().getVipDatas().setMemberId("");
                                            res.getDatas().getVipDatas().setPoints(card.getPoint_after());

                                            res.getDatas().setBillNo("RE"+orderNo);
                                        }*/
                                    }
                                    else
                                    {
                                        otherChannelError.append("调用会员退款查询接口MemberPayRefund失败:" + serviceDescription);
                                        otherChannelRes= false;
                                    }
                                }
                            }
                            else
                            {
                                otherChannelError.append("调用会员退款接口MemberPayRefund失败:");
                                otherChannelRes= false;
                            }

                        }



                    }
                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道:"+loadDocType+",退单类型="+refundType+"暂不支持");
                }

                if(!otherChannelRes)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
                }

                //饿了么美团只需要调用接口，即可，后续平台会推送相应得消息
                if (loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
                {
                    res.setSuccess(otherChannelRes);
                    res.setServiceDescription(otherChannelError.toString());
                    res.setServiceStatus("000");
                    return;
                }

                //有赞同意成功之后，还会直接调用我们DCP_OrderRefund接口，所以不需要更新什么状态，删除下缓存吧
                if (loadDocType.equals(orderLoadDocType.YOUZAN))
                {
                    res.setSuccess(otherChannelRes);
                    res.setServiceDescription(otherChannelError.toString());
                    res.setServiceStatus("000");

                    try
                    {
                        String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopId_db;
                        String hash_key = orderNo;
                        RedisPosPub redis = new RedisPosPub();
                        redis.DeleteHkey(redis_key, hash_key);
                        HelpTools.writelog_waimai("同意退单删除缓存，订单号=" + orderNo + "，redis_key:" + redis_key
                                                          + "，hash_key:" + hash_key );
                    } catch (Exception e)
                    {

                    }
                    try
                    {
                        //写日志
                        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                        orderStatusLog onelv1 = new orderStatusLog();
                        onelv1.setLoadDocType(loadDocType);
                        onelv1.setChannelId(channelId);
                        onelv1.setLoadDocBillType("");
                        onelv1.setLoadDocOrderNo("");
                        onelv1.seteId(eId_para);

                        onelv1.setOpName(opName);
                        onelv1.setOpNo(opNo);
                        onelv1.setShopNo(shopId_db);
                        onelv1.setOrderNo(orderNo);
                        onelv1.setMachShopNo("");
                        onelv1.setShippingShopNo("");
                        String statusType = "99";
                        String updateStaus = "99";
                        onelv1.setStatusType(statusType);
                        onelv1.setStatus(updateStaus);
                        onelv1.setStatusTypeName("其他状态");
                        onelv1.setStatusName("同意退款");

                        String memo = "同意退款";
                        onelv1.setMemo(memo);
                        onelv1.setDisplay("1");

                        String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        onelv1.setUpdate_time(updateDatetime);

                        orderStatusLogList.add(onelv1);

                        StringBuilder errorStatusLogMessage = new StringBuilder();
                        boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                        if (nRet) {
                            HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                        }

                    }
                    catch (Exception e)
                    {

                    }
                    return;
                }


                //0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单）
                //1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
                if(pickGoodsRefundType.equals("0"))
                {
                	//opType=1同意    refundType=0全退   pickGoodsRefundType=0：退钱且退货
                    //先判断下有没有生成销售单
                    List<DataProcessBean> DPB_returnSale = new ArrayList<DataProcessBean>();//记录 销退单的sql
                    List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();//记录 退订单的sql
                    List<Map<String, Object>> saleNoList = this.getSaleNoByOrderNo(eId_para,headOrderNo, orderNo);
                    boolean isCreateSale = false;//是否订转销过
                    if(saleNoList!=null&&saleNoList.size()>0)
                    {
                        isCreateSale = true;
                        //如果存在的话，需要生成退单
                        StringBuffer error1 = new StringBuffer("");
                        
                        DPB_returnSale = this.getReturnSaleSql(eId_para, refundBdate, saleNoList, error1,null,tot_amt,isRefundDeliverAmt,recalculateMap,getQHead.get(0));
                        //订转销了也要生成促销参与明细
                        if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                        {
                            //来源业务类型：0-销售 1-退单 2-无单退货 3-订单 4-退订
                            StringBuffer  strBuff_execsql = new StringBuffer("");
                            strBuff_execsql.append(" insert into PROM_MEMBER (");
                            strBuff_execsql.append(" EID, ID, PROMNO, SDATE, SOURCEBILLTYPE, SOURBILLNO, MEMBERID, CUSTID, DIRECTION )");

                            strBuff_execsql.append(" select eid, sys_guid(), PROMNO, SDATE,'4', SOURBILLNO, MEMBERID, CUSTID, -1 ");

                            strBuff_execsql.append(" from PROM_MEMBER where  SOURCEBILLTYPE='3' and eid='"+eId_para+"' and SOURBILLNO='"+orderNo+"' ");
                            String  execsql = "";
                            execsql = strBuff_execsql.toString();
                            ExecBean exSale_prom_member = new ExecBean(execsql);
                            DPB_returnSale.add(new DataProcessBean(exSale_prom_member));
                        }
                    }
                    else//先搞整单吧，部分提货的干不下去了
                    {
                        DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                        //没有生成销售单，就生成退订单和库存解锁
                        DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",null,tot_amt,isRefundDeliverAmt,recalculateMap);

                        StringBuffer unLockStockError = new StringBuffer();
                        boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);
                    }


                    
                    //更新订单状态
                    up1.addUpdateValue("STATUS", new DataValue("12",Types.VARCHAR));
                    up1.addUpdateValue("REFUNDSTATUS", new DataValue("6",Types.VARCHAR));
                    up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt,Types.VARCHAR));
                    up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                    up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）

                    this.addProcessData(new DataProcessBean(up1));

                    //更新单身已退数量 （整单）
                    String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
                    ExecBean exSale = new ExecBean(execsql);
                    this.addProcessData(new DataProcessBean(exSale));


                    for (DataProcessBean bean : DPB_returnSale)
                    {
                        this.addProcessData(bean);
                    }

                    for (DataProcessBean bean : DPB_returnOrder)
                    {
                        this.addProcessData(bean);
                    }

                    this.doExecuteDataToDB();
                    res.setSuccess(true);
                    if(isWechatOrder&&isCreateSale)
                    {
                        HelpTools.wechatOrderRefundPoint(eId_para, orderNo,headOrderNo, opNo, opName);
                    }

                    String status_update = "12";
                    String refundStatus_update = "6";

                    dcpOrder.setStatus(status_update);
                    dcpOrder.setRefundStatus(refundStatus_update);

                    String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
                    String hash_key = orderNo;
                    String hash_value = pj.beanToJson(dcpOrder);
                    this.updateRedis(redis_key, hash_key, hash_value);

                    //写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);



                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "12";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("1");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet) {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();

                }
                else if(pickGoodsRefundType.equals("1"))
                {
                	//opType=1同意    refundType=0全退   pickGoodsRefundType=1：退钱不退货

                    List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();// 记录退订单的sql

                    // 没有生成销售单，就生成退订单和库存解锁
                    DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                    //没有生成销售单，就生成退订单和库存解锁
                    DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",null,tot_amt,isRefundDeliverAmt,recalculateMap);


                    StringBuffer unLockStockError = new StringBuffer();
                    boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);

                    // 更新订单状态
                    up1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
                    up1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
                    up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt, Types.VARCHAR));
                    up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
                    up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）

                    this.addProcessData(new DataProcessBean(up1));

                    // 更新单身已退数量
                    String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId_para
                            + "' and orderno='" + orderNo + "' ";
                    ExecBean exSale = new ExecBean(execsql);
                    this.addProcessData(new DataProcessBean(exSale));

                    for (DataProcessBean bean : DPB_returnOrder)
                    {
                        this.addProcessData(bean);
                    }

                    this.doExecuteDataToDB();
                    res.setSuccess(true);

                    String status_update = "12";
                    String refundStatus_update = "6";

                    dcpOrder.setStatus(status_update);
                    dcpOrder.setRefundStatus(refundStatus_update);

                    String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
                    String hash_key = orderNo;
                    String hash_value = pj.beanToJson(dcpOrder);
                    this.updateRedis(redis_key, hash_key, hash_value);
                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "12";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("1");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();

                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点类型="+pickGoodsRefundType+" 暂不支持！");
                }

            }
            //部分退
            else if (refundType.equals("1"))
            {
//                UptBean up1 = new UptBean("DCP_ORDER");
//                up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
//                up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
//
//                //更新updatetime
//                up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
//                up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
//                up1.addUpdateValue("LASTREFUNDTIME", new DataValue(refundDatetime, Types.VARCHAR));

                boolean otherChannelRes = false;//退款调用相应渠道接口，成功标识
                StringBuilder otherChannelError = new StringBuilder("");

                if (loadDocType.equals(orderLoadDocType.ELEME)) //饿了么订单
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db,
                                                                                loadDocType,app_poi_code);
                    if (map != null)
                    {
                        String elmAPPKey = map.get("APPKEY").toString();
                        String elmAPPSecret = map.get("APPSECRET").toString();
                        String elmAPPName = map.get("APPNAME").toString();
                        String elmIsTest = map.get("ISTEST").toString();
                        boolean elmIsSandbox = false;
                        if (elmIsTest != null && elmIsTest.equals("Y"))
                        {
                            elmIsSandbox = true;
                        }
                        String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                        String userId = map.getOrDefault("USERID","").toString();
                        if ("Y".equals(isJBP))
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(userId,elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }
                        else
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }
                    }
                    else
                    {
                        otherChannelRes = WMELMOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }

                }
                else if (loadDocType.equals(orderLoadDocType.MEITUAN)) //美团订单
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    String isJbp = map.get("ISJBP").toString();

                    if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                    {
                        otherChannelRes = WMJBPOrderProcess.orderRefundAgree(eId_para, shopId_db, orderNo, "", otherChannelError);
                    }
                    else
                    {
                        otherChannelRes = WMMTOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }
                }
                else if (loadDocType.equals(orderLoadDocType.MTSG))
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    otherChannelRes = WMSGOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                }
                else if (loadDocType.equals(orderLoadDocType.DYWM))
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                    }

                    String clientKey = map.get("APPKEY").toString();
                    String clientSecret = map.get("APPSECRET").toString();
                    //String elmAPPName = map.get("APPNAME").toString();
                    String isTest = map.get("ISTEST").toString();
                    boolean isSandbox = false;
                    if (isTest != null && isTest.equals("Y"))
                    {
                        isSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    //String after_sale_id = "";//退单ID
                    otherChannelRes = WMDYOrderProcess.orderRefundAgree(isSandbox,clientKey,clientSecret,orderNo,after_sale_id,"","",otherChannelError);

                }
                else if (loadDocType.equals(orderLoadDocType.JDDJ)) //京东到家订单
                {
                    if (opNo == null || opNo.trim().isEmpty())
                    {
                        opNo = "pos";
                    }
                    otherChannelRes = HelpJDDJHttpUtil.orderCancelOperate(orderNo, true, opNo,"同意退单", otherChannelError);
                }
                else if(orderLoadDocType.MINI.equals(loadDocType)){
                	//小程序部分退单
                    boolean isCreateSale = false;//是否订转销过
                	isWechatOrder = true;
                    if (isHasPartOrderToSale(eId_para,orderNo)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单("+orderNo+")存在部分提货，请全部提完货再退单！");
                    }
                    
                    //AMOUNT_RETURN	NUMBER(18,4)	Yes		228	本次退款金额
                    BigDecimal amountReturn= new BigDecimal(getQHead.get(0).get("AMOUNT_RETURN")==null?"0":getQHead.get(0).get("AMOUNT_RETURN").toString());
                    //1：退钱不退货
                    if("1".equals(pickGoodsRefundType)&&amountReturn.compareTo(BigDecimal.ZERO)<=0){
                    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单["+orderNo+"]退款金额异常！");
                    }
                    //0：退钱且退货
                    if(pickGoodsRefundType.equals("0")){
                    	if(req.getRequest().getGoods()==null||req.getRequest().getGoods().size()<1){
                    		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "部分退货退款时,商品信息必传!");
                    	}
                    }
                    
                    
                    try{
                        String refundReasonCode_wechat = "4";//退单原因  不传默认0-未知、1-未付款自主取消、2-超时未支付、3-商家拒单、4-用户申请退单
                        String refundReasonDesc = refundReason;//退单原因描述
                        if(refundReasonDesc==null||refundReasonDesc.trim().isEmpty()){
                            refundReasonDesc = "";
                        }
                        JSONObject objReq = new JSONObject();
                        objReq.put("orderNo", orderNo);
                        objReq.put("refund", 1);
                        objReq.put("refundReason", refundReasonCode_wechat);
                        objReq.put("refundReasonDesc", refundReasonDesc);
                        objReq.put("goods", req.getRequest().getGoods());
                        objReq.put("tot_amt", amountReturn.toPlainString());
                        objReq.put("isRefundDeliverAmt", isRefundDeliverAmt);
                        String request = objReq.toString();
                        String microMarkServiceName = "OrderRefund";
                        String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);
                        JSONObject json = new JSONObject(result);
                        try{
                            String success = json.get("success").toString();
                            String serviceDescription = json.get("serviceDescription").toString();
                            if(success.equalsIgnoreCase("true")|| serviceDescription.equals("訂單異常或已退款")||serviceDescription.equals("订单异常或已退款")){
                                otherChannelRes = true;
                            }
                            else{
                                otherChannelRes = false;
                                otherChannelError.append(serviceDescription);
                            }
                        }
                        catch (Exception e){
                            otherChannelRes = false;
                            otherChannelError.append(e.getMessage());
                        }
                    }
                    catch (Exception e){
                        // TODO: handle exception
                        otherChannelRes = false;
                        otherChannelError.append(e.getMessage());
                    }
                    
                    if(!otherChannelRes)
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
                    }
                	
                	//0：退钱且退货（未提货部分生成退订单，退款金额记退订的商品涉及的分摊金额；已提货部分生成销退单）
                    //1：退钱不退货（未提货部分生成退订单，退款金额记全部；已提货部分不用生成销退单）
                    if(pickGoodsRefundType.equals("0")){
                    	//opType=1同意    refundType=1部分退   pickGoodsRefundType=0：退钱且退货
                        //先判断下有没有生成销售单
                        List<DataProcessBean> DPB_returnSale = new ArrayList<DataProcessBean>();//记录 销退单的sql
                        List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();//记录 退订单的sql
                        List<Map<String, Object>> saleNoList = this.getSaleNoByOrderNo(eId_para,headOrderNo, orderNo);
                        isCreateSale = false;//是否订转销过
                        
                        if(saleNoList!=null&&saleNoList.size()>0){
                            isCreateSale = true;
                            //如果存在的话，需要生成退单
                            StringBuffer error1 = new StringBuffer("");
                            DPB_returnSale = this.getReturnSaleSql(eId_para, refundBdate, saleNoList, error1,req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap,getQHead.get(0));
                        }
                        else//
                        {
                            DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                            //没有生成销售单，就生成退订单和库存解锁
                            DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap);

                            StringBuffer unLockStockError = new StringBuffer();
                            boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);
                        }

                        //部分退钱且退货时，调用OrderRefund会更新单据状态。

//                        //更新订单状态
//                        up1.addUpdateValue("STATUS", new DataValue("12",Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDSTATUS", new DataValue("6",Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt,Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//                        up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//
//                        this.addProcessData(new DataProcessBean(up1));

                        //更新单身已退数量 （整单）
//                        String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
//                        ExecBean exSale = new ExecBean(execsql);
//                        this.addProcessData(new DataProcessBean(exSale));


                        for (DataProcessBean bean : DPB_returnSale)
                        {
                            this.addProcessData(bean);
                        }

                        for (DataProcessBean bean : DPB_returnOrder)
                        {
                            this.addProcessData(bean);
                        }

                        this.doExecuteDataToDB();
                        res.setSuccess(true);
                        if(isWechatOrder&&isCreateSale)
                        {
                            HelpTools.wechatOrderRefundPoint(eId_para, orderNo,headOrderNo, opNo, opName);
                        }

                        String status_update = "12";
                        String refundStatus_update = "6";

                        dcpOrder.setStatus(status_update);
                        dcpOrder.setRefundStatus(refundStatus_update);

//                        String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
//                        String hash_key = orderNo;
//                        String hash_value = pj.beanToJson(dcpOrder);
//                        this.updateRedis(redis_key, hash_key, hash_value);

                        //写日志
                        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                        orderStatusLog onelv1 = new orderStatusLog();
                        onelv1.setLoadDocType(loadDocType);
                        onelv1.setChannelId(channelId);
                        onelv1.setLoadDocBillType("");
                        onelv1.setLoadDocOrderNo("");
                        onelv1.seteId(eId_para);

                        onelv1.setOpName(opName);
                        onelv1.setOpNo(opNo);
                        onelv1.setShopNo(shopId_db);
                        onelv1.setOrderNo(orderNo);
                        onelv1.setMachShopNo("");
                        onelv1.setShippingShopNo("");
                        String statusType = "1";
                        String updateStaus = "12";
                        onelv1.setStatusType(statusType);
                        onelv1.setStatus(updateStaus);
                        StringBuilder statusTypeNameObj = new StringBuilder();
                        String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                        String statusTypeName = statusTypeNameObj.toString();
                        onelv1.setStatusTypeName(statusTypeName);
                        onelv1.setStatusName(statusName);

                        String memo = "";
                        memo += statusName;
                        onelv1.setMemo(memo);
                        onelv1.setDisplay("1");

                        String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        onelv1.setUpdate_time(updateDatetime);

                        orderStatusLogList.add(onelv1);

                        StringBuilder errorStatusLogMessage = new StringBuilder();
                        boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                        if (nRet) {
                            HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                        }
                        this.pData.clear();
                    	res.setSuccess(true);
                        res.setServiceDescription("执行成功!");
                        res.setServiceStatus("000");

                    }
                    // 1：仅退钱
                    else if(pickGoodsRefundType.equals("1")){
                    	//pickGoodsRefundType 0：退钱且退货 1：退钱不退货
                    	//opType=1同意    refundType=1部分退   pickGoodsRefundType=1：退钱不退货

                        List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();// 记录退订单的sql

                        // 没有生成销售单，就生成退订单和库存解锁
                        DCP_OrderRefund orderRefund = new DCP_OrderRefund();
                        //没有生成销售单，就生成退订单和库存解锁
                        DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId_para, refundBdate,loadDocType, orderNo,"","","","",req.getRequest().getGoods(),tot_amt,isRefundDeliverAmt,recalculateMap);


                        StringBuffer unLockStockError = new StringBuffer();
                        boolean unLockStockFlag = this.dcpStockUnlock(req, unLockStockError);

                        //部分退钱时，调用OrderRefund会更新单据状态。
                        
//                        // 更新订单状态
//                        up1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDAMT", new DataValue(payAmt, Types.VARCHAR));
//                        up1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//                        up1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(payAmt,Types.VARCHAR));//整单退，取付款金额（订单可能部分支付）
//
//                        this.addProcessData(new DataProcessBean(up1));

//                        // 更新单身已退数量
//                        String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId_para
//                                + "' and orderno='" + orderNo + "' ";
//                        ExecBean exSale = new ExecBean(execsql);
//                        this.addProcessData(new DataProcessBean(exSale));

                        for (DataProcessBean bean : DPB_returnOrder)
                        {
                            this.addProcessData(bean);
                        }

                        this.doExecuteDataToDB();
                        res.setSuccess(true);

                        String status_update = "12";
                        String refundStatus_update = "6";

                        dcpOrder.setStatus(status_update);
                        dcpOrder.setRefundStatus(refundStatus_update);

                        String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
                        String hash_key = orderNo;
                        String hash_value = pj.beanToJson(dcpOrder);
                        this.updateRedis(redis_key, hash_key, hash_value);
                        // 写日志
                        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                        orderStatusLog onelv1 = new orderStatusLog();
                        onelv1.setLoadDocType(loadDocType);
                        onelv1.setChannelId(channelId);
                        onelv1.setLoadDocBillType("");
                        onelv1.setLoadDocOrderNo("");
                        onelv1.seteId(eId_para);

                        onelv1.setOpName(opName);
                        onelv1.setOpNo(opNo);
                        onelv1.setShopNo(shopId_db);
                        onelv1.setOrderNo(orderNo);
                        onelv1.setMachShopNo("");
                        onelv1.setShippingShopNo("");
                        String statusType = "1";
                        String updateStaus = "12";
                        onelv1.setStatusType(statusType);
                        onelv1.setStatus(updateStaus);
                        StringBuilder statusTypeNameObj = new StringBuilder();
                        String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                        String statusTypeName = statusTypeNameObj.toString();
                        onelv1.setStatusTypeName(statusTypeName);
                        onelv1.setStatusName(statusName);

                        String memo = "";
                        memo += statusName;
                        onelv1.setMemo(memo);
                        onelv1.setDisplay("1");

                        String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        onelv1.setUpdate_time(updateDatetime);

                        orderStatusLogList.add(onelv1);

                        StringBuilder errorStatusLogMessage = new StringBuilder();
                        boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                        if (nRet)
                        {
                            HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                        } else
                        {
                            HelpTools.writelog_waimai(
                                    "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                        }
                        this.pData.clear();

                    }
                    else
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已提货商品退单处理节点类型="+pickGoodsRefundType+" 暂不支持！");
                    }
                    if(isWechatOrder&&isCreateSale)
                    {
                        HelpTools.wechatOrderRefundPoint(eId_para, orderNo,headOrderNo, opNo, opName);
                    }
                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道:"+loadDocType+",退单类型="+refundType+"暂不支持");
                }


                if(!otherChannelRes)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
                }
                String status_update = "11";
                String refundStatus_update = "10";

                dcpOrder.setStatus(status_update);
                dcpOrder.setRefundStatus(refundStatus_update);

                String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
                String hash_key = orderNo;
                String hash_value = pj.beanToJson(dcpOrder);
                this.updateRedis(redis_key, hash_key, hash_value);

            }
            else if (refundType.equals("2"))
            {

                UptBean up1 = new UptBean("DCP_ORDER");
                up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

                //更新updatetime
                up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                up1.addUpdateValue("LASTREFUNDTIME", new DataValue(refundDatetime, Types.VARCHAR));

                boolean otherChannelRes = false;//退款调用相应渠道接口，成功标识
                StringBuilder otherChannelError = new StringBuilder("");

                if (loadDocType.equals(orderLoadDocType.ELEME)) //饿了么订单
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db,
                                                                                loadDocType,app_poi_code);
                    if (map != null)
                    {
                        String elmAPPKey = map.get("APPKEY").toString();
                        String elmAPPSecret = map.get("APPSECRET").toString();
                        String elmAPPName = map.get("APPNAME").toString();
                        String elmIsTest = map.get("ISTEST").toString();
                        boolean elmIsSandbox = false;
                        if (elmIsTest != null && elmIsTest.equals("Y"))
                        {
                            elmIsSandbox = true;
                        }
                        String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                        String userId = map.getOrDefault("USERID","").toString();
                        if ("Y".equals(isJBP))
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(userId,elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }
                        else
                        {
                            otherChannelRes = WMELMOrderProcess.orderRefundAgree(elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                                 elmAPPName, orderNo, "", otherChannelError);
                        }

                    }
                    else
                    {
                        otherChannelRes = WMELMOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }

                }
                else if (loadDocType.equals(orderLoadDocType.MEITUAN)) //美团订单
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    String isJbp = map.get("ISJBP").toString();

                    if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                    {
                        otherChannelRes = WMJBPOrderProcess.orderRefundAgree(eId_para, shopId_db, orderNo, "", otherChannelError);
                    }
                    else
                    {
                        otherChannelRes = WMMTOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                    }
                }
                else if (loadDocType.equals(orderLoadDocType.MTSG))
                {

                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                    }
                    otherChannelRes = WMSGOrderProcess.orderRefundAgree(orderNo, "", otherChannelError);
                }
                else if (loadDocType.equals(orderLoadDocType.DYWM))
                {
                    Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                    if (map == null)
                    {
                        HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                    }

                    String clientKey = map.get("APPKEY").toString();
                    String clientSecret = map.get("APPSECRET").toString();
                    //String elmAPPName = map.get("APPNAME").toString();
                    String isTest = map.get("ISTEST").toString();
                    boolean isSandbox = false;
                    if (isTest != null && isTest.equals("Y"))
                    {
                        isSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    //String after_sale_id = "";//退单ID
                    otherChannelRes = WMDYOrderProcess.orderRefundAgree(isSandbox,clientKey,clientSecret,orderNo,after_sale_id,"","",otherChannelError);

                }
                else if (loadDocType.equals(orderLoadDocType.JDDJ)) //京东到家订单
                {
                    if (opNo == null || opNo.trim().isEmpty())
                    {
                        opNo = "pos";
                    }
                    otherChannelRes = HelpJDDJHttpUtil.orderCancelOperate(orderNo, true, opNo,"同意退单", otherChannelError);
                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道:"+loadDocType+",退单类型="+refundType+"暂不支持");
                }


                if(!otherChannelRes)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
                }
                String status_update = "11";
                String refundStatus_update = "10";

                dcpOrder.setStatus(status_update);
                dcpOrder.setRefundStatus(refundStatus_update);

                String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
                String hash_key = orderNo;
                String hash_value = pj.beanToJson(dcpOrder);
                this.updateRedis(redis_key, hash_key, hash_value);

                this.addProcessData(new DataProcessBean(up1));

                this.doExecuteDataToDB();

            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单类型"+refundType+"暂不支持");
            }

            //由于在网页操作同意退单,调用的是这个主动退单接口，所以要区分下，如果是网页上操作，那么就是写下缓存
            if (isWechatOrder)
            {
                try
                {
                    HelpTools.writelog_waimai("【商城订单】【同意退单】开始写缓存,订单号orderNo=" + orderNo);
                    order dcpOrderUpdate = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId_para, loadDocType, orderNo);
                    StringBuffer errorMessage = new StringBuffer();
                    HelpTools.writeOrderRedisByAllShop(dcpOrderUpdate,req.getRequest().getShopId(),errorMessage);

                }
                catch (Exception e)
                {

                }

            }
            if (orderLoadDocType.WAIMAI.equals(loadDocType))
            {
                try
                {
                    HelpTools.writelog_waimai("【鼎捷外卖】【同意退单】开始写缓存,订单号orderNo=" + orderNo);
                    order dcpOrderUpdate = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId_para, loadDocType, orderNo);
                    String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopId_db;;
                    String hash_key = dcpOrder.getOrderNo();
                    //ParseJson pj = new ParseJson();
                    String Response_json = pj.beanToJson(dcpOrderUpdate);
                    RedisPosPub redis = new RedisPosPub();
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret) {
                        HelpTools.writelog_waimai("【鼎捷外卖】【同意退单】【下单门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    } else {
                        HelpTools.writelog_waimai("【鼎捷外卖】【同意退单】【下单门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }

                }
                catch (Exception e)
                {

                }

            }

            //这个服务同意好像不支持部分商品退，所以全退吧
            //增加KDS处理逻辑部分
            //是否启用KDS标记
            String canKDS="N";
            String Crm_channel_KDS_SQL="select * from crm_channel where eid='"+eId_para+"' and appno='KDS' and status=100";
            List<Map<String, Object>> getData_KDS =this.doQueryData(Crm_channel_KDS_SQL,null);
            if (getData_KDS != null && getData_KDS.size()>0)
            {
                canKDS="Y";
            }

            if (canKDS.equals("Y"))
            {
                //查订单单头
                String order_SQL="select * from dcp_order where eid='" + eId_para + "' and LOADDOCTYPE='" + loadDocType +"' and orderno='" + orderNo + "'  ";
                List<Map<String, Object>> getData_Order =this.doQueryData(order_SQL,null);

                //查订单单身
                String ordeDetail_SQL="select a.*,b.category,b.ISDOUBLEGOODS from dcp_order_detail a " +
                        "left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                        "where a.eid='" + eId_para + "' and a.LOADDOCTYPE='" + loadDocType +"' and a.orderno='" + orderNo + "'  ";
                getData_Order_detail=this.doQueryData(ordeDetail_SQL,null);

                // 退单完成后 检查是否生成加工任务单 并同步加工任务单状态
                this.updateProcessTask(req,getData_Order,getData_Order_detail);
            }

            //调用发票作废或折让
            if(isCallPosInvoiceRefund)
            {
                String refundOrderNo = "RE"+orderNo;
                Map<String, Object> outMap = new HashMap<String,Object>();
                StringBuffer errorInvoiceRefund = new StringBuffer();
                boolean callInvoiceRefundResult = HelpTools.OrderInvoiceRefund(eId_para, orderNo, refundOrderNo, refundReasonNo, refundReasonName, invOperateType, opNo, outMap, errorInvoiceRefund);
                if(callInvoiceRefundResult)
                {
                    String rebateNo = outMap.getOrDefault("rebateNo", "").toString();
                    UptBean up1 = new UptBean("DCP_ORDER");
                    up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                    up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

                    //更新updatetime
                    up1.addUpdateValue("INVOPERATETYPE", new DataValue(invOperateType, Types.VARCHAR));
                    up1.addUpdateValue("REBATENO", new DataValue(rebateNo, Types.VARCHAR));
                    up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up1));
                    this.doExecuteDataToDB();


                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "99";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);

                    String statusName = "其他";
                    String statusTypeName = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "发票作废或折让成功";

                    if(rebateNo!=null&&rebateNo.trim().isEmpty()==false)
                    {
                        memo = memo+"<br/>发票折让单号:"+rebateNo;
                    }
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("0");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();

                }
                else
                {
                    UptBean up1 = new UptBean("DCP_ORDER");
                    up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
                    up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

                    //更新updatetime
                    up1.addUpdateValue("EXCEPTIONSTATUS", new DataValue("Y", Types.VARCHAR));
                    up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up1));
                    this.doExecuteDataToDB();

                    // 写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo("");
                    onelv1.seteId(eId_para);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId_db);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo("");
                    onelv1.setShippingShopNo("");
                    String statusType = "1";
                    String updateStaus = "99";
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);

                    String statusName = "其他";
                    String statusTypeName = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "发票作废或折让失败";

                    onelv1.setMemo(memo);
                    onelv1.setDisplay("0");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet)
                    {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }
                    this.pData.clear();


                }
            }


        }
        //2-拒绝
        else
        {
            //拒绝退单
            UptBean up1 = new UptBean("DCP_ORDER");
            up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
            up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

            //更新updatetime
            up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            boolean otherChannelRes = true;//拒绝退单调用相应渠道接口，成功标识
            StringBuilder otherChannelError = new StringBuilder("");

            if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))//商城
            {

                try
                {
                    //不做任何操作 ，只是通知微商城做流传记录
                    String updateTime_wechat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    JSONObject objReq = new JSONObject();
                    objReq.put("orderNo", orderNo);
                    objReq.put("statusType", "4");
                    objReq.put("status", "3");
                    objReq.put("description", "门店拒绝退单");
                    objReq.put("oprId", "");
                    objReq.put("terminalId", "");
                    objReq.put("orgType", "2");
                    objReq.put("orgId", "");
                    objReq.put("orgId", updateTime_wechat);

                    String request = objReq.toString();
                    String microMarkServiceName = "OrderStatusUpdate";

                    //接口都默认返回ture

                    String result = HttpSend.MicroMarkSend(request, eId_para, microMarkServiceName,channelId);

                }
                catch (Exception e)
                {

                }
            }
            else if (loadDocType.equals(orderLoadDocType.ELEME)) //饿了么订单
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db,
                                                                            loadDocType,app_poi_code);
                if (map != null)
                {
                    String elmAPPKey = map.get("APPKEY").toString();
                    String elmAPPSecret = map.get("APPSECRET").toString();
                    String elmAPPName = map.get("APPNAME").toString();
                    String elmIsTest = map.get("ISTEST").toString();
                    boolean elmIsSandbox = false;
                    if (elmIsTest != null && elmIsTest.equals("Y"))
                    {
                        elmIsSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    if ("Y".equals(isJBP))
                    {
                        otherChannelRes = WMELMOrderProcess.orderRefundReject(userId,elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                              elmAPPName, orderNo, "", otherChannelError);
                    }
                    else
                    {
                        otherChannelRes = WMELMOrderProcess.orderRefundReject(elmIsSandbox, elmAPPKey, elmAPPSecret,
                                                                              elmAPPName, orderNo, "", otherChannelError);
                    }
                }
                else
                {
                    otherChannelRes = WMELMOrderProcess.orderRefundReject(orderNo, "", otherChannelError);
                }

            }
            else if (loadDocType.equals(orderLoadDocType.MEITUAN)) //美团订单
            {

                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                }
                String isJbp = map.get("ISJBP").toString();

                if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                {
                    otherChannelRes = WMJBPOrderProcess.orderRefundReject(eId_para, shopId_db, orderNo, "", otherChannelError);
                }
                else
                {
                    otherChannelRes = WMMTOrderProcess.orderRefundReject(orderNo, "", otherChannelError);
                }
            }
            else if (loadDocType.equals(orderLoadDocType.MTSG))
            {

                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                }
                otherChannelRes = WMSGOrderProcess.orderRefundReject(orderNo, "", otherChannelError);
            }
            else if (loadDocType.equals(orderLoadDocType.DYWM))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopId_db, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                }

                String clientKey = map.get("APPKEY").toString();
                String clientSecret = map.get("APPSECRET").toString();
                //String elmAPPName = map.get("APPNAME").toString();
                String isTest = map.get("ISTEST").toString();
                boolean isSandbox = false;
                if (isTest != null && isTest.equals("Y"))
                {
                    isSandbox = true;
                }
                String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                String userId = map.getOrDefault("USERID","").toString();
                //String after_sale_id = "";//退单ID
                otherChannelRes = WMDYOrderProcess.orderRefundReject(isSandbox,clientKey,clientSecret,orderNo,after_sale_id,"","",otherChannelError);

            }
            else if (loadDocType.equals(orderLoadDocType.JDDJ)) //京东到家订单
            {
                if (opNo == null || opNo.trim().isEmpty())
                {
                    opNo = "pos";
                }
                otherChannelRes = HelpJDDJHttpUtil.orderCancelOperate(orderNo, false, opNo,"拒绝退单", otherChannelError);
            }
            else if (loadDocType.equals(orderLoadDocType.POS)) //winpos门店订单
            {
                otherChannelRes = true;
            }
            else if (loadDocType.equals(orderLoadDocType.YOUZAN)) //有赞订单
            {
                YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
                try{
                    Map<String, Object> otherMap = new HashMap<String, Object>();
                    JsonBasicRes thisRes=new JsonBasicRes();
                    thisRes=ycb.refundRefuse(eId_para, orderNo, shopId_db,otherMap,null);
                    if(!thisRes.isSuccess()){
                        otherChannelRes = false;
                        otherChannelError.append(thisRes.getServiceDescription());
                    }
                    else
                    {
                        otherChannelRes = true;
                    }
                }catch (Exception e) {
                    otherChannelRes = false;
                    otherChannelError.append(e.getMessage());
                }

            }
            else if (loadDocType.equals(orderLoadDocType.WAIMAI)) //winpos门店订单
            {
                otherChannelRes = true;
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该渠道:"+loadDocType+"，暂不支持");
            }


            if(!otherChannelRes)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用渠道类型"+loadDocType+"接口返回："+otherChannelError.toString());
            }


            up1.addUpdateValue("REFUNDSTATUS", new DataValue("3",Types.VARCHAR));

            this.addProcessData(new DataProcessBean(up1));

            this.doExecuteDataToDB();



            String redis_key = orderRedisKeyInfo.redis_OrderTableName+":"+eId_para+":"+redis_shop;
            String hash_key = orderNo;
            deleteRedis(redis_key, hash_key);

            res.setSuccess(true);
            //写日志
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            orderStatusLog onelv1 = new orderStatusLog();
            onelv1.setLoadDocType(loadDocType);
            onelv1.setChannelId(channelId);
            onelv1.setLoadDocBillType("");
            onelv1.setLoadDocOrderNo("");
            onelv1.seteId(eId_para);



            onelv1.setOpName(opName);
            onelv1.setOpNo(opNo);
            onelv1.setShopNo(shopId_db);
            onelv1.setOrderNo(orderNo);
            onelv1.setMachShopNo("");
            onelv1.setShippingShopNo("");
            String statusType = "3";
            String updateStaus = "3";
            onelv1.setStatusType(statusType);
            onelv1.setStatus(updateStaus);
            StringBuilder statusTypeNameObj = new StringBuilder();
            String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
            String statusTypeName = statusTypeNameObj.toString();
            onelv1.setStatusTypeName(statusTypeName);
            onelv1.setStatusName(statusName);

            String memo = "";
            memo += statusName;
            onelv1.setMemo(memo);
            onelv1.setDisplay("1");

            String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            onelv1.setUpdate_time(updateDatetime);

            orderStatusLogList.add(onelv1);

            StringBuilder errorStatusLogMessage = new StringBuilder();
            boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
            if (nRet) {
                HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
            } else {
                HelpTools.writelog_waimai(
                        "【写表dcp_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
            }
            this.pData.clear();





        }



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderRefundAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderRefundAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderRefundAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderRefundAgreeOrRejectReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId()))
        {
            errCt++;
            errMsg.append("企业编号eId不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getRefundType()))
        {
            errCt++;
            errMsg.append("退单类型refundType不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errCt++;
            errMsg.append("订单单号orderNo不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getLoadDocType()))
        {
            errCt++;
            errMsg.append("订单渠道类型loadDocType不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getChannelId()))
        {
            errCt++;
            errMsg.append("订单渠道编码channelId不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getRefundBdate()))
        {
            errCt++;
            errMsg.append("退订营业日期refundBdate不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getRefundDatetime()))
        {
            errCt++;
            errMsg.append("退订时间refundDatetime不可为空值, ");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderRefundAgreeOrRejectReq> getRequestType()
    {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderRefundAgreeOrRejectReq>(){};
    }

    @Override
    protected DCP_OrderRefundAgreeOrRejectRes getResponseType()
    {
        // TODO Auto-generated method stub
        return new DCP_OrderRefundAgreeOrRejectRes();
    }

    private  boolean CheckCanReturn(String eId,String orderNo,StringBuffer errorInfo) throws Exception
    {
        String sql = "select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】单号orderNo="+orderNo+"  查询语句："+sql);
        List<Map<String, Object>> getQHead = this.doQueryData(sql, null);
        if(getQHead==null||getQHead.isEmpty())
        {
            errorInfo.append("该订单不存在！");
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询完成,单号orderNo="+orderNo+"该订单不存在！");
            return false;
        }
        String status = getQHead.get(0).get("STATUS").toString();
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询完成,单号orderNo="+orderNo+" 状态status="+status);
        if(status.equals("3"))
        {
            errorInfo.append("该订单状态为已取消！");
            return false;
        }
        if(status.equals("12"))
        {
            errorInfo.append("订单状态为已退单！");
            return false;
        }

        return true;
    }

    /**
     * 查询来源类型是订单，来源单号是订单单号的销售单号
     * @param eId
     * @param orderNo
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getSaleNoByOrderNo(String eId,String headOrderNo,String orderNo) throws Exception
    {
        String sql = "";
        if(headOrderNo!=null&&headOrderNo.trim().length()>0){
        	//有主单号
        	//SOURCESUBORDERNO	NVARCHAR2(64 CHAR)	Yes	''	21	原子单号
        	sql = "select * from dcp_sale where ofno='"+headOrderNo+"' and eid='"+eId+"' and sourcesuborderno='"+orderNo+"'";
        }else{
        	sql = "select * from dcp_sale where ofno='"+orderNo+"' and eid='"+eId+"' ";
        }
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询该订单单号orderNo="+orderNo+"有没有生成销售单，查询sql:"+sql);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        return getQData;
    }

    /**
     * 根据原销售单号生成反向销退单
     * @param eId 企业ID
     * @param bDate 销退单营业日期
     * @param sourceSaleNoList map结构 {SALENO=,SHOPID=}
     * @param errorMessage
     * @return
     * @throws Exception
     */
    private ArrayList<DataProcessBean> getReturnSaleSql(String eId,String bDate,List<Map<String, Object>> sourceSaleNoList,StringBuffer errorMessage,
    		List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods,String tot_amt,String isRefundDeliverAmt,Map<String,Object> recalculateMap,Map<String, Object> ordermap) throws Exception
    {
        String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stime = new SimpleDateFormat("HHmmss").format(new Date());
        String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String tran_time = update_time;
        String bdate = bDate;
        int otype = 0;//退单来源类型
        ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
        
        //isRefundDeliverAmt;//是否退运费 0否1是
        for (Map<String, Object> par : sourceSaleNoList)
        {
        	
            String sourceSaleNo = par.get("SALENO").toString();
            String shopid = par.get("SHOPID").toString();
            //原单 总应付金额
            BigDecimal saleAmtTot=new BigDecimal(par.get("TOT_AMT")==null?"0":par.get("TOT_AMT").toString());
            String saleno = "RE"+sourceSaleNo;//退单的单号saleno
            List<Map<String, Object>> dcpSaleDetailMaps = null;
            
    		int type = 1;//退单的类型type
            String typename = "原单退";
            String ofno = sourceSaleNo;//退单ofno的来源单号
            
            String loadDocType = ordermap.get("LOADDOCTYPE").toString();
            String orderNo = ordermap.get("ORDERNO").toString();
            //单据状态: 0.待审核1.订单开立 2.已接单 3. 已拒单 8.待提货 9.待发货 10.已发货 11.已完成 12.整单已退单
//            String status = ordermap.get("STATUS").toString();
            
            if(!orderLoadDocType.MINI.equals(loadDocType)&&goods!=null&&goods.size()>0){
            	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, 
						"非小程序订单不支持部分退!");
            }
            
            //refundType 退单类型 0：全退,1：部分退
            String refundType=recalculateMap.get("refundType")==null?"":recalculateMap.get("refundType").toString();
            boolean isAllRefund=true;

            if(goods!=null&&goods.size()>0){
            	isAllRefund=false;
            	if(Check.Null(refundType)||"0".equals(refundType)){
            		isAllRefund=true;
            	}
            }
            
            //全退走原先逻辑
            if(isAllRefund){

                //生成单头语句
                StringBuffer strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE (");//分区字段
                strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                                       + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                                       + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                                       + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                                       + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,PARTITION_DATE )");//分区字段已处理

                strBuff.append("select eid,shopid,'"+saleno+"',trno,ver_num,legalper,machine,"+type+",'"+typename+"','"+bdate+"',squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                                       + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                                       + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                                       + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                                       + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理

                strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                String execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));

                //生成商品单身
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                       + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                       + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");

                strBuff.append(" select eid, shopid, '"+saleno+"', warehouse, item, oitem, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                       + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                       + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"'");

                strBuff.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail));

                //生成商品单身折扣
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL_agio (");//分区字段
                strBuff.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理

                strBuff.append(" select  eid, shopid, '"+saleno+"', mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理

                strBuff.append(" from DCP_SALE_DETAIL_agio where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail_agio = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail_agio));

                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE )");//分区字段已处理
                strBuff.append(" select eid, shopid, '"+saleno+"', item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"'");//分区字段已处理

                strBuff.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));

                //交班汇总
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_STATISTIC_INFO (");
                strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
                strBuff.append(" select EID, SHOPID, MACHINE, OPNO, SQUADNO, '"+saleno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, '"+sdate+"', '"+stime+"', 'N', WORKNO, 1, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");

                strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' and ORDERNO='"+sourceSaleNo+"' and type=0");//收款TYPE3，退订TYPE4，销售TYPE0 ，销退TYPE1
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_statistic_info = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_statistic_info));

                //库存流水账生成
                strBuff = new StringBuffer("");
                strBuff.append(" select * from dcp_stock_detail where billtype=20");
                strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();

                HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                //营业日期 -存储过程
                String stockChange_BDATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if(bdate!=null&&bdate.isEmpty()==false)
                {
                    stockChange_BDATE = bdate;
                    if(bdate.length()==8)
                    {
                        stockChange_BDATE = bdate.substring(0,4)+"-"+bdate.substring(4,6)+"-"+bdate.substring(6,8);
                    }
                }
                List<Map<String, Object>> getQData_stockDetail = this.doQueryData(execsql, null);

                //流水表没有到历史流水表里查
                if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                {
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    getQData_stockDetail = this.doQueryData(execsql, null);
                }

                if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                {
                    String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopid);
                    String procedure="SP_DCP_StockChange";
                    for (Map<String, Object> map : getQData_stockDetail)
                    {

                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,map.get("EID").toString());                                       //--企业ID
                        inputParameter.put(2,map.get("ORGANIZATIONNO").toString());                                    //--组织
                        inputParameter.put(3,"21");                                      //--单据类型
                        inputParameter.put(4,saleno);	                                 //--单据号
                        inputParameter.put(5,map.get("ITEM").toString());            //--单据行号
                        inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,stockChange_BDATE);           //--营业日期 yyyy-MM-dd
                        inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                        inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                        inputParameter.put(10,map.get("WAREHOUSE").toString());                                //--仓库
                        inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                        inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                        inputParameter.put(13,map.get("QTY").toString());           //--交易数量
                        inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                        inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                        inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                        inputParameter.put(17,map.get("PRICE").toString());          //--零售价
                        inputParameter.put(18,map.get("AMT").toString());            //--零售金额
                        inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                        inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                        inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                        inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(23,sdate);                                  //--单据日期
                        inputParameter.put(24,"");                                       //--异动原因
                        inputParameter.put(25,"销售单退单");                                //--异动描述
                        inputParameter.put(26,"");                                //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        HelpTools.writelog_waimai("********** 调用存储过程SP_DCP_StockChange参数："+inputParameter.toString());
                        DataPB.add(new DataProcessBean(pdb));

                    }
                }
        	}
            //部分退
            else{
            	//本次退款金额
            	BigDecimal totAmt=new BigDecimal((tot_amt!=null&&tot_amt.trim().length()>0)?tot_amt:"0");
//            	//标记金额已退完/最后一次退款  用来判断是否要退券 1-已退完
//                String isAllRefund=recalculateMap.get("isAllRefund")==null?"":recalculateMap.get("isAllRefund").toString();
                
            	//列表SQL
        		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
        		//说明此时是部分退货
            	//TYPE	NUMBER	Yes	0	9	单据类型:0-销售单1-凭单退货2-无单退货
            	String saleSql="select * from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and ofno='"+sourceSaleNo+"' and type='1'";
            	List<Map<String, Object>> saleMaps = this.doQueryData(saleSql, null);
            	if(saleMaps!=null&&saleMaps.size()>0){
            		saleno+="_"+String.valueOf(saleMaps.size()+1);
            	}
            	
            	List<String> goodsArray=new ArrayList<String>();
            	if(goods!=null&&goods.size()>0){
            		goodsArray=goods.stream().map(x->String.valueOf(x.getItem())).collect(Collectors.toList());
                	goodsArray = goodsArray.stream().distinct().collect(Collectors.toList());
                	goodsArray=goodsArray.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            	}
            	
                if(goodsArray!=null&&goodsArray.size()>0){
                	//说明此时是部分退货
                	String saleDetailSql="select * from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ";
                	dcpSaleDetailMaps = this.doQueryData(saleDetailSql, null);
//                	if(dcpSaleDetailMaps==null||dcpSaleDetailMaps.size()==0){
//                		saleDetailSql="select * from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ";
//                    	dcpSaleDetailMaps = this.doQueryData(saleDetailSql, null);
//                	}
//                	if(goods.size()!=dcpSaleDetailMaps.size()){
//                		
//                	}
                }
                
                
                //生成单头语句
                StringBuffer strBuff = new StringBuffer("");
                String execsql = "";
                
                
                //SALEAMT	NUMBER(23,8)	Yes	0	136	商品成交金额[含税]=单身商品成交金额AMT合计
                BigDecimal saleAmt=BigDecimal.ZERO;
                //SALEDISC	NUMBER(23,8)	Yes		68	商品折扣金额(含税)=单身商品折扣DISC合计
                BigDecimal saleDiscTot=BigDecimal.ZERO;
                //TOT_QTY	NUMBER(23,8)	Yes		65	总数量
                BigDecimal totQty=BigDecimal.ZERO;
                //TOT_OLDAMT	NUMBER(23,8)	Yes		66	原总金额(含税)=SALEAMT+PACKAGEFEE+SHIPPINGFEE
                BigDecimal totOldamt=BigDecimal.ZERO;
                if(dcpSaleDetailMaps!=null&&dcpSaleDetailMaps.size()>0){
                	
                	//查询商品单身折扣
                    String agiosql="select * from DCP_SALE_DETAIL_AGIO where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and mitem in('"+String.join("','",goodsArray)+"') ";
                	List<Map<String, Object>> agioMaps = this.doQueryData(agiosql, null);
                	
                	for(Map<String, Object> detailMap:dcpSaleDetailMaps){
//                		lstData=new ArrayList<DataProcessBean>();
                		String item=detailMap.get("ITEM")==null?"":detailMap.get("ITEM").toString();
                		List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> thisgoods=goods.stream().filter(g->g.getItem().equals(item)).collect(Collectors.toList());
                		//本次退数量
                		BigDecimal rqty=BigDecimal.ZERO;
                		if(thisgoods!=null&&thisgoods.size()==1){
                			rqty=new BigDecimal(String.valueOf(thisgoods.get(0).getQty()));
                		}
                		//单身商品总数量
                		BigDecimal qty =new BigDecimal(detailMap.get("QTY")==null?"0":detailMap.get("QTY").toString());
                		BigDecimal oldprice =new BigDecimal(detailMap.get("OLDPRICE")==null?"0":detailMap.get("OLDPRICE").toString());
                		BigDecimal price =new BigDecimal(detailMap.get("PRICE")==null?"0":detailMap.get("PRICE").toString());
                		BigDecimal oldamount=oldprice.multiply(rqty);
                		BigDecimal amount=price.multiply(rqty);
                		
                		//AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	87	商家实收金额
                		BigDecimal amtMerreceive=amount;
                		//AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	89	顾客实付金额
                		BigDecimal amtCustpayreal=amount;
                		BigDecimal disc=oldamount.subtract(amount);
                		BigDecimal saleDisc=disc;
                		
                		//加总 计算单身数据
                		saleAmt=saleAmt.add(amount);
                		saleDiscTot=saleDiscTot.add(disc);
                		totQty=totQty.add(rqty);
                		totOldamt=totOldamt.add(oldamount);
                		//生成商品单身 DCP_SALE_DETAIL
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                        strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                               + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                               + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");

                        strBuff.append(" select eid, shopid, '"+saleno+"', warehouse, item, oitem, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                                               + ", '"+rqty+"', '"+oldprice+"', price2, price3, canback, bsno, '"+rqty+"', returnuserid, returntableno, refundopno, refundtime, '"+oldamount+"', '"+disc+"', '"+saleDisc+"', paydisc, '"+price+"', additionalprice, '"+amount+"', point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                                               + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, '"+amtMerreceive+"', DISC_CUSTPAYREAL, '"+amtCustpayreal+"','"+bdate+"'");

                        strBuff.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item='"+item+"'");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_detail = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_detail));
                        if(agioMaps!=null&&agioMaps.size()>0){
                        	//DCP_SALE_DETAIL_AGIO
                        	List<Map<String, Object>> newagioMaps=agioMaps.stream().filter(g->g.get("MITEM").toString().equals(item)).collect(Collectors.toList());
                        	if(newagioMaps!=null&&newagioMaps.size()>0){
                        		BigDecimal agiodiscTot=BigDecimal.ZERO;
                        		int agioCount=0;
                    			for(Map<String, Object> agioMap:newagioMaps){
                    				agioCount++;
                    				String thisitem=agioMap.get("ITEM")==null?"":agioMap.get("ITEM").toString();
                    				//QTY	NUMBER(23,8)	Yes		6	参与数量
                    				BigDecimal agioqty =new BigDecimal(agioMap.get("QTY")==null?"0":agioMap.get("QTY").toString());
                    				//AMT	NUMBER(23,8)	Yes		7	参与金额
                    				BigDecimal agioamt =new BigDecimal(agioMap.get("AMT")==null?"0":agioMap.get("AMT").toString());
                    				agioamt=agioamt.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				//DISC	NUMBER(23,8)	Yes		8	折扣金额
                    				BigDecimal agiodisc =new BigDecimal(agioMap.get("DISC")==null?"0":agioMap.get("DISC").toString());
                    				agiodisc=agiodisc.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				if(agioCount==newagioMaps.size()){
                    					agiodisc=disc.subtract(agiodiscTot);
                    				}
                    				
                    				//REALDISC	NUMBER(23,8)	Yes		9	实际折扣金额
                    				BigDecimal agioreal =new BigDecimal(agioMap.get("REALDISC")==null?"0":agioMap.get("REALDISC").toString());
                    				agioreal=agioreal.multiply(rqty).divide(agioqty,2,BigDecimal.ROUND_DOWN);
                    				//生成商品单身折扣
                                    strBuff = new StringBuffer("");
                                    strBuff.append(" insert into DCP_SALE_DETAIL_AGIO (");//分区字段
                                    strBuff.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理

                                    strBuff.append(" select  eid, shopid, '"+saleno+"', mitem, item, '"+rqty+"', '"+agioamt+"', '"+agiodisc+"', '"+agioreal+"', dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理

                                    strBuff.append(" from DCP_SALE_DETAIL_AGIO where eid='"+eId+"' and shopid='"+shopid+"' "
                                    		+ " and saleno='"+sourceSaleNo+"' and MITEM='"+item+"' and ITEM='"+thisitem+"' ");
                                    execsql = "";
                                    execsql = strBuff.toString();
                                    ExecBean exSale_detail_agio = new ExecBean(execsql);
                                    lstData.add(new DataProcessBean(exSale_detail_agio));
                                    agiodiscTot=agiodiscTot.add(agiodisc);
                    			}
                    		}
                        }
                        
//                        StaticInfo.dao.useTransactionProcessData(lstData);
                	}
                }
                BigDecimal l=BigDecimal.ZERO;
                if(totAmt.compareTo(saleAmt)!=0){
                	l=totAmt.subtract(saleAmt);
                }
                
                totOldamt=totOldamt.add(l);
                //TOT_DISC	NUMBER(23,8)	Yes		67	折扣金额(含税)
                BigDecimal totDisc=saleDiscTot;
                //TOT_AMT	NUMBER(23,8)	Yes		71	应付金额(含税)
                BigDecimal TOT_AMT=saleAmt.add(l);
                //PAY_AMT	NUMBER(23,8)	Yes		86	已收抵账金额(含税)=所有支付金额之和，去除找零和溢收
                BigDecimal PAY_AMT=saleAmt.add(l);
                //SHOPINCOME	NUMBER(23,8)	Yes		105	外卖：商家实际到账金额
                BigDecimal SHOPINCOME=saleAmt.add(l);
                //TOT_AMT_MERRECEIVE	NUMBER(23,8)	Yes	0	131	商家实收金额(含税)=付款时商家实收金额
                BigDecimal TOT_AMT_MERRECEIVE=saleAmt.add(l);
                //TOT_AMT_CUSTPAYREAL	NUMBER(23,8)	Yes	0	132	顾客实付金额[含税]=付款时客户实际支付金额
                BigDecimal TOT_AMT_CUSTPAYREAL=saleAmt.add(l);
                
                //生成单头语句 DCP_SALE
                //这几项直接给0
                //DELIVERY_FEE_SHOP	NUMBER(20,2)	Yes		100	外卖：门店承担配送费
                //DELIVERY_FEE_USER	NUMBER(20,2)	Yes		101	外卖：用户承担配送费
                //SHIPPINGFEE	NUMBER(23,8)	Yes		99	外卖：配送费
//                lstData=new ArrayList<DataProcessBean>();
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE (");//分区字段
                strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                                       + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                                       + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                                       + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                                       + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,PARTITION_DATE )");//分区字段已处理

                strBuff.append("select eid,shopid,'"+saleno+"',trno,ver_num,legalper,machine,"+type+",'"+typename+"','"+bdate+"',squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                                       + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                                       + ",'"+totQty+"','"+totOldamt+"','"+totDisc+"','"+saleDiscTot+"',paydisc,erase_amt,'"+TOT_AMT+"',servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,'"+PAY_AMT+"',tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                                       + ",order_id,order_sn,platform_disc,seller_disc,packagefee,[shippingfee],[delivery_fee_shop],[delivery_fee_user],wm_user_paid,platform_fee,wm_extra_fee,'"+SHOPINCOME+"',productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                                       + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, '"+TOT_AMT_MERRECEIVE+"', '"+TOT_AMT_CUSTPAYREAL+"', TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DEPARTNO,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理

                strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = strBuff.toString();
                
                //isRefundDeliverAmt;//是否退运费 0否1是
                if("1".equals(isRefundDeliverAmt)){
                	//退运费
                	execsql=execsql.replace("[shippingfee]", "shippingfee");
                	execsql=execsql.replace("[delivery_fee_shop]", "delivery_fee_shop");
                	execsql=execsql.replace("[delivery_fee_user]", "delivery_fee_user");
                }else{
                	//不退运费
                	execsql=execsql.replace("[shippingfee]", "0");
                	execsql=execsql.replace("[delivery_fee_shop]", "0");
                	execsql=execsql.replace("[delivery_fee_user]", "0");
                }
                
                ExecBean exSale = new ExecBean(execsql);
                lstData.add(new DataProcessBean(exSale));

                String crmPaySql=""
                		+ " SELECT A.*,B.PAYCODEERP,C.AMOUNT,C.CUSTPAYREAL,C.MERRECEIVE,C.NOCODE,C.PURPOSE FROM "
                		+ " (SELECT EID,BILLNO,PAYCHANNEL,MAX(SERIALNO) AS SERIALNO "
                		+ " FROM CRM_ORDERPAY WHERE EID='"+eId+"' AND BILLNO='"+orderNo+"' "//AND PURPOSE='3' 
                		+ " GROUP BY EID,BILLNO,PAYCHANNEL) A "
                		+ " LEFT JOIN DCP_PAYMENT B ON A.EID=B.EID AND A.PAYCHANNEL=B.PAYCODE "
                		+ " LEFT JOIN CRM_ORDERPAY C ON A.EID=C.EID AND A.BILLNO=C.BILLNO AND A.SERIALNO=C.SERIALNO "
                		+ "";
                List<Map<String, Object>> crmPayMaps = this.doQueryData(crmPaySql, null);
        		
                //查询付款单DCP_SALE_PAY
                String paysql="select * from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ";
            	List<Map<String, Object>> payMaps = this.doQueryData(paysql, null);
            	if(payMaps!=null&&payMaps.size()>0){
            		BigDecimal payTot=BigDecimal.ZERO;
            		BigDecimal merreceiveTot=BigDecimal.ZERO;
            		BigDecimal custpayrealTot=BigDecimal.ZERO;
            		int payCount=0;
            		for(Map<String, Object> payMap:payMaps){
            			payCount++;
            			//PAY	NUMBER(23,8)	Yes		9	交易金额，含溢收和找零；提货券时=商品原金额
            			BigDecimal pay =new BigDecimal(payMap.get("PAY")==null?"0":payMap.get("PAY").toString());
            			pay=pay.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				pay=PAY_AMT.subtract(payTot);
        				}
            			//MERRECEIVE	NUMBER(23,8)	Yes	0	40	商家实收金额，移动支付用，例如支付宝，微信等
            			BigDecimal merreceive =new BigDecimal(payMap.get("MERRECEIVE")==null?"0":payMap.get("MERRECEIVE").toString());
            			merreceive=merreceive.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				merreceive=TOT_AMT_MERRECEIVE.subtract(merreceiveTot);
        				}
            			//CUSTPAYREAL	NUMBER(23,8)	Yes	0	42	客户实付金额：移动支付用，例如支付宝，微信等
            			BigDecimal custpayreal =new BigDecimal(payMap.get("CUSTPAYREAL")==null?"0":payMap.get("CUSTPAYREAL").toString());
            			custpayreal=custpayreal.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(payCount==payMaps.size()){
            				custpayreal=TOT_AMT_CUSTPAYREAL.subtract(custpayrealTot);
        				}
            			String thisitem=payMap.get("ITEM")==null?"":payMap.get("ITEM").toString();
            			String paycodeerp=payMap.get("PAYCODEERP")==null?"":payMap.get("PAYCODEERP").toString();
            			String caradno=payMap.get("CARADNO")==null?"":payMap.get("CARADNO").toString();
            			
            			
            			if(crmPayMaps!=null&&crmPayMaps.size()>0){
            				List<Map<String, Object>> newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("PAYCODEERP").equals(paycodeerp)).collect(Collectors.toList());
            				if(newcrmPayMaps!=null&&newcrmPayMaps.size()>0){
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()>1){
            						newcrmPayMaps=crmPayMaps.stream().filter(g->g.get("NOCODE").equals(caradno)).collect(Collectors.toList());
            					}
            					if(newcrmPayMaps!=null&&newcrmPayMaps.size()==1){
            						Map<String, Object> newcrmMap=newcrmPayMaps.get(0);
            						String paychannel=newcrmMap.get("PAYCHANNEL")!=null&&newcrmMap.get("PAYCHANNEL").toString().trim().length()>0?newcrmMap.get("PAYCHANNEL").toString():"";
            						String purpose=newcrmMap.get("PURPOSE")!=null&&newcrmMap.get("PURPOSE").toString().trim().length()>0?newcrmMap.get("PURPOSE").toString():"";
            						//支付通道#03会员卡、#04券、#05积分、#P1微信支付
    								if("1".equals(purpose)&&"#04".equals(paychannel)){
    									//未退完时，券不需要反写
    									continue;	
    								}
            						pay=new BigDecimal(newcrmMap.get("AMOUNT")!=null&&newcrmMap.get("AMOUNT").toString().trim().length()>0
            								?newcrmMap.get("AMOUNT").toString():"0");
            						
            						merreceive=new BigDecimal(newcrmMap.get("MERRECEIVE")!=null&&newcrmMap.get("MERRECEIVE").toString().trim().length()>0
            								?newcrmMap.get("MERRECEIVE").toString():"0");
            						custpayreal=new BigDecimal(newcrmMap.get("CUSTPAYREAL")!=null&&newcrmMap.get("CUSTPAYREAL").toString().trim().length()>0
            								?newcrmMap.get("CUSTPAYREAL").toString():"0");
            					}
            				}
            			}
            			
            			//生成付款单
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                        strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYCHANNELCODE,CHARGEAMOUNT,PARTITION_DATE )");//分区字段已处理
                        
                        strBuff.append(" select eid, shopid, '"+saleno+"', item, paydoctype, paycode, paycodeerp, payname, '"+pay+"', pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, '"+merreceive+"', THIRDDISCOUNT, '"+custpayreal+"', COUPONMARKETPRICE, COUPONPRICE,PAYCHANNELCODE,CHARGEAMOUNT,'"+bdate+"'");//分区字段已处理
                        strBuff.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' and item='"+thisitem+"' ");
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_pay = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_pay));
                        
                        payTot=payTot.add(pay);
                        merreceiveTot=merreceiveTot.add(merreceive);
                        custpayrealTot=custpayrealTot.add(custpayreal);
            		}
            		
            	}
            	
            	//交班汇总DCP_STATISTIC_INFO
            	String statisticsql="select * from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' and orderno='"+sourceSaleNo+"' and type=0 ";
            	List<Map<String, Object>> statisticMaps = this.doQueryData(statisticsql, null);
            	if(statisticMaps!=null&&statisticMaps.size()>0){
            		BigDecimal amtTot=BigDecimal.ZERO;
            		int statisticCount=0;
            		for(Map<String, Object> statisticMap:statisticMaps){
            			statisticCount++;
                        String thisitem=statisticMap.get("ITEM")==null?"":statisticMap.get("ITEM").toString();
                        String thisopno=statisticMap.get("OPNO")==null?"":statisticMap.get("OPNO").toString();
                        
                        //AMT	NUMBER(23,8)	Yes		11	金额,存储整数,不含找零，含溢收
                        BigDecimal amt =new BigDecimal(statisticMap.get("AMT")==null?"0":statisticMap.get("AMT").toString());
                        amt=amt.multiply(saleAmt).divide(saleAmtTot,2,BigDecimal.ROUND_DOWN);
            			if(statisticCount==statisticMaps.size()){
            				amt=PAY_AMT.subtract(amtTot);
        				}
            			
            			//交班汇总
                        strBuff = new StringBuffer("");
                        strBuff.append(" insert into DCP_STATISTIC_INFO (");
                        strBuff.append("        EID, SHOPID, MACHINE, OPNO, SQUADNO, ORDERNO, ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, AMT, SDATE, STIME, ISORDERPAY, WORKNO, TYPE, BDATE, CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,DIRECTION,PAYCHANNELCODE,CHARGEAMOUNT )");
                        strBuff.append(" select EID, SHOPID, MACHINE, OPNO, SQUADNO, '"+saleno+"', ORDERCHANNEL, ITEM, PAYCODE, PAYNAME, '"+amt+"', '"+sdate+"', '"+stime+"', 'N', WORKNO, 1, '"+bdate+"', CARDNO, CUSTOMERNO, CHANGED, EXTRA, ISTURNOVER, STATUS, APPTYPE, CHANNELID, PAYTYPE, MERDISCOUNT, THIRDDISCOUNT,-1,PAYCHANNELCODE,CHARGEAMOUNT ");

                        strBuff.append(" from DCP_STATISTIC_INFO where eid='"+eId+"' and shopid='"+shopid+"' "
                        		+ " and ORDERNO='"+sourceSaleNo+"' and type=0 and item='"+thisitem+"' and opno='"+thisopno+"'");//收款TYPE3，退订TYPE4，销售TYPE0 ，销退TYPE1
                        execsql = "";
                        execsql = strBuff.toString();
                        ExecBean exSale_statistic_info = new ExecBean(execsql);
                        lstData.add(new DataProcessBean(exSale_statistic_info));
                        
                        amtTot=amtTot.add(amt);
            		}
            	}
            	
                
            	//库存流水
            	if(goodsArray!=null&&goodsArray.size()>0){
            		//库存流水账生成
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ");
                    execsql = "";
                    execsql = strBuff.toString();

                    HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                    //营业日期 -存储过程
                    String stockChange_BDATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    if(bdate!=null&&bdate.isEmpty()==false)
                    {
                        stockChange_BDATE = bdate;
                        if(bdate.length()==8)
                        {
                            stockChange_BDATE = bdate.substring(0,4)+"-"+bdate.substring(4,6)+"-"+bdate.substring(6,8);
                        }
                    }
                    List<Map<String, Object>> getQData_stockDetail = this.doQueryData(execsql, null);

                    //流水表没有到历史流水表里查
                    if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                    {
                        strBuff = new StringBuffer("");
                        strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                        strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' and item in('"+String.join("','",goodsArray)+"') ");
                        execsql = "";
                        execsql = strBuff.toString();
                        getQData_stockDetail = this.doQueryData(execsql, null);
                    }

                    if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                    {
                        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopid);
                        String procedure="SP_DCP_StockChange";
                        for (Map<String, Object> map : getQData_stockDetail)
                        {
                        	String thisitem=map.get("ITEM").toString();
                        	BigDecimal thisqty=BigDecimal.ZERO;
                        	List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> thisgoods=goods.stream().filter(g->g.getItem().equals(thisitem)).collect(Collectors.toList());
                    		if(thisgoods!=null&&thisgoods.size()==1){
                    			thisqty=new BigDecimal(String.valueOf(thisgoods.get(0).getQty()));
                    		}
                    		BigDecimal price =new BigDecimal(map.get("PRICE")==null?"0":map.get("PRICE").toString());
                    		BigDecimal amt=price.multiply(thisqty);
                            Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1,map.get("EID").toString());                                       //--企业ID
                            inputParameter.put(2,map.get("ORGANIZATIONNO").toString());                                    //--组织
                            inputParameter.put(3,"21");                                      //--单据类型
                            inputParameter.put(4,saleno);	                                 //--单据号
                            inputParameter.put(5,thisitem);            //--单据行号
                            inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,stockChange_BDATE);           //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                            inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                            inputParameter.put(10,map.get("WAREHOUSE").toString());                                //--仓库
                            inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                            inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                            inputParameter.put(13,thisqty.toPlainString());           //--交易数量
                            inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                            inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                            inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                            inputParameter.put(17,price.toPlainString());          //--零售价
                            inputParameter.put(18,amt.toPlainString());            //--零售金额
                            inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                            inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                            inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,sdate);                                  //--单据日期
                            inputParameter.put(24,"");                                       //--异动原因
                            inputParameter.put(25,"销售单退单");                                //--异动描述
                            inputParameter.put(26,"");                                //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            HelpTools.writelog_waimai("********** 调用存储过程SP_DCP_StockChange参数："+inputParameter.toString());
                            lstData.add(new DataProcessBean(pdb));

                        }
                    }
            	}

                StaticInfo.dao.useTransactionProcessData(lstData);
        	}
            
            
            

        }

        return DataPB;
    }


    /**
     * 库存解锁
     * @param req
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public boolean dcpStockUnlock(DCP_OrderRefundAgreeOrRejectReq req,StringBuffer errorMessage) throws Exception
    {
        String eId_para = req.getRequest().geteId();//请求传入的eId
        //退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
        String refundType = req.getRequest().getRefundType();
        String orderNo = req.getRequest().getOrderNo();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();
        String refundBdate = req.getRequest().getRefundBdate();

        //先查询 锁库表
        String sql ="select * from dcp_stock_lock where eid='"+eId_para+"' and billno='"+orderNo+"' and CHANNELID='"+channelId+"'";
        HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询该订单单号orderNo="+orderNo+" 库存锁定，查询sql:");
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if(getQData==null||getQData.isEmpty())
        {
            errorMessage.append("没有查询到锁定的库存！");
            return false;
        }
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
        condition.put("PLUNO", true);
        condition.put("FEATURENO", true);
        condition.put("SUNIT", true);
        List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);

        DCP_StockUnlock_OpenReq unLockReq = new DCP_StockUnlock_OpenReq();
        unLockReq.setServiceId("DCP_StockUnlock_Open");
        unLockReq.seteId(eId_para);
        unLockReq.setToken(req.getToken());
        DCP_StockUnlock_OpenReq.levelReq unLockReq_request = unLockReq.new levelReq();
        unLockReq_request.setbDate(refundBdate);
        unLockReq_request.setBillNo(orderNo);
        unLockReq_request.setChannelId(channelId);
        unLockReq_request.setUnLockType("-1");// 解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
        unLockReq_request.setPluList(new ArrayList<DCP_StockUnlock_OpenReq.PluList>());
        for (Map<String, Object> map : getQPlu)
        {
            DCP_StockUnlock_OpenReq.PluList oneLv1 = unLockReq.new PluList();
            oneLv1.setOrganizationList(new ArrayList<DCP_StockUnlock_OpenReq.OrgList>());
            String pluNo = map.get("PLUNO").toString();
            String featureNo = map.get("FEATURENO").toString();
            String sUnit = map.get("SUNIT").toString();

            oneLv1.setPluNo(pluNo);
            oneLv1.setFeatureNo(featureNo);
            oneLv1.setsUnit(sUnit);
            for (Map<String, Object> mapDetail : getQData)
            {
                String pluNo_detail = mapDetail.get("PLUNO").toString();
                String featureNo_detail  = mapDetail.get("FEATURENO").toString();
                String sUnit_detail  = mapDetail.get("SUNIT").toString();
                DCP_StockUnlock_OpenReq.OrgList oneLv2 = unLockReq.new OrgList();
                if(pluNo.equals(pluNo_detail)&&featureNo.equals(featureNo_detail)&&sUnit.equals(sUnit_detail))
                {
                    oneLv2.setOrganizationNo(mapDetail.get("ORGANIZATIONNO").toString());
                    oneLv2.setQty(mapDetail.get("SQTY").toString());
                    oneLv2.setWarehouse(mapDetail.get("WAREHOUSE").toString());
                    oneLv1.getOrganizationList().add(oneLv2);
                }
            }

            unLockReq_request.getPluList().add(oneLv1);

        }
        unLockReq.setRequest(unLockReq_request);


        try
        {
            ParseJson pj = new ParseJson();

            String json = pj.beanToJson(unLockReq);
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】【调用库存锁定解锁接口】开始，请求json="+json+" 订单单号orderNo="+orderNo);

            DispatchService ds = DispatchService.getInstance();
            String resXML = ds.callService(json, StaticInfo.dao);
            JSONObject json_res = new JSONObject(resXML);
            HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】【调用库存锁定解锁接口】结束，返回json="+json_res+" 订单单号orderNo="+orderNo);

            boolean success =  json_res.getBoolean("success");
            String serviceDescription = json_res.get("serviceDescription").toString();
            if(success)
            {

                return true;
            }
            else
            {
                errorMessage.append(serviceDescription);
                return false;
            }
        }
        catch(Exception e)
        {
            errorMessage.append(e.getMessage());
        }

        return false;
    }


    private void deleteRedis(String redis_key,String hash_key) throws Exception
    {
        try
        {
            RedisPosPub delredis = new RedisPosPub();
            HelpTools.writelog_waimai(
                    "【删除门店Redis】开始删除  redis_key：" + redis_key + " hash_key：" + hash_key);
            delredis.DeleteHkey(redis_key, hash_key);
            HelpTools.writelog_waimai(
                    "【删除门店Redis】删除成功  redis_key：" + redis_key + " hash_key：" + hash_key);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    private void updateRedis(String redis_key,String hash_key,String hash_value) throws Exception
    {
        try
        {
            RedisPosPub redis = new RedisPosPub();
            String ordermap = redis.getHashMap(redis_key, hash_key);
            if(ordermap==null||ordermap.isEmpty())
            {
                return;
            }
            JSONObject obj = new JSONObject(ordermap);

            String status_redis = obj.optString("status","");
            String refundStatus_redis = obj.optString("refundStatus","");
            if(refundStatus_redis.equals("2")||refundStatus_redis.equals("7"))
            {
                HelpTools.writelog_waimai(
                        "【删除Redis中申请退单状态的缓存】开始删除  redis_key：" + redis_key + " hash_key：" + hash_key);
                redis.DeleteHkey(redis_key, hash_key);
                HelpTools.writelog_waimai(
                        "【删除Redis中申请退单状态的缓存】删除成功  redis_key：" + redis_key + " hash_key：" + hash_key);
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }


    /**
     * 退单成功后 判断该订单是否生成加工任务单，如已生成 则同步加工任务单状态
     * @param req
     * @param getData_Order
     * @param getData_Order_detail
     */
    private void updateProcessTask(DCP_OrderRefundAgreeOrRejectReq req,List<Map<String, Object>> getData_Order,List<Map<String, Object>> getData_Order_detail)
    {
        try
        {
            DCP_OrderRefundAgreeOrRejectReq.levelRequest request = req.getRequest();
            String eId = request.geteId();
            String orderNo = request.getOrderNo();
            String refundReasonName = request.getRefundReasonName();

            String modifyBy = req.getRequest().getOpNo();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());

            // 配送时间
            String shipDate = getData_Order.get(0).get("SHIPDATE").toString();
            String shipStartTime = getData_Order.get(0).get("SHIPSTARTTIME").toString();
            shipStartTime = shipStartTime.replace("-", "");
            if (shipStartTime.isEmpty()) {
                shipStartTime = new SimpleDateFormat("HHmmss").format(new Date());
            }
            String shipStartDateTime = shipDate + shipStartTime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

            // 如果查询有值 则同步单据状态 修改 ISREFUND = N , refundReasonName 退单原因名称
            //更新原单
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_PROCESSTASK");
            ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
            ub1.addUpdateValue("OSTATUS", new DataValue("12", Types.VARCHAR));
            ub1.addUpdateValue("ISREFUND", new DataValue("N", Types.VARCHAR));
            ub1.addUpdateValue("REFUNDREASONNAME", new DataValue(refundReasonName, Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //更新原单
            ub1 = new UptBean("dcp_product_sale");
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //写零售生产单头
            String[] columns_productSale = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","TRNO",
                    "TABLENO", "REPASTTYPE", "DINNERSIGN", "GUESTNUM","PRODUCTSTATUS", "MEMO",
                    "ISTAKEOUT", "CHANNELID","APPTYPE" ,"LOADDOCTYPE","WXOPENID"
                    ,"ORDERTIME","ADULTCOUNT", "SHIPENDTIME","ISBOOK"};
            //
            DataValue[] insValue_productSale = new DataValue[] {
                    new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                    new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                    new DataValue("REFUNDORDER", Types.VARCHAR),
                    new DataValue("RE"+orderNo, Types.VARCHAR),
                    new DataValue(orderNo, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEMO").toString(), Types.VARCHAR),
                    new DataValue("Y", Types.VARCHAR),
                    new DataValue(req.getRequest().getChannelId(), Types.VARCHAR),
                    new DataValue(req.getRequest().getLoadDocType(), Types.VARCHAR),
                    new DataValue(req.getRequest().getLoadDocType(), Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("OUTSELID").toString(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue(shipStartDateTime, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("ISBOOK").toString(), Types.VARCHAR),
            };
            InsBean ib_productSale = new InsBean("dcp_product_sale", columns_productSale);
            ib_productSale.addValues(insValue_productSale);
            this.addProcessData(new DataProcessBean(ib_productSale));

            int v_beforeItem=0;

            BigDecimal v_TotRemainQty=new BigDecimal("0");

            //产生单号
            String v_BeforeProcessTaskNO=this.getProcessTaskNO(eId,req.getRequest().getShopId());
            //退单用,处理加回商品数量到新的预制单

            String sql_beforDish_UseQty="select a.oitem,nvl(a.useqty,0) USEQTY " +
                    "from DCP_BEFOREDISHTASK a " +
                    "inner join dcp_processtask b on a.eid=b.eid and a.shopid=b.shopid and a.billno=b.processtaskno " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+req.getRequest().getShopId()+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and b.otype='BEFORE' ";
            String sql_processDetail_cookQty=" select a.oitem,nvl(a.pqty,0) PQTY from dcp_processtask_detail a " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+req.getRequest().getShopId()+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and a.goodsstatus in ('2','3') ";

            List<Map<String,Object>> getData_beforDish_UseQty=this.doQueryData(sql_beforDish_UseQty, null);
            List<Map<String,Object>> getData_processDetail_cookQty=this.doQueryData(sql_processDetail_cookQty, null);


            String[] columns_Processtask_Detail = {
                    "EID", "SHOPID", "ORGANIZATIONNO", "PROCESSTASKNO", "ITEM", "MUL_QTY", "PQTY",  "PUNIT",
                    "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                    "GOODSSTATUS", "FINALCATEGORY", "PLUBARCODE", "AVAILQTY"
            };


            // 写单头
            String[] columns_Processtask = {
                    "SHOPID", "PROCESSTASKNO", "EID", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE", "CREATEBY",
                    "STATUS", "TOT_CQTY", "PROCESS_STATUS", "BDATE", "TOT_PQTY", "MEMO", "UPDATE_TIME", "WAREHOUSE",
                    "MATERIALWAREHOUSE", "OTYPE", "CREATEDATETIME", "TOT_AMT", "TOT_DISTRIAMT"
            };


            //退的明细
            for (Map<String, Object> map_order_detail : getData_Order_detail)
            {
                //更新原单
                ub1 = new UptBean("DCP_PRODUCT_DETAIL");
                ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
                ub1.addUpdateValue("REFUNDQTY", new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR));
                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
                ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                //是否有备注
                String isMemo=map_order_detail.get("ISMEMO").toString();
                StringBuffer dMemo=new StringBuffer("");
                if (isMemo.equals("Y"))
                {
                    String order_detail_memo_SQL="select  * from dcp_order_detail_memo where eid='"+req.getRequest().geteId()+"' and orderno='"+orderNo+"' and oitem="+map_order_detail.get("ITEM").toString()+" ";
                    List<Map<String, Object>> getData_order_detail_memo =this.doQueryData(order_detail_memo_SQL,null);
                    if (getData_order_detail_memo != null && getData_order_detail_memo.size()>0)
                    {
                        for (Map<String, Object> map_detail_memo : getData_order_detail_memo)
                        {
                            dMemo.append(map_detail_memo.get("MEMO").toString()+",");
                        }
                        if (dMemo.length()>0)
                        {
                            dMemo.deleteCharAt(dMemo.length()-1);
                        }
                    }
                }
                String packageMitem=map_order_detail.get("PACKAGEMITEM").toString();
                if (Check.Null(packageMitem))
                {
                    packageMitem="0";
                }
                //写零售生产单身
                String[] columns_productSale_detail = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","OITEM",
                        "PLUNO", "PLUNAME", "PLUBARCODE", "QTY","SPECNAME", "UNITID",
                        "UNITNAME", "FLAVORSTUFFDETAIL","ISPACKAGE" ,"PGOODSDETAIL","GOODSSTATUS"
                        ,"REPASTTYPE","MEMO","ISURGE","FINALCATEGORY","REFUNDQTY","ATTRNAME","PACKAGEMITEM"};
                //
                DataValue[] insValue_productSale_detail = new DataValue[] {
                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                        new DataValue("REFUNDORDER", Types.VARCHAR),
                        new DataValue("RE"+orderNo, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("QTY").toString(), Types.FLOAT),
                        new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2")?"Y":"N", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(dMemo.toString(), Types.VARCHAR),
                        new DataValue("N", Types.VARCHAR),
                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                        new DataValue(0, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ATTRNAME").toString(), Types.VARCHAR),
                        new DataValue(packageMitem, Types.VARCHAR),
                };
                InsBean ib_productSale_detail = new InsBean("dcp_product_detail", columns_productSale_detail);
                ib_productSale_detail.addValues(insValue_productSale_detail);
                this.addProcessData(new DataProcessBean(ib_productSale_detail));


                //加工任务明细，只更新，不新增
                //更新退的字段值
                //这里注意1个问题，A商品2条，第一条4个，第二条2，买6个现在要退5个，要处理成：第一条更新退4个，第二条更新退1个
                String processTask_detail_SQL="select  * from dcp_processtask_detail where eid='"+req.getRequest().geteId()+"' and OFNO='"+orderNo+"' and OITEM="+map_order_detail.get("ITEM").toString()+" order by OITEM ";
                List<Map<String, Object>> getData_processTask_detail =this.doQueryData(processTask_detail_SQL,null);

                BigDecimal m_qty=new BigDecimal(map_order_detail.get("RQTY").toString());
                for (Map<String, Object> map_of_detail : getData_processTask_detail)
                {
                    BigDecimal refundQty=new BigDecimal(0);
                    if (m_qty.compareTo(new BigDecimal(map_of_detail.get("PQTY").toString()))>0)
                    {
                        refundQty=new BigDecimal(map_of_detail.get("PQTY").toString());//此行全退
                        m_qty=m_qty.subtract(refundQty);
                    }
                    else
                    {
                        refundQty=m_qty;//退前面剩余的部分
                        m_qty=new BigDecimal(0);
                    }

                    ub1 = new UptBean("dcp_processtask_detail");
                    ub1.addUpdateValue("ISREFUNDORDER", new DataValue(req.getRequest().getRefundType().equals("0")?"0":"1", Types.VARCHAR));
                    ub1.addUpdateValue("REFUNDQTY", new DataValue(refundQty.doubleValue(), Types.VARCHAR));
                    //condition
                    ub1.addCondition("EID", new DataValue(map_of_detail.get("EID").toString(), Types.VARCHAR));
                    ub1.addCondition("OFNO", new DataValue(map_of_detail.get("OFNO").toString(), Types.VARCHAR));
                    ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(map_of_detail.get("ITEM").toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }


                //退单直接产生预制单，把预制菜数量加回去。
                //等于是说普通单子做出多余的也自动算成预制菜，没做的菜就不用做了
                //举例：来单共7份，预制菜2份，任务明细4份做了，1份没做，现在要退5份，怎么办?
                //预制菜份数(占用了，要加回来)+ 做好份数-实际购买的数量
                //公式：预制菜(2)+任务明细做好的(4)-( (来单数量(7)-退单数量(5) )=6-2=4个
                //预制菜占用表还要同BILLNO加工任务单号去关联加工任务单，取预制单的，因为普通的也写这个占用表了

                //退菜加回预制菜库存：
                //上面是对原单更新，现在要将预制菜占用及加工好的商品自动产生一张预制菜库存

                //双拼菜标记
                String isDoubleGoods = map_order_detail.get("ISDOUBLEGOODS").toString();

                //预制菜份数占用
                BigDecimal bdm_o_beforDish_UseQty=new BigDecimal("0");
                if (getData_beforDish_UseQty != null && getData_beforDish_UseQty.size()>0)
                {
                    List<Map<String,Object>> temp_beforDish_UseQty= getData_beforDish_UseQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_beforDish_UseQty != null && temp_beforDish_UseQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_beforDish_UseQty=temp_beforDish_UseQty.stream().map(p -> new BigDecimal(p.get("USEQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
                //任务明细做好的
                BigDecimal bdm_o_processDetail_cookQty=new BigDecimal("0");
                if (getData_processDetail_cookQty != null && getData_processDetail_cookQty.size()>0)
                {
                    List<Map<String,Object>> temp_processDetail_cookQty= getData_processDetail_cookQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_processDetail_cookQty != null && temp_processDetail_cookQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_processDetail_cookQty=temp_processDetail_cookQty.stream().map(p -> new BigDecimal(p.get("PQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }

                //公式计算结果:这里全退
                BigDecimal v_canUseQty=bdm_o_beforDish_UseQty.add(bdm_o_processDetail_cookQty);

                //大于0就新产生预制菜明细
                if (v_canUseQty.compareTo(BigDecimal.ZERO)>0)
                {

                    //*************************************双拼菜，使用BOM商品同步************************************
                    if (isDoubleGoods != null && isDoubleGoods.equals("Y"))
                    {
                        //双拼菜数量除除2，因为加工明细里面对应的OITEM有双拼菜主菜+子菜 ，数量翻倍了
                        v_canUseQty=v_canUseQty.divide(new BigDecimal("2"),2,RoundingMode.HALF_UP);

                        StringBuffer sb_bom=new StringBuffer("        select a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty," +
                                                                     "        c.material_pluno,c.qty,c.material_unit,c.material_qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid," +
                                                                     "        mgl.plu_name as materialPluName,mul.uname as materialUnitName,mu.udlength as materialUnitLength," +
                                                                     "        mg.baseunit as materialBaseUnit,mbul.uname as materialBaseUnitName,mgu.unitratio as materialUnitRatio," +
                                                                     "        mg.isbatch as materialIsBatch,mg.price,mg.category,kc.unside,kc.uncook,kc.uncall, " +
                                                                     "        hqkc.unside hq_unside,hqkc.uncook hq_uncook,hqkc.uncall hq_uncall " +
                                                                     "        from (" +
                                                                     "        select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.* from dcp_bom a" +
                                                                     "        left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+req.getRequest().getShopId()+"'" +
                                                                     "        where a.eId='"+eId+"' and a.effdate <=trunc(sysdate) and a.status='100' and a.bomtype = '0'" +
                                                                     "        and a.pluno='"+map_order_detail.get("PLUNO").toString() +"' and a.unit='"+map_order_detail.get("SUNIT").toString()+"'" +
                                                                     "        and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))" +
                                                                     "        )a" +
                                                                     "        inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and c.material_bdate <=trunc(sysdate) and material_edate >=trunc(sysdate)" +
                                                                     "        left  join dcp_goods_lang mgl on mgl.eid=a.eid and mgl.pluno=c.material_pluno and mgl.lang_type='"+req.getLangType()+"'" +
                                                                     "        left  join dcp_unit_lang mul on mul.eid=a.eid and mul.unit=c.material_unit and mul.lang_type='"+req.getLangType()+"'" +
                                                                     "        left  join dcp_unit mu on mu.eid=a.eid and mu.unit=c.material_unit and mu.status='100'" +
                                                                     "        inner join dcp_goods mg on mg.eid=a.eid and mg.pluno=c.material_pluno and mg.status='100'" +
                                                                     "        left  join dcp_unit_lang mbul on mbul.eid=a.eid and mbul.unit=mg.baseunit and mbul.lang_type='"+req.getLangType()+"'" +
                                                                     "        inner join dcp_goods_unit mgu on mgu.eid =a.eid and mgu.pluno=c.material_pluno and mgu.ounit=c.material_unit and mgu.unit=mg.baseunit" +
                                                                     "        left join DCP_KDSDISHES_CONTROL kc on a.eid=kc.eid and kc.shopid='"+req.getRequest().getShopId()+"' and c.material_pluno=kc.pluno " +
                                                                     "        left join dcp_hqkdsdishes_control hqkc on a.eid = hqkc.eid and ((hqkc.goodstype=2 and  c.material_pluno=hqkc.id) or (hqkc.goodstype=1 and mg.category=hqkc.id)) " +
                                                                     "        where a.rn=1" +
                                                                     "        order by a.pluno,c.sortid");

                        List<Map<String, Object>> temp_Bom = this.doQueryData(sb_bom.toString(), null);

                        if (temp_Bom != null && temp_Bom.size()>0)
                        {
                            for (Map<String, Object> map_Bom : temp_Bom)
                            {

                                v_beforeItem+=1;
                                //累加总的
                                v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);

                                BigDecimal bdm_remainQty=v_canUseQty.multiply(new BigDecimal(Convert.toDouble(map_Bom.get("MATERIAL_QTY").toString(), 1d))).divide(new BigDecimal(Convert.toDouble(map_Bom.get("QTY").toString(), 1d)), 2, RoundingMode.HALF_UP);

                                DataValue[] insValueDetail = new DataValue[]
                                        {
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                                new DataValue(v_beforeItem, Types.VARCHAR),
                                                new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                                new DataValue(bdm_remainQty, Types.VARCHAR), // 数量 以单份维度存储
                                                new DataValue(map_Bom.get("MATERIAL_UNIT").toString(), Types.VARCHAR),
                                                new DataValue(bdm_remainQty.multiply(new BigDecimal(Convert.toStr(map_Bom.get("MATERIALUNITRATIO")))).doubleValue(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIAL_PLUNO").toString(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALPLUNAME").toString(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALBASEUNIT").toString(), Types.VARCHAR),
                                                new DataValue("1", Types.VARCHAR), // 单位转换率
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(modifyDate, Types.VARCHAR),//当天
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                                new DataValue(map_Bom.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                                new DataValue("", Types.VARCHAR), // 条码
                                                new DataValue(bdm_remainQty, Types.VARCHAR) // 剩余可用数量
                                        };
                                InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                                ib1.addValues(insValueDetail);
                                this.addProcessData(new DataProcessBean(ib1));
                            }
                        }

                    }
                    else
                    {
                        v_beforeItem+=1;
                        //累加总的
                        v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);

                        DataValue[] insValueDetail = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                        new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                        new DataValue(v_beforeItem, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                        new DataValue(v_canUseQty, Types.VARCHAR), // 数量 以单份维度存储
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue("1", Types.VARCHAR), // 单位转换率
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(modifyDate, Types.VARCHAR),//当天
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR), // 条码
                                        new DataValue(v_canUseQty, Types.VARCHAR) // 剩余可用数量
                                };
                        InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                        ib1.addValues(insValueDetail);
                        this.addProcessData(new DataProcessBean(ib1));
                    }



                }



            }


            //说明有明细，再添加单头
            if (v_beforeItem >0)
            {
                String sql_out_cost_warehouse="select OUT_COST_WAREHOUSE from DCP_ORG where eid = '"+eId+"' and ORGANIZATIONNO = '"+req.getRequest().getShopId()+"' ";
                List<Map<String, Object>> getOut_cost_warehouse = this.doQueryData(sql_out_cost_warehouse, null);
                String out_cost_warehouse = "";
                if(!CollectionUtils.isEmpty(getOut_cost_warehouse)){
                    out_cost_warehouse =  getOut_cost_warehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
                }

                DataValue[] insValue = new DataValue[]
                        {
                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(req.getRequest().getShopId(), Types.VARCHAR),
                                new DataValue(modifyTime, Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(modifyBy, Types.VARCHAR),
                                new DataValue("6", Types.VARCHAR), // status 默认6
                                new DataValue(v_beforeItem, Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(v_TotRemainQty, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
                                new DataValue("BEFORE", Types.VARCHAR), // 单据类型 此处为预制单 Before
                                new DataValue(modifyDate+modifyTime, Types.VARCHAR), // 生产日期
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                        };

                InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
                ib2.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib2));
            }


            this.doExecuteDataToDB();
            HelpTools.writelog_waimai("【用户申请退单同意成功,更新加工任务档 DCP_ProcessTask 成功】" + " 订单号orderNO:" + orderNo);
        }
        catch (Exception e)
        {

        }
    }

    /**
     * 是否存在部分订转销
     * @param eId
     * @param orderNo
     * @return
     * @throws Exception
     */
    private boolean isHasPartOrderToSale(String eId,String orderNo) throws Exception
    {
        boolean nRet = false;
        try
        {
            String sql = "select * from ( " +
                    " select sum(QTY) QTY , sum(PICKQTY) PICKQTY from dcp_order_detail where EID='"+eId+"' and ORDERNO='"+orderNo+"' " +
                    " ) where PICKQTY>0 and QTY<>PICKQTY  ";
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData!=null&&!getQData.isEmpty())
            {
                nRet = true;
            }
        }
        catch (Exception e)
        {

        }


        return nRet;
    }


    private String getProcessTaskNO(String eId,String shopId) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String processTaskNO = null;
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
    }



}
