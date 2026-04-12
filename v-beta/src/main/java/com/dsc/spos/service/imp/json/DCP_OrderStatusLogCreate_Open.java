package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatusLogCreate_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

/**
 * DCP_OrderStatusLogCreate_Open
 * 供CRM调用  同步操作日志
 */
public class DCP_OrderStatusLogCreate_Open extends SPosAdvanceService<DCP_OrderStatusLogCreate_OpenReq, DCP_OrderStatusLogCreate_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderStatusLogCreate_OpenReq req, DCP_OrderStatusLogCreate_OpenRes res) throws Exception {
    	String logStartStr = "【调用DDCP_OrderStatusLogCreate_Open接口】";
    	try{
    		DCP_OrderStatusLogCreate_OpenReq.level1Elm request = req.getRequest();
    		String eId = request.getEId();
    		String orderNo = request.getOrderNo();
    		String sql = " select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
    		HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"  查询语句："+sql);
    		Map<String, Object> ordermap=new HashMap<String, Object>();
    		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
    		if (getQData != null && getQData.size()>0){
    			ordermap=getQData.get(0);
    		}else{
    			HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+",该订单不存在！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"不存在 ！");
    		}
    		
    		String loadDocType = (ordermap.get("LOADDOCTYPE")!=null&&ordermap.get("LOADDOCTYPE").toString().length()>0)?ordermap.get("LOADDOCTYPE").toString():"";
    		String channelId = (ordermap.get("CHANNELID")!=null&&ordermap.get("CHANNELID").toString().length()>0)?ordermap.get("CHANNELID").toString():"";
    		String shopId = (ordermap.get("SHOP")!=null&&ordermap.get("SHOP").toString().length()>0)?ordermap.get("SHOP").toString():"";
    		String opNo=request.getOpNo();
    		String opName=request.getOpName();
    		String description=request.getDescription();
    		String statusType=request.getStatusType();
    		String status=request.getStatus();
    		
    		{
    			//写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId);
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopId);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                onelv1.setStatusType(statusType);
                onelv1.setStatus(status);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, status, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                if ("1".equals(statusType))
                {
                    if ("88".equals(status))
                    {
                        statusName = "其他";
                        onelv1.setStatusName(statusName);
                    }
                }
                onelv1.setMemo(description);
                onelv1.setDisplay("1");
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai(logStartStr+"【写表dcp_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                    		logStartStr+"【写表dcp_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
    		}
    			
    		res.setSuccess(true);
    		res.setServiceStatus("000");
    		res.setServiceDescription("服务执行成功!");
    	} catch (Exception e) {
    		res.setSuccess(false);
    		res.setServiceStatus("200");
    		res.setServiceDescription("服务执行失败!" + e.getMessage());
    	}
    	
        
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderStatusLogCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderStatusLogCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderStatusLogCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderStatusLogCreate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_OrderStatusLogCreate_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getEId())) {
            errMsg.append("企业编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getOrderNo())) {
            errMsg.append("订单号不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderStatusLogCreate_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderStatusLogCreate_OpenReq>() {
        };
    }

    @Override
    protected DCP_OrderStatusLogCreate_OpenRes getResponseType() {
        return new DCP_OrderStatusLogCreate_OpenRes();
    }
}
