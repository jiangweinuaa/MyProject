package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostAllocDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CostAllocDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostAllocDelete extends SPosAdvanceService<DCP_CostAllocDeleteReq, DCP_CostAllocDeleteRes> {
    @Override
    protected void processDUID(DCP_CostAllocDeleteReq req, DCP_CostAllocDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();


        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ALLOCTYPE", DataValues.newString(req.getRequest().getAllocType()));
        condition.add("YEAR", DataValues.newInteger(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newInteger(req.getRequest().getPeriod()));
        condition.add("ALLOCSOURCE", DataValues.newInteger(req.getRequest().getAllocSource()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_costalloc",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostAllocDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostAllocDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostAllocDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostAllocDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostAllocDeleteReq> getRequestType() {
        return new TypeToken<DCP_CostAllocDeleteReq>() {
        };
    }

    @Override
    protected DCP_CostAllocDeleteRes getResponseType() {
        return new DCP_CostAllocDeleteRes();
    }
}
