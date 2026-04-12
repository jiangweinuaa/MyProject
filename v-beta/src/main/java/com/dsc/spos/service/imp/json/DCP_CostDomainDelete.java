package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDomainDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostDomainDelete extends SPosAdvanceService<DCP_CostDomainDeleteReq, DCP_CostDomainDeleteRes> {
    @Override
    protected void processDUID(DCP_CostDomainDeleteReq req, DCP_CostDomainDeleteRes res) throws Exception {
        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("COSTDOMAINID", DataValues.newString(req.getRequest().getCostDomainId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));

        addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTDOMAIN", condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDomainDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDomainDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDomainDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDomainDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainDeleteReq> getRequestType() {
        return new TypeToken<DCP_CostDomainDeleteReq>(){};
    }

    @Override
    protected DCP_CostDomainDeleteRes getResponseType() {
        return new DCP_CostDomainDeleteRes();
    }
}
