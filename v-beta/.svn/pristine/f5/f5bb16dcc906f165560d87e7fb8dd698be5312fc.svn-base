package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ApWrtOffDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApWrtOffDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApWrtOffDetailQuery extends SPosBasicService<DCP_ApWrtOffDetailQueryReq, DCP_ApWrtOffDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApWrtOffDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ApWrtOffDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ApWrtOffDetailQueryReq>(){};
    }

    @Override
    protected DCP_ApWrtOffDetailQueryRes getResponseType() {
        return new DCP_ApWrtOffDetailQueryRes();
    }

    @Override
    protected DCP_ApWrtOffDetailQueryRes processJson(DCP_ApWrtOffDetailQueryReq req) throws Exception {
        DCP_ApWrtOffDetailQueryRes res = this.getResponse();

        //查询核销单
        String sql1="select a.*,b.account as accountName,c.org_name as corpName,d.sname as bizpartnername,e.org_name as receiverName," +
                " g.op_name as createbyname,g1.op_name as modifybyname,g2.op_name as confirmbyname,g3.op_name as cancelbyname  " +
                " from DCP_APWRTOFF a " +
                " left join dcp_acount_setting b on a.eid=b.eid and a.accountid=b.accountid" +
                " left join dcp_org_lang c on c.eid=a.eid and a.corp=c.organizationno and c.lnag_type='"+req.getLangType()+"'" +
                " left join dcp_bizpartner d on d.eid=a.eid and d.bizpartnerno=a.bizpartnerno" +
                " left join dcp_org_lang e on e.eid=a.eid and e.organizationno=a.receiver and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang f on f.eid=a.eid and f.organizationno=a.organizationno and f.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g on g.eid=a.eid and g.opno=a.createby and g.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g1 on g1.eid=a.eid and g1.opno=a.modifyby and g1.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g2 on g2.eid=a.eid and g2.opno=a.confirmby and g2.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g3 on g3.eid=a.eid and g3.opno=a.cancelby and g3.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.wrtOffNo='"+req.getRequest().getWrtOffNo()+"' " +
                " and a.accountid='"+req.getRequest().getAccountId()+"'";
        List<Map<String, Object>> list = this.doQueryData(sql1, null);
        if(CollUtil.isNotEmpty(list)){
            Map<String, Object> singleList = list.get(0);
            DCP_ApWrtOffDetailQueryRes.DatasLevel datasLevel = res.new DatasLevel();

            datasLevel.setTaskId(singleList.get("TASKID").toString());
            datasLevel.setWrtOffNo(singleList.get("WRTOFFNO").toString());
            datasLevel.setAccountId(singleList.get("ACCOUNTID").toString());
            datasLevel.setAccountName(singleList.get("ACCOUNTNAME").toString());
            datasLevel.setCorp(singleList.get("CORP").toString());
            datasLevel.setCorpName(singleList.get("CORPNAME").toString());
            datasLevel.setBDate(singleList.get("BDATE").toString());
            datasLevel.setBizPartnerNo(singleList.get("BIZPARTNERNO").toString());
            datasLevel.setBizPartnerName(singleList.get("BIZPARTNERNAME").toString());
            datasLevel.setReceiver(singleList.get("RECEIVER").toString());
            datasLevel.setReceiverName(singleList.get("RECEIVERNAME").toString());
            datasLevel.setSourceNo(singleList.get("SOURCENO").toString());
            datasLevel.setGlNo(singleList.get("GLNO").toString());
            datasLevel.setOrganizationNo(singleList.get("ORGANIZATIONNO").toString());
            datasLevel.setOrgName(singleList.get("ORGNAME").toString());
            datasLevel.setAccEmployeeNo(singleList.get("ACCEMPLOYEENO").toString());
            datasLevel.setEmployeeName(singleList.get("EMPLOYEENAME").toString());
            datasLevel.setFCYDRTATAmt(singleList.get("FCYDRTATAMT").toString());
            datasLevel.setLCYDRTATAmt(singleList.get("LCYDRTATAMT").toString());
            datasLevel.setFCYCRTATAmt(singleList.get("FCYCRTATAMT").toString());
            datasLevel.setLCYCRTATAmt(singleList.get("LCYCRTATAMT").toString());
            datasLevel.setIsAutoWriteoff(singleList.get("ISAUTOWRITEOFF").toString());

            datasLevel.setApWFList(new ArrayList<>());
            datasLevel.setPmtList(new ArrayList<>());

            datasLevel.setStatus(singleList.get("STATUS").toString());
            datasLevel.setCreateBy(singleList.get("CREATEBY").toString());
            datasLevel.setCreate_Date(singleList.get("CREATE_DATE").toString());
            datasLevel.setCreate_Time(singleList.get("CREATE_TIME").toString());
            datasLevel.setModifyBy(singleList.get("MODIFYBY").toString());
            datasLevel.setModify_Date(singleList.get("MODIFY_DATE").toString());
            datasLevel.setModify_Time(singleList.get("MODIFY_TIME").toString());
            datasLevel.setConfirmBy(singleList.get("CONFIRMBY").toString());
            datasLevel.setConfirm_Date(singleList.get("CONFIRM_DATE").toString());
            datasLevel.setConfirm_Time(singleList.get("CONFIRM_TIME").toString());
            datasLevel.setCancelBy(singleList.get("CANCELBY").toString());
            datasLevel.setCancel_Date(singleList.get("CANCEL_DATE").toString());
            datasLevel.setCancel_Time(singleList.get("CANCEL_TIME").toString());
            String billSql="select a.*,b.org_name as organizationname,c.org_name as sourceorgName,d.subjectname,e.name as employeename," +
                    " f.reason_name as bsname,h.category_name as categoryname,i.sname as bizpartnername,j.org_name as receiverName " +
                    " from DCP_APBILLWRTOFF a " +
                    " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.sourceorgno and c.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_coa d on d.eid=a.eid and d.subjectid=a.apSubjectId and a.accountid=d.accountid" +
                    " left join dcp_employee e on e.eid=a.eid and e.employeeno=a.employeeno  " +
                    " left join DCP_REASON_LANG f on f.eid=a.eid and f.bsno=a.bsno and f.lang_type='"+req.getLangType()+"'" +
                    " left join DCP_DEPARTMENT_LANG g on g.eid=a.eid and g.departno=a.departno and g.lang_type='"+req.getLangType()+"' " +
                    " left join DCP_CATEGORY_LANG h on h.eid=a.eid and h.category=a.category and h.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_bizpartner i on i.eid=a.eid and i.bizpartnerno=a.bizpartnerno " +
                    " left join dcp_org_lang j on j.eid=a.eid and j.organizationno=a.receiver and j.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+req.geteId()+"' and a.wrtOffNo='"+req.getRequest().getWrtOffNo()+"' " +
                    " and a.accountid='"+req.getRequest().getAccountId()+"'";
            List<Map<String, Object>> billList = this.doQueryData(billSql, null);

            for (Map<String, Object> bill : billList){
                DCP_ApWrtOffDetailQueryRes.ApWFListLevel apWFListLevel = res.new ApWFListLevel();

                apWFListLevel.setCorp(bill.get("CORP").toString());
                apWFListLevel.setOrganizationNo(bill.get("ORGANIZATIONNO").toString());
                apWFListLevel.setOrganizationName(bill.get("ORGANIZATIONNAME").toString());
                apWFListLevel.setAccountID(bill.get("ACCOUNTID").toString());
                apWFListLevel.setWrtOffNo(bill.get("WRTOFFNO").toString());
                apWFListLevel.setItem(bill.get("ITEM").toString());
                apWFListLevel.setTaskId(bill.get("TASKID").toString());
                apWFListLevel.setWrtOffType(bill.get("WRTOFFTYPE").toString());
                apWFListLevel.setSourceOrg(bill.get("SOURCEORG").toString());
                apWFListLevel.setSourceOrgName(bill.get("SOURCEORGNAME").toString());
                apWFListLevel.setWrtOffBillNo(bill.get("WRTOFFBILLNO").toString());
                apWFListLevel.setWrtOffBillitem(bill.get("WRTOFFBILLITEM").toString());
                apWFListLevel.setInstPmtSeq(bill.get("INSTPMTSEQ").toString());
                apWFListLevel.setMemo(bill.get("MEMO").toString());
                apWFListLevel.setBsNo(bill.get("BSNO").toString());
                apWFListLevel.setBsName(bill.get("BSNAME").toString());
                apWFListLevel.setWrtOffDirection(bill.get("WRTOFFDIRECTION").toString());
                apWFListLevel.setApSubjectId(bill.get("APSUBJECTID").toString());
                apWFListLevel.setApSubjectName(bill.get("APSUBJECTNAME").toString());
                apWFListLevel.setDepartId(bill.get("DEPARTID").toString());
                apWFListLevel.setDepartName(bill.get("DEPARTNAME").toString());
                apWFListLevel.setExRate(bill.get("EXRATE").toString());
                apWFListLevel.setCateGory(bill.get("CATEGORY").toString());
                apWFListLevel.setDepartId(bill.get("DEPARTID").toString());
                apWFListLevel.setDepartName(bill.get("DEPARTNAME").toString());
                apWFListLevel.setSecRefNo(bill.get("SECREFNO").toString());
                apWFListLevel.setPayDueDate(bill.get("PAYDUEDATE").toString());
                apWFListLevel.setReceiver(bill.get("RECEIVER").toString());
                apWFListLevel.setReceiverName(bill.get("RECEIVERNAME").toString());
                apWFListLevel.setBizPartnerNo(bill.get("BIZPARTNERNO").toString());
                apWFListLevel.setBizPartnerName(bill.get("BIZPARTNERNME").toString());
                apWFListLevel.setFreeChars1(bill.get("FREECHARS1").toString());
                apWFListLevel.setFreeChars2(bill.get("FREECHARS2").toString());
                apWFListLevel.setFreeChars3(bill.get("FREECHARS3").toString());
                apWFListLevel.setFreeChars4(bill.get("FREECHARS4").toString());
                apWFListLevel.setFreeChars5(bill.get("FREECHARS5").toString());
                apWFListLevel.setCurrency(bill.get("CURRENCY").toString());
                apWFListLevel.setInvoiceNumber(bill.get("INVOICENUMBER").toString());
                apWFListLevel.setInvoiceCode(bill.get("INVOICECODE").toString());
                apWFListLevel.setFCYRevAmt(bill.get("FCYREVAMT").toString());
                apWFListLevel.setLCYRevAmt(bill.get("LCYREVAMT").toString());
                apWFListLevel.setFCYBTaxWrtOffAmt(bill.get("FCYBTAXWRTOFFAMT").toString());
                apWFListLevel.setLCYBTaxWrtOffAmt(bill.get("LCYBTAXWRTOFFAMT").toString());
                datasLevel.getApWFList().add(apWFListLevel);
            }

            datasLevel.setPmtList(new ArrayList<>());
            String pmtSql="select a.*,b.org_name as organizationno,c.org_name as sourceorgName,d.payname as pmtName,e.org_name as receivername,f.subjectname as wrtOffSubjectName," +
                    " g.DEPWDRAWNAME as bankDEPWDRAWname,i.sname as transInCustName,h.cfname as cashChgName  " +
                    " from DCP_APBILLPMT a" +
                    " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"'  " +
                    " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.sourceorgno and c.lang_type='"+req.getLangType()+"' " +
                    " left join DCP_PAYTYPE_LANG d on d.eid=a.eid and d.paytype=a.pmtcode and d.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_org_lang e on e.eid=a.eid and e.organizationno=a.receiver and e.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_coa f on f.eid=a.eid and f.subjectid=a.wrtOffSubject and f.accountid=a.accountid " +
                    " left join DCP_DEPWDRAW g on g.eid=a.eid and g.DEPWDRAWCODE=a.bnkDepWdrawCode " +
                    " left join DCP_CFTEMPLATE h on h.eid=a.eid and h.CFCODE=a.cashChgCode " +
                    " left join dcp_bizpartner i on i.eid=a.eid and i.bizpartnerno=a.transInCustCode " +
                    " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' and a.wrtoffno='"+req.getRequest().getWrtOffNo()+"'  " +
                    " ";
            List<Map<String, Object>> pmtList = this.doQueryData(pmtSql,null);
            if(CollUtil.isNotEmpty(pmtList)){
                for (Map<String, Object> pmt  :pmtList){
                    DCP_ApWrtOffDetailQueryRes.PmtListLevel pmtListLevel = res.new PmtListLevel();
                    pmtListLevel.setCorp(pmt.get("CORP").toString());
                    pmtListLevel.setAccountID(pmt.get("ACCOUNTID").toString());
                    pmtListLevel.setWrtOffNo(pmt.get("WRTOFFNO").toString());
                    pmtListLevel.setItem(pmt.get("ITEM").toString());
                    pmtListLevel.setOrganizationNo(pmt.get("ORGANIZATIONNO").toString());
                    pmtListLevel.setOrganizationName(pmt.get("ORGANIZATIONNAME").toString());
                    pmtListLevel.setSourceOrg(pmt.get("SOURCEORG").toString());
                    pmtListLevel.setSourceOrgName(pmt.get("SOURCEORGNAME").toString());
                    pmtListLevel.setTaskId(pmt.get("TASKID").toString());
                    pmtListLevel.setWrtOffPmtType(pmt.get("WRTOFFPMTYPE").toString());
                    pmtListLevel.setPaidBillNo(pmt.get("PAIDBILLNO").toString());
                    pmtListLevel.setWrtOffItem(pmt.get("WRTOFFITEM").toString());
                    pmtListLevel.setPmtCode(pmt.get("PMCODE").toString());
                    pmtListLevel.setPmtName(pmt.get("PMNAME").toString());
                    pmtListLevel.setAccountBillNo(pmt.get("ACCOUNTBILLNO").toString());
                    pmtListLevel.setTransferredData(pmt.get("TRANSFERREDDATA").toString());
                    pmtListLevel.setMemo(pmt.get("MEMO").toString());
                    pmtListLevel.setWrtOffDirection(pmt.get("WRTOFFDIRECTION").toString());
                    pmtListLevel.setWrtOffSubject(pmt.get("WRTOFFSUBJECT").toString());
                    pmtListLevel.setWrtOffSubjectName(pmt.get("WRTOFFSUBJECTNAME").toString());
                    pmtListLevel.setSalerAccount(pmt.get("SALEACCOUNT").toString());
                    pmtListLevel.setSalerAccountCode(pmt.get("SALEACCOUNTCODE").toString());

                    pmtListLevel.setBnkDepWdrawCode(pmt.get("BNKDEPWDRWCODE").toString());
                    pmtListLevel.setBnkDepWdrawName(pmt.get("BNKDEPWDRWNAME").toString());
                    pmtListLevel.setCashChgCode(pmt.get("CASHCHGCODE").toString());
                    pmtListLevel.setCashChgName(pmt.get("CASHCHGNAME").toString());
                    pmtListLevel.setTransInCustCode(pmt.get("TRANSINCUSTCODE").toString());
                    pmtListLevel.setTransInCustName(pmt.get("TRANSINCUSTNAME").toString());
                    pmtListLevel.setTransInPmtBillNo(pmt.get("TRANSINPMTBILNO").toString());
                    pmtListLevel.setPayDueDate(pmt.get("PAYDUEDATE").toString());
                    pmtListLevel.setReceiver(pmt.get("RECEIVER").toString());
                    pmtListLevel.setReceiverName(pmt.get("RECEIVERNAME").toString());
                    pmtListLevel.setSaleName(pmt.get("SALENAME").toString());
                    pmtListLevel.setFreeChars1(pmt.get("FREECHARS1").toString());
                    pmtListLevel.setFreeChars2(pmt.get("FREECHARS2").toString());
                    pmtListLevel.setFreeChars3(pmt.get("FREECHARS3").toString());
                    pmtListLevel.setFreeChars4(pmt.get("FREECHARS4").toString());
                    pmtListLevel.setFreeChars5(pmt.get("FREECHARS5").toString());
                    pmtListLevel.setCurrency(pmt.get("CURRENCY").toString());
                    pmtListLevel.setExRate(pmt.get("EXRATE").toString());
                    pmtListLevel.setFCYRevAmt(pmt.get("FCYREVAMT").toString());
                    pmtListLevel.setLCYRevAmt(pmt.get("LCYREVAMT").toString());

                    datasLevel.getPmtList().add(pmtListLevel);

                }
            }

            res.setDatas(datasLevel);
        }






        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ApWrtOffDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        //分页处理

        return sqlbuf.toString();
    }
}


