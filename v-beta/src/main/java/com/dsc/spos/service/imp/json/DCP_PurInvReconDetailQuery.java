package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurInvReconDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PurInvReconDetailQuery extends SPosBasicService<DCP_PurInvReconDetailQueryReq, DCP_PurInvReconDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurInvReconDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconDetailQueryRes getResponseType() {
        return new DCP_PurInvReconDetailQueryRes();
    }

    @Override
    protected DCP_PurInvReconDetailQueryRes processJson(DCP_PurInvReconDetailQueryReq req) throws Exception {
        DCP_PurInvReconDetailQueryRes res = this.getResponseType();

        List<Map<String, Object>> qData = doQueryData(getQueryPURINVRECONSql(req), null);
        res.setDatas(new ArrayList<>());

        if (CollectionUtils.isNotEmpty(qData)) {

            List<Map<String, Object>> conData = doQueryData(getQuerySql(req), null);
            if (null == conData) {
                conData = new ArrayList<>();
            }

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("PURINVNO", true);
            List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);

            for (Map<String, Object> oneMaster : master) {

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneMaster.get("EID").toString());
                condition.put("PURINVNO", oneMaster.get("PURINVNO").toString());

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);
                List<Map<String, Object>> con = MapDistinct.getWhereMap(conData, condition, true);

                DCP_PurInvReconDetailQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
                oneData.setOrganizationName(oneMaster.get("ORGANIZATIONNAME").toString());
                oneData.setCorp(oneMaster.get("CORP").toString());
                oneData.setCorpName(oneMaster.get("CORPNAME").toString());
                oneData.setBdate(oneMaster.get("CREATE_DATE").toString());
                oneData.setPurInvNo(oneMaster.get("PURINVNO").toString());
                oneData.setInvoiceType(oneMaster.get("INVOICETYPE").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setBizPartnerName(oneMaster.get("BIZPARTNERNAME").toString());
                oneData.setInvProperty(oneMaster.get("INVPROPERTY").toString());
                oneData.setCurrency(oneMaster.get("CURRENCY").toString());
                oneData.setPayDateNo(oneMaster.get("PAYDATENO").toString());
                oneData.setPayDueDate(oneMaster.get("PAYDUEDATE").toString());
                oneData.setApNo(oneMaster.get("APNO").toString());
                oneData.setStatus(oneMaster.get("STATUS").toString());

                oneData.setCreateBy(oneMaster.get("CREATEBY").toString());
                oneData.setCancel_Date(oneMaster.get("CREATE_DATE").toString());
                oneData.setCreate_Time(oneMaster.get("CREATE_TIME").toString());

                oneData.setModifyBy(oneMaster.get("MODIFYBY").toString());
                oneData.setModify_Date(oneMaster.get("MODIFY_DATE").toString());
                oneData.setModify_Time(oneMaster.get("MODIFY_TIME").toString());

                oneData.setConfirmBy(oneMaster.get("CONFIRMBY").toString());
                oneData.setConfirm_Date(oneMaster.get("CONFIRM_DATE").toString());
                oneData.setConfirm_Time(oneMaster.get("CONFIRM_TIME").toString());

                oneData.setCancelBy(oneMaster.get("CANCELBY").toString());
                oneData.setCancel_Date(oneMaster.get("CANCEL_DATE").toString());
                oneData.setCancel_Time(oneMaster.get("CANCEL_TIME").toString());

                oneData.setInvList(new ArrayList<>());
                for (Map<String, Object> oneDetail : detail) {
                    DCP_PurInvReconDetailQueryRes.InvList oneInv = res.new InvList();
                    oneData.getInvList().add(oneInv);

                    oneInv.setPurInvNo(oneDetail.get("PURINVNO").toString());
                    oneInv.setItem(oneDetail.get("ITEM").toString());
                    oneInv.setInvoiceType(oneDetail.get("INVOICETYPE").toString());
                    oneInv.setInvProperty(oneDetail.get("INVPROPERTY").toString());
                    oneInv.setInvoiceNumber(oneDetail.get("INVOICENUMBER").toString());
                    oneInv.setInvoiceCode(oneDetail.get("INVOICECODE").toString());
                    oneInv.setInvoiceDate(oneDetail.get("INVOICEDATE").toString());
                    oneInv.setTaxCode(oneDetail.get("TAXCODE").toString());
                    oneInv.setTaxName(oneDetail.get("TAXNAME").toString());
                    oneInv.setInclTax(oneDetail.get("INCLTAX").toString());
                    oneInv.setTaxRate(oneDetail.get("TAXRATE").toString());
                    oneInv.setCurrency(oneDetail.get("CURRENCY").toString());
                    oneInv.setExRate(oneDetail.get("EXRATE").toString());
                    oneInv.setPayerName(oneDetail.get("PAYERNAME").toString());
                    oneInv.setPayerTaxNo(oneDetail.get("PAYERTAXNO").toString());
                    oneInv.setPayerAddress(oneDetail.get("PAYERADDRESS").toString());
                    oneInv.setPayerTel(oneDetail.get("PAYERTEL").toString());
                    oneInv.setPayerAccount(oneDetail.get("PAYERACCOUNT").toString());
                    oneInv.setPayerAccountCode(oneDetail.get("PAYERACCOUNTCODE").toString());
                    oneInv.setBizPartnerNo(oneDetail.get("BIZPARTNERNO").toString());
                    oneInv.setBizPartnerName(oneDetail.get("BIZPARTNERNAME").toString());
                    oneInv.setSalerAccountNo(oneDetail.get("SALERACCOUNTNO").toString());
                    oneInv.setInvFCYBTAmt(oneDetail.get("INVFCYBTAMT").toString());
                    oneInv.setInvFCYTAmt(oneDetail.get("INVFCYBTAMT").toString());
                    oneInv.setInvFCYATAmt(oneDetail.get("INVFCYATAMT").toString());
                    oneInv.setInvLCYBTAmt(oneDetail.get("INVFCYATAMT").toString());
                    oneInv.setInvLCYTAmt(oneDetail.get("INVLCYTAMT").toString());
                    oneInv.setInvLCYATAmt(oneDetail.get("INVLCYATAMT").toString());
                    oneInv.setSaleName(oneDetail.get("SALENAME").toString());
                    oneInv.setTaxnum(oneDetail.get("SALERTAXNUM").toString());
                    oneInv.setSalerAddress(oneDetail.get("SALERADDRESS").toString());
                    oneInv.setSalerTel(oneDetail.get("SALERTEL").toString());
                    oneInv.setSalerAccount(oneDetail.get("SALERACCOUNT").toString());
                    oneInv.setSalerAccountCode(oneDetail.get("SALERACCOUNTCODE").toString());
                    oneInv.setRecType(oneDetail.get("RECTYPE").toString());
                    oneInv.setDedctblNo(oneDetail.get("DEDCTBLNO").toString());
                    oneInv.setIsEInvoice(oneDetail.get("ISEINVOICE").toString());
                    oneInv.setApNo(oneDetail.get("APNO").toString());
                    oneInv.setPayDateNo(oneDetail.get("PAYDATENO").toString());
                    oneInv.setPayDueDate(oneDetail.get("PAYDUEDATE").toString());
                    oneInv.setDiff(oneDetail.get("DIFF").toString());
                    oneInv.setDiffAmt(oneDetail.get("DIFFAMT").toString());
                }

                oneData.setReconList(new ArrayList<>());

                for (Map<String, Object> oneCon : con) {
                    DCP_PurInvReconDetailQueryRes.ReconList oneRecon = res.new ReconList();
                    oneData.getReconList().add(oneRecon);

                    oneRecon.setOrganizationNo(oneCon.get("ORGANIZATIONNO").toString());
                    oneRecon.setOrganizationName(oneCon.get("ORGANIZATIONNAME").toString());
                    oneRecon.setCorp(oneCon.get("CORP").toString());
                    oneRecon.setCorpName(oneCon.get("CORPNAME").toString());
                    oneRecon.setPurInvNo(oneCon.get("PURINVNO").toString());
                    oneRecon.setItem(oneCon.get("ITEM").toString());
                    oneRecon.setSourceType(oneCon.get("SOURCETYPE").toString());
                    oneRecon.setSourceNo(oneCon.get("SOURCENO").toString());
                    oneRecon.setSourceNoSeq(oneCon.get("SOURCENOSEQ").toString());
                    oneRecon.setSourceOrg(oneCon.get("SOURCEORG").toString());
//                    oneRecon.setRDate(oneCon.get("RDATE").toString());
                    oneRecon.setFee(oneCon.get("FEE").toString());
                    oneRecon.setPluNo(oneCon.get("PLUNO").toString());
                    oneRecon.setPluName(oneCon.get("PLUNAME").toString());
                    oneRecon.setSpec(oneCon.get("SPEC").toString());
                    oneRecon.setPriceUnit(oneCon.get("PRICEUNIT").toString());
                    oneRecon.setCurrency(oneCon.get("CURRENCY").toString());
                    oneRecon.setTaxRate(oneCon.get("TAXRATE").toString());
                    oneRecon.setReferenceNo(oneCon.get("REFERENCENO").toString());
                    oneRecon.setReferenceItem(oneCon.get("REFERENCEITEM").toString());
                    oneRecon.setDirection(oneCon.get("DIRECTION").toString());
                    oneRecon.setBillQty(oneCon.get("QTY").toString());
                    oneRecon.setBillPrice(oneCon.get("BILLPRICE").toString());

                    oneRecon.setFCYBTAmt(oneCon.get("FCYBTAMT").toString());
                    oneRecon.setFCYTAmt(oneCon.get("FCYTAMT").toString());
                    oneRecon.setFCYATAmt(oneCon.get("FCYATAMT").toString());
                    oneRecon.setExRate(oneCon.get("EXRATE").toString());
                    oneRecon.setInvCrncyBTAmt(oneCon.get("INVCRNCYBTAMT").toString());
                    oneRecon.setInvCrncyTAmt(oneCon.get("INVCRNCYTAMT").toString());
                    oneRecon.setInvCrncyATAmt(oneCon.get("INVCRNCYATAMT").toString());
                    oneRecon.setCurInvoiceAmt(oneCon.get("CURINVOICEAMT").toString());

                }

            }

        }

        res.setSuccess(true);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryMasterSql(DCP_PurInvReconDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ")
                .append(" a.* ")
                .append(" FROM DCP_PURINV a ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getPurInvNo())) {
            sb.append(" AND a.PURINVNO='").append(req.getRequest().getPurInvNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("' ");
        }
        return sb.toString();
    }

    protected String getQueryPURINVRECONSql(DCP_PurInvReconDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ")
                .append(" a.CORP,ol1.ORG_NAME ORGANIZATIONNAME,ol2.ORG_NAME CORPNAME ")
                .append(" ,a.APNO,a.BIZPARTNERNO,bz1.SNAME BIZPARTNERNAME,a.INVOICETYPE,a.INVOICENUMBER ")
                .append(" ,a.INVOICEDATE,a.INVOICECODE,b.*,tc.TAXNAME ")
                .append(" FROM DCP_PURINV a ")
                .append(" LEFT JOIN DCP_PURINVRECON b on a.eid=b.eid and a.PURINVNO=b.PURINVNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=b.EID and ol1.ORGANIZATIONNO=b.ORGANIZATIONNO AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.EID=a.EID and ol2.ORGANIZATIONNO=a.CORP AND ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_TAXCATEGORY_LANG tc on tc.eid=b.eid and tc.TAXCODE=b.TAXCODE and tc.TAXAREA='CN' and tc.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.eid=b.eid and bz1.BIZPARTNERNO=b.BIZPARTNERNO ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getPurInvNo())) {
            sb.append(" AND a.PURINVNO='").append(req.getRequest().getPurInvNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_PurInvReconDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ")
                .append(" b.*,a.CORP,ol1.ORG_NAME ORGANIZATIONNAME,ol2.ORG_NAME CORPNAME ")
                .append(" ,a.APNO,a.BIZPARTNERNO,bz1.SNAME BIZPARTNERNAME,a.INVOICETYPE,a.INVOICENUMBER ")
                .append(" ,a.INVOICEDATE,a.INVOICECODE,gl1.PLU_NAME PLUNAME ")
                .append(" FROM DCP_PURINV a ")
                .append(" LEFT JOIN DCP_PURINVDETAIL b on a.eid=b.eid and a.PURINVNO=b.PURINVNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=b.EID and ol1.ORGANIZATIONNO=b.ORGANIZATIONNO AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.EID=a.EID and ol2.ORGANIZATIONNO=a.CORP AND ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.eid=a.eid and bz1.BIZPARTNERNO=a.BIZPARTNERNO ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getPurInvNo())) {
            sb.append(" AND a.PURINVNO='").append(req.getRequest().getPurInvNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        return sb.toString();

    }
}
