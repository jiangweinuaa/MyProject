package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CFTemplateCreateReq;
import com.dsc.spos.json.cust.res.DCP_CFTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CFTemplateCreate extends SPosAdvanceService<DCP_CFTemplateCreateReq, DCP_CFTemplateCreateRes> {
    @Override
    protected void processDUID(DCP_CFTemplateCreateReq req, DCP_CFTemplateCreateRes res) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT CFCODE,ITEM FROM DCP_CFTEMPLATE WHERE EID='").append(req.geteId()).append("'");

        querySql.append(" AND ( 1=2 ");
        for (DCP_CFTemplateCreateReq.Request oneReq : req.getRequest()) {
            querySql.append(" OR ( CFCODE='").append(oneReq.getCfCode()).append("'")
                    .append(" AND ITEM='").append(oneReq.getItem()).append("')");

        }

        querySql.append(" )");

        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            StringBuilder errorMsg = new StringBuilder();
            for (Map<String, Object> oneData : qData) {
                errorMsg.append(oneData.get("CFCODE").toString())
                        .append("-")
                        .append(oneData.get("ITEM").toString())
                        .append(" ")
                ;
            }
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg + "已存在!不可重复");
        }


        for (DCP_CFTemplateCreateReq.Request oneReq : req.getRequest()) {
            ColumnDataValue dcp_dfTemplate = new ColumnDataValue();

            dcp_dfTemplate.add("EID", req.geteId());
            dcp_dfTemplate.add("CFCODE", oneReq.getCfCode());
            dcp_dfTemplate.add("CFNAME", oneReq.getCfName());
            dcp_dfTemplate.add("ITEM", oneReq.getItem());
            dcp_dfTemplate.add("CFTYPE", oneReq.getCfType());
            dcp_dfTemplate.add("TYPE", oneReq.getType());
            dcp_dfTemplate.add("COMPUTE", oneReq.getCompute());

            if (StringUtils.isNotEmpty(oneReq.getStatus())) {
                dcp_dfTemplate.add("STATUS", oneReq.getStatus());
            } else {
                dcp_dfTemplate.add("STATUS", "-1");
            }

            dcp_dfTemplate.add("CREATEOPID", req.getEmployeeNo());
            dcp_dfTemplate.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            dcp_dfTemplate.add("SUBMITOPID", req.getEmployeeNo());
            dcp_dfTemplate.add("SUBMITTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

//            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_DFTEPLATE", dcp_dfTemplate)));
            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CFTEMPLATE", dcp_dfTemplate))); //表改名

        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CFTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CFTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CFTemplateCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CFTemplateCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CFTemplateCreateReq> getRequestType() {
        return new TypeToken<DCP_CFTemplateCreateReq>() {
        };
    }

    @Override
    protected DCP_CFTemplateCreateRes getResponseType() {
        return new DCP_CFTemplateCreateRes();
    }
}
