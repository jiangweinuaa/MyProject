package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BankPayUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BankPayUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_BankPayUpdate extends SPosAdvanceService<DCP_BankPayUpdateReq,DCP_BankPayUpdateRes> {
    @Override
    protected void processDUID(DCP_BankPayUpdateReq req, DCP_BankPayUpdateRes res) throws Exception {

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankPayUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankPayUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankPayUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BankPayUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankPayUpdateReq> getRequestType() {
        return new TypeToken<DCP_BankPayUpdateReq>(){};
    }

    @Override
    protected DCP_BankPayUpdateRes getResponseType() {
        return new DCP_BankPayUpdateRes();
    }
}
