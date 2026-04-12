package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_IniInvCostOpnUpdateReq;
import com.dsc.spos.json.cust.res.DCP_IniInvCostOpnUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_IniInvCostOpnUpdate extends SPosAdvanceService<DCP_IniInvCostOpnUpdateReq, DCP_IniInvCostOpnUpdateRes> {
    @Override
    protected void processDUID(DCP_IniInvCostOpnUpdateReq req, DCP_IniInvCostOpnUpdateRes res) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT PRODNO FROM DCP_INIINVCOSTOPN ");
        querySql.append(" WHERE EID='").append(req.geteId()).append("'")
                .append(" AND ACCOUNTID='").append(req.getRequest().getAccountID()).append("'")
                .append(" AND YEAR='").append(req.getRequest().getYear()).append("'")
                .append(" AND PERIOD='").append(Integer.parseInt(req.getRequest().getPeriod())).append("'")
                .append(" AND COST_CALCULATION='").append(req.getRequest().getCost_Calculation()).append("'")
                .append(" AND ( 1=2 ");

        for (DCP_IniInvCostOpnUpdateReq.InvList oneInv : req.getRequest().getInvList()) {
            querySql.append(" OR PRODNO='").append(oneInv.getPulNo()).append("'");
        }
        querySql.append(" ) ");

        List<Map<String, Object>> eData = doQueryData(querySql.toString(), null);

        if (CollectionUtils.isEmpty(eData)) {
            StringBuilder errorMsg = new StringBuilder();
            for (Map<String, Object> data : eData) {
                errorMsg.append(data.get("PRODNO").toString()).append("  ");
            }
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg + "不存在!");
        }

        querySql = new StringBuilder();
        querySql.append(" SELECT PLUNO FROM DCP_CURINVCOSTSTAT a ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'")
                .append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'")
                .append(" AND a.YEAR=").append(req.getRequest().getYear())
                .append(" AND a.PERIOD=").append(req.getRequest().getPeriod())
        ;

        querySql.append(" AND ( 1=2 ");

        for (DCP_IniInvCostOpnUpdateReq.InvList oneInv : req.getRequest().getInvList()) {
            querySql.append(" OR PLUNO='").append(oneInv.getPulNo()).append("'");
        }
        querySql.append(" ) ");

        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            StringBuilder errorMsg = new StringBuilder();
            for (Map<String, Object> data : eData) {
                errorMsg.append(data.get("PLUNO").toString()).append("  ");
            }
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "本期库存成本统计表中已存在:" + errorMsg);

        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("ACCOUNTID", req.getRequest().getAccountID());
        condition.add("YEAR", DataValues.newDecimal(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newDecimal(req.getRequest().getPeriod()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INIINVCOSTOPN", condition)));

        for (DCP_IniInvCostOpnUpdateReq.InvList oneInv : req.getRequest().getInvList()) {

            ColumnDataValue dcp_iniinvcostopn = new ColumnDataValue();

            dcp_iniinvcostopn.add("EID", DataValues.newString(req.geteId()));
            dcp_iniinvcostopn.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_iniinvcostopn.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
            dcp_iniinvcostopn.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            dcp_iniinvcostopn.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            dcp_iniinvcostopn.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
            dcp_iniinvcostopn.add("ITEM", DataValues.newString(oneInv.getItem()));
            dcp_iniinvcostopn.add("COSTDOMAINID", DataValues.newString(oneInv.getCostDomainId()));
            dcp_iniinvcostopn.add("COSTDOMAINDIS", DataValues.newString(oneInv.getCostDomainDis()));
            dcp_iniinvcostopn.add("PRODNO", DataValues.newString(oneInv.getPulNo()));
            dcp_iniinvcostopn.add("PRODNAME", DataValues.newString(oneInv.getPluName()));
            dcp_iniinvcostopn.add("FEATURENO", DataValues.newString(oneInv.getFeatureNo()));
            dcp_iniinvcostopn.add("QTY", DataValues.newString(oneInv.getQty()));
            dcp_iniinvcostopn.add("TOT_AMT", DataValues.newString(oneInv.getTotAmt()));
            dcp_iniinvcostopn.add("MATERIAL", DataValues.newString(oneInv.getMaterial()));
            dcp_iniinvcostopn.add("LABOR", DataValues.newString(oneInv.getLabor()));
            dcp_iniinvcostopn.add("OEM", DataValues.newString(oneInv.getOem()));
            dcp_iniinvcostopn.add("EXP1", DataValues.newString(oneInv.getExp1()));
            dcp_iniinvcostopn.add("EXP2", DataValues.newString(oneInv.getExp2()));
            dcp_iniinvcostopn.add("EXP3", DataValues.newString(oneInv.getExp3()));
            dcp_iniinvcostopn.add("EXP4", DataValues.newString(oneInv.getExp4()));
            dcp_iniinvcostopn.add("EXP5", DataValues.newString(oneInv.getExp5()));
            dcp_iniinvcostopn.add("AVG_PRICE", DataValues.newString(oneInv.getAvg_Price()));
            dcp_iniinvcostopn.add("AVG_PRICE_MATERIAL", DataValues.newString(oneInv.getAvg_Price_Material()));
            dcp_iniinvcostopn.add("AVG_PRICE_OEM", DataValues.newString(oneInv.getAvg_Price_Oem()));
            dcp_iniinvcostopn.add("AVG_PRICE_EXP1", DataValues.newString(oneInv.getAvg_Price_Exp1()));
            dcp_iniinvcostopn.add("AVG_PRICE_EXP2", DataValues.newString(oneInv.getAvg_Price_Exp2()));
            dcp_iniinvcostopn.add("AVG_PRICE_EXP3", DataValues.newString(oneInv.getAvg_Price_Exp3()));
            dcp_iniinvcostopn.add("AVG_PRICE_EXP4", DataValues.newString(oneInv.getAvg_Price_Exp4()));
            dcp_iniinvcostopn.add("AVG_PRICE_EXP5", DataValues.newString(oneInv.getAvg_Price_Exp5()));

            if (StringUtils.isEmpty(oneInv.getStatus())) {
                dcp_iniinvcostopn.add("STATUS", DataValues.newString("0"));
            } else {
                dcp_iniinvcostopn.add("STATUS", DataValues.newString(oneInv.getStatus()));
            }

            dcp_iniinvcostopn.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_iniinvcostopn.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_iniinvcostopn.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            dcp_iniinvcostopn.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_iniinvcostopn.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INIINVCOSTOPN", dcp_iniinvcostopn)));

        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_IniInvCostOpnUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_IniInvCostOpnUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_IniInvCostOpnUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_IniInvCostOpnUpdateReq req) throws Exception {

        List<DCP_IniInvCostOpnUpdateReq.InvList> pluList = req.getRequest().getInvList();
        Map<String, List<DCP_IniInvCostOpnUpdateReq.InvList>> collect = pluList.stream()
                .collect(Collectors.groupingBy(x -> x.getPulNo() + "-" + x.getCostDomainId()))
                .entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!collect.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();

            collect.forEach((pluNo, productList) -> {
                errorMsg.append(pluNo).append(",");
            });

            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,
                    errorMsg + "重复数据！");
        }


        return false;
    }

    @Override
    protected TypeToken<DCP_IniInvCostOpnUpdateReq> getRequestType() {
        return new TypeToken<DCP_IniInvCostOpnUpdateReq>() {
        };
    }

    @Override
    protected DCP_IniInvCostOpnUpdateRes getResponseType() {
        return new DCP_IniInvCostOpnUpdateRes();
    }
}
