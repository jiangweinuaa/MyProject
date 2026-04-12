package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WoProdRepStatUpdateReq;
import com.dsc.spos.json.cust.res.DCP_WoProdRepStatUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_WoProdRepStatUpdate extends SPosAdvanceService<DCP_WoProdRepStatUpdateReq,DCP_WoProdRepStatUpdateRes> {
    @Override
    protected void processDUID(DCP_WoProdRepStatUpdateReq req, DCP_WoProdRepStatUpdateRes res) throws Exception {



        for (DCP_WoProdRepStatUpdateReq.WorkList wl : req.getRequest().getWorkList()) {
            ColumnDataValue dcp_woprodrepstat = new ColumnDataValue();

            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            condition.add("PORDERNO", DataValues.newString(req.getRequest().getPorderNo()));
            condition.add("ITEM", DataValues.newInteger(wl.getItem()));

//            dcp_woprodrepstat.add("EID", DataValues.newString(req.geteId()));
//            dcp_woprodrepstat.add("CORP", DataValues.newString(req.getRequest().getCorp()));
//            dcp_woprodrepstat.add("PORDERNO", DataValues.newString(req.getRequest().getPorderNo()));

//            dcp_woprodrepstat.add("WDATE", DataValues.newDate(req.getRequest().getBDate()));
            dcp_woprodrepstat.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));

//            dcp_woprodrepstat.add("COSTCENTER", DataValues.newString(wl.getCostCenter()));
//            dcp_woprodrepstat.add("COSTCENTERNO", DataValues.newString(wl.getCostCenterNo()));
//            dcp_woprodrepstat.add("TASKID", DataValues.newString(wl.getTaskId()));
//            dcp_woprodrepstat.add("INVQTY", DataValues.newDecimal(wl.getInvQty()));
//            dcp_woprodrepstat.add("INVAMOUNT", DataValues.newDecimal(wl.getInvAmount()));
//            dcp_woprodrepstat.add("ENDWIPQTY", DataValues.newDecimal(wl.getEndWipEqQty()));
//            dcp_woprodrepstat.add("ENDWIPEQRATE", DataValues.newDecimal(wl.getEndWipEqRate()));
//            dcp_woprodrepstat.add("ENDWIPEQQTY", DataValues.newDecimal(wl.getEndWipEqQty()));
//            dcp_woprodrepstat.add("REPORTEDQUY", DataValues.newDecimal(wl.getReportedQty()));
//            dcp_woprodrepstat.add("ACTHOURS", DataValues.newDecimal(wl.getActHours()));
//            dcp_woprodrepstat.add("ACTMACHINEHRS", DataValues.newDecimal(wl.getActMachineHrs()));
//            dcp_woprodrepstat.add("STDHOURS", DataValues.newDecimal(wl.getStdHours()));
//            dcp_woprodrepstat.add("STDMACHINEHRS", DataValues.newDecimal(wl.getStdMachineHrs()));
//            dcp_woprodrepstat.add("REMARKS", DataValues.newString(wl.getRemarks()));

//            dcp_woprodrepstat.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
//            dcp_woprodrepstat.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
//            dcp_woprodrepstat.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            dcp_woprodrepstat.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_woprodrepstat.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_woprodrepstat",condition,dcp_woprodrepstat)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WoProdRepStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WoProdRepStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WoProdRepStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WoProdRepStatUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoProdRepStatUpdateReq> getRequestType() {
        return new TypeToken<DCP_WoProdRepStatUpdateReq>(){};
    }

    @Override
    protected DCP_WoProdRepStatUpdateRes getResponseType() {
        return new DCP_WoProdRepStatUpdateRes();
    }
}
