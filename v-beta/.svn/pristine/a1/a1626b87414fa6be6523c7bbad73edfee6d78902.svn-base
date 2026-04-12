package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostExecuteCreateReq;
import com.dsc.spos.json.cust.res.DCP_CostExecuteCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostExecuteCreate extends SPosAdvanceService<DCP_CostExecuteCreateReq, DCP_CostExecuteCreateRes> {

    public enum ExecuteStatus {
        READY(0),
        RUNNING(1),
        FAILED(-1),
        SUCCESS(100);

        ExecuteStatus(int status) {
            this.status = status;
        }

        int status;
    }

    @Override
    protected void processDUID(DCP_CostExecuteCreateReq req, DCP_CostExecuteCreateRes res) throws Exception {

//        String querySql = "  ";
        for (DCP_CostExecuteCreateReq.Executelist oneExecute : req.getRequest().getExecutelist()) {
            ColumnDataValue dcp_costexecute_detail = new ColumnDataValue();
            dcp_costexecute_detail.add("EID", DataValues.newString(req.geteId()));
            dcp_costexecute_detail.add("MAINTASKID", DataValues.newString(oneExecute.getMainTaskId()));
            dcp_costexecute_detail.add("SUBTASKID", DataValues.newString(oneExecute.getSubtaskId()));

            dcp_costexecute_detail.add("ITEM", DataValues.newString(oneExecute.getItem()));
            dcp_costexecute_detail.add("TYPE", DataValues.newString(oneExecute.getType()));
            dcp_costexecute_detail.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
            dcp_costexecute_detail.add("MEMO", DataValues.newString(oneExecute.getMemo()));
            dcp_costexecute_detail.add("IMPORTPRG", DataValues.newString(oneExecute.getInputPrg()));
            dcp_costexecute_detail.add("IMPSTATEINFO", DataValues.newString(oneExecute.getImpStateInfo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTEXECUTE_DETAIL", dcp_costexecute_detail)));
        }

        ColumnDataValue dcp_costexecute = new ColumnDataValue();
        dcp_costexecute.add("EID", DataValues.newString(req.geteId()));
        dcp_costexecute.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        dcp_costexecute.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
        dcp_costexecute.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        dcp_costexecute.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
        dcp_costexecute.add("MAINTASKID", DataValues.newString(req.getRequest().getMainTaskId()));
        dcp_costexecute.add("IMPORTPRG", DataValues.newString(req.getRequest().getInputPrg()));
        dcp_costexecute.add("IMPSTATEINFO", DataValues.newString(req.getRequest().getImpStateInfo()));
        dcp_costexecute.add("STATUS", DataValues.newString("0"));

        dcp_costexecute.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_costexecute.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_costexecute.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        dcp_costexecute.add("MEMO", DataValues.newString(req.getRequest().getMemo()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTEXECUTE", dcp_costexecute)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功!");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostExecuteCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostExecuteCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostExecuteCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostExecuteCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostExecuteCreateReq> getRequestType() {
        return new TypeToken<DCP_CostExecuteCreateReq>() {
        };
    }

    @Override
    protected DCP_CostExecuteCreateRes getResponseType() {
        return new DCP_CostExecuteCreateRes();
    }
}
