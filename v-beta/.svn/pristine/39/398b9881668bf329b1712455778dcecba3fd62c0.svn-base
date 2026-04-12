package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDataStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CostDataStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostDataStatusUpdate extends SPosAdvanceService<DCP_CostDataStatusUpdateReq, DCP_CostDataStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_CostDataStatusUpdateReq req, DCP_CostDataStatusUpdateRes res) throws Exception {

        String opType = req.getRequest().getOpType();

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据");
        }
        String status = qData.get(0).get("STATUS").toString();

        ColumnDataValue uptCondition = new ColumnDataValue();
        ColumnDataValue uptValue = new ColumnDataValue();

        uptCondition.add("EID", DataValues.newString(req.geteId()));

        if (Constant.OPR_TYPE_CONFIRM.equals(opType)){
            uptValue.add("STATUS", DataValues.newString("100"));
        }else if (Constant.OPR_TYPE_UNCONFIRM.equals(opType)){
            uptValue.add("STATUS", DataValues.newString("0"));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTDATA",uptCondition,uptValue)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected String getQuerySql(DCP_CostDataStatusUpdateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT a.*")
                .append(" FROM DCP_COSTDATA a ")
        ;

        querySql.append(" WHERE a.EIO='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getCostNo())) {
            querySql.append(" AND a.COSTNO='").append(req.getRequest().getCostNo()).append("'");
        }

        return querySql.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDataStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDataStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDataStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDataStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_CostDataStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_CostDataStatusUpdateRes getResponseType() {
        return new DCP_CostDataStatusUpdateRes();
    }
}
