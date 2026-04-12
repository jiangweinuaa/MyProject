package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HrExpStatDeleteReq;
import com.dsc.spos.json.cust.res.DCP_HrExpStatDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_HrExpStatDelete extends SPosAdvanceService<DCP_HrExpStatDeleteReq, DCP_HrExpStatDeleteRes> {
    @Override
    protected void processDUID(DCP_HrExpStatDeleteReq req, DCP_HrExpStatDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
        condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_HREXPSTAT",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_HREXPDETAIL",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HrExpStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HrExpStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HrExpStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_HrExpStatDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_HrExpStatDeleteReq> getRequestType() {
        return new TypeToken<DCP_HrExpStatDeleteReq>(){};
    }

    @Override
    protected DCP_HrExpStatDeleteRes getResponseType() {
        return new DCP_HrExpStatDeleteRes();
    }
}
