package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BottomLevelDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BottomLevelDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_BottomLevelDelete extends SPosAdvanceService<DCP_BottomLevelDeleteReq, DCP_BottomLevelDeleteRes> {
    @Override
    protected void processDUID(DCP_BottomLevelDeleteReq req, DCP_BottomLevelDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BOTTOMLEVEL",condition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BottomLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BottomLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BottomLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BottomLevelDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BottomLevelDeleteReq> getRequestType() {
        return new TypeToken<DCP_BottomLevelDeleteReq>(){};
    }

    @Override
    protected DCP_BottomLevelDeleteRes getResponseType() {
        return new DCP_BottomLevelDeleteRes();
    }
}
