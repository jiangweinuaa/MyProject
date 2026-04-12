package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataDeleteReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_InterSettleDataDelete extends SPosAdvanceService<DCP_InterSettleDataDeleteReq, DCP_InterSettleDataDeleteRes> {
    @Override
    protected void processDUID(DCP_InterSettleDataDeleteReq req, DCP_InterSettleDataDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("BILLNO", req.getRequest().getBillNo());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettleDataDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettleDataDeleteReq> getRequestType() {
        return new TypeToken<DCP_InterSettleDataDeleteReq>() {
        };
    }

    @Override
    protected DCP_InterSettleDataDeleteRes getResponseType() {
        return new DCP_InterSettleDataDeleteRes();
    }
}
