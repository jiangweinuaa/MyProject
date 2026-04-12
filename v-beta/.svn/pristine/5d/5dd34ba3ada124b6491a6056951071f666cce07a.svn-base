package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_Acount_SetingDeleteReq;
import com.dsc.spos.json.cust.res.DCP_Acount_SetingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_Acount_SetingDelete extends SPosAdvanceService<DCP_Acount_SetingDeleteReq, DCP_Acount_SetingDeleteRes> {
    @Override
    protected void processDUID(DCP_Acount_SetingDeleteReq req, DCP_Acount_SetingDeleteRes res) throws Exception {
        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ACOUNT_SETTING", condition)));
        this.doExecuteDataToDB();


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_Acount_SetingDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_Acount_SetingDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_Acount_SetingDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_Acount_SetingDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_Acount_SetingDeleteReq> getRequestType() {
        return new TypeToken<DCP_Acount_SetingDeleteReq>(){};
    }

    @Override
    protected DCP_Acount_SetingDeleteRes getResponseType() {
        return new DCP_Acount_SetingDeleteRes();
    }
}
