package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WrtOffDeleteReq;
import com.dsc.spos.json.cust.res.DCP_WrtOffDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class DCP_WrtOffDelete extends SPosAdvanceService<DCP_WrtOffDeleteReq, DCP_WrtOffDeleteRes> {

    @Override
    protected void processDUID(DCP_WrtOffDeleteReq req, DCP_WrtOffDeleteRes res) throws Exception {


        String wrtOffNo = req.getRequest().getWrtOffNo();

        ColumnDataValue delWrtOffCondition = new ColumnDataValue();
        delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
        delWrtOffCondition.add("WRTOFFBILLNO", DataValues.newString(wrtOffNo));
        delWrtOffCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
        delWrtOffCondition.add("WRTOFFTYPE", DataValues.newString(req.getRequest().getWrtOffType()));
        delWrtOffCondition.add("ITEM", DataValues.newString(req.getRequest().getItem()));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLWRTOFF", delWrtOffCondition)));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WrtOffDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WrtOffDeleteReq> getRequestType() {
        return new TypeToken<DCP_WrtOffDeleteReq>() {
        };
    }

    @Override
    protected DCP_WrtOffDeleteRes getResponseType() {
        return new DCP_WrtOffDeleteRes();
    }
}
