package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LockCreateReq;
import com.dsc.spos.json.cust.res.DCP_LockCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_LockCreate extends SPosAdvanceService<DCP_LockCreateReq,DCP_LockCreateRes> {
    @Override
    protected void processDUID(DCP_LockCreateReq req, DCP_LockCreateRes res) throws Exception {

        String querySql = " SELECT * FROM DCP_LOCKDETAIL WHERE EID='"+req.geteId() + "' AND CORP='" + req.getRequest().getCorp() + "' AND LOCKNO='" +req.getRequest().getLockNo()+"'";
        List<Map<String, Object>> qData = doQueryData(querySql,null);
        if (qData != null && !qData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"单据已被锁定；不可编辑!");
        }

        ColumnDataValue dcp_lockdetail = new ColumnDataValue();

        dcp_lockdetail.add("EID", DataValues.newString(req.geteId()));
        dcp_lockdetail.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        dcp_lockdetail.add("LOCKNO", DataValues.newString(req.getRequest().getLockNo()));

        dcp_lockdetail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_lockdetail.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_lockdetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_LOCKDETAIL",dcp_lockdetail)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LockCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LockCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LockCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_LockCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_LockCreateReq> getRequestType() {
        return new TypeToken<DCP_LockCreateReq>(){};
    }

    @Override
    protected DCP_LockCreateRes getResponseType() {
        return new DCP_LockCreateRes();
    }
}
