package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostLevelDetailDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelDetailDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostLevelDetailDelete extends SPosAdvanceService<DCP_CostLevelDetailDeleteReq, DCP_CostLevelDetailDeleteRes> {
    @Override
    protected void processDUID(DCP_CostLevelDetailDeleteReq req, DCP_CostLevelDetailDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));

        condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

        if ("1".equals(req.getRequest().getType())){
            condition.add("ITEM", DataValues.newString(req.getRequest().getItem()));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTLEVELDETAIL",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostLevelDetailDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostLevelDetailDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostLevelDetailDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostLevelDetailDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelDetailDeleteReq> getRequestType() {
        return new TypeToken<DCP_CostLevelDetailDeleteReq>(){};
    }

    @Override
    protected DCP_CostLevelDetailDeleteRes getResponseType() {
        return new DCP_CostLevelDetailDeleteRes();
    }
}
