package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepWdrawDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DepWdrawDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_DepWdrawDelete extends SPosAdvanceService<DCP_DepWdrawDeleteReq,DCP_DepWdrawDeleteRes> {
    @Override
    protected void processDUID(DCP_DepWdrawDeleteReq req, DCP_DepWdrawDeleteRes res) throws Exception {
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("DEPWDRAWCODE", req.getRequest().getDepWdrawCode());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_DEPWDRAW", condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepWdrawDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepWdrawDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepWdrawDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_DepWdrawDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DepWdrawDeleteReq> getRequestType() {
        return new TypeToken<DCP_DepWdrawDeleteReq>(){};
    }

    @Override
    protected DCP_DepWdrawDeleteRes getResponseType() {
        return new DCP_DepWdrawDeleteRes();
    }
}
