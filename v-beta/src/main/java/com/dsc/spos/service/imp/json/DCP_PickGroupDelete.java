package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PickGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PickGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_PickGroupDelete extends SPosAdvanceService<DCP_PickGroupDeleteReq, DCP_PickGroupDeleteRes> {
    @Override
    protected void processDUID(DCP_PickGroupDeleteReq req, DCP_PickGroupDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        condition.add("PICKGROUPNO", DataValues.newString(req.getRequest().getPickGroupNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PICKGROUP",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PICKGROUP_OBJECT",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PICKGROUP_RANGE",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PickGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PickGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PickGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PickGroupDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PickGroupDeleteReq> getRequestType() {
        return new TypeToken<DCP_PickGroupDeleteReq>(){};
    }

    @Override
    protected DCP_PickGroupDeleteRes getResponseType() {
        return new DCP_PickGroupDeleteRes();
    }
}
