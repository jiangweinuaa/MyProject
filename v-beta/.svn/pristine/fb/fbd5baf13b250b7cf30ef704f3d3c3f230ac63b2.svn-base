package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_invMthClosingProcessReq;
import com.dsc.spos.json.cust.res.DCP_invMthClosingProcessRes;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.progress.imp.InvMthClosingProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_invMthClosingProcess extends SPosAdvanceService<DCP_invMthClosingProcessReq, DCP_invMthClosingProcessRes> {

    @Override
    protected void processDUID(DCP_invMthClosingProcessReq req, DCP_invMthClosingProcessRes res) throws Exception {

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();
        String processName = req.getRequest().getAccountID() + "-" + req.getRequest()
                .getYear() + req.getRequest().getPeriod();
        InvMthClosingProcess process = (InvMthClosingProcess) factory.getProgress(ProgressService.ProgressType.ProgressType_B.getType(),processName);

        if (process != null && !process.finished()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"已创建相同的库存月结,请查询进度或等待完成！");
        }

        process = new InvMthClosingProcess(req);
        process.setProgressName(processName);
        //process.runProgress();
        factory.startNewProgress(process);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("库存月结正在执行！");



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_invMthClosingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_invMthClosingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_invMthClosingProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_invMthClosingProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_invMthClosingProcessReq> getRequestType() {
        return new TypeToken<DCP_invMthClosingProcessReq>() {
        };
    }

    @Override
    protected DCP_invMthClosingProcessRes getResponseType() {
        return new DCP_invMthClosingProcessRes();
    }
}

