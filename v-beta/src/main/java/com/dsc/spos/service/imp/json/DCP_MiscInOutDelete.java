package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MiscInOutDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MiscInOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_MiscInOutDelete extends SPosAdvanceService<DCP_MiscInOutDeleteReq, DCP_MiscInOutDeleteRes> {
    @Override
    protected void processDUID(DCP_MiscInOutDeleteReq req, DCP_MiscInOutDeleteRes res) throws Exception {
//        账套+年度+期别
        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
        condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
        condition.add("BILLTYPE", DataValues.newString(req.getRequest().getType()));
        //        账套+年度+期别+来源单号
        if (StringUtils.isNotEmpty(req.getRequest().getReferenceNo())){
            condition.add("REFERENCENO",DataValues.newString(req.getRequest().getReferenceNo()));
        }
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_MISCINOUT",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MiscInOutDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MiscInOutDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MiscInOutDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_MiscInOutDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_MiscInOutDeleteReq> getRequestType() {
        return new TypeToken<DCP_MiscInOutDeleteReq>() {
        };
    }

    @Override
    protected DCP_MiscInOutDeleteRes getResponseType() {
        return new DCP_MiscInOutDeleteRes();
    }
}
