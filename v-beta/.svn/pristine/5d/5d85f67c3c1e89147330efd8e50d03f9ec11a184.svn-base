package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CFTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CFTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CFTemplateDelete extends SPosAdvanceService<DCP_CFTemplateDeleteReq, DCP_CFTemplateDeleteRes> {
    @Override
    protected void processDUID(DCP_CFTemplateDeleteReq req, DCP_CFTemplateDeleteRes res) throws Exception {

        for (DCP_CFTemplateDeleteReq.Request oneReq : req.getRequest()) {
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", req.geteId());
            condition.add("CFCODE", oneReq.getCfCode());
            condition.add("ITEM", oneReq.getItem());

//            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_DFTEPLATE", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CFTEMPLATE", condition)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CFTemplateDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CFTemplateDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CFTemplateDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CFTemplateDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CFTemplateDeleteReq> getRequestType() {
        return new TypeToken<DCP_CFTemplateDeleteReq>() {
        };
    }

    @Override
    protected DCP_CFTemplateDeleteRes getResponseType() {
        return new DCP_CFTemplateDeleteRes();
    }
}
