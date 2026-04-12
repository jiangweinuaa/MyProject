package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BottomLevelProcessReq;
import com.dsc.spos.json.cust.res.DCP_BottomLevelProcessRes;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.progress.imp.BottomLevelProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_BottomLevelProcess extends SPosAdvanceService<DCP_BottomLevelProcessReq, DCP_BottomLevelProcessRes> {

    @Override
    protected void processDUID(DCP_BottomLevelProcessReq req, DCP_BottomLevelProcessRes res) throws Exception {

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();

        String processName = req.getRequest().getYear() +
                req.getRequest().getPeriod() +
                req.getRequest().getAccountID() +
                req.getRequest().getCorp();

        BottomLevelProcess process = (BottomLevelProcess) factory.getProgress(
                ProgressService.ProgressType.ProgressType_C.getType()
                , processName);

        if (process != null && !process.finished()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已创建相同的成本阶计算,请查询进度或等待完成！");
        }
        process = new BottomLevelProcess(req);
        process.setProgressName(processName);

        factory.startNewProgress(process);


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("低阶码计算-正在执行！");


    }

    private String getSqlQuery(DCP_BottomLevelProcessReq req) {
        String bDate = DateFormatUtils.getDate(req.getRequest().getYear() + req.getRequest().getPeriod());

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT PLUNO,MIN(BOTTOMLEVEL) BOTTOMLEVEL ")
                .append("FROM ( ")
                .append("   SELECT PLUNO, 99 as BOTTOMLEVEL ")
                .append("      FROM DCP_GOODS")
                .append("      WHERE STATUS = '100'")
                .append(" UNION ALL ")
                .append("   SELECT PLUNO, CASE ")
                .append("                 WHEN BOMTYPE = '0' THEN 0 ")
                .append("                 WHEN BOMTYPE = '1' THEN 98 ")
                .append("                 ELSE 99 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM ")
                .append("      WHERE STATUS = '100'")
                .append(" UNION ALL ")
                .append(" SELECT MATERIAL_PLUNO, ")
                .append("             CASE WHEN NVL(b.PLUNO, '') = '' THEN 99 ")
                .append("               ELSE 1 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM_MATERIAL a ")
                .append("      LEFT JOIN DCP_BOM b ON a.EID = b.EID and a.MATERIAL_PLUNO = b.PLUNO ")
                .append(" WHERE TO_CHAR(MATERIAL_EDATE,'yyyy-mm-dd')>='").append(bDate).append("' AND TO_CHAR(MATERIAL_BDATE,'yyyy-mm-dd')<='").append(bDate).append("'")
                .append(" UNION ALL ")
                .append(" SELECT REPLACE_PLUNO, ")
                .append(" CASE WHEN NVL(b.PLUNO, '') = '' THEN 99 ELSE 1 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM_MATERIAL_R a ")
                .append("      LEFT JOIN DCP_BOM b ON a.EID = b.EID and a.REPLACE_PLUNO = b.PLUNO ")
                .append(" WHERE TO_CHAR(REPLACE_EDATE,'yyyy-mm-dd') >='").append(bDate).append("' AND TO_CHAR(REPLACE_BDATE,'yyyy-mm-dd')<='").append(bDate).append("'")
                .append(")TEMP GROUP BY PLUNO");

        return sb.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BottomLevelProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BottomLevelProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BottomLevelProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BottomLevelProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BottomLevelProcessReq> getRequestType() {
        return new TypeToken<DCP_BottomLevelProcessReq>() {
        };
    }

    @Override
    protected DCP_BottomLevelProcessRes getResponseType() {
        return new DCP_BottomLevelProcessRes();
    }
}
