package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustomerPriceAdjustDelete extends SPosAdvanceService<DCP_CustomerPriceAdjustDeleteReq, DCP_CustomerPriceAdjustDeleteRes> {
    @Override
    protected void processDUID(DCP_CustomerPriceAdjustDeleteReq req, DCP_CustomerPriceAdjustDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTPRICEADJUST", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTPRICEADJUST_DETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTPRICEADJUST_RANGE", condition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPriceAdjustDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPriceAdjustDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPriceAdjustDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustDeleteReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustDeleteReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceAdjustDeleteRes getResponseType() {
        return new DCP_CustomerPriceAdjustDeleteRes();
    }
}
