package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_costLevelDeleteReq;
import com.dsc.spos.json.cust.res.DCP_costLevelDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_costLevelDelete extends SPosAdvanceService<DCP_costLevelDeleteReq,DCP_costLevelDeleteRes> {
    @Override
    protected void processDUID(DCP_costLevelDeleteReq req, DCP_costLevelDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();


        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("COSTGROUPINGID", DataValues.newString(req.getRequest().getCostGroupingId()));



        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTLEVEL",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTLEVELDETAIL",condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_costLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_costLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_costLevelDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_costLevelDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_costLevelDeleteReq> getRequestType() {
        return new TypeToken<DCP_costLevelDeleteReq>(){};
    }

    @Override
    protected DCP_costLevelDeleteRes getResponseType() {
        return new DCP_costLevelDeleteRes();
    }
}
