package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDomainUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainUpdateReqRes;
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

public class DCP_CostDomainUpdate extends SPosAdvanceService<DCP_CostDomainUpdateReq, DCP_CostDomainUpdateReqRes> {
    @Override
    protected void processDUID(DCP_CostDomainUpdateReq req, DCP_CostDomainUpdateReqRes res) throws Exception {

        String querySql = String.format(" SELECT * FROM DCP_COSTDOMAIN WHERE EID='%s' AND CORP='%s' AND COSTDOMAINTYPE='%s' AND COST_CALCULATION='%s' ",
                req.geteId(), req.getRequest().getCorp(),req.getRequest().getCostDomainType(),req.getRequest().getCost_Calculation()
        );

        List<Map<String, Object>> existed = doQueryData(querySql, null);
        if (null == existed || existed.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "成本域不存在");
        }
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        condition.add("COSTDOMAINTYPE", DataValues.newString(req.getRequest().getCostDomainType()));
        condition.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTDOMAIN",condition)));

        for (DCP_CostDomainUpdateReq.Datas oneData : req.getRequest().getDatas()) {
            ColumnDataValue dcp_costDomain = new ColumnDataValue();

            dcp_costDomain.add("EID", DataValues.newString(req.geteId()));
            dcp_costDomain.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_costDomain.add("COSTDOMAINTYPE", DataValues.newString(req.getRequest().getCostDomainType()));
            dcp_costDomain.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));

            dcp_costDomain.add("COSTDOMAINID", DataValues.newString(oneData.getCostDomainId()));
            dcp_costDomain.add("COSTDOMAIN", DataValues.newString(oneData.getCostDomain()));

            dcp_costDomain.add("STATUS", DataValues.newInteger(oneData.getStatus()));
            dcp_costDomain.add("MEMO", DataValues.newString(oneData.getMemo()));

            dcp_costDomain.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costDomain.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_costDomain.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            dcp_costDomain.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costDomain.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_costDomain.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTDOMAIN", dcp_costDomain)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDomainUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDomainUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDomainUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDomainUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainUpdateReq> getRequestType() {
        return new TypeToken<DCP_CostDomainUpdateReq>(){};
    }

    @Override
    protected DCP_CostDomainUpdateReqRes getResponseType() {
        return new DCP_CostDomainUpdateReqRes();
    }
}
