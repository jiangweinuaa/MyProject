package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ApPrePayDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApPrePayDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApPrePayDetailQuery extends SPosBasicService<DCP_ApPrePayDetailQueryReq, DCP_ApPrePayDetailQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_ApPrePayDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ApPrePayDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ApPrePayDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ApPrePayDetailQueryRes getResponseType() {
        return new DCP_ApPrePayDetailQueryRes();
    }

    @Override
    protected DCP_ApPrePayDetailQueryRes processJson(DCP_ApPrePayDetailQueryReq req) throws Exception {
        DCP_ApPrePayDetailQueryRes res = this.getResponseType();

        DCP_ApPrePayDetailQueryRes.Datas apData = res.new Datas();

        StringBuffer apBillSb=new StringBuffer("" +
                " select a.*,b.account,c.org_name as corpname,d.org_name as organizationname,e.name as accemployeename," +
                " f.sname as bizpartnername,g.sname as receivername,h.taxname,i.name as employeename,j.departname " +
                " k.subjectname as feesubjectname,l.subjectname as apSubjectName,m.name as currencyname," +
                " n1.op_name as createbyName,n2.op_name as modifybyname,n3.op_name as confirmbyName,n4.op_name as cancelbyname" +
                " from DCP_APBILL a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.corp and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.organizationno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee e on e.eid=a.eid and e.employeeno=a.accemployeeno " +
                " left join dcp_bizpartner f on f.eid=a.eid and f.bizpartnerno=a.bizpartnerno " +
                " left join dcp_bizpartner g on g.eid=a.eid and g.bizpartnerno=a.receiver " +
                " left join dcp_taxcategory_lang h on h.eid=a.eid and h.taxcode=a.taxcode and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee i on i.eid=a.eid and i.employeeno=a.employee " +
                " left join DCP_DEPARTMENT_LANG j on j.eid=a.eid and j.departno=a.departid and j.lang_type='"+req.getLangType()+"' " +
                " left join DCP_COA k on k.eid=a.eid and k.accountid=a.accountid and k.subjectid=a.feesubjectid " +
                " left join dcp_coa l on l.eid=a.eid and l.accountid=a.accountid and l.subjectid=a.apSubjectId" +
                " left join DCP_CURRENCY_LANG m on m.eid=a.eid and m.currency=a.currency and m.lang_type='"+req.getLangType()+"' " +
                " left join platforms_staffs_lang n1 on n1.eid=a.eid and n1.opno=a.craeteby and n1.lang_type='"+req.getLangType()+"' " +
                " left join platforms_staffs_lang n2 on n2.eid=a.eid and n2.opno=a.modyfyby and n2.lang_type='"+req.getLangType()+"' " +
                " left join platforms_staffs_lang n3 on n3.eid=a.eid and n3.opno=a.confirmby and n3.lang_type='"+req.getLangType()+"' " +
                " left join platforms_staffs_lang n4 on n4.eid=a.eid and n4.opno=a.cancelby and n4.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' " +
                " and a.taskid='"+req.getRequest().getTaskId()+"'  and a.accountid='"+req.getRequest().getAccountId()+"' " +
                "");
        if(Check.NotNull(req.getRequest().getStatus())){
            apBillSb.append(" and a.status='"+req.getRequest().getStatus()+"' ");
        }
        if(Check.NotNull(req.getRequest().getBizPartnerNo())){
            apBillSb.append(" and a.bizpartnerno='"+req.getRequest().getBizPartnerNo()+"' ");
        }

        List<Map<String, Object>> apList = this.doQueryData(apBillSb.toString(), null);

        StringBuffer appredSql=new StringBuffer("" +
                " select a.*,b.account,c.org_name as corpname,d.org_name,e.org_name as sourceorgname,f.name as currencyname," +
                "  as organizationname,g.subjectname as apsubjectname " +
                " from DCP_APPERD a" +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.corp and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.organizationno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang e on e.eid=a.eid and e.organizationno=a.sourceorgno and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG f on f.eid=a.eid and f.currency=a.currency and f.lang_type='"+req.getLangType()+"'  " +
                " left join dcp_coa g on g.eid=a.eid and g.subjectid=a.apsubjectid and g.accountid=a.accountid " +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " );
        List<Map<String, Object>> appredList = this.doQueryData(appredSql.toString(), null);

        if(CollUtil.isNotEmpty(apList)){
            Map<String, Object> som = apList.get(0);
            apData.setAccountId(som.get("ACCOUNTID").toString());
            apData.setAccount(som.get("ACCOUNT").toString());
            apData.setApType(som.get("APTYPE").toString());
            apData.setCorp(som.get("ORGANIZATIONNO").toString());
            apData.setCorpName(som.get("CORPNAME").toString());
            apData.setOrganizationNo(som.get("ORGANIZATIONNO").toString());
            apData.setOrganizationName(som.get("ORGANIZATIONNAME").toString());
            apData.setPDate(som.get("PDATE").toString());
            apData.setAccEmployeeNo(som.get("ACCEMPLOYEENO").toString());
            apData.setAccEmployeeName(som.get("ACCEMPLOYEENAME").toString());
            apData.setBizPartnerNo(som.get("BIZPARTNERNO").toString());
            apData.setBizPartnerName(som.get("BIZPARTNERNAME").toString());
            apData.setReceiver(som.get("RECEIVER").toString());
            apData.setReceiverName(som.get("RECEIVERNAME").toString());
            apData.setTaskId(som.get("TASKID").toString());
            apData.setPayDateNo(som.get("PAYDATENO").toString());
            apData.setPayDueDate(som.get("PAYDUEDATE").toString());
            apData.setTaxCode(som.get("TAXCODE").toString());
            apData.setTaxName(som.get("TAXNAME").toString());
            apData.setTaxRate(som.get("TAXRATE").toString());
            apData.setInclTax(som.get("INCLTAX").toString());
            apData.setApplicant(som.get("APPLICANT").toString());
            apData.setEmployeeNo(som.get("EMPLOYEENO").toString());
            apData.setEmployeeName(som.get("EMPLOYEENAME").toString());
            apData.setDepartId(som.get("DEPARTID").toString());
            apData.setDepartName(som.get("DEPARTNAME").toString());
            apData.setSourceType(som.get("SOURCETYPE").toString());
            apData.setSourceNo(som.get("SOURCENO").toString());
            apData.setPendOffsetNo(som.get("PENDOFFSETNO").toString());
            apData.setFeeSubjectId(som.get("FEESUBJECTID").toString());
            apData.setFeeSubjectName(som.get("FEESUBJECTNAME").toString());
            apData.setApSubjectId(som.get("APSUBJECTID").toString());
            apData.setApSubjectName(som.get("APSUBJECTNAME").toString());
            apData.setGlNo(som.get("GLNO").toString());
            apData.setGrpPmtNo(som.get("GRPMTNO").toString());
            apData.setMemo(som.get("MEMO").toString());
            apData.setPayList(som.get("PAYLIST").toString());
            apData.setCurrency(som.get("CURRENCY").toString());
            apData.setCurrencyName(som.get("CURRENCYNAME").toString());
            apData.setExRate(som.get("EXRATE").toString());
            apData.setFCYBTAmt(som.get("FCYBTAMT").toString());
            apData.setFCYTAmt(som.get("FCYTAMT").toString());
            apData.setFCYRevAmt(som.get("FCYREVAMT").toString());
            apData.setFCYTATAmt(som.get("FCYTATAmt").toString());
            apData.setLCYBTAmt(som.get("LCYBTAMT").toString());
            apData.setLCYTAmt(som.get("LCYTAMT").toString());
            apData.setLCYRevAmt(som.get("LCYREVAMT").toString());
            apData.setLCYPmtAmt(som.get("LCYPMTAMT").toString());
            apData.setFCYPmtAmt(som.get("FCYPMTAMT").toString());
            apData.setStatus(som.get("STATUS").toString());
            apData.setCreateBy(som.get("CREATEBY").toString());
            apData.setCreateByName(som.get("CREATEBYNAME").toString());
            apData.setCreate_Date(som.get("CREATE_DATE").toString());
            apData.setCreate_Time(som.get("CREATE_TIME").toString());
            apData.setModifyBy(som.get("MODIFYBY").toString());
            apData.setModifyByName(som.get("MODIFYBYNAME").toString());
            apData.setModify_Date(som.get("MODIFY_DATE").toString());
            apData.setModify_Time(som.get("MODIFY_TIME").toString());
            apData.setConfirmBy(som.get("CONFIRMBY").toString());
            apData.setConfirmByName(som.get("CONFIRMBYNAME").toString());
            apData.setConfirm_Date(som.get("CONFIRM_DATE").toString());
            apData.setConfirm_Time(som.get("CONFIRM_TIME").toString());
            apData.setCancelBy(som.get("CANCELBY").toString());
            apData.setCancelByName(som.get("CANCELBYNAME").toString());
            apData.setCancel_Date(som.get("CANCEL_DATE").toString());
            apData.setCancel_Time(som.get("CANCEL_TIME").toString());

            apData.setApPredList(new ArrayList<>());

            if(CollUtil.isNotEmpty(appredList)){
                for (Map<String, Object> appred : appredList) {
                    DCP_ApPrePayDetailQueryRes.ApPredList apPredList = res.new ApPredList();

                    apPredList.setAccountID(appred.get("ACCOUNTID").toString());
                    apPredList.setAccountName(appred.get("ACCOUNTNAME").toString());
                    apPredList.setCorp(appred.get("ORGANIZATIONNO").toString());
                    apPredList.setCorpName(appred.get("CORPNAME").toString());
                    apPredList.setSourceOrg(appred.get("SOURCEORG").toString());
                    apPredList.setSourceOrgName(appred.get("SOURCEORGNAME").toString());
                    apPredList.setOrganizationNo(appred.get("ORGANIZATIONNO").toString());
                    apPredList.setOrganizationName(appred.get("ORGANIZATIONNAME").toString());
                    apPredList.setApNo(appred.get("APNO").toString());
                    apPredList.setItem(appred.get("ITEM").toString());
                    apPredList.setInstPmtSeq(appred.get("INSTPMTSEQ").toString());
                    apPredList.setPayType(appred.get("PAYTYPE").toString());
                    apPredList.setPayDueDate(appred.get("PAYDUEDATE").toString());
                    apPredList.setBillDueDate(appred.get("BILLDUEDATE").toString());
                    apPredList.setDirection(appred.get("DIRECTION").toString());
                    apPredList.setFCYReqAmt(appred.get("FCYREQAMT").toString());
                    apPredList.setCurrency(appred.get("CURRENCY").toString());
                    apPredList.setCurrencyName(appred.get("CURRENCYNAME").toString());
                    apPredList.setExRate(appred.get("EXRATE").toString());
                    apPredList.setFCYRevsedRate(appred.get("FCYREVSEDRATE").toString());
                    apPredList.setFCYTATAmt(appred.get("FCYTATAmt").toString());
                    apPredList.setFCYPmtRevAmt(appred.get("FCYPMTREVAMT").toString());
                    apPredList.setRevalAdjNum(appred.get("REVALADJNUM").toString());
                    apPredList.setLCYTATAmt(appred.get("LCYTATAmt").toString());
                    apPredList.setLCYPmtRevAmt(appred.get("LCYPMTREVAMT").toString());
                    apPredList.setRevalAdjNum(appred.get("REVALADJNUM").toString());
                    apPredList.setLCYTATAmt(appred.get("LCYTATAmt").toString());
                    apPredList.setLCYPmtRevAmt(appred.get("LCYPMTREVAMT").toString());
                    apPredList.setPayDateNo(appred.get("PAYDATENO").toString());
                    apPredList.setPmtCategory(appred.get("PMTCATEGORY").toString());
                    apPredList.setPurOrderNo(appred.get("PURORDERNO").toString());
                    apPredList.setApSubjectId(appred.get("APSUBJECTID").toString());
                    apPredList.setApSubjectName(appred.get("APSUBJECTNAME").toString());
                    apPredList.setInvoiceNumber(appred.get("INVOICENUMBER").toString());
                    apPredList.setInvoiceCode(appred.get("INVOICECODE").toString());
                    apPredList.setInvoiceDate(appred.get("INVOICEDATE").toString());
                    apData.getApPredList().add(apPredList);
                }
            }
        }



        res.setDatas(apData);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ApPrePayDetailQueryReq req) throws Exception {
        return null;
    }


}
