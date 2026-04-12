package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDomainEnableReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainEnableRes;
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

public class DCP_CostDomainEnable extends SPosAdvanceService<DCP_CostDomainEnableReq, DCP_CostDomainEnableRes> {
    @Override
    protected void processDUID(DCP_CostDomainEnableReq req, DCP_CostDomainEnableRes res) throws Exception {
        String querySql = String.format(" SELECT * FROM DCP_COSTDOMAIN WHERE EID='%s' AND COSTDOMAINID='%s' ", req.geteId(), req.getRequest().getCostDomainId());

        List<Map<String, Object>> existed = doQueryData(querySql, null);
        if (null == existed || existed.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "成本域不存在");
        }

        ColumnDataValue dcp_costDomain = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("COSTDOMAINID", DataValues.newString(req.getRequest().getCostDomainId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));

        dcp_costDomain.add("STATUS", DataValues.newInteger("1".equals(req.getRequest().getOprType())?100:0));

        dcp_costDomain.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_costDomain.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
        dcp_costDomain.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));


        addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTDOMAIN", condition, dcp_costDomain)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDomainEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDomainEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDomainEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDomainEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainEnableReq> getRequestType() {
        return null;
    }

    @Override
    protected DCP_CostDomainEnableRes getResponseType() {
        return null;
    }
}
