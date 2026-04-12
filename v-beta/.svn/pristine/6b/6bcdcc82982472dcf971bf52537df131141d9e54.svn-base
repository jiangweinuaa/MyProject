package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ExpenseDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ExpenseDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ExpenseDetailQuery extends SPosBasicService<DCP_ExpenseDetailQueryReq, DCP_ExpenseDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ExpenseDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpenseDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ExpenseDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ExpenseDetailQueryRes getResponseType() {
        return new DCP_ExpenseDetailQueryRes();
    }

    @Override
    protected DCP_ExpenseDetailQueryRes processJson(DCP_ExpenseDetailQueryReq req) throws Exception {
        DCP_ExpenseDetailQueryRes res = this.getResponseType();

        DCP_ExpenseDetailQueryRes.Datas master = res.new Datas();
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        if (getData != null && !getData.isEmpty()) {
            master.setDatas(new ArrayList<>());
            Map<String, Object> oneData = getData.get(0);

            master.setOrganizationNo(oneData.get("ORGANIZATIONNO").toString());
            master.setOrg_Name(oneData.get("ORGANIZATIONNAME").toString());
            master.setBdate(oneData.get("BDATE").toString());
            master.setDoc_Type(oneData.get("DOC_TYPE").toString());
            master.setCorp(oneData.get("CORP").toString());
            master.setCorpName(oneData.get("CORPNAME").toString());
            master.setSupplierNo(oneData.get("SETTLEDBY").toString());
            master.setSupplierName(oneData.get("SETTLEDBYNAME").toString());
            master.setTot_Amt(oneData.get("TOT_AMT").toString());
            master.setBfeeNo(oneData.get("BFEENO").toString());

            master.setSettMode(oneData.get("SETTMODE").toString());
            master.setBillDateNo(oneData.get("BILLDATENO").toString());
            master.setBillDateName(oneData.get("BILLDATENAME").toString());
            master.setPayDateNo(oneData.get("PAYDATENO").toString());
            master.setPayDateName(oneData.get("PAYDATENAME").toString());

            master.setBillDateNo(oneData.get("SETTMODE").toString());
            master.setSettType(oneData.get("SETTTYPE").toString());
            master.setTot_Amt(oneData.get("TOT_AMT").toString());
            master.setBfeeNo(oneData.get("BFEENO").toString());
            master.setSourceType(oneData.get("MSOURCETYPE").toString());
            master.setEmployeeNo(oneData.get("EMPLOYEENO").toString());
            master.setName(oneData.get("NAME").toString());
            master.setDepartNo(oneData.get("DEPARTNO").toString());
            master.setDepartName(oneData.get("DEPARTNAME").toString());
            master.setCurrency(oneData.get("CURRENCY").toString());
            master.setCurrencyName(oneData.get("CURRENCYNAME").toString());

            master.setCreateBy(oneData.get("CREATEBY").toString());
            master.setCreateByName(oneData.get("CREATEBYNAME").toString());
            master.setCreate_Date(oneData.get("CREATE_DATE").toString());
            master.setCreate_Time(oneData.get("CREATE_TIME").toString());

            master.setModifyBy(oneData.get("MODIFYBY").toString());
            master.setModifyByName(oneData.get("MODIFYBYNAME").toString());
            master.setModify_Date(oneData.get("MODIFY_DATE").toString());
            master.setModify_Time(oneData.get("MODIFY_TIME").toString());

            master.setConfirmBy(oneData.get("CONFIRMBY").toString());
            master.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
            master.setConfirm_Date(oneData.get("CONFIRM_DATE").toString());
            master.setConfirm_Time(oneData.get("CONFIRM_TIME").toString());

            master.setCancelBy(oneData.get("CANCELBY").toString());
            master.setCancelByName(oneData.get("CANCELBYNAME").toString());
            master.setCancel_Date(oneData.get("CANCEL_DATE").toString());
            master.setCancel_Time(oneData.get("CANCEL_TIME").toString());

            master.setSubmitBy(oneData.get("SUBMITBY").toString());
            master.setSubmitByName(oneData.get("SUBMITBYNAME").toString());
            master.setSubmit_Date(oneData.get("SUBMIT_DATE").toString());
            master.setSubmit_Time(oneData.get("SUBMIT_TIME").toString());

            master.setStatus(oneData.get("STATUS").toString());

            for (Map<String, Object> data : getData) {

                DCP_ExpenseDetailQueryRes.Detail oneDetail = res.new Detail();
                oneDetail.setBfeeNo(data.get("BFEENO").toString());
                oneDetail.setItem(data.get("ITEM").toString());
                oneDetail.setFee(data.get("FEE").toString());
                oneDetail.setFeeName(data.get("FEE_NAME").toString());
                oneDetail.setFeeNature(data.get("FEENATURE").toString());
                oneDetail.setAmt(data.get("AMT").toString());
                oneDetail.setMeno(data.get("MEMO").toString());
                oneDetail.setTaxCode(data.get("TAXCODE").toString());
                oneDetail.setTaxRate(data.get("TAXRATE").toString());
                oneDetail.setOrganizationNo(data.get("ORGANIZATIONNO").toString());
                oneDetail.setOrg_Name(data.get("ORG_NAME").toString());
                oneDetail.setPriceCategory(data.get("PRICECATEGORY").toString());
                oneDetail.setCurrency(data.get("CURRENCY").toString());
                oneDetail.setTaxName(data.get("TAXNAME").toString());
                oneDetail.setTaxCodeInfo(data.get("TAXNAME").toString());
                oneDetail.setFeeType(data.get("FEETYPE").toString());
                oneDetail.setStartDate(data.get("STARTDATE").toString());
                oneDetail.setEndDate(data.get("ENDDATE").toString());
                oneDetail.setSettAccPeriod(data.get("SETTACCPERIOD").toString());
                oneDetail.setCategory(data.get("CATEGORY").toString());
                oneDetail.setDepartNo(data.get("DEPARTNO").toString());
                oneDetail.setDepartName(data.get("DEPARTNAME").toString());
                oneDetail.setIsInSettDoc(data.get("ISINSETTDOC").toString());
                oneDetail.setExpAttriType(data.get("EXPATTRITYPE").toString());
                oneDetail.setExpAttriOrgName(data.get("EXPATTRIORGNAME").toString());
                oneDetail.setExpAttriOrg(data.get("EXPATTRIORG").toString());
                oneDetail.setPluNo(data.get("PLUNO").toString());
                oneDetail.setPluName(data.get("PLU_NAME").toString());
                oneDetail.setIsInvoiceIncl(data.get("ISINVOCEINCL").toString());
                oneDetail.setSourceNo(data.get("SOURCENO").toString());
                oneDetail.setSourceNoSeq(data.get("SOURCENOSEQ").toString());

                master.getDatas().add(oneDetail);
            }
        }


        res.setDatas(master);

        res.setSuccess(true);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ExpenseDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("select a.DOC_TYPE,a.BDATE,a.CREATE_DATE,a.STATUS,a.SETTMODE,a.SETTTYPE," +
                        " a.CORP,a.TOT_AMT, b.*, ol1.ORG_NAME ORGANIZATIONNAME,ol2.ORG_NAME CORPNAME, " +
                        " a.SOURCETYPE MSOURCETYPE,a.SOURCENO MSOUTCENO,a.SOURCENOSEQ MSOURCENOSEQ,a.CURRENCY, " +
                        " a.EMPLOYEENO,a.NAME,a.DEPARTNO,a.DEPARTNAME,a.SETTLEDBY,a.TOT_AMT, " +
                        " c.SNAME SETTLEDBYNAME,fl1.FEE_NAME,c.PAYDATENO,pl1.NAME PAYDATENAME," +
                        " c.BILLDATENO,bl1.NAME BILLDATENAME,cl1.NAME CURRENCYNAME,fl1.FEENATURE, " +
                        " tl1.TAXNAME,gl1.PLU_NAME " +
                        " ,a.CREATE_DATE,a.CREATE_TIME,a.MODIFY_DATE,a.MODIFY_TIME" +
                        " ,a.CONFIRM_DATE,a.CONFIRM_TIME,a.SUBMIT_DATE,a.SUBMIT_TIME" +
                        " ,a.CANCEL_DATE,a.CANCEL_TIME,ol3.ORG_NAME EXPATTRIORGNAME " +
                        " ,a.CREATEBY,a.MODIFYBY,a.CONFIRMBY,a.SUBMITBY,a.CANCELBY " +
                        " ,ee0.name as CREATEBYNAME,ee1.name as MODIFYBYNAME,ee2.name as CONFIRMBYNAME" +
                        " ,ee3.name as SUBMITBYNAME,ee4.name as CANCELBYNAME")
                .append(" FROM DCP_EXPSHEET a ")
                .append(" INNER JOIN DCP_EXPDETAIL b on a.EID=b.EID and a.BFEENO=b.BFEENO ")
                .append(" LEFT JOIN DCP_BIZPARTNER c on a.EID=c.EID and a.SETTLEDBY=c.BIZPARTNERNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=a.EID and ol1.ORGANIZATIONNO=a.ORGANIZATIONNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.EID=a.EID and ol2.ORGANIZATIONNO=a.CORP and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol3 on ol3.EID=b.EID and ol3.ORGANIZATIONNO=b.EXPATTRIORG and ol3.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.EID=a.EID and bz1.BIZPARTNERNO=a.CORP ")
                .append(" LEFT JOIN DCP_FEE fl1 on fl1.EID=b.EID and fl1.FEE=b.FEE")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_PAYDATE pl1 on pl1.eid=c.eid and c.PAYDATENO=pl1.PAYDATENO ")
                .append(" LEFT JOIN DCP_BILLDATE bl1 on bl1.eid=c.eid and c.BILLDATENO=bl1.BILLDATENO ")
                .append(" LEFT JOIN DCP_CURRENCY_LANG cl1 on cl1.eid=a.eid and cl1.CURRENCY=a.CURRENCY AND cl1.NATION='CN' and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_TAXCATEGORY_LANG tl1 on tl1.eid=b.eid and tl1.TAXCODE=b.TAXCODE and TAXAREA='CN' and tl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.CREATEBY   ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.MODIFYBY ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.CONFIRMBY ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.SUBMITBY ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee4.EID=a.EID and ee4.EMPLOYEENO=a.CANCELBY  ")
        ;


        sb.append(" WHERE a.EID='").append(req.geteId()).append("'")
                .append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBfeeNo())) {
            sb.append(" and a.BFEENO='").append(req.getRequest().getBfeeNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSupplierNo())) {
            sb.append(" and a.SETTLEDBY='").append(req.getRequest().getSupplierNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" and a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getDoc_Type())) {
            sb.append(" and a.DOC_TYPE='").append(req.getRequest().getDoc_Type()).append("'");
        }

        return sb.toString();
    }
}
