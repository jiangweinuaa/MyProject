package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BsNoSetSubjectStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BsNoSetSubjectStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_BsNoSetSubjectStatusUpdate extends SPosAdvanceService<DCP_BsNoSetSubjectStatusUpdateReq, DCP_BsNoSetSubjectStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_BsNoSetSubjectStatusUpdateReq req, DCP_BsNoSetSubjectStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String opType = req.getRequest().getOpType();
        //List<DCP_BsNoSetSubjectStatusUpdateReq.accList> acctList = req.getRequest().getAccList();
        if("100".equals(opType)){

            UptBean ub1 = new UptBean("DCP_BSNOSETUPSUBJECT");
            ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }
        if("0".equals(opType)){
            UptBean ub1 = new UptBean("DCP_BSNOSETUPSUBJECT");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }
        if("-1".equals(opType)){
            UptBean ub1 = new UptBean("DCP_BSNOSETUPSUBJECT");
            ub1.addUpdateValue("STATUS", new DataValue("-1", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BsNoSetSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BsNoSetSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BsNoSetSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BsNoSetSubjectStatusUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_BsNoSetSubjectStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_BsNoSetSubjectStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_BsNoSetSubjectStatusUpdateRes getResponseType() {
        return new DCP_BsNoSetSubjectStatusUpdateRes();
    }
}

