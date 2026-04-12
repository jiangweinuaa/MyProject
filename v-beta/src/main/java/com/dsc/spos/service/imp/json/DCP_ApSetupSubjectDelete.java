package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApSetupSubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ApSetupSubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ApSetupSubjectDelete extends SPosAdvanceService<DCP_ApSetupSubjectDeleteReq, DCP_ApSetupSubjectDeleteRes> {

    @Override
    protected void processDUID(DCP_ApSetupSubjectDeleteReq req, DCP_ApSetupSubjectDeleteRes res) throws Exception {

        String eId = req.geteId();

        String accountId = req.getRequest().getAccountId();
        String setupType = req.getRequest().getSetupType();

        //TEMPLATEID
        DelBean db1 = new DelBean("DCP_APSETUPSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
        db1.addCondition("SETUPTYPE", new DataValue(setupType, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApSetupSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApSetupSubjectDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_ApSetupSubjectDeleteReq> getRequestType() {
        return new TypeToken<DCP_ApSetupSubjectDeleteReq>() {
        };
    }

    @Override
    protected DCP_ApSetupSubjectDeleteRes getResponseType() {
        return new DCP_ApSetupSubjectDeleteRes();
    }
}


