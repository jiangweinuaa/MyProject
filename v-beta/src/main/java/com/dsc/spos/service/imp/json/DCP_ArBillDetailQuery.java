package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArBillDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArBillDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ArBillDetailQuery extends SPosBasicService<DCP_ArBillDetailQueryReq, DCP_ArBillDetailQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_ArBillDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArBillDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ArBillDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ArBillDetailQueryRes getResponseType() {
        return new DCP_ArBillDetailQueryRes();
    }

    @Override
    protected DCP_ArBillDetailQueryRes processJson(DCP_ArBillDetailQueryReq req) throws Exception {
        DCP_ArBillDetailQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> qDetail = doQueryData(getQueryDetailSql(req), null);
        List<Map<String, Object>> qPerd = doQueryData(getQueryArPerdSql(req), null);
        List<Map<String, Object>> qInv = doQueryData(getQueryArInvSql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {

            DCP_ArBillDetailQueryRes.Datas oneData = res.new Datas();
            res.getDatas().add(oneData);
            Map<String, Object> master = qData.get(0);
            oneData.setAccountId(master.get("ACCOUNTID").toString());
            oneData.setArType(master.get("ARTYPE").toString());
            oneData.setCorp(master.get("CORP").toString());
            oneData.setPDate(master.get("PDATE").toString());
            oneData.setOrganizationNo(master.get("ORGANIZATIONNO").toString());
            oneData.setAccEmployeeNo(master.get("ACCEMPLOYEENO").toString());
            oneData.setBizPartnerNo(master.get("BIZPARTNERNO").toString());
            oneData.setReceiver(master.get("RECEIVER").toString());
            oneData.setTaskId(master.get("TASKID").toString());
            oneData.setPayDateNo(master.get("PAYDATENO").toString());
            oneData.setPayDueDate(master.get("PAYDUEDATE").toString());
            oneData.setTaxCode(master.get("TAXCODE").toString());
            oneData.setTaxRate(master.get("TAXRATE").toString());
            oneData.setInclTax(master.get("INCLTAX").toString());
//            oneData.setApplicant(master.get("INCLTAX").toString());
            oneData.setEmployeeNo(master.get("EMPLOYEENO").toString());
            oneData.setDepartId(master.get("DEPARTNO").toString());
            oneData.setSourceType(master.get("SOURCETYPE").toString());
            oneData.setSourceNo(master.get("SOURCENO").toString());
            oneData.setPendOffsetNo(master.get("PENDOFFSETNO").toString());
//            oneData.setFeeSubjectId(master.get("PENDOFFSETNO").toString());
            oneData.setArSubjectId(master.get("ARSUBJECTID").toString());
            oneData.setGlNo(master.get("GLNO").toString());
            oneData.setGrpPmtNo(master.get("GRPPMTNO").toString());
            oneData.setCurrency(master.get("CURRENCY").toString());
            oneData.setMemo(master.get("MEMO").toString());
//            oneData.setPayList(master.get("MEMO").toString());
            oneData.setExRate(master.get("EXRATE").toString());
            oneData.setFCYBTAmt(master.get("FCYBTAMT").toString());
            oneData.setFCYTAmt(master.get("FCYTAMT").toString());
            oneData.setFCYRevAmt(master.get("FCYREVAMT").toString());
            oneData.setFCYTATAmt(master.get("FCYTATAMT").toString());
            oneData.setLCYBTAmt(master.get("LCYBTAMT").toString());
            oneData.setLCYTAmt(master.get("LCYTAMT").toString());
            oneData.setLCYRevAmt(master.get("LCYREVAMT").toString());
            oneData.setLCYTATAmt(master.get("LCYTATAMT").toString());
            oneData.setStatus(master.get("STATUS").toString());
//            oneData.setFCYPmtAmt(master.get("STATUS").toString());
//            oneData.setLCYPmtAmt(master.get("STATUS").toString());

            oneData.setCreateBy(master.get("CREATEBY").toString());
            oneData.setCreate_Date(master.get("CREATE_DATE").toString());
            oneData.setCreate_Time(master.get("CREATE_TIME").toString());
            oneData.setModifyBy(master.get("MODIFYBY").toString());
            oneData.setModify_Date(master.get("MODIFY_DATE").toString());
            oneData.setModify_Time(master.get("MODIFY_TIME").toString());
            oneData.setConfirmBy(master.get("CONFIRMBY").toString());
            oneData.setConfirm_Date(master.get("CONFIRM_DATE").toString());
            oneData.setConfirm_Time(master.get("CONFIRM_TIME").toString());
            oneData.setCancelBy(master.get("CANCELBY").toString());
            oneData.setCancel_Date(master.get("CANCEL_DATE").toString());
            oneData.setCancel_Time(master.get("CANCEL_TIME").toString());

//            private List<DCP_ArBillDetailQueryRes.ArBillSumList> arBillSumList;
//            private List<DCP_ArBillDetailQueryRes.ArPerdList> arPerdList;
//            private List<DCP_ArBillDetailQueryRes.ArWFList> arWFList;
//            private List<DCP_ArBillDetailQueryRes.RecList> recList;
//            private List<DCP_ArBillDetailQueryRes.EstList> estList;
//            private List<DCP_ArBillDetailQueryRes.ArInvList> arInvList;
            oneData.setArBillSumList(new ArrayList<>());
            oneData.setArPerdList(new ArrayList<>());
            oneData.setArWFList(new ArrayList<>());
            oneData.setRecList(new ArrayList<>());
            oneData.setEstList(new ArrayList<>());
            oneData.setArInvList(new ArrayList<>());
//            for (Map<String, Object> detail : qDetail) {
//
//               DCP_ArBillDetailQueryRes.ArInvList arList = res.new ArInvList();
//               oneData.getArInvList().add(arList);
//            }

            for (Map<String, Object> detail : qPerd) {

                DCP_ArBillDetailQueryRes.ArPerdList perdList = res.new ArPerdList();
                oneData.getArPerdList().add(perdList);

                perdList.setAccountID(detail.get("ACCOUNTID").toString());
                perdList.setCorp(detail.get("CORP").toString());
//               perdList.setSourceOrg(detail.get("ORGANIZATIONNO").toString());
                perdList.setOrganizationNo(detail.get("ORGANIZATIONNO").toString());
                perdList.setArNo(detail.get("ARNO").toString());
                perdList.setItem(detail.get("ITEM").toString());
                perdList.setInstPmtSeq(detail.get("INSTPMTSEQ").toString());
                perdList.setPayType(detail.get("PAYTYPE").toString());
                perdList.setPayDateNo(detail.get("PAYDUEDATE").toString());
                perdList.setBillDueDate(detail.get("BILLDUEDATE").toString());
                perdList.setDirection(detail.get("DIRECTION").toString());
//               perdList.setFCYReqAmt(detail.get("DIRECTION").toString());
                perdList.setCurrency(detail.get("CURRENCY").toString());
                perdList.setExRate(detail.get("EXRATE").toString());
                perdList.setFCYRevsedRate(detail.get("FCYREVSEDRATE").toString());
                perdList.setFCYTATAmt(detail.get("FCYTATAMT").toString());
                perdList.setFCYPmtRevAmt(detail.get("FCYPMTREVAMT").toString());
                perdList.setLCYRevalAdjNum(detail.get("LCYREVALADJNUM").toString());
                perdList.setLCYTATAmt(detail.get("LCYTATAMT").toString());
                perdList.setLCYPmtRevAmt(detail.get("LCYPMTREVAMT").toString());
                perdList.setPayDateNo(detail.get("PAYDATENO").toString());
                perdList.setPmtCategory(detail.get("PMTCATEGORY").toString());
                perdList.setPoNo(detail.get("PONO").toString());
                perdList.setArSubjectId(detail.get("ARSUBJECTID").toString());
                perdList.setInvoiceNumber(detail.get("INVOICENUMBER").toString());
                perdList.setInvoiceCode(detail.get("INVOICECODE").toString());
                perdList.setInvoiceDate(detail.get("INVOICEDATE").toString());

            }

            for (Map<String, Object> detail : qInv) {

                DCP_ArBillDetailQueryRes.ArInvList arList = res.new ArInvList();
                oneData.getArInvList().add(arList);

                arList.setPurInvNo(detail.get("PURINVNO").toString());
                arList.setItem(detail.get("ITEM").toString());
                arList.setInvSource(detail.get("INVSOURCE").toString());
                arList.setBizPartnerNo(detail.get("BIZPARTNERNO").toString());
                arList.setOrganizationNo(detail.get("ORGANIZATIONNO").toString());
                arList.setInvoiceType(detail.get("INVOICETYPE").toString());
                arList.setInvoiceCode(detail.get("INVOICECODE").toString());
                arList.setInvoiceNumber(detail.get("INVOICENUMBER").toString());
                arList.setInvoiceDate(detail.get("INVOICEDATE").toString());
                arList.setTaxCode(detail.get("TAXCODE").toString());
                arList.setTaxRate(detail.get("TAXRATE").toString());
                arList.setIsAfterTax(detail.get("ISAFTERTAX").toString());
                arList.setCurrency(detail.get("CURRENCY").toString());
                arList.setExRate(detail.get("EXRATE").toString());
                arList.setInvFCYBTAmt(detail.get("INVFCYBTAMT").toString());
                arList.setInvFCYTAmt(detail.get("INVFCYTAMT").toString());
                arList.setInvFCYATAmt(detail.get("INVFCYATAMT").toString());
                arList.setInvLCYBTAmt(detail.get("INVLCYBTAMT").toString());
                arList.setInvLCYTAmt(detail.get("INVLCYTAMT").toString());
                arList.setInvLCYATAmt(detail.get("INVLCYATAMT").toString());
                arList.setRecType(detail.get("RECTYPE").toString());
                arList.setDedctblNo(detail.get("DEDCTBLNO").toString());
                arList.setArNo(detail.get("APNO").toString());
                arList.setIsEInvoice(detail.get("ISEINVOICE").toString());

            }

        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }


    protected String getQueryArInvSql(DCP_ArBillDetailQueryReq req) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT c.*,b.INVSOURCE,b.BIZPARTNERNO  ")
                .append(" FROM DCP_ARBILL a ")
                .append(" LEFT JOIN DCP_PURINV b on a.eid=b.eid  and a.ARNO=b.APNO ")
                .append(" LEFT JOIN DCP_PURINVDETAIL c on b.eid=c.eid and b.PURINVNO=c.PURINVNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            builder.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND a.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        return builder.toString();
    }


    protected String getQueryArPerdSql(DCP_ArBillDetailQueryReq req) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT b.*  ")
                .append(" FROM DCP_ARBILL a ")
                .append(" LEFT JOIN DCP_ARPERD b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID and a.ARNO=b.ARNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            builder.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND a.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        return builder.toString();
    }


    protected String getQueryDetailSql(DCP_ArBillDetailQueryReq req) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT b.*  ")
                .append(" FROM DCP_ARBILL a ")
                .append(" LEFT JOIN DCP_ARBILLDETAIL b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID and a.ARNO=b.ARNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            builder.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND a.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        return builder.toString();
    }

    @Override
    protected String getQuerySql(DCP_ArBillDetailQueryReq req) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT a.*  ")
                .append(" FROM DCP_ARBILL a ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            builder.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND a.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }


        return builder.toString();
    }
}
