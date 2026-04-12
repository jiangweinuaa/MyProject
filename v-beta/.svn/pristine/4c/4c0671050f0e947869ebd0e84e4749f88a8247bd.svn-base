package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepWdrawCreateReq;
import com.dsc.spos.json.cust.res.DCP_DepWdrawCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_DepWdrawCreate extends SPosAdvanceService<DCP_DepWdrawCreateReq, DCP_DepWdrawCreateRes> {
    @Override
    protected void processDUID(DCP_DepWdrawCreateReq req, DCP_DepWdrawCreateRes res) throws Exception {


        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT DEPWDRAWCODE FROM DCP_DEPWDRAW WHERE EID='").append(req.geteId()).append("'");
        querySql.append(" AND DEPWDRAWCODE='").append(req.getRequest().getDepWdrawCode()).append("'");

//        querySql.append(" )");
        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isNotEmpty(qData)) {

            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getDepWdrawCode() + "已存在!不可重复");
        }

        DCP_DepWdrawCreateReq.Request oneReq = req.getRequest();

        ColumnDataValue dcp_depwdraw = new ColumnDataValue();

        dcp_depwdraw.add("EID", req.geteId());
        dcp_depwdraw.add("DEPWDRAWCODE", oneReq.getDepWdrawCode());
        dcp_depwdraw.add("DEPWDRAWNAME", oneReq.getDepWdrawName());
        dcp_depwdraw.add("DWTYPE", oneReq.getDwType());
        dcp_depwdraw.add("CFCODE", oneReq.getCfCode());
        dcp_depwdraw.add("CFNAME", oneReq.getCfName());
        dcp_depwdraw.add("SUBJECTID", oneReq.getSubjectId());
        dcp_depwdraw.add("SUBJECTNAME", oneReq.getSubjectName());
        dcp_depwdraw.add("ACCTSET", oneReq.getAcctSet());
        dcp_depwdraw.add("ACCOUNTID", oneReq.getAccountID());
        dcp_depwdraw.add("ACCOUNT", oneReq.getAccount());
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())){
            dcp_depwdraw.add("STATUS",req.getRequest().getStatus());
        }else {
            dcp_depwdraw.add("STATUS","100");
        }


        dcp_depwdraw.add("CREATEOPID", req.getEmployeeNo());
        dcp_depwdraw.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
        dcp_depwdraw.add("SUBMITOPID", req.getEmployeeNo());
        dcp_depwdraw.add("SUBMITTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_DEPWDRAW", dcp_depwdraw)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepWdrawCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepWdrawCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepWdrawCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_DepWdrawCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DepWdrawCreateReq> getRequestType() {
        return new TypeToken<DCP_DepWdrawCreateReq>() {
        };
    }

    @Override
    protected DCP_DepWdrawCreateRes getResponseType() {
        return new DCP_DepWdrawCreateRes();
    }
}
