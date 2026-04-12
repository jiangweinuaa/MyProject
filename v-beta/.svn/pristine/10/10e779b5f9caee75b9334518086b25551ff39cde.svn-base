package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostAllocEnableReq;
import com.dsc.spos.json.cust.res.DCP_CostAllocEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.List;

public class DCP_CostAllocEnable extends SPosAdvanceService<DCP_CostAllocEnableReq, DCP_CostAllocEnableRes> {
    @Override
    protected void processDUID(DCP_CostAllocEnableReq req, DCP_CostAllocEnableRes res) throws Exception {

        String lastModiTime = DateFormatUtils.getNowDateTime();
        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_costalloc = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ALLOCTYPE", DataValues.newString(req.getRequest().getAllocType()));
        condition.add("YEAR", DataValues.newInteger(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newInteger(req.getRequest().getPeriod()));
        condition.add("ALLOCSOURCE", DataValues.newInteger(req.getRequest().getAllocSource()));

        int status = "1".equals(req.getRequest().getOprType())?100:0;

        dcp_costalloc.add("STATUS", DataValues.newInteger(status));
        dcp_costalloc.add("LASTMODITIME", DataValues.newDate(lastModiTime));
        dcp_costalloc.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_costalloc",condition,dcp_costalloc)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostAllocEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostAllocEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostAllocEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostAllocEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostAllocEnableReq> getRequestType() {
        return new TypeToken<DCP_CostAllocEnableReq>() {
        };
    }

    @Override
    protected DCP_CostAllocEnableRes getResponseType() {
        return new DCP_CostAllocEnableRes();
    }
}
