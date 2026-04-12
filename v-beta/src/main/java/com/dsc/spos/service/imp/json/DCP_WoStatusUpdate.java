package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WoStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_WoStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_WoStatusUpdate extends SPosAdvanceService<DCP_WoStatusUpdateReq, DCP_WoStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_WoStatusUpdateReq req, DCP_WoStatusUpdateRes res) throws Exception {


        for (DCP_WoStatusUpdateReq.WostatusList oneWork : req.getRequest().getWostatusList()) {

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WoStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WoStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WoStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WoStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_WoStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_WoStatusUpdateRes getResponseType() {
        return new DCP_WoStatusUpdateRes();
    }
}
