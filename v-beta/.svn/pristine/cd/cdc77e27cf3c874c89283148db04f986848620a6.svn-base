package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ARInvUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ARInvUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_ARInvUpdate extends SPosAdvanceService<DCP_ARInvUpdateReq, DCP_ARInvUpdateRes> {
    @Override
    protected void processDUID(DCP_ARInvUpdateReq req, DCP_ARInvUpdateRes res) throws Exception {

        for (DCP_ARInvUpdateReq.ArInvList oneArInv : req.getRequest().getArInvList()) {
            ColumnDataValue dcp_salesInv = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            condition.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            condition.add("ITEM", DataValues.newString(oneArInv.getItem()));

            dcp_salesInv.add("PDATE", DataValues.newDate(req.getRequest().getPDate()));
            dcp_salesInv.add("INVOICETYPE", DataValues.newString(oneArInv.getInvoiceCode()));
            dcp_salesInv.add("ISEINVOICE", DataValues.newString(oneArInv.getIsEInvoice()));
            dcp_salesInv.add("INVOICECODE", DataValues.newString(oneArInv.getInvoiceCode()));
            dcp_salesInv.add("INVOICENO", DataValues.newString(oneArInv.getInvoiceNo()));
            dcp_salesInv.add("INVCOPYNO", DataValues.newString(oneArInv.getInvoiceCopyNo()));
            dcp_salesInv.add("INVANTIRANDCODE", DataValues.newString(oneArInv.getInvAntiRadndCode()));
            dcp_salesInv.add("INVOICEDATE", DataValues.newString(oneArInv.getInvoiceDate()));
            dcp_salesInv.add("INVOICETIME", DataValues.newString(oneArInv.getInvoiceTime()));
            dcp_salesInv.add("INVCLIENTFULLNM", DataValues.newString(oneArInv.getInvClientFullNm()));
            dcp_salesInv.add("PURTAXPAYERID", DataValues.newString(oneArInv.getPurTaxPayerID()));
            dcp_salesInv.add("PURADDRESS", DataValues.newString(oneArInv.getPurAddress()));
            dcp_salesInv.add("SALESTAXPAYERID", DataValues.newString(oneArInv.getSalesTaxPayerID()));
            dcp_salesInv.add("BIZPARTNERNO", DataValues.newString(oneArInv.getBizPartnerNo()));
            dcp_salesInv.add("INVDIFFSTATUS", DataValues.newString(oneArInv.getInvDiffStatus()));
            dcp_salesInv.add("TAXCODE", DataValues.newString(oneArInv.getTaxCode()));
            dcp_salesInv.add("TAXRATE", DataValues.newString(oneArInv.getTaxRate()));
            dcp_salesInv.add("ISAFTERTAX", DataValues.newString(oneArInv.getIsAfterTax()));
            dcp_salesInv.add("CURRENCY", DataValues.newString(oneArInv.getCurrency()));
            dcp_salesInv.add("EXRATE", DataValues.newString(oneArInv.getExRate()));
            dcp_salesInv.add("INVFCYBTAMT", DataValues.newString(oneArInv.getInvFCYBTAmt()));
            dcp_salesInv.add("INVFCYTAMT", DataValues.newString(oneArInv.getInvFCYTAmt()));
            dcp_salesInv.add("INVFCYATAMT", DataValues.newString(oneArInv.getInvFCYATAmt()));
            dcp_salesInv.add("INVLCYBTAMT", DataValues.newString(oneArInv.getInvLCYBTAmt()));
            dcp_salesInv.add("INVLCYTAMT", DataValues.newString(oneArInv.getInvLCYTAmt()));
            dcp_salesInv.add("INVLCYATAMT", DataValues.newString(oneArInv.getInvLCYATAmt()));
            dcp_salesInv.add("DEDCTBLNO", DataValues.newString(oneArInv.getDedctblNo()));
            dcp_salesInv.add("RECTYPE", DataValues.newString(oneArInv.getRecType()));

            dcp_salesInv.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            dcp_salesInv.add("MODIFY_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_salesInv.add("MODIFY_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SALESINV", condition, dcp_salesInv)));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ARInvUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ARInvUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ARInvUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ARInvUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ARInvUpdateReq> getRequestType() {
        return new TypeToken<DCP_ARInvUpdateReq>() {
        };
    }

    @Override
    protected DCP_ARInvUpdateRes getResponseType() {
        return new DCP_ARInvUpdateRes();
    }
}
