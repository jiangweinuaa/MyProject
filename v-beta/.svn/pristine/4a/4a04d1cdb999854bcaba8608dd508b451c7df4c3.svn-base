package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDataDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostDataDetailQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostDataDetailQuery extends SPosAdvanceService<DCP_CostDataDetailQueryReq, DCP_CostDataDetailQueryRes> {

    @Override
    protected void processDUID(DCP_CostDataDetailQueryReq req, DCP_CostDataDetailQueryRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        DCP_CostDataDetailQueryRes.Datas oneData = res.new Datas();
        res.setDatas(oneData);
        oneData.setCostinList(new ArrayList<>());
        oneData.setCostoutList(new ArrayList<>());
        if (CollectionUtils.isEmpty(qData)) {
            Map<String, Object> q = qData.get(0);
            oneData.setAccountID(q.get("ACCOUNTID").toString());
            oneData.setAccount(q.get("ACCOUNT").toString());
            oneData.setCostType(q.get("COSTTYPE").toString());
            oneData.setCostNo(q.get("COSTNO").toString());
            oneData.setCorp(q.get("CORP").toString());
            oneData.setBDate(q.get("BDATE").toString());
            oneData.setCost_Calculation(q.get("COST_CALCULATION").toString());
            oneData.setYear(q.get("YEAR").toString());
            oneData.setPeriod(q.get("PERIOD").toString());
            oneData.setEmployeeNo(q.get("EMPLOYEENO").toString());
            oneData.setEmployeeNamt(q.get("EMPLOYEENAMT").toString());
            oneData.setDepartNo(q.get("DEPARTNO").toString());
            oneData.setDepartName(q.get("DEPARTNAME").toString());
            oneData.setGlNo(q.get("GLNO").toString());
            oneData.setTotCostInAmt(q.get("TOTCOSTINAMT").toString());
            oneData.setGlNo(q.get("GLNO").toString());

            oneData.setCreateOpId(q.get("CREATEOPID").toString());
            oneData.setCreateTime(q.get("CREATETIME").toString());
            oneData.setLastmodiopID(q.get("LASTMODIOPID").toString());
            oneData.setLastmodiTime(q.get("LASTMODITIME").toString());
            oneData.setConfirmopID(q.get("CONFIRMOPID").toString());
            oneData.setConfirmopTime(q.get("CONFIRMTIME").toString());

            List<Map<String, Object>> inData = doQueryData(getQueryInSql(req), null);
            List<Map<String, Object>> outData = doQueryData(getQueryOutSql(req), null);

            double totCostInAmt = 0;
            double totCostOutAmt = 0;

            for (Map<String, Object> in : inData) {
                DCP_CostDataDetailQueryRes.CostinList oneIn = res.new CostinList();
                oneData.getCostinList().add(oneIn);

                oneIn.setCostNo(in.get("COSTNO").toString());
                oneIn.setItem(in.get("ITEM").toString());
                oneIn.setBillNo(in.get("BILLNO").toString());
                oneIn.setBillItem(in.get("BILLITEM").toString());
                oneIn.setBType(in.get("BTYPE").toString());
                oneIn.setCType(in.get("CTYPE").toString());
                oneIn.setPluNo(in.get("PLUNO").toString());
                oneIn.setPluName(in.get("PLUNAME").toString());
                oneIn.setFeatureNo(in.get("FEATURENO").toString());
                oneIn.setSubjectId(in.get("SUBJECTID").toString());
                oneIn.setSubjectName(in.get("SUBJECTNAME").toString());
                oneIn.setMemo(in.get("MEMO").toString());
                oneIn.setCostDomainID(in.get("COSTDOMAINID").toString());
                oneIn.setCostDomainName(in.get("COSTDOMAINNAME").toString());
                oneIn.setBsNo(in.get("BSNO").toString());
                oneIn.setBsName(in.get("BSNAME").toString());
                oneIn.setCostCenter(in.get("COSTCENTER").toString());
                oneIn.setCostCenterName(in.get("COSTCENTERNAME").toString());
                oneIn.setQty(in.get("QTY").toString());
                oneIn.setTotAmt(in.get("TOTAMT").toString());
                totCostInAmt += Double.parseDouble(in.get("TOTAMT").toString());
                oneIn.setMaterial(in.get("MATERIAL").toString());
                oneIn.setLabor(in.get("LABOR").toString());
                oneIn.setOem(in.get("OEM").toString());
                oneIn.setExp1(in.get("EXP1").toString());
                oneIn.setExp2(in.get("EXP2").toString());
                oneIn.setExp3(in.get("EXP3").toString());
                oneIn.setExp4(in.get("EXP4").toString());
                oneIn.setExp5(in.get("EXP5").toString());
                oneIn.setEmployeeName(in.get("EMPLOYEENAME").toString());
                oneIn.setEmployeeNo(in.get("EMPLOYEENO").toString());
                oneIn.setDepartName(in.get("DEPARTNAME").toString());
                oneIn.setDepartId(in.get("DEPARTID").toString());
                oneIn.setFreeChars1(in.get("FREECHARS1").toString());
                oneIn.setFreeChars2(in.get("FREECHARS2").toString());
                oneIn.setFreeChars3(in.get("FREECHARS3").toString());
                oneIn.setFreeChars4(in.get("FREECHARS4").toString());
                oneIn.setFreeChars5(in.get("FREECHARS5").toString());
                oneIn.setCategory(in.get("CATEGORY").toString());
                oneIn.setCategoryName(in.get("CATEGORYNAME").toString());

            }

            for (Map<String, Object> out : outData) {
                DCP_CostDataDetailQueryRes.CostoutList oneOut = res.new CostoutList();
                oneData.getCostoutList().add(oneOut);

                oneOut.setCostNo(out.get("COSTNO").toString());
                oneOut.setItem(out.get("ITEM").toString());
                oneOut.setBillNo(out.get("BILLNO").toString());
                oneOut.setBillItem(out.get("BILLITEM").toString());
                oneOut.setBType(out.get("BTYPE").toString());
                oneOut.setCType(out.get("CTYPE").toString());
                oneOut.setPluNo(out.get("PLUNO").toString());
                oneOut.setPluName(out.get("PLUNAME").toString());
                oneOut.setFeatureNo(out.get("FEATURENO").toString());
                oneOut.setSubjectId(out.get("SUBJECTID").toString());
                oneOut.setSubjectName(out.get("SUBJECTNAME").toString());
                oneOut.setMemo(out.get("MEMO").toString());
                oneOut.setCostDomainID(out.get("COSTDOMAINID").toString());
                oneOut.setCostDomainName(out.get("COSTDOMAINNAME").toString());
                oneOut.setBsNo(out.get("BSNO").toString());
                oneOut.setBsName(out.get("BSNAME").toString());
                oneOut.setCostCenter(out.get("COSTCENTER").toString());
                oneOut.setCostCenterName(out.get("COSTCENTERNAME").toString());
                oneOut.setQty(out.get("QTY").toString());
                oneOut.setTotAmt(out.get("TOTAMT").toString());
                totCostOutAmt += Double.parseDouble(out.get("TOTAMT").toString());
                oneOut.setMaterial(out.get("MATERIAL").toString());
                oneOut.setLabor(out.get("LABOR").toString());
                oneOut.setOem(out.get("OEM").toString());
                oneOut.setExp1(out.get("EXP1").toString());
                oneOut.setExp2(out.get("EXP2").toString());
                oneOut.setExp3(out.get("EXP3").toString());
                oneOut.setExp4(out.get("EXP4").toString());
                oneOut.setExp5(out.get("EXP5").toString());
                oneOut.setEmployeeName(out.get("EMPLOYEENAME").toString());
                oneOut.setEmployeeNo(out.get("EMPLOYEENO").toString());
                oneOut.setDepartName(out.get("DEPARTNAME").toString());
                oneOut.setDepartId(out.get("DEPARTID").toString());
                oneOut.setFreeChars1(out.get("FREECHARS1").toString());
                oneOut.setFreeChars2(out.get("FREECHARS2").toString());
                oneOut.setFreeChars3(out.get("FREECHARS3").toString());
                oneOut.setFreeChars4(out.get("FREECHARS4").toString());
                oneOut.setFreeChars5(out.get("FREECHARS5").toString());
                oneOut.setCategory(out.get("CATEGORY").toString());
                oneOut.setCategoryName(out.get("CATEGORYNAME").toString());
            }

            oneData.setTotCostInAmt(String.valueOf(totCostInAmt));
            oneData.setTotCostOutAmt(String.valueOf(totCostOutAmt));
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDataDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDataDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDataDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDataDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CostDataDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_CostDataDetailQueryRes getResponseType() {
        return new DCP_CostDataDetailQueryRes();
    }

    protected String getQuerySql(DCP_CostDataDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.* FROM DCP_COSTDATA a ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getCostNo())) {
            querySql.append(" AND a.COSTNO ='").append(req.getRequest().getCostNo()).append("'");
        }

        return querySql.toString();
    }


    protected String getQueryInSql(DCP_CostDataDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT b.* FROM DCP_COSTDATA a ")
                .append(" LEFT JOIN DCP_COSTDETAILIN b on a.eid=b.eid and a.COSTNO=b.COSTNO ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getCostNo())) {
            querySql.append(" AND a.COSTNO ='").append(req.getRequest().getCostNo()).append("'");
        }
        return querySql.toString();
    }

    protected String getQueryOutSql(DCP_CostDataDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT b.* FROM DCP_COSTDATA a ")
                .append(" LEFT JOIN DCP_COSTDETAILOUT b on a.eid=b.eid and a.COSTNO=b.COSTNO ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getCostNo())) {
            querySql.append(" AND a.COSTNO ='").append(req.getRequest().getCostNo()).append("'");
        }
        return querySql.toString();
    }

}
