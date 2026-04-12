package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HrExpStatProcessReq;
import com.dsc.spos.json.cust.req.DCP_HrExpStatUpdateReq;
import com.dsc.spos.json.cust.res.DCP_HrExpStatProcessRes;
import com.dsc.spos.json.cust.res.DCP_HrExpStatUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_HrExpStatUpdate extends SPosAdvanceService<DCP_HrExpStatUpdateReq, DCP_HrExpStatUpdateRes> {
    @Override
    protected void processDUID(DCP_HrExpStatUpdateReq req, DCP_HrExpStatUpdateRes res) throws Exception {

        ServiceAgentUtils<DCP_HrExpStatProcessReq, DCP_HrExpStatProcessRes> agentUtils
                = new ServiceAgentUtils<>();

        DCP_HrExpStatProcessReq req1 = new DCP_HrExpStatProcessReq();
        DCP_HrExpStatProcessReq.Request request = req1.new Request();
        req1.setRequest(request);
        request.setCorp(req.getRequest().getCorp());
        request.setPeriod(req.getRequest().getPeriod());
        request.setAccountID(req.getRequest().getAccountID());
        request.setYear(req.getRequest().getYear());
        request.setCost_Calculation(req.getRequest().getCost_Calculation());
        request.setCorp_Name(req.getRequest().getCorp_Name());
        request.setAccount(req.getRequest().getAccount());
        request.setStatus(req.getRequest().getStatus());

        String costCenterNo = "";
        StringBuilder desc = new StringBuilder();
        boolean success = false;
        for (DCP_HrExpStatUpdateReq.HrList oneHr : req.getRequest().getHrList()) {
            if (costCenterNo.equals(oneHr.getCostCenterNo())) {
                continue;
            }
            costCenterNo = oneHr.getCostCenterNo();
            request.setCostCenterNo(costCenterNo);

            if (!agentUtils.agentServiceSuccess(req1, new TypeToken<DCP_HrExpStatProcessRes>() {
            })) {
                desc.append(costCenterNo).append("执行失败");
            } else {
                success = true;
            }

        }
        res.setSuccess(success);
        res.setServiceStatus("000");
        res.setServiceDescription(desc.toString());

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HrExpStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HrExpStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HrExpStatUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_HrExpStatUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_HrExpStatUpdateReq> getRequestType() {
        return null;
    }

    @Override
    protected DCP_HrExpStatUpdateRes getResponseType() {
        return null;
    }
}
