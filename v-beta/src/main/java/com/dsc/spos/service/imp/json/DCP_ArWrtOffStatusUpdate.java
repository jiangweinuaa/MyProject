package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ArWrtOffStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ArWrtOffStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_ArWrtOffStatusUpdate extends SPosAdvanceService<DCP_ArWrtOffStatusUpdateReq, DCP_ArWrtOffStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_ArWrtOffStatusUpdateReq req, DCP_ArWrtOffStatusUpdateRes res) throws Exception {


        for (DCP_ArWrtOffStatusUpdateReq.WrtOffList oneWrtOff : req.getRequest().getWrtOffList()) {
            ColumnDataValue uptCondtion = new ColumnDataValue();
            ColumnDataValue dcp_arWrtOff = new ColumnDataValue();

            uptCondtion.add("EID", DataValues.newString(req.geteId()));
            uptCondtion.add("ARNO", DataValues.newString(oneWrtOff.getArNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ARWRTOFF", uptCondtion, dcp_arWrtOff)));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArWrtOffStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArWrtOffStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArWrtOffStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ArWrtOffStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArWrtOffStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ArWrtOffStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ArWrtOffStatusUpdateRes getResponseType() {
        return new DCP_ArWrtOffStatusUpdateRes();
    }
}
