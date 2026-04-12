package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeSetupSubjectStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_FeeSetupSubjectStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_FeeSetupSubjectStatusUpdate extends SPosAdvanceService<DCP_FeeSetupSubjectStatusUpdateReq, DCP_FeeSetupSubjectStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_FeeSetupSubjectStatusUpdateReq req, DCP_FeeSetupSubjectStatusUpdateRes res) throws Exception {

        String querySql = " SELECT COAREFID FROM DCP_FEESETUPSUBJECT WHERE EID='" + req.geteId() + "'" +
                " AND ACCOUNTID='" + req.getRequest().getAccountId() + "'" +
                " AND COAREFID='" + req.getRequest().getCoaRefID() + "'";

        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCoaRefID() + "不存在，不可修改!");
        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("ACCOUNTID", req.getRequest().getAccountId());
        condition.add("COAREFID", req.getRequest().getCoaRefID());

        ColumnDataValue dcp_feeSetupSubject = new ColumnDataValue();
//        '-1 未启用 100 已启用 0 已禁用
        dcp_feeSetupSubject.add("MODIFYBY", req.getEmployeeNo());
        dcp_feeSetupSubject.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
        dcp_feeSetupSubject.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

        if ("-1".equals(req.getRequest().getOpType())) {
            dcp_feeSetupSubject.add("CONFIRMBY", req.getEmployeeNo());
            dcp_feeSetupSubject.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_feeSetupSubject.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

        } else if ("100".equals(req.getRequest().getOpType())) {

            dcp_feeSetupSubject.add("CONFIRMBY", req.getEmployeeNo());
            dcp_feeSetupSubject.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_feeSetupSubject.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

        } else {
            dcp_feeSetupSubject.add("CANCELBY", req.getEmployeeNo());
            dcp_feeSetupSubject.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_feeSetupSubject.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

        }
        dcp_feeSetupSubject.add("STATUS", req.getRequest().getOpType());


        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_FEESETUPSUBJECT", condition, dcp_feeSetupSubject)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeSetupSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeSetupSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeSetupSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeSetupSubjectStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeSetupSubjectStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_FeeSetupSubjectStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_FeeSetupSubjectStatusUpdateRes getResponseType() {
        return new DCP_FeeSetupSubjectStatusUpdateRes();
    }
}
