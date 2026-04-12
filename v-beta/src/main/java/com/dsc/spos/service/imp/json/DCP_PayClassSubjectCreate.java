package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayClassSubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_PayClassSubjectCreateRes;
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

public class DCP_PayClassSubjectCreate extends SPosAdvanceService<DCP_PayClassSubjectCreateReq, DCP_PayClassSubjectCreateRes> {
    @Override
    protected void processDUID(DCP_PayClassSubjectCreateReq req, DCP_PayClassSubjectCreateRes res) throws Exception {

        String querySql = " SELECT COAREFID FROM DCP_PAYClASSSUBJECT WHERE EID='" + req.geteId() + "'" +
                " AND ACCOUNTID='" + req.getRequest().getAccountId() + "'" +
                " AND COAREFID='" + req.getRequest().getCoaRefID() + "'";

        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isNotEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCoaRefID() + "已存在，不可保存!");
        }

        for (DCP_PayClassSubjectCreateReq.ClassList oneClass : req.getRequest().getClassList()) {

            ColumnDataValue dcp_payClassSubject = new ColumnDataValue();
            dcp_payClassSubject.add("EID",req.geteId());
            dcp_payClassSubject.add("ACCOUNTID",req.getRequest().getAccountId());
            dcp_payClassSubject.add("COAREFID",req.getRequest().getCoaRefID());

            dcp_payClassSubject.add("STATUS","-1");

            dcp_payClassSubject.add("CLASSNO",oneClass.getClassNo());
            dcp_payClassSubject.add("DEBITSUBJECT",oneClass.getDebitSubject());
            dcp_payClassSubject.add("PAYSUBJECT",oneClass.getPaySubject());
            dcp_payClassSubject.add("REVSUBJECT",oneClass.getRevSubject());
            dcp_payClassSubject.add("ADVSUBJECT",oneClass.getAdvSubject());

            dcp_payClassSubject.add("CREATEBY", req.getEmployeeNo());
            dcp_payClassSubject.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PAYClASSSUBJECT",dcp_payClassSubject)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PayClassSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PayClassSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PayClassSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PayClassSubjectCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PayClassSubjectCreateReq> getRequestType() {
        return new TypeToken<DCP_PayClassSubjectCreateReq>() {
        };
    }

    @Override
    protected DCP_PayClassSubjectCreateRes getResponseType() {
        return new DCP_PayClassSubjectCreateRes();
    }
}
