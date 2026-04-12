package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDataUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CostDataUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostDataUpdate extends SPosAdvanceService<DCP_CostDataUpdateReq, DCP_CostDataUpdateRes> {
    @Override
    protected void processDUID(DCP_CostDataUpdateReq req, DCP_CostDataUpdateRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("E(D", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("COSTNO", DataValues.newString(req.getRequest().getCostNo()));

        ColumnDataValue uptValue = new ColumnDataValue();

        uptValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        uptValue.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTDATA", condition, uptValue)));

        for (DCP_CostDataUpdateReq.CostoutList reqData : req.getRequest().getCostoutList()) {
            condition.add("ITEM", DataValues.newString(reqData.getItem()));

            ColumnDataValue outValue = new ColumnDataValue();

            outValue.add("MEMO", DataValues.newString(reqData.getMemo()));
            outValue.add("SUBJECTID", DataValues.newString(reqData.getSubjectId()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTDETAILOUT", condition, outValue)));
        }

        for (DCP_CostDataUpdateReq.CostinList reqData : req.getRequest().getCostinList()) {
            condition.add("ITEM", DataValues.newString(reqData.getItem()));

            ColumnDataValue outValue = new ColumnDataValue();

            outValue.add("MEMO", DataValues.newString(reqData.getMemo()));
            outValue.add("SUBJECTID", DataValues.newString(reqData.getSubjectId()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTDETAILIN", condition, outValue)));
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDataUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataUpdateReq> getRequestType() {
        return new TypeToken<DCP_CostDataUpdateReq>() {
        };
    }

    @Override
    protected DCP_CostDataUpdateRes getResponseType() {
        return new DCP_CostDataUpdateRes();
    }
}
