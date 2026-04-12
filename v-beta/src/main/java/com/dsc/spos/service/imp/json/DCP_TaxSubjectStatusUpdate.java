package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TaxSubjectStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TaxSubjectStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_TaxSubjectStatusUpdate extends SPosAdvanceService<DCP_TaxSubjectStatusUpdateReq, DCP_TaxSubjectStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_TaxSubjectStatusUpdateReq req, DCP_TaxSubjectStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String opType = req.getRequest().getOpType();
        List<DCP_TaxSubjectStatusUpdateReq.AccList> acctList = req.getRequest().getAcctList();
        if("100".equals(opType)&& CollUtil.isNotEmpty(acctList)){

            for (DCP_TaxSubjectStatusUpdateReq.AccList acct : acctList){

                UptBean ub1 = new UptBean("DCP_TAXSUBJECT");
                ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ACCOUNTID", new DataValue(acct.getAccountId(), Types.VARCHAR));
                ub1.addCondition("COAREFID", new DataValue(acct.getCoaRefID(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

        }
        if("0".equals(opType)&& CollUtil.isNotEmpty(acctList)){
            for (DCP_TaxSubjectStatusUpdateReq.AccList acct : acctList) {
                UptBean ub1 = new UptBean("DCP_TAXSUBJECT");
                ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ACCOUNTID", new DataValue(acct.getAccountId(), Types.VARCHAR));
                ub1.addCondition("COAREFID", new DataValue(acct.getCoaRefID(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TaxSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TaxSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TaxSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TaxSubjectStatusUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_TaxSubjectStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_TaxSubjectStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_TaxSubjectStatusUpdateRes getResponseType() {
        return new DCP_TaxSubjectStatusUpdateRes();
    }
}

