package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurInvReconUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_PurInvReconUpdate extends SPosAdvanceService<DCP_PurInvReconUpdateReq, DCP_PurInvReconUpdateRes> {
    @Override
    protected void processDUID(DCP_PurInvReconUpdateReq req, DCP_PurInvReconUpdateRes res) throws Exception {

        String billNo = req.getRequest().getPurInvNo();

        String taxCode = "";
        TaxUtils taxUtils = new TaxUtils();

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("PURINVNO", billNo);

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURINVDETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURINVRECON", condition)));

        for (DCP_PurInvReconUpdateReq.InvList detail : req.getRequest().getInvList()) {

            ColumnDataValue dcp_purInvRecon = new ColumnDataValue();

            dcp_purInvRecon.add("EID", req.geteId());
            dcp_purInvRecon.add("CREATEBY", req.getRequest().getCreateBy());
            dcp_purInvRecon.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvRecon.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());
            dcp_purInvRecon.add("MODIFYBY", req.getRequest().getCreateBy());
            dcp_purInvRecon.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvRecon.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvRecon.add("STATUS", "N");
            dcp_purInvRecon.add("CORP", req.getRequest().getCorp());
            dcp_purInvRecon.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
            dcp_purInvRecon.add("BDATE", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            dcp_purInvRecon.add("BIZPARTNERNO", req.getRequest().getBizPartnerNo());

            dcp_purInvRecon.add("PURINVNO", billNo);
            dcp_purInvRecon.add("ITEM", detail.getItem());
            dcp_purInvRecon.add("INVOICETYPE", detail.getInvoiceType());
            dcp_purInvRecon.add("INVPROPERTY", req.getRequest().getInvProperty());
            dcp_purInvRecon.add("INVOICENUMBER", detail.getInvoiceNumber());
            dcp_purInvRecon.add("INVOICECODE", detail.getInvoiceCode());
            dcp_purInvRecon.add("INVOICEDATE", DataValues.newDate(DateFormatUtils.getDate(detail.getInvoiceDate())));

            TaxUtils.Tax tax = null;
            if (!StringUtils.equals(taxCode, detail.getTaxCode())) {
                taxCode = detail.getTaxCode();
                tax = taxUtils.getTax(req.geteId(), taxCode);
            }
            if (null != tax) {
                dcp_purInvRecon.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                dcp_purInvRecon.add("INCLTAX", DataValues.newString(tax.getInclTax()));
                dcp_purInvRecon.add("TAXRATE", DataValues.newString(tax.getTaxRate()));
            }


            if (StringUtils.isEmpty(detail.getCurrency())) {
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
            dcp_purInvRecon.add("SALENAME", detail.getSaleName());
//            dcp_purInvRecon.add("SALERTAXNUM", detail.getsalerta());
            dcp_purInvRecon.add("SALERADDRESS", detail.getSalerAddress());
            dcp_purInvRecon.add("SALERTEL", detail.getSalerTel());
            dcp_purInvRecon.add("SALERACCOUNT", detail.getSalerAccount());
            dcp_purInvRecon.add("SALERACCOUNTCODE", detail.getSalerAccountNo());
            dcp_purInvRecon.add("RECTYPE", detail.getRecType());
            dcp_purInvRecon.add("DEDCTBLNO", detail.getDedctblNo());
            dcp_purInvRecon.add("ISEINVOICE", detail.getIsEInvoice());
            dcp_purInvRecon.add("APNO", detail.getApNo());
            dcp_purInvRecon.add("PAYDATENO", detail.getPayDateNo());
            dcp_purInvRecon.add("PAYDUEDATE", detail.getPayDueDate());
            dcp_purInvRecon.add("DIFF", detail.getDiff());
            dcp_purInvRecon.add("DIFFAMT", detail.getDiffAmt());


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PURINVRECON", dcp_purInvRecon)));
        }

        for (DCP_PurInvReconUpdateReq.ReconList oneRecon : req.getRequest().getReconList()) {

            ColumnDataValue dcp_purInvDetail = new ColumnDataValue();

            dcp_purInvDetail.add("EID", req.geteId());
            dcp_purInvDetail.add("CREATEBY", req.getRequest().getCreateBy());
            dcp_purInvDetail.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvDetail.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());
            dcp_purInvDetail.add("MODIFYBY", req.getRequest().getCreateBy());
            dcp_purInvDetail.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvDetail.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvDetail.add("STATUS", "N");
            dcp_purInvDetail.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
            dcp_purInvDetail.add("PURINVNO", billNo);
            dcp_purInvDetail.add("ITEM", oneRecon.getItem());
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
            dcp_purInvDetail.add("CURRENCY", oneRecon.getCurrency());
            dcp_purInvDetail.add("FCYBTAMT", oneRecon.getFCYBTAmt());
            dcp_purInvDetail.add("FCYTAMT", oneRecon.getFCYTAmt());
            dcp_purInvDetail.add("FCYATAMT", oneRecon.getFCYATAmt());
            dcp_purInvDetail.add("EXRATE", oneRecon.getExRate());
            dcp_purInvDetail.add("INVCRNCYBTAMT", oneRecon.getInvCrncyBTAmt());
            dcp_purInvDetail.add("INVCRNCYTAMT", oneRecon.getInvCrncyTAmt());
            dcp_purInvDetail.add("INVCRNCYATAMT", oneRecon.getInvCrncyATAmt());
            dcp_purInvDetail.add("CURINVOICEAMT", oneRecon.getCurInvoiceAmt());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PURINVDETAIL", dcp_purInvDetail)));
        }

        ColumnDataValue dcp_purInv = new ColumnDataValue();

//        dcp_purInv.add("EID",req.geteId());
//        dcp_purInv.add("PURINVNO",billNo);

        dcp_purInv.add("CREATEBY", req.getRequest().getCreateBy());
        dcp_purInv.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
        dcp_purInv.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

        dcp_purInv.add("STATUS", "N");
        dcp_purInv.add("CORP", req.getRequest().getCorp());

        dcp_purInv.add("ITEM", "1");
        dcp_purInv.add("BIZPARTNERNO", req.getRequest().getBizPartnerNo());
        dcp_purInv.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
        dcp_purInv.add("INVOICETYPE", req.getRequest().getInvoiceType());
        dcp_purInv.add("CURRENCY", req.getRequest().getCurrency());


        dcp_purInv.add("APNO", req.getRequest().getApNo());

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURINV", condition, dcp_purInv)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurInvReconUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurInvReconUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurInvReconUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurInvReconUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconUpdateReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconUpdateRes getResponseType() {
        return new DCP_PurInvReconUpdateRes();
    }
}
