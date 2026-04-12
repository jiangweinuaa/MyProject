package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_invClosingDateProcessReq;
import com.dsc.spos.json.cust.res.DCP_invClosingDateProcessRes;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.progress.imp.InvClosingDateProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_invClosingDateProcess extends SPosAdvanceService<DCP_invClosingDateProcessReq, DCP_invClosingDateProcessRes> {
    @Override
    protected void processDUID(DCP_invClosingDateProcessReq req, DCP_invClosingDateProcessRes res) throws Exception {


        List<Map<String, Object>> qData = doQueryData(getQuerySql(req),null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"未查询到对应帐套信息");
        }

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();
        String processName = req.getRequest().getYear() +
                req.getRequest().getPeriod() +
                req.getRequest().getAccountID() +
                req.getRequest().getCorp();

        InvClosingDateProcess process = (InvClosingDateProcess) factory.getProgress(
                ProgressService.ProgressType.ProgressType_A.getType()
                , processName);

        if (process != null && !process.finished()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已创建相同的成本阶计算,请查询进度或等待完成！");
        }
        process = new InvClosingDateProcess(req);
        process.setProgressName(processName);

        factory.startNewProgress(process);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("库存关账-正在执行！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_invClosingDateProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_invClosingDateProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_invClosingDateProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_invClosingDateProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_invClosingDateProcessReq> getRequestType() {
        return new TypeToken<DCP_invClosingDateProcessReq>() {
        };
    }

    @Override
    protected DCP_invClosingDateProcessRes getResponseType() {
        return new DCP_invClosingDateProcessRes();
    }

    @Override
    protected String getQuerySql(DCP_invClosingDateProcessReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        String eid = req.geteId();
        String corp = req.getRequest().getCorp();
        String accountId = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();

        sb.append(" SELECT a.* FROM  DCP_ACOUNT_SETTING a");

        sb.append(" WHERE a.EID = '").append(eid).append("'")
                .append(" AND a.CORP = '").append(corp).append("'")
                .append(" AND a.ACCOUNTID = '").append(accountId).append("'")
                .append(" AND a.ACCOUNT = '").append(account).append("'");

        return sb.toString();
    }


}
