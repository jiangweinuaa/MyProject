package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CFTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CFTemplateUpdateRes;
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

public class DCP_CFTemplateUpdate extends SPosAdvanceService<DCP_CFTemplateUpdateReq, DCP_CFTemplateUpdateRes> {
    @Override
    protected void processDUID(DCP_CFTemplateUpdateReq req, DCP_CFTemplateUpdateRes res) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT CFCODE,ITEM FROM DCP_CFTEMPLATE WHERE EID='").append(req.geteId()).append("'");
        querySql.append(" AND (1=2 ");
        for (DCP_CFTemplateUpdateReq.Request oneReq : req.getRequest()) {
            querySql.append(" OR ( CFCODE='").append(oneReq.getCfCode()).append("'")
                    .append(" AND ITEM='").append(oneReq.getItem()).append("' )");

        }
        querySql.append(" )");
        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        for (DCP_CFTemplateUpdateReq.Request oneReq : req.getRequest()) {
            ColumnDataValue dcp_dfTemplate = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", req.geteId());
            condition.add("CFCODE", oneReq.getCfCode());
            condition.add("ITEM", oneReq.getItem());

            dcp_dfTemplate.add("CFNAME", oneReq.getCfName());
            dcp_dfTemplate.add("CFTYPE", oneReq.getCfType());
            dcp_dfTemplate.add("TYPE", oneReq.getType());
            dcp_dfTemplate.add("COMPUTE", oneReq.getCompute());
            dcp_dfTemplate.add("STATUS", oneReq.getStatus());

            dcp_dfTemplate.add("LASTMODIOPID", req.getEmployeeNo());
            dcp_dfTemplate.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

//            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_DFTEPLATE", condition, dcp_dfTemplate)));
            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_CFTEMPLATE", condition, dcp_dfTemplate)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CFTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CFTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CFTemplateUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CFTemplateUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CFTemplateUpdateReq> getRequestType() {
        return new TypeToken<DCP_CFTemplateUpdateReq>() {
        };
    }

    @Override
    protected DCP_CFTemplateUpdateRes getResponseType() {
        return new DCP_CFTemplateUpdateRes();
    }
}
