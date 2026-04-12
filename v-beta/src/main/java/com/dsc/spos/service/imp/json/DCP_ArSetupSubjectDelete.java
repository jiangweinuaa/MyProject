package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ArSetupSubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ArSetupSubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ArSetupSubjectDelete extends SPosAdvanceService<DCP_ArSetupSubjectDeleteReq, DCP_ArSetupSubjectDeleteRes> {

    @Override
    protected void processDUID(DCP_ArSetupSubjectDeleteReq req, DCP_ArSetupSubjectDeleteRes res) throws Exception {

        String eId = req.geteId();


        DelBean db1 = new DelBean("DCP_ARSETUPSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
        db1.addCondition("SETUPTYPE", new DataValue(req.getRequest().getSetupType(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ArSetupSubjectDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_ArSetupSubjectDeleteReq> getRequestType() {
        return new TypeToken<DCP_ArSetupSubjectDeleteReq>() {
        };
    }

    @Override
    protected DCP_ArSetupSubjectDeleteRes getResponseType() {
        return new DCP_ArSetupSubjectDeleteRes();
    }
}

