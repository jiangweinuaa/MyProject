package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HrExpStatProcessReq;
import com.dsc.spos.json.cust.res.DCP_HrExpStatProcessRes;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.progress.imp.HrExpStatProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class DCP_HrExpStatProcess extends SPosAdvanceService<DCP_HrExpStatProcessReq, DCP_HrExpStatProcessRes> {
    @Override
    protected void processDUID(DCP_HrExpStatProcessReq req, DCP_HrExpStatProcessRes res) throws Exception {

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();
        String processName = req.getRequest().getAccountID() + "-" + req.getRequest()
                .getYear() + req.getRequest().getPeriod();
        HrExpStatProcess process = (HrExpStatProcess) factory.getProgress(HrExpStatProcess.class.getName(), processName);

        if (process != null && !process.finished()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已创建相同的工时费用统计,请查询进度或等待完成！");
        }

        process = new HrExpStatProcess(req);
        process.setProgressName(processName);

//        factory.startNewProgress(process);
        Future<String> future = factory.submitNewProgress(process);
        if (null == future) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无法创建线程！");
        }

        String result = future.get();
        if (StringUtils.isNotEmpty(result)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, result);
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
//        res.setServiceDescription("工时费用正在统计！");
        res.setServiceDescription("工时费用统计完成！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HrExpStatProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HrExpStatProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HrExpStatProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_HrExpStatProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_HrExpStatProcessReq> getRequestType() {
        return new TypeToken<DCP_HrExpStatProcessReq>() {
        };
    }

    @Override
    protected DCP_HrExpStatProcessRes getResponseType() {
        return new DCP_HrExpStatProcessRes();
    }
}
