package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostLevelDetailProcessReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelDetailProcessRes;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.progress.imp.CostLevelDetailProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostLevelDetailProcess extends SPosAdvanceService<DCP_CostLevelDetailProcessReq, DCP_CostLevelDetailProcessRes> {
    @Override
    protected void processDUID(DCP_CostLevelDetailProcessReq req, DCP_CostLevelDetailProcessRes res) throws Exception {

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();
        String processName = req.getRequest().getYear() +
                req.getRequest().getPeriod() +
                req.getRequest().getAccountID() +
                req.getRequest().getCorp();

        CostLevelDetailProcess process = (CostLevelDetailProcess) factory.getProgress(
                ProgressService.ProgressType.ProgressType_D.getType()
                , processName);

        if (process != null && !process.finished()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已创建相同的成本阶计算,请查询进度或等待完成！");
        }
        process = new CostLevelDetailProcess(req);
        process.setProgressName(processName);

        factory.startNewProgress(process);

        res.setMainTaskId(process.getProgressName());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("成本阶计算-正在执行！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostLevelDetailProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostLevelDetailProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostLevelDetailProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostLevelDetailProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelDetailProcessReq> getRequestType() {
        return new TypeToken<DCP_CostLevelDetailProcessReq>() {
        };
    }

    @Override
    protected DCP_CostLevelDetailProcessRes getResponseType() {
        return new DCP_CostLevelDetailProcessRes();
    }
}
