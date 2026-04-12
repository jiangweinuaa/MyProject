package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderCostUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderCostUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_OrderCostUpdate
 * 服务说明：订单超区费，配送费，加急费修改
 * @author jinzma
 * @since  2023-12-06
 */
public class DCP_OrderCostUpdate extends SPosAdvanceService<DCP_OrderCostUpdateReq, DCP_OrderCostUpdateRes> {
    @Override
    protected void processDUID(DCP_OrderCostUpdateReq req, DCP_OrderCostUpdateRes res) throws Exception {
        try {
            StringBuffer logmemo = new StringBuffer();
            String eId = req.geteId();
            String orderNo = req.getRequest().getOrderNo();
            String sql = " select orderno,loaddoctype,channelid,loaddocbilltype,loaddocorderno,deliverymoney,superzonemoney,urgentmoney "
                    + " from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (CollectionUtil.isEmpty(getQData)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在: "+orderNo);
            }
            String loadDocType = getQData.get(0).get("LOADDOCTYPE").toString();
            String channelId = getQData.get(0).get("CHANNELID").toString();
            String loadDocBillType = getQData.get(0).get("LOADDOCBILLTYPE").toString();
            String loadDocOrderNo = getQData.get(0).get("LOADDOCORDERNO").toString();
            String deliveryMoney = getQData.get(0).get("DELIVERYMONEY").toString();       //配送费
            String superZoneMoney = getQData.get(0).get("SUPERZONEMONEY").toString();     //超区费
            String urgentMoney = getQData.get(0).get("URGENTMONEY").toString();           //加急费
            
            UptBean ub = new UptBean("DCP_ORDER");
            if (PosPub.isNumericType(req.getRequest().getDeliveryMoney())) {
                if(!req.getRequest().getDeliveryMoney().equals(deliveryMoney)) {
                    ub.addUpdateValue("DELIVERYMONEY", new DataValue(req.getRequest().getDeliveryMoney(), Types.VARCHAR));
                    logmemo.append( "配送费：" + deliveryMoney + "-->" + req.getRequest().getDeliveryMoney() + "<br>");
                }
            }
            if (PosPub.isNumericType(req.getRequest().getSuperZoneMoney())) {
                if(!req.getRequest().getSuperZoneMoney().equals(superZoneMoney)) {
                    ub.addUpdateValue("SUPERZONEMONEY", new DataValue(req.getRequest().getSuperZoneMoney(), Types.VARCHAR));
                    logmemo.append( "超区费：" + superZoneMoney + "-->" + req.getRequest().getSuperZoneMoney() + "<br>");
                }
            }
            if (PosPub.isNumericType(req.getRequest().getUrgentMoney())) {
                if(!req.getRequest().getUrgentMoney().equals(urgentMoney)) {
                    ub.addUpdateValue("URGENTMONEY", new DataValue(req.getRequest().getUrgentMoney(), Types.VARCHAR));
                    logmemo.append( "加急费：" + urgentMoney + "-->" + req.getRequest().getUrgentMoney() + "<br>");
                }
            }
            
            // condition
            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            
            this.addProcessData(new DataProcessBean(ub));
            
            this.doExecuteDataToDB();
            
            //写下日志
            if (logmemo.length()>1) {
                try {
                    List<orderStatusLog> orderStatusLogList = new ArrayList<>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setNeed_callback("N");
                    onelv1.setNeed_notify("N");
                    onelv1.seteId(eId);
                    onelv1.setOpNo(req.getOpNO());
                    onelv1.setOpName(req.getOpName());
                    onelv1.setOrderNo(orderNo);
                    onelv1.setLoadDocBillType(loadDocBillType);
                    onelv1.setLoadDocOrderNo(loadDocOrderNo);
                    onelv1.setStatusType("99");
                    onelv1.setStatus("99");
                    String statusName_log = "订单修改";
                    String statusTypeName_log = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName_log);
                    onelv1.setStatusName(statusName_log);
                    
                    StringBuffer memo = new StringBuffer();
                    memo.append(statusTypeName_log + "-->" + statusName_log + "<br>");
                    memo.append(logmemo);
                    onelv1.setMemo(memo.toString());
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    orderStatusLogList.add(onelv1);
                    
                    StringBuilder errorMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
                    if (nRet) {
                        HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else {
                        HelpTools.writelog_waimai(
                                "【写表DCP_orderStatuslog异常】" + errorMessage + " 订单号orderNO:" + orderNo);
                    }
                    
                } catch (Exception e) {
                    HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.getMessage() + " 订单号orderNO:" + orderNo);
                }
      
            }
            
          
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderCostUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderCostUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderCostUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_OrderCostUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (Check.Null(req.getRequest().getOrderNo())) {
            errMsg.append("订单编号不能为空,");
            isFail = true;
        }
        
        
        String deliveryMoney = req.getRequest().getDeliveryMoney();
        String superZoneMoney = req.getRequest().getSuperZoneMoney();
        String urgentMoney = req.getRequest().getUrgentMoney();
        
        if (!PosPub.isNumericType(deliveryMoney) && !PosPub.isNumericType(superZoneMoney) && !PosPub.isNumericType(urgentMoney)) {
            errMsg.append("配送费,超区费,加急费 不能全部为空,");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_OrderCostUpdateReq> getRequestType() {
        return new TypeToken<DCP_OrderCostUpdateReq>(){};
    }
    
    @Override
    protected DCP_OrderCostUpdateRes getResponseType() {
        return new DCP_OrderCostUpdateRes();
    }
}
