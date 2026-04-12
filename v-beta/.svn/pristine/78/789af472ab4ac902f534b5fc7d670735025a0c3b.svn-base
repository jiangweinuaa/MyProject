package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDomainCreateReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostDomainCreate extends SPosAdvanceService<DCP_CostDomainCreateReq, DCP_CostDomainCreateRes> {

    @Override
    protected void processDUID(DCP_CostDomainCreateReq req, DCP_CostDomainCreateRes res) throws Exception {

//        String querySql = String.format(" SELECT * FROM DCP_COSTDOMAIN WHERE EID='%s' AND CORP='%s' AND COSTDOMAINTYPE='%s' AND COST_CALCULATION='%s' ",
        String querySql = String.format(" SELECT * FROM DCP_COSTDOMAIN WHERE EID='%s' AND CORP='%s'  ",
                req.geteId(), req.getRequest().getCorp()
        );

        List<Map<String, Object>> existed = doQueryData(querySql, null);

        if (null != existed && !existed.isEmpty()) {
            if (req.getRequest().getCorp().equals(existed.get(0).get("CORP"))) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已存在法人" + req.getRequest().getCorp() + "成本域");
            }
        }

//        String costdomainid = getOrderNO(req, "CBY");
//        String costdomainid = req.getRequest().getCostDomainId();

        for (DCP_CostDomainCreateReq.Datas oneData:req.getRequest().getDatas()){
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

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTDOMAIN", dcp_costDomain)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDomainCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDomainCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDomainCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDomainCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainCreateReq> getRequestType() {
        return new TypeToken<DCP_CostDomainCreateReq>() {
        };
    }

    @Override
    protected DCP_CostDomainCreateRes getResponseType() {
        return new DCP_CostDomainCreateRes();
    }
}
