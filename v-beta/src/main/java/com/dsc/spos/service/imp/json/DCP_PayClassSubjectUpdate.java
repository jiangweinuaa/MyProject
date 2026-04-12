package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayClassSubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PayClassSubjectUpdateRes;
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

public class DCP_PayClassSubjectUpdate extends SPosAdvanceService<DCP_PayClassSubjectUpdateReq, DCP_PayClassSubjectUpdateRes> {
    @Override
    protected void processDUID(DCP_PayClassSubjectUpdateReq req, DCP_PayClassSubjectUpdateRes res) throws Exception {

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

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PAYClASSSUBJECT", condition)));

        for (DCP_PayClassSubjectUpdateReq.ClassList oneClass : req.getRequest().getClassList()) {

            ColumnDataValue dcp_payClassSubject = new ColumnDataValue();
            dcp_payClassSubject.add("EID", req.geteId());
            dcp_payClassSubject.add("ACCOUNTID", req.getRequest().getAccountId());
            dcp_payClassSubject.add("COAREFID", req.getRequest().getCoaRefID());

            dcp_payClassSubject.add("STATUS", req.getRequest().getStatus());

            dcp_payClassSubject.add("CLASSNO", oneClass.getClassNo());
            dcp_payClassSubject.add("DEBITSUBJECT", oneClass.getDebitSubject());
            dcp_payClassSubject.add("PAYSUBJECT", oneClass.getPaySubject());
            dcp_payClassSubject.add("REVSUBJECT", oneClass.getRevSubject());
            dcp_payClassSubject.add("ADVSUBJECT", oneClass.getAdvSubject());

            dcp_payClassSubject.add("CREATEBY", req.getEmployeeNo());
            dcp_payClassSubject.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            dcp_payClassSubject.add("MODIFYBY", req.getEmployeeNo());
            dcp_payClassSubject.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_payClassSubject.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PAYClASSSUBJECT", dcp_payClassSubject)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PayClassSubjectUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PayClassSubjectUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PayClassSubjectUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PayClassSubjectUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PayClassSubjectUpdateReq> getRequestType() {
        return new TypeToken<DCP_PayClassSubjectUpdateReq>() {
        };
    }

    @Override
    protected DCP_PayClassSubjectUpdateRes getResponseType() {
        return new DCP_PayClassSubjectUpdateRes();
    }
}
