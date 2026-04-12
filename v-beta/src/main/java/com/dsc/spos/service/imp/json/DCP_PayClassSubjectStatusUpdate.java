package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayClassSubjectStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PayClassSubjectStatusUpdateRes;
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

public class DCP_PayClassSubjectStatusUpdate extends SPosAdvanceService<DCP_PayClassSubjectStatusUpdateReq,DCP_PayClassSubjectStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_PayClassSubjectStatusUpdateReq req, DCP_PayClassSubjectStatusUpdateRes res) throws Exception {

        String querySql = " SELECT COAREFID FROM DCP_PAYClASSSUBJECT WHERE EID='" + req.geteId() + "'" +
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

        ColumnDataValue dcp_payClassSubject = new ColumnDataValue();
//        '-1 未启用 100 已启用 0 已禁用
        dcp_payClassSubject.add("MODIFYBY", req.getEmployeeNo());
        dcp_payClassSubject.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
        dcp_payClassSubject.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

        if ("-1".equals(req.getRequest().getOpType())) {
            dcp_payClassSubject.add("CONFIRMBY", req.getEmployeeNo());
            dcp_payClassSubject.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

        } else if ("100".equals(req.getRequest().getOpType())) {

            dcp_payClassSubject.add("CONFIRMBY", req.getEmployeeNo());
            dcp_payClassSubject.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

        } else {
            dcp_payClassSubject.add("CANCELBY", req.getEmployeeNo());
            dcp_payClassSubject.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

        }
        dcp_payClassSubject.add("STATUS", req.getRequest().getOpType());


        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PAYClASSSUBJECT", condition, dcp_payClassSubject)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PayClassSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PayClassSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PayClassSubjectStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PayClassSubjectStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PayClassSubjectStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PayClassSubjectStatusUpdateReq>(){};
    }

    @Override
    protected DCP_PayClassSubjectStatusUpdateRes getResponseType() {
        return new DCP_PayClassSubjectStatusUpdateRes();
    }
}
