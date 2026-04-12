package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ApPerdQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApPerdQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApPerdQuery  extends SPosBasicService<DCP_ApPerdQueryReq, DCP_ApPerdQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApPerdQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ApPerdQueryReq> getRequestType() {
        return new TypeToken<DCP_ApPerdQueryReq>(){};
    }

    @Override
    protected DCP_ApPerdQueryRes getResponseType() {
        return new DCP_ApPerdQueryRes();
    }

    @Override
    protected DCP_ApPerdQueryRes processJson(DCP_ApPerdQueryReq req) throws Exception {
        DCP_ApPerdQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());

        String eId = req.geteId();
        String langType = req.getLangType();
        String accountId = req.getRequest().getAccountId();
        String apNo = req.getRequest().getApNo();

        String apBillSql="select a.*,b.account as accountname,c.org_name as corpname,d.org_name as organizationname,e.name as accEmployeeName," +
                " f.sname as bizpartnername,g.taxname,h.name as employeename,i.departname,j.name as currencyname,k1.op_name as createbyname,k2.op_name as modifybyname,k3.op_name as confirmbyname,k4.op_name as cancelbyname " +
                " from DCP_ApPerdQuery a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.corp and c.Lang_type='"+langType+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.organizationno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee e on e.eid=a.eid and e.employeeno=a.accEmployeeNo  " +
                " left join dcp_bizpartner f on f.eid=a.eid and f.bizpartnerno=a.bizpartnerno " +
                " left join DCP_TAXCATEGORY_LANG g on g.eid=a.eid and g.taxcode=a.taxcode and g.lang_type='"+langType+"' " +
                " left join dcp_employee h on h.eid=a.eid and h.employeeno=a.employeeno " +
                " left join dcp_department_lang i on i.eid=a.eid and i.departno=a.departid and i.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG j on j.eid=a.eid and j.currency=a.currency and j.lang_type='"+langType+"' " +
                " left join platform_staffs_lang k1 on k1.eid=a.eid and k1.opno=a.createby and k1.lang_type='"+langType+"' " +
                " left join platform_staffs_lang k2 on k2.eid=a.eid and k2.opno=a.modifyBy and k2.lang_type='"+langType+"' " +
                " left join platform_staffs_lang k3 on k3.eid=a.eid and k3.opno=a.confirmby and k3.lang_type='"+langType+"' " +
                " left join platform_staffs_lang k4 on k4.eid=a.eid and k4.opno=a.cancelby and k4.lang_type='"+langType+"' " +
                " where a.eid='"+eId+"' and a.apno='"+apNo+"' and a.accountid='"+accountId+"' ";
        List<Map<String, Object>> list = this.doQueryData(apBillSql, null);
        if(CollUtil.isNotEmpty(list)) {
            Map<String, Object> singleList = list.get(0);
            DCP_ApPerdQueryRes.level1Elm level1Elm = res.new level1Elm();
            level1Elm.setAccountId(singleList.get("ACCOUNTID").toString());
            level1Elm.setApNo(singleList.get("APNO").toString());
            level1Elm.setAccountName(singleList.get("ACCOUNTNAME").toString());
            level1Elm.setApType(singleList.get("APTYPE").toString());
            level1Elm.setCorp(singleList.get("CORP").toString());
            level1Elm.setCorpName(singleList.get("CORPNAME").toString());
            level1Elm.setPDate(singleList.get("PDATE").toString());
            level1Elm.setOrganizationNo(singleList.get("ORGANIZATIONNO").toString());
            level1Elm.setOrganizationName(singleList.get("ORGANIZATIONNAME").toString());
            level1Elm.setAccEmployeeNo(singleList.get("ACCEMPLOYEENO").toString());
            level1Elm.setAccEmployeeName(singleList.get("ACCEMPLOYEENAME").toString());
            level1Elm.setBizPartnerNo(singleList.get("BIZPARTNERNO").toString());
            level1Elm.setBizPartnerName(singleList.get("BIZPARTNERNAME").toString());
            level1Elm.setReceiver(singleList.get("RECEIVER").toString());
            level1Elm.setTaskId(singleList.get("TASKID").toString());
            level1Elm.setPayDateNo(singleList.get("PAYDATENO").toString());
            level1Elm.setPayDueDate(singleList.get("PAYDUEDATE").toString());
            level1Elm.setTaxCode(singleList.get("TAXCODE").toString());
            level1Elm.setTaxName(singleList.get("TAXNAME").toString());
            level1Elm.setTaxRate(singleList.get("TAXRATE").toString());
            level1Elm.setInclTax(singleList.get("INCLTAX").toString());
            level1Elm.setCurrency(singleList.get("CURRENCY").toString());
            level1Elm.setCurrencyName(singleList.get("CURRENCYNAME").toString());
            level1Elm.setExRate(singleList.get("EXRATE").toString());
            level1Elm.setFCYBTAmt(singleList.get("FCYBTAmt").toString());
            level1Elm.setFCYTAmt(singleList.get("FCYTAmt").toString());
            level1Elm.setFCYRevAmt(singleList.get("FCYREVAMT").toString());
            level1Elm.setFCYTATAmt(singleList.get("FCYTATAmt").toString());
            level1Elm.setLCYBTAmt(singleList.get("LCYBTAmt").toString());
            level1Elm.setLCYTAmt(singleList.get("LCYTAmt").toString());
            level1Elm.setLCYRevAmt(singleList.get("LCYREVAMT").toString());
            level1Elm.setLCYTATAmt(singleList.get("LCYTATAmt").toString());
            level1Elm.setFCYPmtAmt(singleList.get("FCYPMTAMT").toString());
            level1Elm.setLCYPmtAmt(singleList.get("LCYPMTAMT").toString());
            level1Elm.setStatus(singleList.get("STATUS").toString());
            level1Elm.setCreateBy(singleList.get("CREATEBY").toString());
            level1Elm.setCreateByName(singleList.get("CREATEBYNAME").toString());
            level1Elm.setCreate_Date(singleList.get("CREATE_DATE").toString());
            level1Elm.setCreate_Time(singleList.get("CREATE_TIME").toString());
            level1Elm.setModifyBy(singleList.get("MODIFYBY").toString());
            level1Elm.setModifyByName(singleList.get("MODIFYBYNAME").toString());
            level1Elm.setModify_Date(singleList.get("MODIFY_DATE").toString());
            level1Elm.setModify_Time(singleList.get("MODIFY_TIME").toString());
            level1Elm.setConfirmBy(singleList.get("CONFIRMBY").toString());
            level1Elm.setConfirmByName(singleList.get("CONFIRMBYNAME").toString());
            level1Elm.setConfirm_Date(singleList.get("CONFIRM_DATE").toString());
            level1Elm.setConfirm_Time(singleList.get("CONFIRM_TIME").toString());
            level1Elm.setCancelBy(singleList.get("CANCELBY").toString());
            level1Elm.setCancelByName(singleList.get("CANCELBYNAME").toString());
            level1Elm.setCancel_Date(singleList.get("CANCEL_DATE").toString());
            level1Elm.setCancel_Time(singleList.get("CANCEL_TIME").toString());

            //level1Elm.setApBillList(new ArrayList<>());
            level1Elm.setApPerdList(new ArrayList<>());
            //level1Elm.setApPayList(new ArrayList<>());
            //level1Elm.setPmtList(new ArrayList<>());
            //level1Elm.setEstList(new ArrayList<>());

            //String apBillDSql="select a.*,b.uname as priceunitname,c.plu_name as pluname,d.departname,e.taxname,g.name as employeename," +
             //       " h.name as currencyname " +
             //       " from DCP_APBILLDETAIL a " +
             //       " left join dcp_unit_lang b on a.eid=b.eid and a.priceunit=b.unit and b.lang_type='"+langType+"' " +
              //      " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+langType+"' " +
             //       " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.departid and d.lang_type='"+langType+"' " +
            //        " left join DCP_TAXCATEGORY_LANG e on e.eid=a.eid and e.taxcode=a.taxcode and e.lang_type='"+langType+"' " +
             //       " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=a.category and f.lang_type='"+langType+"' " +
             //       " left join dcp_employee g on g.eid=a.eid and g.employeeno=a.employeeno " +
             //       " left join DCP_CURRENCY_LANG h on h.eid=a.eid and h.currency=a.currency and h.lang_type='"+langType+"'" +
             //       " where a.eid='"+eId+"' and a.accountid='"+accountId+"' and a.apno='"+apNo+"'" +
             //       "";
            /**
            List<Map<String, Object>> apBillDList = this.doQueryData(apBillDSql,null);
            if(apBillDList.size()>0){
                for(Map<String, Object> singleDList:apBillDList){
                    DCP_ApPerdQueryRes.ApBillList apBillList = res.new ApBillList();

                    apBillList.setAccountId(singleDList.get("ACCOUNTID").toString());
                    apBillList.setAccountName(singleDList.get("ACCOUNTNAME").toString());
                    apBillList.setOrganizationNo(singleDList.get("ORGANIZATIONNO").toString());
                    apBillList.setOrganizationName(singleDList.get("ORGANIZATIONNAME").toString());
                    apBillList.setApNo(singleDList.get("APNO").toString());
                    apBillList.setItem(singleDList.get("ITEM").toString());
                    apBillList.setBizPartnerNo(singleDList.get("BIZPARTNERNO").toString());
                    apBillList.setBizPartnerName(singleDList.get("BIZPARTNERNAME").toString());
                    apBillList.setReceiver(singleDList.get("RECEIVER").toString());
                    apBillList.setSourceType(singleDList.get("SOURCETYPE").toString());
                    apBillList.setSourceNo(singleDList.get("SOURCENO").toString());
                    apBillList.setSourceItem(singleDList.get("SOURCEITEM").toString());
                    apBillList.setSourceOrg(singleDList.get("SOURCEORG").toString());
                    apBillList.setPluNo(singleDList.get("PLUNO").toString());
                    apBillList.setPluName(singleDList.get("PLUNAME").toString());
                    apBillList.setSpec(singleDList.get("SPEC").toString());
                    apBillList.setPriceUnit(singleDList.get("PRICEUNIT").toString());
                    apBillList.setPriceUnitName(singleDList.get("PRICEUNITNAME").toString());
                    apBillList.setQty(singleDList.get("QTY").toString());
                    apBillList.setFCYPrice(singleDList.get("FCYPRICE").toString());
                    apBillList.setFee(singleDList.get("FEE").toString());
                    apBillList.setOofNo(singleDList.get("OOFNO").toString());
                    apBillList.setOoItem(singleDList.get("OOITEM").toString());
                    apBillList.setDepartId(singleDList.get("DEPARTID").toString());
                    apBillList.setDepartName(singleDList.get("DEPARTNAME").toString());
                    apBillList.setCateGory(singleDList.get("CATEGORY").toString());
                    apBillList.setCateGoryName(singleDList.get("CATEGORYNAME").toString());
                    apBillList.setIsGift(singleDList.get("ISGIFT").toString());
                    apBillList.setBsNo(singleDList.get("BSNO").toString());
                    apBillList.setTaxCode(singleDList.get("TAXCODE").toString());
                    apBillList.setTaxName(singleDList.get("TAXNAME").toString());
                    apBillList.setFeeSubjectId(singleDList.get("FEESUBJECTID").toString());
                    apBillList.setDirection(singleDList.get("DIRECTION").toString());
                    apBillList.setIsRevEst(singleDList.get("ISREVEST").toString());
                    apBillList.setApSubjectId(singleDList.get("APSUBJECTID").toString());
                    apBillList.setPayDateNo(singleDList.get("PAYDATENO").toString());
                    apBillList.setEmployeeNo(singleDList.get("EMPLOYEENO").toString());
                    apBillList.setEmployeeName(singleDList.get("EMPLOYEENAME").toString());
                    apBillList.setFreeChars1(singleDList.get("FREECHARS1").toString());
                    apBillList.setFreeChars2(singleDList.get("FREECHARS2").toString());
                    apBillList.setFreeChars3(singleDList.get("FREECHARS3").toString());
                    apBillList.setFreeChars4(singleDList.get("FREECHARS4").toString());
                    apBillList.setFreeChars5(singleDList.get("FREECHARS5").toString());
                    apBillList.setMemo(singleDList.get("MEMO").toString());
                    apBillList.setCurrency(singleDList.get("CURRENCY").toString());
                    apBillList.setCurrencyName(singleDList.get("CURRENCYNAME").toString());
                    apBillList.setLCYPrice(singleDList.get("LCYPRICE").toString());
                    apBillList.setBillPrice(singleDList.get("BILLPRICE").toString());
                    apBillList.setExRate(singleDList.get("EXRATE").toString());
                    apBillList.setFCYBTAmt(singleDList.get("FCYBTAMT").toString());
                    apBillList.setFCYTAmt(singleDList.get("FCYTAMT").toString());
                    apBillList.setFCYTATAmt(singleDList.get("FCYTATAMT").toString());
                    apBillList.setLCYBTAmt(singleDList.get("LCYBTAMT").toString());
                    apBillList.setLCYTAmt(singleDList.get("LCYTAMT").toString());
                    apBillList.setLCYTATAmt(singleDList.get("LCYTATAMT").toString());
                    apBillList.setLCYStdCostAmt(singleDList.get("LCYSTDCOSTAMT").toString());
                    apBillList.setLCYActCostAmt(singleDList.get("LCYACTCOSTAMT").toString());
                    apBillList.setPurOrderNo(singleDList.get("PURORDERNO").toString());

                    level1Elm.getApBillList().add(apBillList);
                }
            }
             **/

            String apPerdSql="select a.*,b.name as currencyname " +
                    " from DCP_APPERD a " +
                    " left join DCP_CURRENCY_LANG b on b.eid=a.eid and b.currency=a.currency and b.lang_type='"+langType+"'" +
                    " where a.eid='"+eId+"' and a.accountid='"+accountId+"' and a.apno='"+apNo+"'";
            List<Map<String, Object>> apRerdList = this.doQueryData(apPerdSql,null);
            if(apRerdList!=null && apRerdList.size()>0){
                for (Map<String,Object> singleRerd:apRerdList){
                    DCP_ApPerdQueryRes.ApRerdList apRerd = res.new ApRerdList();

                    apRerd.setAccountID(singleRerd.get("ACCOUNTID").toString());
                    apRerd.setAccountName(singleRerd.get("ACCOUNTNAME").toString());
                    apRerd.setCorp(singleRerd.get("CORP").toString());
                    apRerd.setCorpName(singleRerd.get("CORPNAME").toString());
                    apRerd.setSourceOrg(singleRerd.get("SOURCEORG").toString());
                    apRerd.setOrganizationNo(singleRerd.get("ORGANIZATIONNO").toString());
                    apRerd.setOrganizationName(singleRerd.get("ORGANIZATIONNAME").toString());
                    apRerd.setApNo(singleRerd.get("APNO").toString());
                    apRerd.setItem(singleRerd.get("ITEM").toString());
                    apRerd.setInstPmtSeq(singleRerd.get("INSTPMTSQ").toString());
                    apRerd.setPayType(singleRerd.get("PAYTYPE").toString());
                    apRerd.setPayDueDate(singleRerd.get("PAYDUEDATE").toString());
                    apRerd.setBillDueDate(singleRerd.get("BILLDUEDATE").toString());
                    apRerd.setDirection(singleRerd.get("DIRECTION").toString());
                    apRerd.setFCYReqAmt(singleRerd.get("FCYREQAMT").toString());
                    apRerd.setCurrency(singleRerd.get("CURRENCY").toString());
                    apRerd.setCurrencyName(singleRerd.get("CURRENCYNAME").toString());
                    apRerd.setExRate(singleRerd.get("EXRATE").toString());
                    apRerd.setFCYRevsedRate(singleRerd.get("FCYREVSEDRATE").toString());
                    apRerd.setFCYTATAmt(singleRerd.get("FCYTATAMT").toString());
                    apRerd.setFCYPmtRevAmt(singleRerd.get("FCYPMTREVAMT").toString());
                    apRerd.setRevalAdjNum(singleRerd.get("REVALADJNUM").toString());
                    apRerd.setLCYTATAmt(singleRerd.get("LCYTATAMT").toString());
                    apRerd.setLCYPmtRevAmt(singleRerd.get("LCYPMTREVAMT").toString());
                    apRerd.setPayDateNo(singleRerd.get("PAYDATENO").toString());
                    apRerd.setPmtCategory(singleRerd.get("PMTCATEGORY").toString());
                    apRerd.setPurOrderNo(singleRerd.get("PURORDERNO").toString());
                    apRerd.setApSubjectId(singleRerd.get("APSUBJECTID").toString());
                    apRerd.setInvoiceNumber(singleRerd.get("INVOICENUMBER").toString());
                    apRerd.setInvoiceCode(singleRerd.get("INVOICECODE").toString());
                    apRerd.setInvoiceDate(singleRerd.get("INVOICEDATE").toString());

                    level1Elm.getApPerdList().add(apRerd);

                }
            }


        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ApPerdQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();


        return sqlbuf.toString();
    }

}


