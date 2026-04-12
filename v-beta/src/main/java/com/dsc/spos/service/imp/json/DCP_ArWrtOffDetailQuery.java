package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArWrtOffDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArWrtOffDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ArWrtOffDetailQuery extends SPosBasicService<DCP_ArWrtOffDetailQueryReq, DCP_ArWrtOffDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ArWrtOffDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArWrtOffDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ArWrtOffDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ArWrtOffDetailQueryRes getResponseType() {
        return new DCP_ArWrtOffDetailQueryRes();
    }

    @Override
    protected DCP_ArWrtOffDetailQueryRes processJson(DCP_ArWrtOffDetailQueryReq req) throws Exception {
        DCP_ArWrtOffDetailQueryRes res = this.getResponseType();

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> recData = doQueryData(getQueryArBillRec(req), null);
        List<Map<String, Object>> wrtoffData = doQueryData(getQueryArBillWrtOff(req), null);

        if (CollectionUtils.isNotEmpty(qData)) {

            res.setDatas(new ArrayList<>());

            for (Map<String, Object> q : qData) {
                DCP_ArWrtOffDetailQueryRes.Request oneData = res.new Request();
                res.getDatas().add(oneData);

                oneData.setAccountId(q.get("ACCOUNTID").toString());
                oneData.setAccountName(q.get("ACCOUNTI").toString());
                oneData.setCorp(q.get("CORP").toString());
                oneData.setCorpName(q.get("CORPNAME").toString());
                oneData.setArType(q.get("ARTYPE").toString());
                oneData.setBDate(q.get("BDATE").toString());
                oneData.setOrganizationNo(q.get("ORGANIZATIONNO").toString());
                oneData.setOrganizationName(q.get("ORGANIZATIONNNAME").toString());
                oneData.setBizPartnerNo(q.get("BIZPARTNERNO").toString());
                oneData.setBizPartnerName(q.get("BIZPARTNERNANE").toString());
                oneData.setReceiver(q.get("RECEIVER").toString());
                oneData.setReceiverName(q.get("RECEIVERNAME").toString());
                oneData.setAccEmployeeNo(q.get("ACCEMPLOYEENO").toString());
                oneData.setAccEmployeeName(q.get("ACCEMPLOYEENAME").toString());
                oneData.setSourceNo(q.get("SOURCENO").toString());
//                oneData.setWrtOff(q.get("SOURCENO").toString());
                oneData.setGlNo(q.get("SOURCENO").toString());
                oneData.setGrpPmtNo(q.get("GRPPMTNO").toString());
                oneData.setFCYDRTATAmt(q.get("FCYDRTATAMT").toString());
                oneData.setFCYCRTATAmt(q.get("FCYCRTATAMT").toString());
                oneData.setLCYCRTATAmt(q.get("LCYDRTATAMT").toString());
                oneData.setLCYDRTATAmt(q.get("LCYCRTATAMT").toString());
                oneData.setMemo(q.get("MEMO").toString());

                oneData.setStatus(q.get("STATUS").toString());
                oneData.setCreateBy(q.get("CREATEBY").toString());
                oneData.setCreate_Date(q.get("CREATE_DATE").toString());
                oneData.setCreate_Time(q.get("CREATE_TIME").toString());
                oneData.setModifyBy(q.get("MODIFYBY").toString());
                oneData.setModify_Date(q.get("MODIFY_DATE").toString());
                oneData.setModify_Time(q.get("MODIFY_TIME").toString());
                oneData.setConfirmBy(q.get("CONFIRMBY").toString());
                oneData.setConfirm_Date(q.get("CONFIRM_DATE").toString());
                oneData.setConfirm_Time(q.get("CONFIRM_TIME").toString());
                oneData.setCancelBy(q.get("CANCELBY").toString());
                oneData.setCancel_Date(q.get("CANCEL_DATE").toString());
                oneData.setCancel_Time(q.get("CANCEL_DATE").toString());

                oneData.setArWFList(new ArrayList<>());
                oneData.setArRecList(new ArrayList<>());

                Map<String, Object> filter = new HashMap<>();
                filter.put("EID", q.get("EID"));
                filter.put("ACCOUNTID", q.get("ACCOUNTID"));
                filter.put("ARNO", q.get("ARNO"));
                List<Map<String, Object>> recList = MapDistinct.getWhereMap(recData, filter, true);

                for (Map<String, Object> rec : recList) {

                    DCP_ArWrtOffDetailQueryRes.ArRecList oneRec = res.new ArRecList();
                    oneData.getArRecList().add(oneRec);

                    oneRec.setAccountId(rec.get("ACCOUNTID").toString());
                    oneRec.setArNo(rec.get("ARNO").toString());
                    oneRec.setItem(rec.get("ITEM").toString());
                    oneRec.setAccOrg(rec.get("ACCORG").toString());
                    oneRec.setOrganizationNo(rec.get("ORGANIZATIONNO").toString());
                    oneRec.setOrganizationName(rec.get("ORGANIZATIONNAME").toString());
                    oneRec.setTaskId(rec.get("TASKID").toString());
                    oneRec.setWrtOffType(rec.get("WRTOFFTYPE").toString());
                    oneRec.setBizPartnerNo(rec.get("BIZPARTNERNO").toString());
                    oneRec.setBizPartnerName(rec.get("BIZPARTNERNAME").toString());
                    oneRec.setReceiver(rec.get("RECEIVER").toString());
                    oneRec.setReceiverName(rec.get("RECEIVERNNAME").toString());
                    oneRec.setSourceNo(rec.get("SOURCENO").toString());
//                   oneRec.setSourceOrg(rec.get("ACCORG").toString());
//                   oneRec.setSourceOrgName(rec.get("ACCORG").toString());
                    oneRec.setSourceType(rec.get("SOURCETYPE").toString());
                    oneRec.setSourceItem(rec.get("SOURCEITEM").toString());
//                   oneRec.setTypeNature(rec.get("SOURCEITEM").toString());
                    oneRec.setCardNo(rec.get("CARDNO").toString());
//                   oneRec.setCardName(rec.get("CARDNO").toString());
                    oneRec.setClassNo(rec.get("CLASSNO").toString());
                    oneRec.setBnkDepWdrawCode(rec.get("BNKDEPWDRAWCODE").toString());
                    oneRec.setBnkDepWdrawName(rec.get("BNKDEPWDRAWNAME").toString());
                    oneRec.setCashChgCode(rec.get("CASHCHGCODE").toString());
                    oneRec.setCashChgName(rec.get("CASHCHGNAME").toString());
                    oneRec.setTransInPmtBillNo(rec.get("TRANSINPMTBILLNO").toString());
                    oneRec.setWrtOffDirection(rec.get("WRTOFFDIRECTION").toString());
                    oneRec.setArSubjectId(rec.get("ARSUBJECTID").toString());
                    oneRec.setArSubjectName(rec.get("ARSUBJECTNAME").toString());
                    oneRec.setEmployeeNo(rec.get("EMPLOYEENO").toString());
                    oneRec.setEmployeeName(rec.get("EMPLOYEENAME").toString());
                    oneRec.setDepartId(rec.get("DEPARTNO").toString());
                    oneRec.setDepartName(rec.get("DEPARTNAME").toString());
                    oneRec.setCurrency(rec.get("CURRENCY").toString());
                    oneRec.setExRate(rec.get("EXRATE").toString());
                    oneRec.setFCYRevAmt(rec.get("FCYREVAMT").toString());
                    oneRec.setLCYRevAmt(rec.get("LCYREVAMT").toString());
                    oneRec.setRecDate(rec.get("RECDATE").toString());
                    oneRec.setCateGory(rec.get("CATEGORY").toString());
                    oneRec.setCateGoryName(rec.get("CATEGORYNAME").toString());
                    oneRec.setFreeChars1(rec.get("FREECHARS1").toString());
                    oneRec.setFreeChars2(rec.get("FREECHARS2").toString());
                    oneRec.setFreeChars3(rec.get("FREECHARS3").toString());
                    oneRec.setFreeChars4(rec.get("FREECHARS4").toString());
                    oneRec.setFreeChars5(rec.get("FREECHARS5").toString());
                    oneRec.setMemo(rec.get("MEMO").toString());

                }

                Map<String, Object> filter1 = new HashMap<>();
                filter.put("EID", q.get("EID"));
                filter.put("ACCOUNTID", q.get("ACCOUNTID"));
                filter.put("WRTOFFNO", q.get("ARNO"));

                List<Map<String, Object>> wrtoffList = MapDistinct.getWhereMap(wrtoffData, filter1, true);
                for (Map<String, Object> wrtoff : wrtoffList) {

                    DCP_ArWrtOffDetailQueryRes.ArWFList oneWf = res.new ArWFList();
                    oneData.getArWFList().add(oneWf);

                    oneWf.setCorp(wrtoff.get("CORP").toString());
                    oneWf.setOrganizationNo(wrtoff.get("ORGANIZATIONNO").toString());
                    oneWf.setOrganizationName(wrtoff.get("ORGANIZATIONNAME").toString());
                    oneWf.setAccountID(wrtoff.get("ACCOUNTID").toString());
                    oneWf.setWrtOffNo(wrtoff.get("WRTOFFNO").toString());
                    oneWf.setItem(wrtoff.get("ITEM").toString());
                    oneWf.setTaskId(wrtoff.get("TASKID").toString());
                    oneWf.setWrtOffType(wrtoff.get("WRTOFFTYPE").toString());
                    oneWf.setSourceOrg(wrtoff.get("SOURCEORG").toString());
                    oneWf.setSourceOrgName(wrtoff.get("SOURCEORGNAME").toString());
                    oneWf.setWrtOffBillNo(wrtoff.get("WRTOFFBILLNO").toString());
                    oneWf.setWrtOffBillitem(wrtoff.get("WRTOFFBILLITEM").toString());
                    oneWf.setInstPmtSeq(wrtoff.get("INSTPMTSEQ").toString());
                    oneWf.setMemo(wrtoff.get("MEMO").toString());
                    oneWf.setWrtOffDirection(wrtoff.get("WRTOFFDIRECTION").toString());
                    oneWf.setArSubjectId(wrtoff.get("ARSUBJECTID").toString());
                    oneWf.setArSubjectName(wrtoff.get("ARSUBJECTNAME").toString());
                    oneWf.setEmployeeNo(wrtoff.get("EMPLOYEENO").toString());
                    oneWf.setEmployeeName(wrtoff.get("EMPLOYEENAME").toString());
                    oneWf.setDepartId(wrtoff.get("DEPARTNO").toString());
                    oneWf.setDepartName(wrtoff.get("DEPARTNAME").toString());
                    oneWf.setCateGory(wrtoff.get("CATEGORY").toString());
                    oneWf.setCateGoryName(wrtoff.get("CATEGORYNAME").toString());
                    oneWf.setSecRefNo(wrtoff.get("SECREFNO").toString());
                    oneWf.setGlNo(wrtoff.get("GLNO").toString());
//                    oneWf.setPayDueDate(wrtoff.get("GLNO").toString());
                    oneWf.setBizPartnerNo(wrtoff.get("BIZPARTNERNO").toString());
                    oneWf.setBizPartnerName(wrtoff.get("BIZPARTNERNAME").toString());
                    oneWf.setReceiver(wrtoff.get("RECEIVER").toString());
                    oneWf.setReceiverName(wrtoff.get("RECEIVERNAME").toString());
                    oneWf.setFreeChars1(wrtoff.get("FREECHARS1").toString());
                    oneWf.setFreeChars2(wrtoff.get("FREECHARS2").toString());
                    oneWf.setFreeChars3(wrtoff.get("FREECHARS3").toString());
                    oneWf.setFreeChars4(wrtoff.get("FREECHARS4").toString());
                    oneWf.setFreeChars5(wrtoff.get("FREECHARS5").toString());
                    oneWf.setCurrency(wrtoff.get("CURRENCY").toString());
                    oneWf.setExRate(wrtoff.get("EXRATE").toString());
                    oneWf.setFCYRevAmt(wrtoff.get("FCYREVAMT").toString());
                    oneWf.setLCYRevAmt(wrtoff.get("LCYREVAMT").toString());
                    oneWf.setFCYBTaxWrtOffAmt(wrtoff.get("FCYBTAXWRTOFFAMT").toString());
                    oneWf.setLCYBTaxWrtOffAmt(wrtoff.get("LCYBTAXWRTOFFAMT").toString());
                    oneWf.setInvoiceNumber(wrtoff.get("INVOICENUMBER").toString());
                    oneWf.setInvoiceCode(wrtoff.get("INVOICECODE").toString());
                    oneWf.setBillPrice(wrtoff.get("BILLPRICE").toString());
                    oneWf.setDirection(wrtoff.get("DIRECTION").toString());
                    oneWf.setPendOffsetNo(wrtoff.get("PENDOFFSETNO").toString());
                    oneWf.setApNo(wrtoff.get("APNO").toString());
                    oneWf.setAdvNo(wrtoff.get("ADVNO").toString());
                    
                }

            }


        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private String getQueryArBillWrtOff(DCP_ArWrtOffDetailQueryReq req) throws Exception {

        StringBuilder querSql = new StringBuilder();

        querSql.append("SELECT ")
                .append(" c.*,b.TASKID,b.ARTYPE ")
                .append(" FROM DCP_ARWRTOFF  a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.eid=b.eid and a.ARNO=b.ARNO and a.ACCOUNTID=b.ACCOUNTID ")
                .append(" LEFT JOIN DCP_ARBILLWRTOFF c on c.eid=a.eid and c.ACCOUNTID=a.ACCOUNTID and c.WRTOFFNO=a.ARNO ")
        ;

        querSql.append(" WHERE a.eid ='").append(req.geteId()).append("' ");
        if (Check.isNotEmpty(req.getRequest().getArNo())) {
            querSql.append(" and a.ARNO = '").append(req.getRequest().getArNo()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            querSql.append(" and a.ACCOUNTID ='").append(req.getRequest().getAccountId()).append("' ");
        }
        if (Check.isNotEmpty(req.getRequest().getTaskId())) {
            querSql.append(" and b.TASKID ='").append(req.getRequest().getTaskId()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            querSql.append(" and a.BIZPARTNERNO ='").append(req.getRequest().getBizPartnerNo()).append("' ");
        }


        return querSql.toString();
    }

    private String getQueryArBillRec(DCP_ArWrtOffDetailQueryReq req) throws Exception {
        StringBuilder querSql = new StringBuilder();

        querSql.append("SELECT ")
                .append(" c.*,b.TASKID,b.ARTYPE ")
                .append(" FROM DCP_ARWRTOFF  a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.eid=b.eid and a.ARNO=b.ARNO and a.ACCOUNTID=b.ACCOUNTID ")
                .append(" LEFT JOIN DCP_ARBILLREC c on c.eid=a.eid and c.ACCOUNTID=a.ACCOUNTID and c.ARNO=a.ARNO ")
        ;

        querSql.append(" WHERE a.eid ='").append(req.geteId()).append("' ");
        if (Check.isNotEmpty(req.getRequest().getArNo())) {
            querSql.append(" and a.ARNO = '").append(req.getRequest().getArNo()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            querSql.append(" and a.ACCOUNTID ='").append(req.getRequest().getAccountId()).append("' ");
        }
        if (Check.isNotEmpty(req.getRequest().getTaskId())) {
            querSql.append(" and b.TASKID ='").append(req.getRequest().getTaskId()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            querSql.append(" and a.BIZPARTNERNO ='").append(req.getRequest().getBizPartnerNo()).append("' ");
        }


        return querSql.toString();
    }

    @Override
    protected String getQuerySql(DCP_ArWrtOffDetailQueryReq req) throws Exception {

        StringBuilder querSql = new StringBuilder();

        querSql.append("SELECT ")
                .append(" a.*,b.TASKID,b.ARTYPE ")
                .append(" FROM DCP_ARWRTOFF  a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.eid=b.eid and a.ARNO=b.ARNO and a.ACCOUNTID=b.ACCOUNTID ")
        ;
        querSql.append(" WHERE a.eid ='").append(req.geteId()).append("' ");
        if (Check.isNotEmpty(req.getRequest().getArNo())) {
            querSql.append(" and a.ARNO = '").append(req.getRequest().getArNo()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            querSql.append(" and a.ACCOUNTID ='").append(req.getRequest().getAccountId()).append("' ");
        }
        if (Check.isNotEmpty(req.getRequest().getTaskId())) {
            querSql.append(" and b.TASKID ='").append(req.getRequest().getTaskId()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            querSql.append(" and a.BIZPARTNERNO ='").append(req.getRequest().getBizPartnerNo()).append("' ");
        }


        return querSql.toString();
    }
}
