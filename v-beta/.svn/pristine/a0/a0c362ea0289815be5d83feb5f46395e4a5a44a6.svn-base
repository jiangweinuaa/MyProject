package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurInvReconCreateReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DCP_PurInvReconCreate extends SPosAdvanceService<DCP_PurInvReconCreateReq, DCP_PurInvReconCreateRes> {
    @Override
    protected void processDUID(DCP_PurInvReconCreateReq req, DCP_PurInvReconCreateRes res) throws Exception {

        String billNo = getOrderNO(req, "GYFP");

        TaxUtils taxUtils = new TaxUtils();

        String paydateno = req.getRequest().getPayDateNo();
        String payduedate = req.getRequest().getPayDueDate();
        if (StringUtils.isEmpty(payduedate)) {
            paydateno = req.getRequest().getBDate();
        }
        String orgNo = req.getRequest().getOrganizationNo();
        if (StringUtils.isEmpty(orgNo)) {
            orgNo = req.getOrganizationNO();
        }

        String invoiceType = req.getRequest().getInvoiceType();
        String invoiceCode = "";
        String invoiceDate = "";
        String taxCode = "";
        String taxRate = "";
        String incTax = "";
        String isDoc = req.getRequest().getIsDoc();
        if (Check.isEmpty(isDoc)) {
            isDoc = "N";
        }
        String currency = req.getRequest().getCurrency();

        String exRate = "1";
        String payerName = "";
        String payerTaxNo = "";
        String payerAddress = "";
        String payerTel = "";
        String payerAccount = "";
        String payerAccountCode = "";
        String salerAccountNo = "";
        String saleName = "";
        String saleAddress = "";
        String salerTel = "";
        String salerAccount = "";
        String salerAccountCode = "";
        String recType = "";
        String dedctBlNo = "";
        String isEInvoice = "";
        String apNo = req.getRequest().getApNo();

        double invFcyBTAmt = 0;
        double invFcyTAmt = 0;
        double invFcyATAmt = 0;
        double invLcyBTAmt = 0;
        double invLcyTAmt = 0;
        double invLcyATAmt = 0;
        int maxItem = 0;
        for (DCP_PurInvReconCreateReq.InvList detail : req.getRequest().getInvList()) {

            ColumnDataValue dcp_purInvRecon = new ColumnDataValue();

            if (StringUtils.isEmpty(paydateno)) {
                paydateno = detail.getPayDateNo();
            }
            if (StringUtils.isEmpty(payduedate)) {
                payduedate = detail.getPayDueDate();
            }

//            if (StringUtils.isEmpty(invoiceType)) {
//                invoiceType = detail.getInvoiceType();
//            }
            if (StringUtils.isEmpty(invoiceCode)) {
                invoiceCode = detail.getInvoiceCode();
            }
            if (StringUtils.isEmpty(invoiceDate)) {
                invoiceDate = detail.getInvoiceDate();
            }
            TaxUtils.Tax tax = null;
            if (!StringUtils.equals(taxCode, detail.getTaxCode())) {
                taxCode = detail.getTaxCode();
                tax = taxUtils.getTax(req.geteId(), taxCode);
            }

            if (StringUtils.isEmpty(payerName)) {
                payerName = detail.getPayerName();
            }
            if (StringUtils.isEmpty(payerTaxNo)) {
                payerTaxNo = detail.getPayerTaxNo();
            }
            if (StringUtils.isEmpty(payerAddress)) {
                payerAddress = detail.getPayerAddress();
            }
            if (StringUtils.isEmpty(payerTel)) {
                payerTel = detail.getPayerTel();
            }
            if (StringUtils.isEmpty(payerAccount)) {
                payerAccount = detail.getPayerAccount();
            }
            if (StringUtils.isEmpty(payerAccountCode)) {
                payerAccountCode = detail.getPayerAccountCode();
            }
            if (StringUtils.isEmpty(salerAccountNo)) {
                salerAccountNo = detail.getSalerAccountNo();
            }
            if (StringUtils.isEmpty(saleName)) {
                saleName = detail.getSaleName();
            }
            if (StringUtils.isEmpty(saleAddress)) {
                saleAddress = detail.getSalerAddress();
            }
            if (StringUtils.isEmpty(salerTel)) {
                salerTel = detail.getSalerTel();
            }
            if (StringUtils.isEmpty(salerAccount)) {
                salerAccount = detail.getSalerAccount();
            }
            if (StringUtils.isEmpty(salerAccountCode)) {
                salerAccountCode = detail.getSalerAccountCode();
            }
            if (StringUtils.isEmpty(recType)) {
                recType = detail.getRecType();
            }
            if (StringUtils.isEmpty(dedctBlNo)) {
                dedctBlNo = detail.getDedctblNo();
            }
            if (StringUtils.isEmpty(isEInvoice)) {
                isEInvoice = detail.getIsEInvoice();
            }
            if (StringUtils.isEmpty(apNo)) {
                apNo = detail.getApNo();
            }

            dcp_purInvRecon.add("EID", req.geteId());
            dcp_purInvRecon.add("CREATEBY", req.getEmployeeNo());
            dcp_purInvRecon.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvRecon.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvRecon.add("STATUS", "N");
            dcp_purInvRecon.add("CORP", req.getRequest().getCorp());
            dcp_purInvRecon.add("ORGANIZATIONNO", orgNo);
            dcp_purInvRecon.add("BDATE", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            dcp_purInvRecon.add("BIZPARTNERNO", req.getRequest().getBizPartnerNo());

            dcp_purInvRecon.add("PURINVNO", billNo);
            dcp_purInvRecon.add("ITEM", DataValues.newString(++maxItem));
            dcp_purInvRecon.add("INVOICETYPE", DataValues.newString(req.getRequest().getInvoiceType()));
            dcp_purInvRecon.add("INVPROPERTY", req.getRequest().getInvProperty());
            dcp_purInvRecon.add("INVOICENUMBER", detail.getInvoiceNumber());
            dcp_purInvRecon.add("INVOICECODE", detail.getInvoiceCode());
            dcp_purInvRecon.add("INVOICEDATE", DataValues.newDate(DateFormatUtils.getDate(detail.getInvoiceDate())));

            if (null != tax) {
                taxRate = String.valueOf(tax.getTaxRate());
                incTax = tax.getInclTax();
                dcp_purInvRecon.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                dcp_purInvRecon.add("INCLTAX", DataValues.newString(tax.getInclTax()));
                dcp_purInvRecon.add("TAXRATE", DataValues.newString(tax.getTaxRate()));
            }

            if (Check.isEmpty(detail.getCurrency())) {
                dcp_purInvRecon.add("CURRENCY", req.getRequest().getCurrency());
            } else {
                dcp_purInvRecon.add("CURRENCY", detail.getCurrency());
            }
            dcp_purInvRecon.add("EXRATE", detail.getExRate());
            dcp_purInvRecon.add("PAYERNAME", detail.getPayerName());
            dcp_purInvRecon.add("PAYERTAXNO", detail.getPayerTaxNo());
            dcp_purInvRecon.add("PAYERADDRESS", detail.getPayerAddress());
            dcp_purInvRecon.add("PAYERTEL", detail.getPayerTel());
            dcp_purInvRecon.add("PAYERACCOUNT", detail.getPayerAccount());
            dcp_purInvRecon.add("PAYERACCOUNTCODE", detail.getPayerAccountCode());
            dcp_purInvRecon.add("RECEIVER", detail.getReceiver());
            dcp_purInvRecon.add("SALERACCOUNTNO", detail.getSalerAccountCode());
            dcp_purInvRecon.add("INVFCYBTAMT", detail.getInvFCYBTAmt());
            dcp_purInvRecon.add("INVFCYTAMT", detail.getInvFCYTAmt());
            dcp_purInvRecon.add("INVFCYATAMT", detail.getInvFCYATAmt());
            dcp_purInvRecon.add("INVLCYBTAMT", detail.getInvLCYBTAmt());
            dcp_purInvRecon.add("INVLCYTAMT", detail.getInvLCYTAmt());
            dcp_purInvRecon.add("INVLCYATAMT", detail.getInvLCYATAmt());

            invFcyBTAmt += Double.parseDouble(detail.getInvFCYBTAmt());
            invFcyTAmt += Double.parseDouble(detail.getInvFCYTAmt());
            invFcyATAmt += Double.parseDouble(detail.getInvFCYATAmt());
            invLcyBTAmt += Double.parseDouble(detail.getInvLCYBTAmt());
            invLcyTAmt += Double.parseDouble(detail.getInvLCYTAmt());
            invLcyATAmt += Double.parseDouble(detail.getInvLCYATAmt());

            dcp_purInvRecon.add("SALENAME", detail.getSaleName());
//            dcp_purInvRecon.add("SALERTAXNUM", detail.getsalerta());
            dcp_purInvRecon.add("SALERADDRESS", detail.getSalerAddress());
            dcp_purInvRecon.add("SALERTEL", detail.getSalerTel());
            dcp_purInvRecon.add("SALERACCOUNT", detail.getSalerAccount());
            dcp_purInvRecon.add("SALERACCOUNTCODE", detail.getSalerAccountNo());
            dcp_purInvRecon.add("RECTYPE", detail.getRecType());
            if (Check.isEmpty(detail.getDedctblNo())) {
                dcp_purInvRecon.add("DEDCTBLNO", "1");
            } else {
                dcp_purInvRecon.add("DEDCTBLNO", detail.getDedctblNo());
            }
            dcp_purInvRecon.add("ISEINVOICE", detail.getIsEInvoice());
            dcp_purInvRecon.add("APNO", detail.getApNo());
            if (StringUtils.isEmpty(detail.getPayDateNo())) {
                dcp_purInvRecon.add("PAYDATENO", DataValues.newDate(DateFormatUtils.getDate(payduedate)));
            } else {
                dcp_purInvRecon.add("PAYDATENO", DataValues.newDate(DateFormatUtils.getDate(detail.getPayDateNo())));
            }

            if (StringUtils.isEmpty(detail.getPayDueDate())) {
                dcp_purInvRecon.add("PAYDUEDATE", req.getRequest().getPayDueDate());
            } else {
                dcp_purInvRecon.add("PAYDUEDATE", detail.getPayDueDate());
            }

            if (Check.isEmpty(detail.getDiffAmt())) {
////                dcp_purInvRecon.add("DIFFAMT", DataValues.newString(Double.parseDouble(detail.getInvFCYATAmt()) - Double.parseDouble()));
            } else {
                dcp_purInvRecon.add("DIFFAMT", detail.getDiffAmt());
            }

            dcp_purInvRecon.add("ISDOC", isDoc);

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PURINVRECON", dcp_purInvRecon)));
        }

        List<ReconDetailKey> reconDetailKeys = new ArrayList<>();

        int item = 1;
        double diffAmt = 0;
        for (DCP_PurInvReconCreateReq.ReconList oneRecon : req.getRequest().getReconList()) {

            ColumnDataValue dcp_purInvDetail = new ColumnDataValue();

            dcp_purInvDetail.add("EID", req.geteId());
            dcp_purInvDetail.add("CREATEBY", req.getEmployeeNo());
            dcp_purInvDetail.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvDetail.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvDetail.add("STATUS", "N");
            dcp_purInvDetail.add("ORGANIZATIONNO", orgNo);
            dcp_purInvDetail.add("PURINVNO", billNo);
            dcp_purInvDetail.add("ITEM", DataValues.newInteger(item++));
            dcp_purInvDetail.add("SOURCETYPE", oneRecon.getSourceType());
            dcp_purInvDetail.add("SOURCENO", oneRecon.getSourceNo());
            dcp_purInvDetail.add("SOURCEORG", oneRecon.getSourceOrg());
            dcp_purInvDetail.add("SOURCENOSEQ", oneRecon.getSourceNoSeq());
            dcp_purInvDetail.add("FEE", oneRecon.getFee());
            dcp_purInvDetail.add("PLUNO", oneRecon.getPluNo());
            dcp_purInvDetail.add("SPEC", oneRecon.getSpec());
            dcp_purInvDetail.add("PRICEUNIT", oneRecon.getPriceUnit());
            dcp_purInvDetail.add("QTY", oneRecon.getBillQty());
            dcp_purInvDetail.add("DIRECTION", oneRecon.getDirection());
            dcp_purInvDetail.add("REFERENCENO", oneRecon.getReferenceNo());
            dcp_purInvDetail.add("REFERENCEITEM", oneRecon.getReferenceItem());
            dcp_purInvDetail.add("BILLPRICE", oneRecon.getBillPrice());
            dcp_purInvDetail.add("TAXRATE", oneRecon.getTaxRate());
            dcp_purInvDetail.add("TAXCODE", oneRecon.getTaxCode());
            dcp_purInvDetail.add("CURRENCY", currency);

            dcp_purInvDetail.add("FCYBTAMT", DataValues.newDecimal(oneRecon.getFCYBTAmt()));
            dcp_purInvDetail.add("FCYTAMT", DataValues.newDecimal(oneRecon.getFCYTAmt()));
            dcp_purInvDetail.add("FCYATAMT", DataValues.newDecimal(oneRecon.getFCYATAmt()));
            dcp_purInvDetail.add("EXRATE", exRate);
            dcp_purInvDetail.add("INVCRNCYBTAMT", oneRecon.getInvCrncyBTAmt());
            dcp_purInvDetail.add("INVCRNCYTAMT", oneRecon.getInvCrncyTAmt());
            dcp_purInvDetail.add("INVCRNCYATAMT", oneRecon.getInvCrncyATAmt());
            dcp_purInvDetail.add("CURINVOICEAMT", oneRecon.getCurInvoiceAmt());
            diffAmt += Double.parseDouble(oneRecon.getFCYATAmt());
            ReconDetailKey oneReconDetailKey = new ReconDetailKey();
            oneReconDetailKey.setReconNo(oneRecon.getSourceNo());
            oneReconDetailKey.setItem(oneRecon.getSourceNoSeq());

            if (reconDetailKeys.contains(oneReconDetailKey)) {
                oneReconDetailKey = reconDetailKeys.get(reconDetailKeys.indexOf(oneReconDetailKey));
                oneReconDetailKey.setReconAmt(oneReconDetailKey.getReconAmt() + Double.parseDouble(oneRecon.getFCYATAmt()));
            } else {
                oneReconDetailKey.setReconAmt(Double.parseDouble(oneRecon.getFCYATAmt()));
                reconDetailKeys.add(oneReconDetailKey);
            }


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PURINVDETAIL", dcp_purInvDetail)));
        }

        ColumnDataValue dcp_purInv = new ColumnDataValue();

        dcp_purInv.add("EID", req.geteId());
        dcp_purInv.add("CREATEBY", req.getEmployeeNo());
        dcp_purInv.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
        dcp_purInv.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

        dcp_purInv.add("STATUS", "N");
        dcp_purInv.add("CORP", req.getRequest().getCorp());
        dcp_purInv.add("PURINVNO", billNo);
        dcp_purInv.add("ITEM", "1");
        dcp_purInv.add("INVSOURCE", "1");

        dcp_purInv.add("BIZPARTNERNO", req.getRequest().getBizPartnerNo());
        dcp_purInv.add("ORGANIZATIONNO", orgNo);
        dcp_purInv.add("INVOICETYPE", invoiceType);
        dcp_purInv.add("INVOICECODE", invoiceCode);
        dcp_purInv.add("INVOICEDATE", DataValues.newDate(DateFormatUtils.getDate(invoiceDate)));
        dcp_purInv.add("TAXCODE", taxCode);
        dcp_purInv.add("ISAFTERTAX", incTax);
        dcp_purInv.add("TAXRATE", taxRate);
        dcp_purInv.add("CURRENCY", currency);
        dcp_purInv.add("EXRATE", exRate);
        dcp_purInv.add("PAYERNAME", payerName);
        dcp_purInv.add("PAYERTAXNO", payerTaxNo);
        dcp_purInv.add("PAYERADDRESS", payerAddress);
        dcp_purInv.add("PAYERTEL", payerTel);
        dcp_purInv.add("PAYERACCOUNT", payerAccount);
        dcp_purInv.add("PAYERACCOUNTCODE", payerAccountCode);
        dcp_purInv.add("SALERACCOUNTNO", salerAccountNo);
        dcp_purInv.add("SALENAME", saleName);
        dcp_purInv.add("SALERADDRESS", saleAddress);
        dcp_purInv.add("SALERTEL", salerTel);
        dcp_purInv.add("SALERACCOUNT", salerAccount);
        dcp_purInv.add("SALERACCOUNTCODE", salerAccountCode);
        dcp_purInv.add("RECTYPE", recType);
        dcp_purInv.add("DEDCTBLNO", dedctBlNo);
        dcp_purInv.add("ISEINVOICE", isEInvoice);
        dcp_purInv.add("APNO", apNo);

        dcp_purInv.add("INVFCYBTAMT", DataValues.newDecimal(invFcyBTAmt));
        dcp_purInv.add("INVFCYTAMT", DataValues.newDecimal(invFcyTAmt));
        dcp_purInv.add("INVFCYATAMT", DataValues.newDecimal(invFcyATAmt));
        dcp_purInv.add("INVLCYBTAMT", DataValues.newDecimal(invLcyBTAmt));
        dcp_purInv.add("INVLCYTAMT", DataValues.newDecimal(invLcyTAmt));
        dcp_purInv.add("INVLCYATAMT", DataValues.newDecimal(invLcyATAmt));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PURINV", dcp_purInv)));

        //修改原单
        for (ReconDetailKey oneReconDetailKey : reconDetailKeys) {
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("RECONNO", DataValues.newString(oneReconDetailKey.getReconNo()));
            condition.add("ITEM", DataValues.newString(oneReconDetailKey.getItem()));

            ColumnDataValue dcp_reconDetail = new ColumnDataValue();

            dcp_reconDetail.add("RECONAMT", DataValues.newString(oneReconDetailKey.getReconAmt()));
            dcp_reconDetail.add("UNPAIDAMT", DataValues.newString(oneReconDetailKey.getReconAmt()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECONDETAIL", condition, dcp_reconDetail)));
        }
        diffAmt = invFcyATAmt - diffAmt;

        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue uptValue = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("PURINVNO", DataValues.newString(billNo));
        condition.add("ITEM", DataValues.newString(maxItem));

        uptValue.add("DIFFAMT", DataValues.newString(diffAmt));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURINVRECON", condition, uptValue)));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Data
    @EqualsAndHashCode(of = {"reconNo", "item"})
    private class ReconDetailKey {
        private String reconNo;
        private String item;

        private double reconAmt;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurInvReconCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurInvReconCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurInvReconCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurInvReconCreateReq req) throws Exception {

        StringBuilder errorMsg = new StringBuilder();

        if (StringUtils.isEmpty(req.getRequest().getBizPartnerNo())) {
            if (StringUtils.isEmpty(req.getRequest().getPayDueDate())) {
                errorMsg.append("付款日不可为空");
            }
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconCreateReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconCreateReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconCreateRes getResponseType() {
        return new DCP_PurInvReconCreateRes();
    }
}
