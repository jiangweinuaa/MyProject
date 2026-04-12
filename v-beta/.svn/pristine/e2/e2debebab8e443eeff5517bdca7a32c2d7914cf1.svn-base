package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SettleDataDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SettleDataDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_SettleDataDelete extends SPosAdvanceService<DCP_SettleDataDeleteReq,DCP_SettleDataDeleteRes> {
    @Override
    protected void processDUID(DCP_SettleDataDeleteReq req, DCP_SettleDataDeleteRes res) throws Exception {

        ColumnDataValue delCondition = new ColumnDataValue();

        delCondition.add("EID", DataValues.newString(req.geteId()));
        delCondition.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        delCondition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
        delCondition.add("BIZTYPE", DataValues.newString(req.getRequest().getBizType()));
        delCondition.add("BTYPE", DataValues.newString(req.getRequest().getBType()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SETTLEDATA",delCondition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SettleDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_SettleDataDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SettleDataDeleteReq> getRequestType() {
        return new TypeToken<DCP_SettleDataDeleteReq>(){};
    }

    @Override
    protected DCP_SettleDataDeleteRes getResponseType() {
        return new DCP_SettleDataDeleteRes();
    }
}
