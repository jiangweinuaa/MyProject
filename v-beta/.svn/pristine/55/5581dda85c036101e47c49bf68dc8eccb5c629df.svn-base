package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ApBillDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApBillDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ApBillDetailQuery  extends SPosBasicService<DCP_ApBillDetailQueryReq, DCP_ApBillDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ApBillDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ApBillDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ApBillDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ApBillDetailQueryRes getResponseType() {
        return new DCP_ApBillDetailQueryRes();
    }

    @Override
    protected DCP_ApBillDetailQueryRes processJson(DCP_ApBillDetailQueryReq req) throws Exception {
        DCP_ApBillDetailQueryRes res = this.getResponseType();
        DCP_ApBillDetailQueryRes.Datas datas = res.new Datas();
        datas.setApBillSumList(new ArrayList<>());
        datas.setApPerdList(new ArrayList<>());
        datas.setApWFList(new ArrayList<>());
        datas.setPmtList(new ArrayList<>());
        datas.setEstList(new ArrayList<>());
        datas.setApInvList(new ArrayList<>());
        datas.setApBillList(new ArrayList<>());

        //应付单 DCP_APBILL
        String apBillSql="select a.*,b.account as accountname,c.org_name as organizationname,d.name as accEmployeeName,e.sname as bizPartnerName,f.taxname," +
                " g.name as employeename,h.departname,h1.departname as apdepartname,i.subjectname as feesubjectname,i1.subjectname as apsubjectname,j.name as currencyname,j1.name as icycurrencyname,j2.name as fcycurrencyname," +
                " k.op_name as createbyName,l.op_name as modifybyname,m.op_name as confirmbyname,n.op_name as cancelbyname," +
                " o.name as applicantname,p.name as paydatename,q.sname as receivername,b.coarefid " +
                " from DCP_APBILL a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on c.eid=a.eid and a.organizationNo=c.organizationNo and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_employee d on d.eid=a.eid and a.accEmployeeNo=d.employeeno " +
                " left join dcp_bizpartner e on e.eid=a.eid and e.bizPartnerno=a.bizPartnerNo " +
                " left join DCP_TAXCATEGORY_LANG f on f.eid=a.eid and f.taxcode=a.taxcode and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee g on g.eid=a.eid and g.employeeNo=a.employeeNo " +
                " left join dcp_department_lang h on h.eid=a.eid and h.departno=a.departno and h.lang_type='"+req.getLangType()+"'" +
                " left join dcp_department_lang h1 on h1.eid=a.eid and h1.departno=a.apdepartno and h1.lang_type='"+req.getLangType()+"'" +
                " left join DCP_COA i on i.eid=a.eid and i.subjectid=a.feesubjectid and i.accountid=a.accountid " +
                " left join DCP_COA i1 on i1.eid=a.eid and i1.subjectid=a.apsubjectid and i1.accountid=a.accountid " +
                " left join DCP_CURRENCY_LANG j on j.eid=a.eid and a.currency=j.currency " +
                " left join DCP_CURRENCY_LANG j1 on j1.eid=a.eid and a.lcycurrency=j1.currency " +
                " left join DCP_CURRENCY_LANG j2 on j2.eid=a.eid and a.fcycurrency=j2.currency " +
                " left join platform_staffs_lang k on k.eid=a.eid and k.opno=a.createby and k.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang l on l.eid=a.eid and l.opno=a.modifyby and l.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang m on m.eid=a.eid and m.opno=a.confirmby and m.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang n on n.eid=a.eid and n.opno=a.cancelby and n.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee o on o.eid=a.eid and o.employeeno=a.applicant  " +
                " left join DCP_PAYDATE_LANG p on p.eid=a.eid and p.paydateno=a.paydateno and p.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner q on q.eid=a.eid and a.receiver=q.bizpartnerno " +

                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apBillList = this.doQueryData(apBillSql,null);

        String apBillDetailSql="select distinct a.*,b.sname as bizpartnername,c.org_name as receivername,d.org_name as sourceorgName,e.plu_name as pluname,f.uname as priceunitname," +
                " g.fee_name as feename,h.departname,i.category_name as categoryname,j.reason_name as bsname,k.subjectname as feeSubjectName,k0.subjectname as apSubjectName,k1.subjectname as taxSubjectName," +
                " l.name as paydatename,m.name as employeename,n.name as currencyname,o.taxname  " +
                " from DCP_APBILLDETAIL a " +
                " left join dcp_bizpartner b on a.eid=b.eid and a.bizpartnerno=b.bizpartnerno " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.receiver and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.sourceorg and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_lang e on e.eid=a.eid and e.pluno=a.pluno and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.priceUnit and f.lang_type='"+req.getLangType()+"' " +
                " left join DCP_FEE_LANG g on g.eid=a.eid and g.fee=a.fee and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_department_lang h on h.eid=a.eid and h.departno=a.departno and h.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG i on i.eid=a.eid and i.category=a.category and i.lang_type='"+req.getLangType()+"' " +
                " left join DCP_REASON_LANG j on j.eid=a.eid and j.bsno=a.bsno and j.lang_type='"+req.getLangType()+"'" +
                " left join dcp_coa k on k.eid=a.eid and k.accountid=a.accountid and k.subjectid=a.feeSubjectId " +
                " left join dcp_coa k0 on k0.eid=a.eid and k0.accountid=a.accountid and k0.subjectid=a.apSubjectId " +
                " left join dcp_coa k1 on k1.eid=a.eid and k1.accountid=a.accountid and k1.subjectid=a.taxSubjectId " +
                " left join DCP_PAYDATE_LANG l on l.eid=a.eid and l.paydateno=a.paydateno and l.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee m on m.eid=a.eid and m.employeeno=a.employeeNo " +
                " left join DCP_CURRENCY_LANG n on n.eid=a.eid and n.currency=a.currency and n.lang_type='"+req.getLangType()+"' " +
                " left join DCP_TAXCATEGORY_LANG o on o.eid=a.eid and o.taxcode=a.taxcode and o.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apBillDetailList = this.doQueryData(apBillDetailSql,null);

        //应付单明细汇总 DCP_APBILLDETAILSUM
        String apBillDetailSumSql="select distinct a.*,b.sname as bizpartnername,c.org_name as receivername,d.org_name as sourceorgName," +
                " h.departname,i.category_name as categoryname,k.subjectname as feeSubjectName,k0.subjectname as apSubjectName,k1.subjectname as taxSubjectName," +
                " m.name as employeename,n.name as currencyname  " +
                " from DCP_APBILLDETAILSUM a " +
                " left join dcp_bizpartner b on a.eid=b.eid and a.bizpartnerno=b.bizpartnerno " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.receiver and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.sourceorg and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_department_lang h on h.eid=a.eid and h.departno=a.departno and h.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG i on i.eid=a.eid and i.category=a.category and i.lang_type='"+req.getLangType()+"' " +
                " left join dcp_coa k on k.eid=a.eid and k.accountid=a.accountid and k.subjectid=a.feeSubjectId " +
                " left join dcp_coa k0 on k0.eid=a.eid and k0.accountid=a.accountid and k0.subjectid=a.apSubjectId " +
                " left join dcp_coa k1 on k1.eid=a.eid and k1.accountid=a.accountid and k1.subjectid=a.taxSubjectId " +
                " left join dcp_employee m on m.eid=a.eid and m.employeeno=a.employeeNo " +
                " left join DCP_CURRENCY_LANG n on n.eid=a.eid and n.currency=a.currency and n.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apBillDetailSumList = this.doQueryData(apBillDetailSumSql,null);
        //应付单 DCP_APBILLDETAIL
        //String apBillDetailSql="select a.* " +
        //        " from DCP_APBILLDETAIL a " +
        //        " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        //List<Map<String, Object>> apBillDetailList = this.doQueryData(apBillDetailSql,null);
        //应付单账期 DCP_APPERD
        String apPerdSql="select distinct a.*,b.account as accountname,c.org_name as sourceorgname,e.name as currencyname,f.category_name as pmtcategoryname,g.subjectname as apsubjectname " +
                " from DCP_APPERD a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.sourceorg and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.organizationno and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG e on e.eid=a.eid and e.currency=a.currency and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG f on f.eid=a.eid and a.pmtcategory=f.category and f.lang_type='"+req.getLangType()+"'" +
                " left join dcp_coa g on g.eid=a.eid and g.accountid=a.accountid and a.apSubjectId=g.subjectid " +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apPerdList = this.doQueryData(apPerdSql,null);
        //应付单核销明细 DCP_APBILLWRTOFF
        String apWFSql="select distinct a.*,b.account as accountname,c.org_name as organizationname,d.org_name as sourceorgName,e.reason_name as bsname, " +
                " f.subjectname as apSubjectName,g.name as employeename,h.departname,j.sname as bizpartnername,k.org_name as receivername,l.name as currencyname " +
                " from DCP_APBILLWRTOFF a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.sourceorg and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_REASON_LANG e on e.eid=a.eid and e.bsno=a.bsno and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_coa f on f.eid=a.eid and f.accountid=a.accountid and f.subjectid=a.apSubjectId " +
                " left join dcp_employee g on g.eid=a.eid and g.employeeno=a.employeeno " +
                " left join dcp_department_lang h on h.eid=a.eid and h.departno=a.departno and h.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG i on i.eid=a.eid and i.category=a.category and i.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.bizpartnerno " +
                " left join dcp_org_lang k on k.eid=a.eid and k.organizationno=a.receiver and k.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG l on l.eid=a.eid and l.currency=a.currency and l.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.WRTOFFNO='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apWFList = this.doQueryData(apWFSql,null);
        //应付单付款明细 DCP_APBILLPMT
        String apPmtSql="select distinct a.*,b.account as accountname,c.org_name as organizationname,d.org_name as sourceorgname,e.name as currencyname " +
                " from DCP_APBILLPMT a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.sourceorg and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG e on e.eid=a.eid and e.currency=a.currency and e.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.WRTOFFNO='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apPmtList = this.doQueryData(apPmtSql,null);

        //应付单冲暂估明细DCP_APBILLESTDTL
        String apEstSql="select a.* " +
                " from DCP_APBILLESTDTL a " +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apEstList = this.doQueryData(apEstSql,null);

        //进项发票主档 DCP_PURINV
        String apInvSql="select a.* " +
                " from DCP_PURINV a " +
                " where a.eid='"+req.geteId()+"' and a.apno='"+req.getRequest().getApNo()+"' ";
        List<Map<String, Object>> apInvList = this.doQueryData(apInvSql,null);

        if(apBillList.size()>0){
            Map<String, Object> apBill = apBillList.get(0);
            datas.setAccountId(apBill.get("ACCOUNTID").toString());
            datas.setAccountName(apBill.get("ACCOUNTNAME").toString());
            datas.setApType(apBill.get("APTYPE").toString());
            datas.setCorp(apBill.get("CORP").toString());
            datas.setPDate(apBill.get("PDATE").toString());
            datas.setOrganizationNo(apBill.get("ORGANIZATIONNO").toString());
            datas.setOrganizationName(apBill.get("ORGANIZATIONNAME").toString());
            datas.setAccEmployeeNo(apBill.get("ACCEMPLOYEENO").toString());
            datas.setAccEmployeeName(apBill.get("ACCEMPLOYEENAME").toString());
            datas.setBizPartnerNo(apBill.get("BIZPARTNERNO").toString());
            datas.setBizPartnerName(apBill.get("BIZPARTNERNAME").toString());
            datas.setReceiver(apBill.get("RECEIVER").toString());
            datas.setReceiverName(apBill.get("RECEIVERNAME").toString());
            datas.setTaskId(apBill.get("TASKID").toString());
            datas.setPayDateNo(apBill.get("PAYDATENO").toString());
            datas.setPayDateName(apBill.get("PAYDATENAME").toString());
            datas.setPayDueDate(apBill.get("PAYDUEDATE").toString());
            datas.setTaxCode(apBill.get("TAXCODE").toString());
            datas.setTaxName(apBill.get("TAXNAME").toString());
            datas.setTaxRate(apBill.get("TAXRATE").toString());
            datas.setInclTax(apBill.get("INCLTAX").toString());
            datas.setEmployeeNo(apBill.get("EMPLOYEENO").toString());
            datas.setEmployeeName(apBill.get("EMPLOYEENAME").toString());
            datas.setDepartNo(apBill.get("DEPARTNO").toString());
            datas.setDepartName(apBill.get("DEPARTNAME").toString());
            datas.setApDepart(apBill.get("APDEPARTNO").toString());
            datas.setApDepartName(apBill.get("APDEPARTNAME").toString());
            datas.setSourceType(apBill.get("SOURCETYPE").toString());
            datas.setSourceNo(apBill.get("SOURCENO").toString());
            datas.setPendOffsetNo(apBill.get("PENDOFFSETNO").toString());
            datas.setFeeSubjectId(apBill.get("FEESUBJECTID").toString());
            datas.setApSubjectId(apBill.get("APSUBJECTID").toString());
            datas.setGlNo(apBill.get("GLNO").toString());
            datas.setGrpPmtNo(apBill.get("GRPPMTNO").toString());
            datas.setMemo(apBill.get("MEMO").toString());
            datas.setPayList(apBill.get("PAYLIST").toString());
            datas.setCurrency(apBill.get("CURRENCY").toString());
            datas.setCurrencyName(apBill.get("CURRENCYNAME").toString());
            datas.setExRate(apBill.get("EXRATE").toString());
            datas.setFCYBTAmt(apBill.get("FCYBTAMT").toString());
            datas.setFCYTAmt(apBill.get("FCYTAMT").toString());
            datas.setFCYRevAmt(apBill.get("FCYREVAMT").toString());
            datas.setFCYTATAmt(apBill.get("FCYTATAMT").toString());
            datas.setLCYBTAmt(apBill.get("LCYBTAMT").toString());
            datas.setLCYTAmt(apBill.get("LCYTAMT").toString());
            datas.setLCYRevAmt(apBill.get("LCYREVAMT").toString());
            datas.setLCYTATAmt(apBill.get("LCYTATAMT").toString());
            datas.setStatus(apBill.get("STATUS").toString());
            datas.setCreateBy(apBill.get("CREATEBY").toString());
            datas.setCreateByName(apBill.get("CREATEBYNAME").toString());
            datas.setCreate_Date(apBill.get("CREATE_DATE").toString());
            datas.setCreate_Time(apBill.get("CREATE_TIME").toString());
            datas.setModifyBy(apBill.get("MODIFYBY").toString());
            datas.setModifyByName(apBill.get("MODIFYBYNAME").toString());
            datas.setModify_Date(apBill.get("MODIFY_DATE").toString());
            datas.setModify_Time(apBill.get("MODIFY_TIME").toString());
            datas.setConfirmBy(apBill.get("CONFIRMBY").toString());
            datas.setConfirmByName(apBill.get("CONFIRMBYNAME").toString());
            datas.setConfirm_Date(apBill.get("CONFIRM_DATE").toString());
            datas.setConfirm_Time(apBill.get("CONFIRM_TIME").toString());
            datas.setCancelBy(apBill.get("CANCELBY").toString());
            datas.setCancelByName(apBill.get("CANCELBYNAME").toString());
            datas.setCancel_Date(apBill.get("CANCEL_DATE").toString());
            datas.setCancel_Time(apBill.get("CANCEL_TIME").toString());
            datas.setApplicant(apBill.get("APPLICANT").toString());
            datas.setApplicantName(apBill.get("APPLICANTNAME").toString());
            datas.setFeeSubjectName(apBill.get("FEESUBJECTNAME").toString());
            datas.setApSubjectName(apBill.get("APSUBJECTNAME").toString());
            datas.setICYCurrency(apBill.get("LCYCURRENCY").toString());
            datas.setICYCurrencyName(apBill.get("ICYCURRENCYNAME").toString());
            datas.setFCYCurrency(apBill.get("FCYCURRENCY").toString());
            datas.setFCYCurrencyName(apBill.get("FCYCURRENCYNAME").toString());
            datas.setCoaRefId(apBill.get("COAREFID").toString());

            for (Map<String, Object> apBillDetailSum : apBillDetailSumList){
                DCP_ApBillDetailQueryRes.ApBillSumList apBillSumList = res.new ApBillSumList();
                apBillSumList.setAccountId(apBillDetailSum.get("ACCOUNTID").toString());
                apBillSumList.setAccountName(apBillDetailSum.get("ACCOUNTNAME").toString());
                apBillSumList.setOrganizationNo(apBillDetailSum.get("ORGANIZATIONNO").toString());
                apBillSumList.setOrganizationName(apBillDetailSum.get("ORGANIZATIONNAME").toString());
                apBillSumList.setApNo(apBillDetailSum.get("APNO").toString());
                apBillSumList.setItem(apBillDetailSum.get("ITEM").toString());
                apBillSumList.setBizPartnerNo(apBillDetailSum.get("BIZPARTNERNO").toString());
                apBillSumList.setBizPartnerName(apBillDetailSum.get("BIZPARTNERNAME").toString());
                apBillSumList.setReceiver(apBillDetailSum.get("RECEIVER").toString());
                apBillSumList.setReceiverName(apBillDetailSum.get("RECEIVERNAME").toString());
                apBillSumList.setSourceType(apBillDetailSum.get("SOURCETYPE").toString());
                apBillSumList.setSourceNo(apBillDetailSum.get("SOURCENO").toString());
                apBillSumList.setSourceItem(apBillDetailSum.get("SOURCEITEM").toString());
                apBillSumList.setSourceOrg(apBillDetailSum.get("SOURCEORG").toString());
                apBillSumList.setSourceOrgName(apBillDetailSum.get("SOURCEORGNAME").toString());
                apBillSumList.setPluNo(apBillDetailSum.get("PLUNO").toString());
                apBillSumList.setPluName(apBillDetailSum.get("PLUNAME").toString());
                apBillSumList.setSpec(apBillDetailSum.get("SPEC").toString());
                apBillSumList.setPriceUnit(apBillDetailSum.get("PRICEUNIT").toString());
                apBillSumList.setPriceUnitName(apBillDetailSum.get("PRICEUNITNAME").toString());
                apBillSumList.setQty(apBillDetailSum.get("QTY").toString());
                apBillSumList.setPrice(apBillDetailSum.get("PRICE").toString());
                apBillSumList.setFee(apBillDetailSum.get("FEE").toString());
                apBillSumList.setFeeName(apBillDetailSum.get("FEENAME").toString());
                apBillSumList.setOofNo(apBillDetailSum.get("OOFNO").toString());
                apBillSumList.setOoItem(apBillDetailSum.get("OOITEM").toString());
                apBillSumList.setDepartId(apBillDetailSum.get("DEPARTNO").toString());
                apBillSumList.setDepartName(apBillDetailSum.get("DEPARTNAME").toString());
                apBillSumList.setCateGory(apBillDetailSum.get("CATEGORY").toString());
                apBillSumList.setCateGoryName(apBillDetailSum.get("CATEGORYNAME").toString());
                apBillSumList.setIsGift(apBillDetailSum.get("ISGIFT").toString());
                apBillSumList.setBsNo(apBillDetailSum.get("BSNO").toString());
                apBillSumList.setBsName(apBillDetailSum.get("BSNAME").toString());
                apBillSumList.setTaxCode(apBillDetailSum.get("TAXCODE").toString());
                apBillSumList.setTaxName(apBillDetailSum.get("TAXNAME").toString());
                apBillSumList.setFeeSubjectId(apBillDetailSum.get("FEESUBJECTID").toString());
                apBillSumList.setFeeSubjectName(apBillDetailSum.get("FEESUBJECTNAME").toString());
                apBillSumList.setDirection(apBillDetailSum.get("DIRECTION").toString());
                apBillSumList.setIsRevEst(apBillDetailSum.get("ISREVEST").toString());
                apBillSumList.setApSubjectId(apBillDetailSum.get("APSUBJECTID").toString());
                apBillSumList.setApSubjectName(apBillDetailSum.get("APSUBJECTNAME").toString());
                apBillSumList.setPayDateNo(apBillDetailSum.get("PAYDATENO").toString());
                apBillSumList.setPayDateName(apBillDetailSum.get("PAYDATENAME").toString());
                apBillSumList.setEmployeeNo(apBillDetailSum.get("EMPLOYEENO").toString());
                apBillSumList.setEmployeeName(apBillDetailSum.get("EMPLOYEENAME").toString());
                apBillSumList.setFreeChars1(apBillDetailSum.get("FREECHARS1").toString());
                apBillSumList.setFreeChars2(apBillDetailSum.get("FREECHARS2").toString());
                apBillSumList.setFreeChars3(apBillDetailSum.get("FREECHARS3").toString());
                apBillSumList.setFreeChars4(apBillDetailSum.get("FREECHARS4").toString());
                apBillSumList.setFreeChars5(apBillDetailSum.get("FREECHARS5").toString());
                apBillSumList.setMemo(apBillDetailSum.get("MEMO").toString());
                apBillSumList.setCurrency(apBillDetailSum.get("CURRENCY").toString());
                apBillSumList.setCurrencyName(apBillDetailSum.get("CURRENCYNAME").toString());
                apBillSumList.setBillPrice(apBillDetailSum.get("BILLPRICE").toString());
                apBillSumList.setExRate(apBillDetailSum.get("EXRATE").toString());
                apBillSumList.setFCYBTAmt(apBillDetailSum.get("FCYBTAMT").toString());
                apBillSumList.setFCYTAmt(apBillDetailSum.get("FCYTAMT").toString());
                apBillSumList.setFCYTATAmt(apBillDetailSum.get("FCYTATAMT").toString());
                apBillSumList.setFCYStdCostAmt(apBillDetailSum.get("FCYSTDCOSTAMT").toString());
                apBillSumList.setFCYActCostAmt(apBillDetailSum.get("FCYACTCOSTAMT").toString());
                apBillSumList.setICYPrice(apBillDetailSum.get("ICYPRICE").toString());
                apBillSumList.setLCYBTAmt(apBillDetailSum.get("LCYBTAMT").toString());
                apBillSumList.setLCYTAmt(apBillDetailSum.get("LCYTAMT").toString());
                apBillSumList.setLCYTATAmt(apBillDetailSum.get("LCYTATAMT").toString());
                apBillSumList.setLCYStdCostAmt(apBillDetailSum.get("LCYSTDCOSTAMT").toString());
                apBillSumList.setPurOrderNo(apBillDetailSum.get("PURORDERNO").toString());

                datas.getApBillSumList().add(apBillSumList);
            }

            //apBillDetailList
            for (Map<String, Object> apBillDetail : apBillDetailList){
                DCP_ApBillDetailQueryRes.ApBillList singleApBill = res.new ApBillList();
                singleApBill.setItem(apBillDetail.get("ITEM").toString());
                singleApBill.setBizPartnerNo(apBillDetail.get("BIZPARTNERNO").toString());
                singleApBill.setBizPartnerName(apBillDetail.get("BIZPARTNERNAME").toString());
                singleApBill.setReceiver(apBillDetail.get("RECEIVER").toString());
                singleApBill.setReceiverName(apBillDetail.get("RECEIVERNAME").toString());
                singleApBill.setSourceType(apBillDetail.get("SOURCETYPE").toString());
                singleApBill.setSourceItem(apBillDetail.get("SOURCEITEM").toString());
                singleApBill.setSourceNo(apBillDetail.get("SOURCENO").toString());
                singleApBill.setSourceOrg(apBillDetail.get("SOURCEORG").toString());
                singleApBill.setSourceOrgName(apBillDetail.get("SOURCEORGNAME").toString());
                singleApBill.setPluName(apBillDetail.get("PLUNAME").toString());
                singleApBill.setPluNo(apBillDetail.get("PLUNO").toString());
                singleApBill.setSpec(apBillDetail.get("SPEC").toString());
                singleApBill.setPriceUnit(apBillDetail.get("PRICEUNIT").toString());
                singleApBill.setPriceUnitName(apBillDetail.get("PRICEUNITNAME").toString());
                singleApBill.setQty(apBillDetail.get("QTY").toString());
                singleApBill.setBillPrice(apBillDetail.get("BILLPRICE").toString());
                singleApBill.setFee(apBillDetail.get("FEE").toString());
                singleApBill.setFeeName(apBillDetail.get("FEENAME").toString());
                singleApBill.setOofNo(apBillDetail.get("OOFNO").toString());
                singleApBill.setOoItem(apBillDetail.get("OOITEM").toString());
                singleApBill.setDepartId(apBillDetail.get("DEPARTNO").toString());
                singleApBill.setDepartName(apBillDetail.get("DEPARTNAME").toString());
                singleApBill.setCategory(apBillDetail.get("CATEGORY").toString());
                singleApBill.setCategoryName(apBillDetail.get("CATEGORYNAME").toString());
                singleApBill.setIsGift(apBillDetail.get("ISGIFT").toString());
                singleApBill.setBsNo(apBillDetail.get("BSNO").toString());
                singleApBill.setBsName(apBillDetail.get("BSNAME").toString());
                singleApBill.setTaxRate(apBillDetail.get("TAXRATE").toString());
                singleApBill.setFeeSubjectId(apBillDetail.get("FEESUBJECTID").toString());
                singleApBill.setFeeSubjectName(apBillDetail.get("FEESUBJECTNAME").toString());
                singleApBill.setApSubjectId(apBillDetail.get("APSUBJECTID").toString());
                singleApBill.setApSubjectName(apBillDetail.get("APSUBJECTNAME").toString());
                singleApBill.setDirection(apBillDetail.get("DIRECTION").toString());
                singleApBill.setTaxSubjectId(apBillDetail.get("TAXSUBJECTID").toString());
                singleApBill.setTaxSubjectName(apBillDetail.get("TAXSUBJECTNAME").toString());
                singleApBill.setIsRevEst(apBillDetail.get("ISREVEST").toString());
                singleApBill.setPayDateNo(apBillDetail.get("PAYDATENO").toString());
                singleApBill.setPayDateName(apBillDetail.get("PAYDATENAME").toString());
                singleApBill.setEmployeeNo(apBillDetail.get("EMPLOYEENO").toString());
                singleApBill.setEmployeeName(apBillDetail.get("EMPLOYEENAME").toString());
                singleApBill.setFreeChars1(apBillDetail.get("FREECHARS1").toString());
                singleApBill.setFreeChars2(apBillDetail.get("FREECHARS2").toString());
                singleApBill.setFreeChars3(apBillDetail.get("FREECHARS3").toString());
                singleApBill.setFreeChars4(apBillDetail.get("FREECHARS4").toString());
                singleApBill.setFreeChars5(apBillDetail.get("FREECHARS5").toString());
                singleApBill.setMemo(apBillDetail.get("MEMO").toString());
                singleApBill.setFCYPrice(apBillDetail.get("FCYPRICE").toString());
                singleApBill.setCurrency(apBillDetail.get("CURRENCY").toString());
                singleApBill.setCurrencyName(apBillDetail.get("CURRENCYNAME").toString());
                singleApBill.setExRate(apBillDetail.get("EXRATE").toString());
                singleApBill.setFCYBTAmt(apBillDetail.get("FCYBTAMT").toString());
                singleApBill.setFCYTAmt(apBillDetail.get("FCYTAMT").toString());
                singleApBill.setFCYTATAmt(apBillDetail.get("FCYTATAMT").toString());
                singleApBill.setFCYStdCostAmt(apBillDetail.get("FCYSTDCOSTAMT").toString());
                singleApBill.setFCYActCostAmt(apBillDetail.get("FCYACTCOSTAMT").toString());
                singleApBill.setLCYPrice(apBillDetail.get("LCYPRICE").toString());
                singleApBill.setLCYBTAmt(apBillDetail.get("LCYBTAMT").toString());
                singleApBill.setLCYTAmt(apBillDetail.get("LCYTAMT").toString());
                singleApBill.setLCYTATAmt(apBillDetail.get("LCYTATAMT").toString());
                singleApBill.setLCYStdCostAmt(apBillDetail.get("LCYSTDCOSTAMT").toString());
                singleApBill.setLCYActCostAmt(apBillDetail.get("LCYACTCOSTAMT").toString());
                singleApBill.setPurOrderNo(apBillDetail.get("PURORDERNO").toString());
                singleApBill.setTaxCode(apBillDetail.get("TAXCODE").toString());
                singleApBill.setTaxName(apBillDetail.get("TAXNAME").toString());
                datas.getApBillList().add(singleApBill);
            }

            //apPerdList
            for (Map<String, Object> singleApPerd : apPerdList){
                DCP_ApBillDetailQueryRes.ApPerdList apPerd= res.new ApPerdList();
                apPerd.setAccountID(singleApPerd.get("ACCOUNTID").toString());
                apPerd.setAccountName(singleApPerd.get("ACCOUNTNAME").toString());
                apPerd.setCorp(singleApPerd.get("CORP").toString());
                apPerd.setSourceOrg(singleApPerd.get("SOURCEORG").toString());
                apPerd.setSourceOrgName(singleApPerd.get("SOURCEORGNAME").toString());
                apPerd.setOrganizationNo(singleApPerd.get("ORGANIZATIONNO").toString());
                apPerd.setOrganizationName(singleApPerd.get("ORGANIZATIONNAME").toString());
                apPerd.setApNo(singleApPerd.get("APNO").toString());
                apPerd.setItem(singleApPerd.get("ITEM").toString());
                apPerd.setInstPmtSeq(singleApPerd.get("INSTPMTSQ").toString());
                apPerd.setPayType(singleApPerd.get("PAYTYPE").toString());
                apPerd.setPayDueDate(singleApPerd.get("PAYDUEDATE").toString());
                apPerd.setBillDueDate(singleApPerd.get("BILLDUEDATE").toString());
                apPerd.setDirection(singleApPerd.get("DIRECTION").toString());
                apPerd.setFCYReqAmt(singleApPerd.get("FCYREQAMT").toString());
                apPerd.setCurrency(singleApPerd.get("CURRENCY").toString());
                apPerd.setCurrencyName(singleApPerd.get("CURRENCYNAME").toString());
                apPerd.setExRate(singleApPerd.get("EXRATE").toString());
                apPerd.setFCYRevsedRate(singleApPerd.get("FCYREVSEDRATE").toString());
                apPerd.setFCYTATAmt(singleApPerd.get("FCYTATAMT").toString());
                apPerd.setFCYPmtRevAmt(singleApPerd.get("FCYPMTREVAMT").toString());
                apPerd.setRevalAdjNum(singleApPerd.get("REVALADJNUM").toString());
                apPerd.setLCYTATAmt(singleApPerd.get("LCYTATAMT").toString());
                apPerd.setLCYPmtRevAmt(singleApPerd.get("LCYPMTREVAMT").toString());
                apPerd.setPayDateNo(singleApPerd.get("PAYDATENO").toString());
                apPerd.setPmtCategory(singleApPerd.get("PMTCATEGORY").toString());
                apPerd.setPurOrderNo(singleApPerd.get("PURORDERNO").toString());
                apPerd.setApSubjectId(singleApPerd.get("APSUBJECTID").toString());
                apPerd.setApSubjectName(singleApPerd.get("APSUBJECTNAME").toString());
                apPerd.setInvoiceNumber(singleApPerd.get("INVOICENUMBER").toString());
                apPerd.setInvoiceCode(singleApPerd.get("INVOICECODE").toString());
                apPerd.setInvoiceDate(singleApPerd.get("INVOICEDATE").toString());
                datas.getApPerdList().add(apPerd);
            }

            //apWFList
            for (Map<String, Object> singleApWF : apWFList){
                DCP_ApBillDetailQueryRes.ApWFList apWF = res.new ApWFList();
                apWF.setCorp(singleApWF.get("CORP").toString());
                apWF.setOrganizationNo(singleApWF.get("ORGANIZATIONNO").toString());
                apWF.setOrganizationName(singleApWF.get("ORGANIZATIONNAME").toString());
                apWF.setAccountID(singleApWF.get("ACCOUNTID").toString());
                apWF.setAccountName(singleApWF.get("ACCOUNTNAME").toString());
                apWF.setWrtOffNo(singleApWF.get("WRTOFFNO").toString());
                apWF.setItem(singleApWF.get("ITEM").toString());
                apWF.setTaskId(singleApWF.get("TASKID").toString());
                apWF.setWrtOffType(singleApWF.get("WRTOFFTYPE").toString());
                apWF.setSourceOrg(singleApWF.get("SOURCEORG").toString());
                apWF.setSourceOrgName(singleApWF.get("SOURCEORGNAME").toString());
                apWF.setWrtOffBillNo(singleApWF.get("WRTOFFBILLNO").toString());
                apWF.setWrtOffBillitem(singleApWF.get("WRTOFFBILLITEM").toString());
                apWF.setInstPmtSeq(singleApWF.get("INSTPMTSQ").toString());
                apWF.setMemo(singleApWF.get("MEMO").toString());
                apWF.setBsNo(singleApWF.get("BSNO").toString());
                apWF.setBsName(singleApWF.get("BSNAME").toString());
                apWF.setWrtOffDirection(singleApWF.get("WRTOFFDIRECTION").toString());
                apWF.setApSubjectId(singleApWF.get("APSUBJECTID").toString());
                apWF.setApSubjectName(singleApWF.get("APSUBJECTNAME").toString());
                apWF.setEmployeeNo(singleApWF.get("EMPLOYEENO").toString());
                apWF.setEmployeeName(singleApWF.get("EMPLOYEENAME").toString());
                apWF.setDepartId(singleApWF.get("DEPARTNO").toString());
                apWF.setDepartName(singleApWF.get("DEPARTNAME").toString());
                apWF.setCateGory(singleApWF.get("CATEGORY").toString());
                apWF.setCateGoryName(singleApWF.get("CATEGORYNAME").toString());
                apWF.setSecRefNo(singleApWF.get("SECREFNO").toString());
                apWF.setGlNo(singleApWF.get("GLNO").toString());
                apWF.setPayDueDate(singleApWF.get("PAYDUEDATE").toString());
                apWF.setBizPartnerNo(singleApWF.get("BIZPARTNERNO").toString());
                apWF.setBizPartnerName(singleApWF.get("BIZPARTNERNAME").toString());
                apWF.setReceiver(singleApWF.get("RECEIVER").toString());
                apWF.setReceiverName(singleApWF.get("RECEIVERNAME").toString());
                apWF.setFreeChars1(singleApWF.get("FREECHARS1").toString());
                apWF.setFreeChars2(singleApWF.get("FREECHARS2").toString());
                apWF.setFreeChars3(singleApWF.get("FREECHARS3").toString());
                apWF.setFreeChars4(singleApWF.get("FREECHARS4").toString());
                apWF.setFreeChars5(singleApWF.get("FREECHARS5").toString());
                apWF.setCurrency(singleApWF.get("CURRENCY").toString());
                apWF.setCurrencyName(singleApWF.get("CURRENCYNAME").toString());
                apWF.setExRate(singleApWF.get("EXRATE").toString());
                apWF.setFCYRevAmt(singleApWF.get("FCYREVAMT").toString());
                apWF.setLCYRevAmt(singleApWF.get("LCYREVAMT").toString());
                apWF.setFCYBTaxWrtOffAmt(singleApWF.get("FCYBTAXWRTOFFAMT").toString());
                apWF.setLCYBTaxWrtOffAmt(singleApWF.get("LCYBTAXWRTOFFAMT").toString());
                apWF.setInvoiceNumber(singleApWF.get("INVOICENUMBER").toString());
                apWF.setInvoiceCode(singleApWF.get("INVOICECODE").toString());

                datas.getApWFList().add(apWF);

            }

            //apPmtList
            for (Map<String, Object> singleApPmt : apPmtList){
                DCP_ApBillDetailQueryRes.PmtList apPmt = res.new PmtList();
                apPmt.setCorp(singleApPmt.get("CORP").toString());
                apPmt.setAccountID(singleApPmt.get("ACCOUNTID").toString());
                apPmt.setAccountName(singleApPmt.get("ACCOUNTNAME").toString());
                apPmt.setWrtOffNo(singleApPmt.get("WRTOFFNO").toString());
                apPmt.setItem(singleApPmt.get("ITEM").toString());
                apPmt.setOrganizationNo(singleApPmt.get("ORGANIZATIONNO").toString());
                apPmt.setOrganizationName(singleApPmt.get("ORGANIZATIONNAME").toString());
                apPmt.setSourceOrg(singleApPmt.get("SOURCEORG").toString());
                apPmt.setSourceOrgName(singleApPmt.get("SOURCEORGNAME").toString());
                apPmt.setTaskId(singleApPmt.get("TASKID").toString());
                apPmt.setWrtOffPmtType(singleApPmt.get("WRTOFFPMTYPE").toString());
                apPmt.setPaidBillNo(singleApPmt.get("PAIDBILLNO").toString());
                apPmt.setWrtOffItem(singleApPmt.get("WRTOFFITEM").toString());
                apPmt.setPmtCode(singleApPmt.get("PMTCATEGORY").toString());
                apPmt.setAccountBillNo(singleApPmt.get("ACCOUNTBILLNO").toString());
                apPmt.setTransferredData(singleApPmt.get("TRANSFERREDDATA").toString());
                apPmt.setMemo(singleApPmt.get("MEMO").toString());
                apPmt.setBnkDepWdrawCode(singleApPmt.get("BNKDEPWDRWCODE").toString());
                apPmt.setCashChgCode(singleApPmt.get("CASHCHGCODE").toString());
                apPmt.setTransInCustCode(singleApPmt.get("TRANSINCUSTCODE").toString());
                apPmt.setTransInPmtBillNo(singleApPmt.get("TRANSINPMTBILLNO").toString());
                apPmt.setWrtOffDirection(singleApPmt.get("WRTOFFDIRECTION").toString());
                apPmt.setWrtOffSubject(singleApPmt.get("WRTOFFSUBJECT").toString());
                apPmt.setPayDueDate(singleApPmt.get("PAYDUEDATE").toString());
                apPmt.setReceiver(singleApPmt.get("RECEIVER").toString());
                apPmt.setSalerAccount(singleApPmt.get("SALEACCOUNT").toString());
                apPmt.setSalerAccountCode(singleApPmt.get("SALEACCOUNTCODE").toString());
                apPmt.setSaleName(singleApPmt.get("SALENAME").toString());
                apPmt.setFreeChars1(singleApPmt.get("FREECHARS1").toString());
                apPmt.setFreeChars2(singleApPmt.get("FREECHARS2").toString());
                apPmt.setFreeChars3(singleApPmt.get("FREECHARS3").toString());
                apPmt.setFreeChars4(singleApPmt.get("FREECHARS4").toString());
                apPmt.setFreeChars5(singleApPmt.get("FREECHARS5").toString());
                apPmt.setCurrency(singleApPmt.get("CURRENCY").toString());
                apPmt.setCurrencyName(singleApPmt.get("CURRENCYNAME").toString());
                apPmt.setExRate(singleApPmt.get("EXRATE").toString());
                apPmt.setFCYRevAmt(singleApPmt.get("FCYREVAMT").toString());
                apPmt.setLCYRevAmt(singleApPmt.get("LCYREVAMT").toString());
                datas.getPmtList().add(apPmt);
            }

            //apEstList
            for (Map<String, Object> singleApEst : apEstList){
                DCP_ApBillDetailQueryRes.EstList est = res.new EstList();
                //est.setCorp(singleApEst.get("CORP").toString());
                est.setAccountID(singleApEst.get("ACCOUNTID").toString());
                est.setSourceOrg(singleApEst.get("SOURCEORG").toString());
                est.setApNo(singleApEst.get("APNO").toString());
                est.setItem(singleApEst.get("ITEM").toString());
                est.setGlItem(singleApEst.get("GLITEM").toString());
                est.setTaskId(singleApEst.get("TASKID").toString());
                est.setWrtOffType(singleApEst.get("WRTOFFTYPE").toString());
                est.setWrtOffQty(singleApEst.get("WRTOFFQTY").toString());
                est.setEstBillNo(singleApEst.get("ESTBILLNO").toString());
                est.setEstBillItem(singleApEst.get("ESTBILLITEM").toString());
                est.setPeriod(singleApEst.get("PERIOD").toString());
                est.setTaxRate(singleApEst.get("TAXRATE").toString());
                est.setWrtOffAPSubject(singleApEst.get("WRTOFFAPSUBJECT").toString());
                est.setWrtOffBTaxSubject(singleApEst.get("WRTOFFBTAXSUBJECT").toString());
                est.setWrtOffTaxSubject(singleApEst.get("WRTOFFTAXSUBJECT").toString());
                est.setWrtOffPrcDiffSubject(singleApEst.get("WRTOFFPRCDIFFSUBJECT").toString());
                est.setWrtOffExDiffSubject(singleApEst.get("WRTOFFEXDIFFSUBJECT").toString());
                est.setDepartId(singleApEst.get("DEPARTNO").toString());
                est.setTradeCustomer(singleApEst.get("TRADECUSTOMER").toString());
                est.setPmtCustomer(singleApEst.get("PMTCUSTOMER").toString());
                est.setCateGory(singleApEst.get("CATEGORY").toString());
                est.setEmployeeNo(singleApEst.get("EMPLOYEENO").toString());
                est.setFreeChars1(singleApEst.get("FREECHARS1").toString());
                est.setFreeChars2(singleApEst.get("FREECHARS2").toString());
                est.setFreeChars3(singleApEst.get("FREECHARS3").toString());
                est.setFreeChars4(singleApEst.get("FREECHARS4").toString());
                est.setFreeChars5(singleApEst.get("FREECHARS5").toString());
                est.setMemo(singleApEst.get("MEMO").toString());
                est.setFCYBillPrice(singleApEst.get("FCYBILLPRICE").toString());
                est.setExRate(singleApEst.get("EXRATE").toString());
                est.setFCYBTAmt(singleApEst.get("FCYBTAMT").toString());
                est.setFCYTAmt(singleApEst.get("FCYTAMT").toString());
                est.setFCYTATAmt(singleApEst.get("FCYTATAmt").toString());
                est.setFCYWrtOffDiffAmt(singleApEst.get("FCYWRTOFFDIFFAMT").toString());
                est.setLCYBillPrice(singleApEst.get("LCYBILLPRICE").toString());
                est.setLCYBTAmt(singleApEst.get("LCYBTAMT").toString());
                est.setLCYTAmt(singleApEst.get("LCYTAMT").toString());
                est.setLCYTATAmt(singleApEst.get("LCYTATAmt").toString());
                est.setLCYPrcDiffAmt(singleApEst.get("LCYPRCDIFFAMT").toString());
                est.setLCYExDiffAmt(singleApEst.get("LCYEXDIFFAMT").toString());
                datas.getEstList().add(est);
            }

            //apInvList
            for (Map<String, Object> singleApInv : apInvList){
                DCP_ApBillDetailQueryRes.ApInvList apInv = res.new ApInvList();
                apInv.setPurInvNo(singleApInv.get("PURINVNO").toString());
                apInv.setItem(singleApInv.get("ITEM").toString());
                apInv.setInvSource(singleApInv.get("INVSOURCE").toString());
                apInv.setBizPartnerNo(singleApInv.get("BIZPARTNERNO").toString());
                apInv.setOrganizationNo(singleApInv.get("ORGANIZATIONNO").toString());
                apInv.setInvoiceType(singleApInv.get("INVOICETYPE").toString());
                apInv.setInvoiceNumber(singleApInv.get("INVOICENUMBER").toString());
                apInv.setInvoiceCode(singleApInv.get("INVOICECODE").toString());
                apInv.setInvoiceDate(singleApInv.get("INVOICEDATE").toString());
                apInv.setTaxCode(singleApInv.get("TAXCODE").toString());
                apInv.setIsAfterTax(singleApInv.get("ISAFTERTAX").toString());
                apInv.setTaxRate(singleApInv.get("TAXRATE").toString());
                apInv.setCurrency(singleApInv.get("CURRENCY").toString());
                apInv.setExRate(singleApInv.get("EXRATE").toString());
                apInv.setInvFCYBTAmt(singleApInv.get("INVFCYBTAMT").toString());
                apInv.setInvFCYTAmt(singleApInv.get("INVFCYTAMT").toString());
                apInv.setInvFCYTATAmt(singleApInv.get("INVFCYTATAmt").toString());
                apInv.setInvLCYBTAmt(singleApInv.get("INVLCYBTAMT").toString());
                apInv.setInvLCYTAmt(singleApInv.get("INVLCYTAMT").toString());
                apInv.setInvLCYTATAmt(singleApInv.get("INVLCYTATAmt").toString());
                apInv.setRecType(singleApInv.get("RECTYPE").toString());
                apInv.setDedctblNo(singleApInv.get("DEDCTBLNO").toString());
                apInv.setApNo(singleApInv.get("APNO").toString());
                apInv.setIsEInvoice(singleApInv.get("ISEINVOICE").toString());
                datas.getApInvList().add(apInv);
            }


        }

        res.setDatas(datas);
        res.setSuccess(true);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ApBillDetailQueryReq req) throws Exception {

        return "";
    }
}
