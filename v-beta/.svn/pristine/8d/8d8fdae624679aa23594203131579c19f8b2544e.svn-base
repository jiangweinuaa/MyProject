package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LockDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LockDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_LockDelete extends SPosAdvanceService<DCP_LockDeleteReq,DCP_LockDeleteRes> {
    @Override
    protected void processDUID(DCP_LockDeleteReq req, DCP_LockDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        condition.add("LOCKNO", DataValues.newString(req.getRequest().getLockNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_LOCKDETAIL",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LockDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LockDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LockDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_LockDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_LockDeleteReq> getRequestType() {
        return new TypeToken<DCP_LockDeleteReq>(){

        };
    }

    @Override
    protected DCP_LockDeleteRes getResponseType() {
        return new DCP_LockDeleteRes();
    }
}
