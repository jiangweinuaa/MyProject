package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReconliationStatusUpdate extends SPosAdvanceService<DCP_ReconliationStatusUpdateReq, DCP_ReconliationStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_ReconliationStatusUpdateReq req, DCP_ReconliationStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String reconNo = req.getRequest().getReconNo();
        String opType = req.getRequest().getOpType();
        //confirm审核 unConfirm取消审核 cancel作废

        //V3-供应商对账单状态更新	单据审核/取消审核 调用服务：：涉及表：DCP_RECONLIATION，DCP_RECONDETAIL内对应单据做更新状态；
        //【审核】时需更新DCP_SETTLEDATA结算底稿对应单据+项次单据状态更新为：对账完成；
        //【取消审核】时需更新DCP_SETTLEDATA结算底稿对应单据+项次单据状态更新为：对账中；
        //【作废】同时需更新DCP_SETTLEDATA结算底稿对应单据+项次的settleAmt已对账金额扣减，且需将未对账金额+已对账金额做计算；将单据状态更新为：未对账；
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String sql="select a.status,b.sourceno,b.sourcenoseq,a.year,a.month,b.RECONAMT,a.BIZPARTNERNO " +
                " from DCP_RECONLIATION a " +
                " inner join DCP_RECONDETAIL b on a.eid=b.eid and a.reconno=b.reconno " +
                " where a.eid='"+eId+"' and a.reconno='"+reconNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql,null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "对账单不存在！");
        }
        String status = list.get(0).get("STATUS").toString();
        String year = list.get(0).get("YEAR").toString();
        String month = list.get(0).get("MONTH").toString();
        String bizPartnerNo = list.get(0).get("BIZPARTNERNO").toString();

        String settleSql="select * from DCP_SETTLEDATA a " +
                " where a.eid='"+eId+"' " +
                //" and a.year='"+year+"' " +
                //" and a.month='"+month+"' " +
                " and a.bizpartnerno='"+bizPartnerNo+"'";
        List<Map<String, Object>> settleList = this.doQueryData(settleSql,null);

        if("confirm".equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "对账单状态异常！");
            }
            UptBean ub1 = new UptBean("DCP_RECONLIATION");
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", DataValues.newString(createDate));
            ub1.addUpdateValue("CONFIRM_TIME", DataValues.newString(createTime));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_RECONDETAIL");
            ub2.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));

            for (Map<String, Object> singleObject  : list){
                String sourceNo = singleObject.get("SOURCENO").toString();
                String sourceNoSeq = singleObject.get("SOURCENOSEQ").toString();
                List<Map<String, Object>> singleRows = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(sourceNo) && x.get("ITEM").toString().equals(sourceNoSeq)).collect(Collectors.toList());
                if(singleRows.size()>0){
                    //更新状态  对账完成
                    UptBean ub3 = new UptBean("DCP_SETTLEDATA");
                    ub3.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                    //condition
                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub3.addCondition("BILLNO", new DataValue(sourceNo, Types.VARCHAR));
                    ub3.addCondition("ITEM", new DataValue(sourceNoSeq, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub3));
                }
            }
        }
        else if("unConfirm".equals(opType)){
            if(!"2".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "对账单状态异常！");
            }
            UptBean ub1 = new UptBean("DCP_RECONLIATION");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", DataValues.newString(createDate));
            ub1.addUpdateValue("CONFIRM_TIME", DataValues.newString(createTime));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_RECONDETAIL");
            ub2.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));
            for (Map<String, Object> singleObject  : list){
                String sourceNo = singleObject.get("SOURCENO").toString();
                String sourceNoSeq = singleObject.get("SOURCENOSEQ").toString();
                List<Map<String, Object>> singleRows = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(sourceNo) && x.get("ITEM").toString().equals(sourceNoSeq)).collect(Collectors.toList());
                if(singleRows.size()>0){
                    //更新状态  对账完成
                    UptBean ub3 = new UptBean("DCP_SETTLEDATA");
                    ub3.addUpdateValue("status", new DataValue("1", Types.VARCHAR));
                    //condition
                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub3.addCondition("BILLNO", new DataValue(sourceNo, Types.VARCHAR));
                    ub3.addCondition("ITEM", new DataValue(sourceNoSeq, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub3));
                }

            }

        }
        else if("cancel".equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "对账单状态异常！");
            }
            UptBean ub1 = new UptBean("DCP_RECONLIATION");
            ub1.addUpdateValue("STATUS", new DataValue("-1", Types.VARCHAR));
            ub1.addUpdateValue("CANCELBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_DATE", DataValues.newString(createDate));
            ub1.addUpdateValue("CANCEL_TIME", DataValues.newString(createTime));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_RECONDETAIL");
            ub2.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));

            for (Map<String, Object> singleObject  : list){
                String sourceNo = singleObject.get("SOURCENO").toString();
                String sourceNoSeq = singleObject.get("SOURCENOSEQ").toString();
                BigDecimal reconAmt = new BigDecimal(singleObject.get("RECONAMT").toString());
                List<Map<String, Object>> singleRows = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(sourceNo) && x.get("ITEM").toString().equals(sourceNoSeq)).collect(Collectors.toList());
                if(singleRows.size()>0){
                    BigDecimal settleAmt = new BigDecimal(singleRows.get(0).get("SETTLEAMT").toString());
                    BigDecimal billAmt = new BigDecimal(singleRows.get(0).get("BILLAMT").toString());
                    settleAmt=settleAmt.subtract(reconAmt);
                    BigDecimal unSettleAmt=billAmt.subtract(settleAmt);
                    //更新状态  对账中
                    UptBean ub3 = new UptBean("DCP_SETTLEDATA");
                    ub3.addUpdateValue("status", new DataValue("0", Types.VARCHAR));
                    ub3.addUpdateValue("SETTLEAMT", new DataValue(settleAmt.toString(), Types.VARCHAR));
                    ub3.addUpdateValue("UNSETTLEAMT", new DataValue(unSettleAmt.toString(), Types.VARCHAR));
                    //condition
                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub3.addCondition("BILLNO", new DataValue(sourceNo, Types.VARCHAR));
                    ub3.addCondition("ITEM", new DataValue(sourceNoSeq, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub3));
                }
            }
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReconliationStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReconliationStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ReconliationStatusUpdateRes getResponseType() {
        return new DCP_ReconliationStatusUpdateRes();
    }
}

