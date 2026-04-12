package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderPerInfoInPutReq;
import com.dsc.spos.json.cust.res.DCP_OrderPerInfoInPutRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
public class DCP_OrderPerInfoInPut extends SPosAdvanceService<DCP_OrderPerInfoInPutReq, DCP_OrderPerInfoInPutRes> {
    @Override
    protected void processDUID(DCP_OrderPerInfoInPutReq req, DCP_OrderPerInfoInPutRes res) throws Exception {
        
        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String[] orderList = req.getRequest().getOrderList();
        List<String> failOrderList = new ArrayList<>();
        List<String> successOrderList = new ArrayList<>();
        boolean isAllSuccess = true;
        
        String sql = "";
        for (String orderNo : orderList)
        {
            try
            {
                sql = "select * from DCP_order where EID='" + eId + "' and orderno='" + orderNo + "'";
                
                List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
                if (getQDataDetail == null || getQDataDetail.isEmpty())
                {
                    isAllSuccess = false;
                    failOrderList.add(orderNo);
                    continue;
                }
                
                String loadDocType = getQDataDetail.get(0).get("LOADDOCTYPE").toString();
                String channelId = getQDataDetail.get(0).get("CHANNELID").toString();
                String loadDocBillType = getQDataDetail.get(0).get("LOADDOCBILLTYPE").toString();
                String loadDocOrderNo = getQDataDetail.get(0).get("LOADDOCORDERNO").toString();
                
                String delId = getQDataDetail.get(0).getOrDefault("DELID", "").toString();
                String delName = getQDataDetail.get(0).getOrDefault("DELNAME", "").toString();
                String delTelephone = getQDataDetail.get(0).getOrDefault("DELTELEPHONE", "").toString();
                String packerId = getQDataDetail.get(0).getOrDefault("PACKERID", "").toString();
                String packerName = getQDataDetail.get(0).getOrDefault("PACKERNAME", "").toString();
                String packerTelephone = getQDataDetail.get(0).getOrDefault("PACKERTELEPHONE", "").toString();
                String machShop = getQDataDetail.get(0).getOrDefault("MACHSHOP", "").toString();
                
                this.pData.clear();
                
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_ORDER");
                
                // condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));
                ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
                //需要不需要更新，就不用执行语句
                boolean isNeedUpdate = false;
                StringBuffer logmemo = new StringBuffer("");
                
                if (req.getRequest().getPackerId() != null)
                {
                    if(req.getRequest().getPackerId().equals(packerId)==false)
                    {
                        ub1.addUpdateValue("PACKERID", new DataValue(req.getRequest().getPackerId(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo.append( "打包人ID：" + packerId + "-->" + req.getRequest().getPackerId() + "<br>");
                    }
                    
                }
                if (req.getRequest().getPackerName() != null)
                {
                    if(req.getRequest().getPackerName().equals(packerName)==false)
                    {
                        ub1.addUpdateValue("PACKERNAME", new DataValue(req.getRequest().getPackerName(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo .append( "打包人：" + packerName + "-->" + req.getRequest().getPackerName() + "<br>");
                    }
                    
                }
                if (req.getRequest().getPackerTelephone() != null)
                {
                    if(req.getRequest().getPackerTelephone().equals(packerTelephone)==false)
                    {
                        ub1.addUpdateValue("PACKERTELEPHONE", new DataValue(req.getRequest().getPackerTelephone(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo .append( "打包人电话：" + packerTelephone + "-->" + req.getRequest().getPackerTelephone() + "<br>");
                    }
                    
                }
                
                if (req.getRequest().getDelId() != null)
                {
                    if(req.getRequest().getDelId().equals(delId)==false)
                    {
                        ub1.addUpdateValue("DELID", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo .append( "配送人ID：" + delId + "-->" + req.getRequest().getDelId() + "<br>");
                    }
                    
                }
                if (req.getRequest().getDelName() != null)
                {
                    if(req.getRequest().getDelName().equals(delName)==false)
                    {
                        ub1.addUpdateValue("DELNAME", new DataValue(req.getRequest().getDelName(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo .append( "配送人：" + delName + "-->" + req.getRequest().getDelName() + "<br>");
                    }
                    
                }
                if (req.getRequest().getDelTelephone() != null)
                {
                    if(req.getRequest().getDelTelephone().equals(delTelephone)==false)
                    {
                        ub1.addUpdateValue("DELTELEPHONE", new DataValue(req.getRequest().getDelTelephone(), Types.VARCHAR));
                        
                        isNeedUpdate = true;
                        logmemo .append( "配送人电话：" + delTelephone + "-->" + req.getRequest().getDelTelephone() + "<br>");
                    }
                    
                }
                
                if (isNeedUpdate == false)
                {
                    //无需更新，没有修改
                    continue;
                }
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
                
                
                //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                // 之前易成用的是（易成用的要货发货单功能）--服务端 by jinzma 20231218
                sql = " select stockoutno,transfer_shop from dcp_stockout "
                        + " where eid='"+eId+"'  and shopid='"+machShop+"' and ofno='"+orderNo+"'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQData)) {
                    //更新出库单
                    UptBean ub2 = new UptBean("DCP_STOCKOUT");
                    ub2.addUpdateValue("DELIVERYBY", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));
                    // condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("SHOPID", new DataValue(machShop, Types.VARCHAR));
                    ub2.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));
                    
                    //更新收货通知单
                    UptBean ub3 = new UptBean("DCP_RECEIVING");
                    ub3.addUpdateValue("DELIVERYBY", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));
                    // condition
                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub3.addCondition("SHOPID", new DataValue(getQData.get(0).get("TRANSFER_SHOP").toString(), Types.VARCHAR));
                    ub3.addCondition("TRANSFER_SHOP", new DataValue(machShop, Types.VARCHAR));
                    ub3.addCondition("LOAD_DOCNO", new DataValue(getQData.get(0).get("STOCKOUTNO").toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub3));
                    
                    //更新入库单
                    UptBean ub4 = new UptBean("DCP_STOCKIN");
                    ub4.addUpdateValue("DELIVERYBY", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));
                    // condition
                    ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub4.addCondition("SHOPID", new DataValue(getQData.get(0).get("TRANSFER_SHOP").toString(), Types.VARCHAR));
                    ub4.addCondition("TRANSFER_SHOP", new DataValue(machShop, Types.VARCHAR));
                    ub4.addCondition("LOAD_DOCNO", new DataValue(getQData.get(0).get("STOCKOUTNO").toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub4));
                    
                }
                
                
                
                
                
                this.doExecuteDataToDB();
                HelpTools.writelog_waimai("【调用DCP_OrderPerInfoInPut接口，打包人等信息】修改成功，单号OrderNO=" + orderNo);
                
                // region 写下日志
                try
                {
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setNeed_callback("N");
                    onelv1.setNeed_notify("N");
                    onelv1.seteId(eId);
                    onelv1.setOpNo(opNo);
                    onelv1.setOpName(opName);
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
                    
                    
                } catch (Exception e)
                {
                    HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
                }
                // endregion
                
                
            }
            catch (Exception e)
            {
                isAllSuccess = false;
                failOrderList.add(orderNo);
                continue;
            }
        }
        
        if (failOrderList.isEmpty())
        {
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            
        }
        else
        {
            StringBuffer errorDescBuffer = new StringBuffer();
            for (String failOrderNo : failOrderList)
            {
                errorDescBuffer.append("<br>"+failOrderNo);
            }
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("以下订单失败:"+errorDescBuffer.toString());
            
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderPerInfoInPutReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderPerInfoInPutReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderPerInfoInPutReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_OrderPerInfoInPutReq req) throws Exception {
        
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        if (req.getRequest().getOrderList()==null||req.getRequest().getOrderList().length==0)
        {
            isFail = true;
            errMsg.append("订单列表orderList节点不能为空，");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 防止没有传更新的节点，那么就不用执行语句
        boolean isNeedUpdate = false;
        if (req.getRequest().getPackerId() != null)
        {
            if (req.getRequest().getPackerId().length() > 100)
            {
                isFail = true;
                errMsg.append("传入的packerId节点值太大，长度不能超过100，");
            }
            isNeedUpdate = true;
        }
        if (req.getRequest().getPackerName() != null)
        {
            if (req.getRequest().getPackerName().length() > 100)
            {
                isFail = true;
                errMsg.append("传入的packerName节点值太大，长度不能超过100，");
            }
            isNeedUpdate = true;
        }
        if (req.getRequest().getPackerTelephone() != null)
        {
            if (req.getRequest().getPackerTelephone().length() > 100)
            {
                isFail = true;
                errMsg.append("传入的packerTelephone节点值太大，长度不能超过100，");
            }
            isNeedUpdate = true;
        }
        if (req.getRequest().getDelId() != null)
        {
            if (req.getRequest().getDelId().length() > 10)
            {
                isFail = true;
                errMsg.append("传入的delId节点值太大，长度不能超过10，");
            }
            isNeedUpdate = true;
        }
        if (req.getRequest().getDelName() != null)
        {
            if (req.getRequest().getDelName().length() > 100)
            {
                isFail = true;
                errMsg.append("传入的delName节点值太大，长度不能超过100，");
            }
            isNeedUpdate = true;
            
        }
        if (req.getRequest().getDelTelephone() != null)
        {
            if (req.getRequest().getDelTelephone().length() > 100)
            {
                isFail = true;
                errMsg.append("传入的delTelephone节点值太大，长度不能超过100，");
            }
            isNeedUpdate = true;
        }
        if (!isNeedUpdate)
        {
            isFail = true;
            errMsg.append("没有传入任何节点，");
        }
        
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_OrderPerInfoInPutReq> getRequestType() {
        return new TypeToken<DCP_OrderPerInfoInPutReq>(){};
    }
    
    @Override
    protected DCP_OrderPerInfoInPutRes getResponseType() {
        return new DCP_OrderPerInfoInPutRes();
    }
}
