package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CategorySubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CategorySubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_CategorySubjectDelete extends SPosAdvanceService<DCP_CategorySubjectDeleteReq, DCP_CategorySubjectDeleteRes> {

    @Override
    protected void processDUID(DCP_CategorySubjectDeleteReq req, DCP_CategorySubjectDeleteRes res) throws Exception {

        String eId = req.geteId();

        DelBean db1 = new DelBean("DCP_CATEGORYSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CategorySubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CategorySubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CategorySubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CategorySubjectDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_CategorySubjectDeleteReq> getRequestType() {
        return new TypeToken<DCP_CategorySubjectDeleteReq>() {
        };
    }

    @Override
    protected DCP_CategorySubjectDeleteRes getResponseType() {
        return new DCP_CategorySubjectDeleteRes();
    }
}

