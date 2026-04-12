package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusModifyReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_OrderShippingRes;
import com.dsc.spos.json.cust.res.DCP_OrderStatusModifyRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 订单状态更新
 * @author: wangzyc
 * @create: 2022-01-25
 */
public class DCP_OrderStatusModify extends SPosAdvanceService<DCP_OrderStatusModifyReq, DCP_OrderStatusModifyRes> {
    @Override
    protected void processDUID(DCP_OrderStatusModifyReq req, DCP_OrderStatusModifyRes res) throws Exception {
        DCP_OrderStatusModifyReq.level1Elm request = req.getRequest();
        String eId = request.getEId();
        // 优先取传参中的企业id 入参未传入则取 token\sign中的企业Id
        if(Check.Null(eId)){
            eId = req.geteId();
        }
        if (eId==null||eId.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        req.seteId(eId);
        req.getRequest().setEId(eId);
        
        String fileName="";
        DCP_OrderShipping dcp_OrderShipping = null;
        ParseJson pj = null;
        DCP_OrderShippingRes dcp_OrderShipping_Res = null;
        try {
            String orderNo = request.getOrderNo();
            String status = request.getStatus();
            String deliverystatus = request.getDeliverystatus();

            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from DCP_order where EID='" + eId + "'");
            sqlbuf.append(" and orderno='" + orderNo + "'");
            String sql = sqlbuf.toString();
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
            if (getQDataDetail == null || getQDataDetail.isEmpty())
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("订单["+orderNo+"]不存在!");
                return;
            }
            
            Map<String, Object> orderMap=getQDataDetail.get(0);
            String channelId = orderMap.get("CHANNELID")==null?"":orderMap.get("CHANNELID").toString();
            String loadDocType = orderMap.get("LOADDOCTYPE")==null?"":orderMap.get("LOADDOCTYPE").toString();
            String loadDocBillType = orderMap.get("LOADDOCBILLTYPE")==null?"":orderMap.get("LOADDOCBILLTYPE").toString();
            String loadDocOrderNo = orderMap.get("LOADDOCORDERNO")==null?"":orderMap.get("LOADDOCORDERNO").toString();
            String deliverystatusOld = orderMap.get("DELIVERYSTATUS")==null?"":orderMap.get("DELIVERYSTATUS").toString();
            String statusOld = orderMap.get("STATUS")==null?"":orderMap.get("STATUS").toString();
            String shipType = orderMap.get("SHIPTYPE")==null?"":orderMap.get("SHIPTYPE").toString();
            
            if(orderLoadDocType.YOUZAN.equals(loadDocType)){
            	fileName=DCP_OrderStatusModify.class.getSimpleName()+"_"+orderLoadDocType.YOUZAN;
            }else if(orderLoadDocType.XIAOYOU.equals(loadDocType)){
            	fileName=DCP_OrderStatusModify.class.getSimpleName()+"_"+orderLoadDocType.XIAOYOU;
            }if(orderLoadDocType.QIMAI.equals(loadDocType)){
            	fileName=DCP_OrderStatusModify.class.getSimpleName()+"_"+orderLoadDocType.QIMAI;
            }
            
            Log(fileName, com.alibaba.fastjson.JSON.toJSONString(req));
            if (req.getRequest().getOpName()==null||req.getRequest().getOpName().isEmpty())
            {
                req.getRequest().setOpName("渠道("+loadDocType+")操作人员");
            }
            
            if(orderLoadDocType.YOUZAN.equals(loadDocType)){
            	if(status!=null&&status.length()>0){
//            		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道["+loadDocType+"]不支持修改订单状态!");
//            		res.setServiceDescription("渠道["+loadDocType+"]暂不支持修改订单状态,状态修改请求无效!");
//            		status="";
            		//允许更新为已完成
            		if("11".equals(status)){
            			
            		}else{
            			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道["+loadDocType+"]不支持修改订单状态为["+status+"-"+HelpTools.GetOrderStatusName("1", status, null)+"]!");
            		}
            	}
            }

            UptBean ubecOrder=new UptBean("DCP_ORDER");
            ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
			ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));


            
            Boolean orderToSale=false;

            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            
            StringBuilder sbDes = new StringBuilder();
            // 写下订单修改日志
            if(status!=null&&status.length()>0){

                String statusType="1";//订单状态
                StringBuilder statusTypeName = new StringBuilder();
                String statusName=HelpTools.GetOrderStatusName(statusType, status, statusTypeName);
                String statusOldName=HelpTools.GetOrderStatusName(statusType, statusOld, null);
            	if("3".equals(statusOld)){
            		sbDes.append("订单["+orderNo+"]当前"+statusTypeName+"["+statusOld+"-"+statusOldName+"]不允许修改,订单状态修改参数无效!");
            		status="";
            	}
            	else if("11".equals(statusOld)){
            		sbDes.append("订单["+orderNo+"]当前"+statusTypeName+"["+statusOld+"-"+statusOldName+"]不允许修改,订单状态修改参数无效!");
            		status="";
            	}
            	else if("12".equals(statusOld)){
            		sbDes.append("订单["+orderNo+"]当前"+statusTypeName+"["+statusOld+"-"+statusOldName+"]不允许修改,订单状态修改参数无效!");
            		status="";
            	}
            	if(statusOld.equals(status)){
            		sbDes.append("订单["+orderNo+"]当前"+statusTypeName+"为["+statusOld+"-"+statusOldName+"],订单状态修改参数无效!");
            		status="";
            	}
            }
            
            if(deliverystatus!=null&&deliverystatus.length()>0){
            	String statusType="2";//配送状态
                StringBuilder statusTypeName = new StringBuilder();
                String statusName=HelpTools.GetOrderStatusName(statusType, deliverystatus, statusTypeName);
                if(deliverystatusOld.equals(deliverystatus)){
                	sbDes.append("订单["+orderNo+"]当前"+statusTypeName+"为["+deliverystatus+"-"+statusName+"],配送状态修改参数无效!");
            	}
            }
            

            res.setSuccess(true);
            res.setServiceStatus("000");
            if(sbDes!=null&&sbDes.toString().length()>0){
            	res.setServiceDescription("服务执行成功!"+sbDes.toString());
            }else{
            	res.setServiceDescription("服务执行成功!");
            }
            
            
            // 写下订单修改日志
            if(status!=null&&status.length()>0){

                String statusType="1";//订单状态
                StringBuilder statusTypeName = new StringBuilder();
                String statusName=HelpTools.GetOrderStatusName(statusType, status, statusTypeName);
            	//11-已完成  做自动订转销
            	if("11".equals(status)&&!orderLoadDocType.XIAOYOU.equals(loadDocType)){
            		orderToSale=true;
            		if (orderLoadDocType.QIMAI.equals(loadDocType)&&"5".equals(shipType))
                    {
                        //【ID1030772】【3.0 嘉华】 总部配送订单 第三方状态变更优化
                        //增加逻辑，当订单状态为11的时候，如果是企迈渠道订单，且是总部配送 SHIPTYPE枚举值是5；不在做订转销
                        orderToSale=false;
                    }
            	}
            	
            	ubecOrder.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);

                onelv1.setNeed_callback("N");
                onelv1.setNeed_notify("N");

                onelv1.seteId(eId);

                String opNO = req.getRequest().getOpNo() == null ? "" : req.getRequest().getOpNo();

                String o_opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();

                onelv1.setOpNo(opNO);
                onelv1.setOpName(o_opName);
                onelv1.setOrderNo(orderNo);
                onelv1.setLoadDocBillType(loadDocBillType);
                onelv1.setLoadDocOrderNo(loadDocOrderNo);


                onelv1.setStatusType(statusType);
                onelv1.setStatusTypeName(statusTypeName.toString());
                onelv1.setStatus(status);
                onelv1.setStatusName(statusName);
                
                String statusNameOld=HelpTools.GetOrderStatusName(statusType, statusOld, statusTypeName);
                StringBuffer memo = new StringBuffer("");
                memo.append( statusNameOld + "-->" + statusName + "<br>");
                if ("11".equals(status)&&orderToSale==false)
                {
                    memo.append("不需要自动订转销<br>");
                }
                onelv1.setMemo(memo.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);
            }
            
            
			//更新下配送员电话以及手机
            if(request.getDelName()!=null&&request.getDelName().length()>0){
            	ubecOrder.addUpdateValue("DELNAME", new DataValue(request.getDelName(), Types.VARCHAR));
            }
            if(request.getDelTelephone()!=null&&request.getDelTelephone().length()>0){
            	ubecOrder.addUpdateValue("DELTELEPHONE", new DataValue(request.getDelTelephone(), Types.VARCHAR));
            }
			if (request.getDeliveryType()!=null&&request.getDeliveryType().length()>0){
				ubecOrder.addUpdateValue("DELIVERYTYPE", new DataValue(request.getDeliveryType(), Types.VARCHAR));
            }
			if (request.getDeliveryNo()!=null&&request.getDeliveryNo().length()>0){
				
				ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(request.getDeliveryNo(), Types.VARCHAR));
			}
			
			
            if(deliverystatus!=null&&deliverystatus.length()>0){
            	String statusType="2";//配送状态
                StringBuilder statusTypeName = new StringBuilder();
                String statusName=HelpTools.GetOrderStatusName(statusType, deliverystatus, statusTypeName);
                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(deliverystatus, Types.VARCHAR));
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);

                onelv1.setNeed_callback("N");
                onelv1.setNeed_notify("N");

                onelv1.seteId(eId);

                String opNO = req.getRequest().getOpNo() == null ? "" : req.getRequest().getOpNo();

                String o_opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();

                onelv1.setOpNo(opNO);
                onelv1.setOpName(o_opName);
                onelv1.setOrderNo(orderNo);
                onelv1.setLoadDocBillType(loadDocBillType);
                onelv1.setLoadDocOrderNo(loadDocOrderNo);

                

                onelv1.setStatusType(statusType);
                onelv1.setStatusTypeName(statusTypeName.toString());
                onelv1.setStatus(deliverystatus);
                onelv1.setStatusName(statusName);
                
                String statusNameOld=HelpTools.GetOrderStatusName(statusType, deliverystatusOld, statusTypeName);
                StringBuffer memo = new StringBuffer("");
                memo.append( statusNameOld + "-->" + statusName + "<br>");
                onelv1.setMemo(memo.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);
            }

            this.addProcessData(new DataProcessBean(ubecOrder));
            
            if(orderToSale){
            	//1.深拷贝一份源服务请求
                pj = new ParseJson();
                String jsonReq = pj.beanToJson(req);
                DCP_OrderShippingReq dcp_OrderShippingReq = pj.jsonToBean(jsonReq, new TypeToken<DCP_OrderShippingReq>() {
                });
                //2.目标服务部分字段需重新给值
                dcp_OrderShippingReq.setServiceId("DCP_OrderShipping");
                dcp_OrderShippingReq.getRequest().setOrderList(new String[]{orderNo});
                dcp_OrderShippingReq.getRequest().setOpType("1");
                //3.调用目标服务
                dcp_OrderShipping = new DCP_OrderShipping();
                dcp_OrderShipping_Res = new DCP_OrderShippingRes();
                dcp_OrderShipping.setDao(this.dao);
                dcp_OrderShipping.processDUID(dcp_OrderShippingReq, dcp_OrderShipping_Res);
                //订转销不成功  抛出异常
                if(!dcp_OrderShipping_Res.isSuccess()){
//                	res.setSuccess(dcp_OrderShipping_Res.isSuccess());
//                    res.setServiceStatus(dcp_OrderShipping_Res.getServiceStatus());
//                    res.setServiceDescription(dcp_OrderShipping_Res.getServiceDescription());
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, dcp_OrderShipping_Res.getServiceDescription());
                }
            }
            this.doExecuteDataToDB();
            
            if(orderStatusLogList!=null&&orderStatusLogList.size()>0){
            	StringBuilder errorMessage = new StringBuilder();
            	try
            	{
            		boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
            	}
            	catch (Exception e)
            	{
            		
            	}
            }

            this.pData.clear();
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("订单状态修改失败：" + e.getMessage());
        }finally{
            dcp_OrderShipping = null;
            pj = null;
            dcp_OrderShipping_Res = null;
        	Log(fileName, com.alibaba.fastjson.JSON.toJSONString(res));
        }

    }
    
    public void Log(String fileName,String log) throws Exception {
    	if(fileName!=null&&fileName.trim().length()>0){
    		HelpTools.writelog_fileName(log, fileName);
    	}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderStatusModifyReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderStatusModifyReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderStatusModifyReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderStatusModifyReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_OrderStatusModifyReq.level1Elm request = req.getRequest();
        if(request==null){
        	errMsg.append("request节点不可为空, ");
    		isFail = true;
        }else{
        	if(Check.Null(request.getOrderNo())){
        		errMsg.append("订单号不可为空值, ");
        		isFail = true;
        	}
        	if(Check.Null(request.getStatus())&&Check.Null(request.getDeliverystatus())){
        		errMsg.append("单据状态/物流状态不可都为空值, ");
        		isFail = true;
        	}
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderStatusModifyReq> getRequestType() {
        return new TypeToken<DCP_OrderStatusModifyReq>(){};
    }

    @Override
    protected DCP_OrderStatusModifyRes getResponseType() {
        return new DCP_OrderStatusModifyRes();
    }
}
