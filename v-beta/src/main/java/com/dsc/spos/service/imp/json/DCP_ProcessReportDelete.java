package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ProcessReportDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProcessReportDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_ProcessReportDelete extends SPosAdvanceService<DCP_ProcessReportDeleteReq, DCP_ProcessReportDeleteRes> {
    @Override
    protected void processDUID(DCP_ProcessReportDeleteReq req, DCP_ProcessReportDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("REPORTNO", DataValues.newString(req.getRequest().getReportNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_DETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_MATERIAL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_SCRAP", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_USER", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_MO", condition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessReportDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessReportDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessReportDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessReportDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessReportDeleteReq> getRequestType() {
        return new TypeToken<DCP_ProcessReportDeleteReq>() {
        };
    }

    @Override
    protected DCP_ProcessReportDeleteRes getResponseType() {
        return new DCP_ProcessReportDeleteRes();
    }
}
