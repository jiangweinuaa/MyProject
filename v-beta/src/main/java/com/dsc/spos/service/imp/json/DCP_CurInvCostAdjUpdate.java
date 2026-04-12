package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjUpdate extends SPosAdvanceService<DCP_CurInvCostAdjUpdateReq, DCP_CurInvCostAdjUpdateRes> {
    @Override
    protected void processDUID(DCP_CurInvCostAdjUpdateReq req, DCP_CurInvCostAdjUpdateRes res) throws Exception {
        String querySql = " SELECT DISTINCT a.DATASOURCE,b.CORP,b.COSTDOMAINID FROM DCP_CURINVCOSTADJ a" +
                " LEFT JOIN DCP_CURINVCOSTDETAILADJ b ON a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID and a.REFERENCENO=b.REFERENCENO " +
                " WHERE a.eid='" + req.geteId() + "'" + " AND a.REFERENCENO='" + req.getRequest().getReferenceNo() + "'";
        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在！");
        }

        if (!"1".equals(qData.get(0).get("DATASOURCE").toString())){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "非人工输入单据，不可编辑!");
        }

        String corp = qData.get(0).get("CORP").toString();
        String costDoMain = qData.get(0).get("COSTDOMAINID").toString();

        if (StringUtils.isNotEmpty(costDoMain)) {
            querySql = " SELECT * FROM DCP_COSTDOMAIN " +
                    " WHERE eid='" + req.geteId() + "' AND CORP='" + corp + "'";
            qData = doQueryData(querySql, null);
            if (CollectionUtils.isNotEmpty(qData)) {
                for (DCP_CurInvCostAdjUpdateReq.InvList inv : req.getRequest().getAdjList()) {
                    if (StringUtils.isEmpty(inv.getCostDomainId())) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, corp + "启用成本域:costDomainId不可空白！");
                    }
                }
            }
        }

        ColumnDataValue dcp_curinvcostadj = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
//        condition.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
        condition.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));
        condition.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));

        dcp_curinvcostadj.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

        dcp_curinvcostadj.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
//        dcp_curinvcostadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_curinvcostadj.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDate()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_CURINVCOSTADJ", condition, dcp_curinvcostadj)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTDETAILADJ", condition)));

        for (DCP_CurInvCostAdjUpdateReq.InvList inv : req.getRequest().getAdjList()) {
            ColumnDataValue dcp_curinvcostdetailadj = new ColumnDataValue();

            dcp_curinvcostdetailadj.add("EID", DataValues.newString(req.geteId()));
            dcp_curinvcostdetailadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            dcp_curinvcostdetailadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            dcp_curinvcostdetailadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_curinvcostdetailadj.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
            dcp_curinvcostdetailadj.add("CORP", DataValues.newString(corp));
//            dcp_curinvcostdetailadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
            dcp_curinvcostdetailadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));
            dcp_curinvcostdetailadj.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));
            dcp_curinvcostdetailadj.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

            dcp_curinvcostdetailadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_curinvcostdetailadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_curinvcostdetailadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            dcp_curinvcostdetailadj.add("ITEM", DataValues.newString(inv.getItem()));
            dcp_curinvcostdetailadj.add("COSTDOMAINID", DataValues.newString(inv.getCostDomainId()));
            dcp_curinvcostdetailadj.add("COSTDOMAINDIS", DataValues.newString(inv.getCostDomainDis()));
            dcp_curinvcostdetailadj.add("PLUNO", DataValues.newString(inv.getPluNo()));
            dcp_curinvcostdetailadj.add("PLUNAME", DataValues.newString(inv.getPluName()));
            dcp_curinvcostdetailadj.add("FEATURENO", DataValues.newString(StringUtils.toString(inv.getFeatureNo(), " ")));
            dcp_curinvcostdetailadj.add("MEMO", DataValues.newString(inv.getMemo()));
            dcp_curinvcostdetailadj.add("TOT_AMT", DataValues.newString(inv.getTotAmt()));
            dcp_curinvcostdetailadj.add("MATERIAL", DataValues.newString(inv.getMaterial()));
            dcp_curinvcostdetailadj.add("LABOR", DataValues.newString(inv.getLabor()));
            dcp_curinvcostdetailadj.add("OEM", DataValues.newString(inv.getOem()));
            dcp_curinvcostdetailadj.add("EXP1", DataValues.newString(inv.getExp1()));
            dcp_curinvcostdetailadj.add("EXP2", DataValues.newString(inv.getExp2()));
            dcp_curinvcostdetailadj.add("EXP3", DataValues.newString(inv.getExp3()));
            dcp_curinvcostdetailadj.add("EXP4", DataValues.newString(inv.getExp4()));
            dcp_curinvcostdetailadj.add("EXP5", DataValues.newString(inv.getExp5()));

            dcp_curinvcostdetailadj.add("TOT_PRETAXAMT", DataValues.newString(inv.getTotPretaxAmt()));
            dcp_curinvcostdetailadj.add("PRETAXMATERIAL", DataValues.newString(inv.getPretaxMaterial()));
            dcp_curinvcostdetailadj.add("PRETAXLABOR", DataValues.newString(inv.getPretaxLabor()));
            dcp_curinvcostdetailadj.add("PRETAXOEM", DataValues.newString(inv.getPretaxOem()));
            dcp_curinvcostdetailadj.add("PRETAXEXP1", DataValues.newString(inv.getPretaxExp1()));
            dcp_curinvcostdetailadj.add("PRETAXEXP2", DataValues.newString(inv.getPretaxExp2()));
            dcp_curinvcostdetailadj.add("PRETAXEXP3", DataValues.newString(inv.getPretaxExp3()));
            dcp_curinvcostdetailadj.add("PRETAXEXP4", DataValues.newString(inv.getPretaxExp4()));
            dcp_curinvcostdetailadj.add("PRETAXEXP5", DataValues.newString(inv.getPretaxExp5()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTDETAILADJ", dcp_curinvcostdetailadj)));

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurInvCostAdjUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurInvCostAdjUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurInvCostAdjUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjUpdateReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjUpdateReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjUpdateRes getResponseType() {
        return new DCP_CurInvCostAdjUpdateRes();
    }
}
