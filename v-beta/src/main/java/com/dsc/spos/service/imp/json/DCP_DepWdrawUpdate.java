package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepWdrawUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DepWdrawUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_DepWdrawUpdate extends SPosAdvanceService<DCP_DepWdrawUpdateReq, DCP_DepWdrawUpdateRes> {
    @Override
    protected void processDUID(DCP_DepWdrawUpdateReq req, DCP_DepWdrawUpdateRes res) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT DEPWDRAWCODE FROM DCP_DEPWDRAW WHERE EID='").append(req.geteId()).append("'");
        querySql.append(" AND DEPWDRAWCODE='").append(req.getRequest().getDepWdrawCode()).append("'");

//        querySql.append(" )");
        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getDepWdrawCode() + "不存在!不可编辑");
        }

        DCP_DepWdrawUpdateReq.Request oneReq = req.getRequest();

        ColumnDataValue dcp_depwdraw = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("DEPWDRAWCODE", oneReq.getDepWdrawCode());

        dcp_depwdraw.add("DEPWDRAWNAME", oneReq.getDepWdrawName());
        dcp_depwdraw.add("DWTYPE", oneReq.getDwType());
        dcp_depwdraw.add("CFCODE", oneReq.getCfCode());
        dcp_depwdraw.add("CFNAME", oneReq.getCfName());
        dcp_depwdraw.add("SUBJECTID", oneReq.getSubjectId());
        dcp_depwdraw.add("SUBJECTNAME", oneReq.getSubjectName());
        dcp_depwdraw.add("ACCTSET", oneReq.getAcctSet());
        dcp_depwdraw.add("ACCOUNTID", oneReq.getAccountID());
        dcp_depwdraw.add("ACCOUNT", oneReq.getAccount());
        dcp_depwdraw.add("STATUS", oneReq.getStatus());

        dcp_depwdraw.add("LASTMODIOPID", req.getEmployeeNo());
        dcp_depwdraw.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_DEPWDRAW", condition, dcp_depwdraw)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepWdrawUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepWdrawUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepWdrawUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_DepWdrawUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DepWdrawUpdateReq> getRequestType() {
        return new TypeToken<DCP_DepWdrawUpdateReq>() {
        };
    }

    @Override
    protected DCP_DepWdrawUpdateRes getResponseType() {
        return new DCP_DepWdrawUpdateRes();
    }
}
