package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_APDocCreateReq;
import com.dsc.spos.json.cust.res.DCP_APDocCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_APDocCreate extends SPosAdvanceService<DCP_APDocCreateReq, DCP_APDocCreateRes> {

    @Override
    protected void processDUID(DCP_APDocCreateReq req, DCP_APDocCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_APDocCreateReq.level1Elm request = req.getRequest();
        //String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());


        List<DCP_APDocCreateReq.ApBillList> apBillList = request.getApBillList();
        List<DCP_APDocCreateReq.ApBillSumList> apBillSumList = request.getApBillSumList();
        List<DCP_APDocCreateReq.ApPerdList> apPerdList = request.getApPerdList();
        List<DCP_APDocCreateReq.ApWFList> apWFList = request.getApWFList();
        List<DCP_APDocCreateReq.PmtList> pmtList = request.getPmtList();
        List<DCP_APDocCreateReq.EstList> estList = request.getEstList();

        String apNo = this.getApNo(req);

        if(apBillList.size()>0){
            for (DCP_APDocCreateReq.ApBillList apBill : apBillList) {
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("APNO", DataValues.newString(apNo));
                detailColumns.add("ACCOUNTID", DataValues.newString(apBill.getAccountId()));
                detailColumns.add("ITEM", DataValues.newString(apBill.getItem()));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(apBill.getBizPartnerNo()));
                detailColumns.add("RECEIVER", DataValues.newString(apBill.getReceiver()));
                detailColumns.add("SOURCETYPE", DataValues.newString(apBill.getSourceType()));
                detailColumns.add("SOURCENO", DataValues.newString(apBill.getSourceNo()));
                detailColumns.add("SOURCEITEM", DataValues.newString(apBill.getSourceItem()));
                detailColumns.add("SOURCEORG", DataValues.newString(apBill.getSourceOrg()));
                detailColumns.add("PLUNO", DataValues.newString(apBill.getPluNo()));
                detailColumns.add("SPEC", DataValues.newString(apBill.getSpec()));
                detailColumns.add("PRICEUNIT", DataValues.newString(apBill.getPriceUnit()));
                detailColumns.add("QTY", DataValues.newString(apBill.getQty()));
                detailColumns.add("BILLPRICE", DataValues.newString(apBill.getBillPrice()));
                detailColumns.add("FEE", DataValues.newString(apBill.getFee()));
                detailColumns.add("OOFNO", DataValues.newString(apBill.getOofNo()));
                detailColumns.add("OOITEM", DataValues.newString(apBill.getOoItem()));
                detailColumns.add("DEPARTNO", DataValues.newString(apBill.getDepartId()));
                detailColumns.add("CATEGORY", DataValues.newString(apBill.getCateGory()));
                detailColumns.add("ISGIFT", DataValues.newString(apBill.getIsGift()));
                detailColumns.add("BSNO", DataValues.newString(apBill.getBsNo()));
                detailColumns.add("TAXRATE", DataValues.newString(apBill.getTaxRate()));
                detailColumns.add("TAXCODE", DataValues.newString(apBill.getTaxCode()));

                detailColumns.add("FEESUBJECTID", DataValues.newString(apBill.getFeeSubjectId()));
                detailColumns.add("APSUBJECTID", DataValues.newString(apBill.getApSubjectId()));
                detailColumns.add("TAXSUBJECTID", DataValues.newString(apBill.getTaxSubjectId()));
                detailColumns.add("DIRECTION", DataValues.newString(apBill.getDirection()));
                detailColumns.add("ISREVEST", DataValues.newString(apBill.getIsRevEst()));
                detailColumns.add("PAYDATENO", DataValues.newString(apBill.getPayDateNo()));
                detailColumns.add("EMPLOYEENO", DataValues.newString(apBill.getEmployeeNo()));
                detailColumns.add("MEMO", DataValues.newString(apBill.getMemo()));
                detailColumns.add("CURRENCY", DataValues.newString(apBill.getCurrency()));
                detailColumns.add("FCYPRICE", DataValues.newString(apBill.getFCYPrice()));
                detailColumns.add("EXRATE", DataValues.newString(apBill.getExRate()));
                detailColumns.add("FCYBTAMT", DataValues.newString(apBill.getFCYBTAmt()));
                detailColumns.add("FCYTAMT", DataValues.newString(apBill.getFCYTAmt()));
                detailColumns.add("FCYTATAMT", DataValues.newString(apBill.getFCYTATAmt()));
                detailColumns.add("FCYSTDCOSTAMT", DataValues.newString(apBill.getFCYStdCostAmt()));
                detailColumns.add("FCYACTCOSTAMT", DataValues.newString(apBill.getFCYActCostAmt()));
                detailColumns.add("LCYPRICE", DataValues.newString(apBill.getLCYPrice()));
                detailColumns.add("LCYBTAMT", DataValues.newString(apBill.getLCYBTAmt()));
                detailColumns.add("LCYTAMT", DataValues.newString(apBill.getLCYTAmt()));
                detailColumns.add("LCYTATAMT", DataValues.newString(apBill.getLCYTATAmt()));
                detailColumns.add("LCYSTDCOSTAMT", DataValues.newString(apBill.getLCYStdCostAmt()));
                detailColumns.add("LCYACTCOSTAMT", DataValues.newString(apBill.getLCYActCostAmt()));
                detailColumns.add("PURORDERNO", DataValues.newString(apBill.getPurOrderNo()));



                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLDETAIL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(apBillSumList)){
            for (DCP_APDocCreateReq.ApBillSumList apBillSum : apBillSumList) {
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ACCOUNTID", DataValues.newString(apBillSum.getAccountId()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(apBillSum.getOrganizationNo()));
                detailColumns.add("APNO", DataValues.newString(apNo));
                detailColumns.add("ITEM", DataValues.newString(apBillSum.getItem()));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(apBillSum.getBizPartnerNo()));
                detailColumns.add("RECEIVER", DataValues.newString(apBillSum.getReceiver()));
                detailColumns.add("SOURCETYPE", DataValues.newString(apBillSum.getSourceType()));
                detailColumns.add("SOURCENO", DataValues.newString(apBillSum.getSourceNo()));
                detailColumns.add("SOURCEORG", DataValues.newString(apBillSum.getSourceOrg()));
                detailColumns.add("FEE", DataValues.newString(apBillSum.getFee()));
                detailColumns.add("DEPARTNO", DataValues.newString(apBillSum.getDepartId()));
                detailColumns.add("CATEGORY", DataValues.newString(apBillSum.getCateGory()));
                detailColumns.add("ISGIFT", DataValues.newString(apBillSum.getIsGift()));
                detailColumns.add("TAXRATE", DataValues.newString(apBillSum.getTaxRate()));
                detailColumns.add("FEESUBJECTID", DataValues.newString(apBillSum.getFeeSubjectId()));
                detailColumns.add("APSUBJECTID", DataValues.newString(apBillSum.getApSubjectId()));
                detailColumns.add("TAXSUBJECTID", DataValues.newString(apBillSum.getTaxSubjectId()));
                detailColumns.add("DIRECTION", DataValues.newString(apBillSum.getDirection()));
                detailColumns.add("ISREVEST", DataValues.newString(apBillSum.getIsRevEst()));
                detailColumns.add("EMPLOYEENO", DataValues.newString(apBillSum.getEmployeeNo()));
                detailColumns.add("FreeChars1", DataValues.newString(apBillSum.getFreeChars1()));
                detailColumns.add("FreeChars2", DataValues.newString(apBillSum.getFreeChars2()));
                detailColumns.add("FreeChars3", DataValues.newString(apBillSum.getFreeChars3()));
                detailColumns.add("FreeChars4", DataValues.newString(apBillSum.getFreeChars4()));
                detailColumns.add("FreeChars5", DataValues.newString(apBillSum.getFreeChars5()));
                detailColumns.add("CURRENCY", DataValues.newString(apBillSum.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newString(apBillSum.getExRate()));
                detailColumns.add("FCYBTAMT", DataValues.newString(apBillSum.getFCYBTAmt()));
                detailColumns.add("FCYTAMT", DataValues.newString(apBillSum.getFCYTAmt()));
                detailColumns.add("FCYTATAMT", DataValues.newString(apBillSum.getFCYTATAmt()));
                detailColumns.add("FCYSTDCOSTAMT", DataValues.newString(apBillSum.getFCYStdCostAmt()));
                detailColumns.add("FCYACTCOSTAMT", DataValues.newString(apBillSum.getFCYActCostAmt()));
                detailColumns.add("LCYBTAMT", DataValues.newString(apBillSum.getLCYBTAmt()));
                detailColumns.add("LCYTAMT", DataValues.newString(apBillSum.getLCYTATAmt()));
                detailColumns.add("LCYTATAMT", DataValues.newString(apBillSum.getLCYTATAmt()));
                detailColumns.add("LCYSTDCOSTAMT", DataValues.newString(apBillSum.getLCYStdCostAmt()));
                detailColumns.add("LCYACTCOSTAMT", DataValues.newString(apBillSum.getLCYActCostAmt()));


                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLDETAILSUM",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(apPerdList)){
            for (DCP_APDocCreateReq.ApPerdList apPerd : apPerdList) {

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ACCOUNTID", DataValues.newString(apPerd.getAccountId()));
                detailColumns.add("CORP", DataValues.newString(apPerd.getCorp()));
                detailColumns.add("SOURCEORG", DataValues.newString(apPerd.getSourceOrg()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(apPerd.getOrganizationNo()));
                detailColumns.add("APNO", DataValues.newString(apNo));
                detailColumns.add("ITEM", DataValues.newString(apPerd.getItem()));
                detailColumns.add("INSTPMTSEQ", DataValues.newString(apPerd.getInstPmtSeq()));
                detailColumns.add("PAYTYPE", DataValues.newString(apPerd.getPayType()));
                detailColumns.add("PAYDUEDATE", DataValues.newString(apPerd.getPayDueDate()));
                detailColumns.add("BILLDUEDATE", DataValues.newString(apPerd.getBillDueDate()));
                detailColumns.add("DIRECTION", DataValues.newString(apPerd.getDirection()));
                detailColumns.add("FCYREQAMT", DataValues.newString(apPerd.getFCYReqAmt()));
                detailColumns.add("CURRENCY", DataValues.newString(apPerd.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newString(apPerd.getExRate()));
                detailColumns.add("FCYREVSEDRATE", DataValues.newString(apPerd.getFCYRevsedRate()));
                detailColumns.add("FCYTATAMT", DataValues.newString(apPerd.getFCYTATAmt()));
                detailColumns.add("FCYPMTREVAMT", DataValues.newString(apPerd.getFCYPmtRevAmt()));
                detailColumns.add("REVALADJNUM", DataValues.newString(apPerd.getRevalAdjNum()));
                detailColumns.add("LCYTATAMT", DataValues.newString(apPerd.getLCYTATAmt()));
                detailColumns.add("LCYPMTREVAMT", DataValues.newString(apPerd.getLCYPmtRevAmt()));
                detailColumns.add("PAYDATENO", DataValues.newString(apPerd.getPayDateNo()));
                detailColumns.add("PMTCATEGORY", DataValues.newString(apPerd.getPmtCategory()));
                detailColumns.add("PURORDERNO", DataValues.newString(apPerd.getPurOrderNo()));
                detailColumns.add("APSUBJECTID", DataValues.newString(apPerd.getApSubjectId()));
                detailColumns.add("INVOICENUMBER", DataValues.newString(apPerd.getInvoiceNumber()));
                detailColumns.add("INVOICECODE", DataValues.newString(apPerd.getInvoiceCode()));
                detailColumns.add("INVOICEDATE", DataValues.newDate(apPerd.getInvoiceDate()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APPERD",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(apWFList)){
            for (DCP_APDocCreateReq.ApWFList apWF : apWFList) {

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ACCOUNTID", DataValues.newString(apWF.getAccountId()));
                detailColumns.add("CORP", DataValues.newString(apWF.getCorp()));
                detailColumns.add("SOURCEORG", DataValues.newString(apWF.getSourceOrg()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(apWF.getOrganizationNo()));
                detailColumns.add("WRTOFFNO", DataValues.newString(apWF.getWrtOffNo()));
                detailColumns.add("ITEM", DataValues.newString(apWF.getItem()));
                detailColumns.add("TASKID", DataValues.newString(apWF.getTaskId()));
                detailColumns.add("WRTOFFTYPE", DataValues.newString(apWF.getWrtOffType()));
                detailColumns.add("WRTOFFBILLNO", DataValues.newString(apWF.getWrtOffBillNo()));
                detailColumns.add("WRTOFFBILLITEM", DataValues.newString(apWF.getWrtOffBillitem()));
                detailColumns.add("INSTPMTSEQ", DataValues.newString(apWF.getInstPmtSeq()));
                detailColumns.add("MEMO", DataValues.newString(apWF.getMemo()));
                detailColumns.add("BSNO", DataValues.newString(apWF.getBsNo()));
                detailColumns.add("WRTOFFDIRECTION", DataValues.newString(apWF.getWrtOffDirection()));
                detailColumns.add("APSUBJECTID", DataValues.newString(apWF.getApSubjectId()));
                detailColumns.add("EMPLOYEENO", DataValues.newString(apWF.getEmployeeNo()));
                detailColumns.add("DEPARTNO", DataValues.newString(apWF.getDepartId()));
                detailColumns.add("CATEGORY", DataValues.newString(apWF.getCateGory()));
                detailColumns.add("SECREFNO", DataValues.newString(apWF.getSecRefNo()));
                detailColumns.add("GLNO", DataValues.newString(apWF.getGlNo()));
                detailColumns.add("PAYDUEDATE", DataValues.newString(apWF.getPayDueDate()));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(apWF.getBizPartnerNo()));
                detailColumns.add("RECEIVER", DataValues.newString(apWF.getReceiver()));
                detailColumns.add("FREECHARS1", DataValues.newString(apWF.getFreeChars1()));
                detailColumns.add("FREECHARS2", DataValues.newString(apWF.getFreeChars2()));
                detailColumns.add("FREECHARS3", DataValues.newString(apWF.getFreeChars3()));
                detailColumns.add("FREECHARS4", DataValues.newString(apWF.getFreeChars4()));
                detailColumns.add("FREECHARS5", DataValues.newString(apWF.getFreeChars5()));
                detailColumns.add("CURRENCY", DataValues.newString(apWF.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newString(apWF.getExRate()));
                detailColumns.add("FCYREVAMT", DataValues.newString(apWF.getFCYRevAmt()));
                detailColumns.add("LCYREVAMT", DataValues.newString(apWF.getLCYRevAmt()));
                detailColumns.add("FCYBTAXWRTOFFAMT", DataValues.newString(apWF.getFCYBTaxWrtOffAmt()));
                detailColumns.add("LCYBTAXWRTOFFAMT", DataValues.newString(apWF.getLCYBTaxWrtOffAmt()));
                detailColumns.add("INVOICENUMBER", DataValues.newString(apWF.getInvoiceNumber()));
                detailColumns.add("INVOICECODE", DataValues.newString(apWF.getInvoiceCode()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLWRTOFF",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(pmtList)){
            for (DCP_APDocCreateReq.PmtList pmt : pmtList) {
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("CORP", DataValues.newString(pmt.getCorp()));
                detailColumns.add("ACCOUNTID", DataValues.newString(pmt.getAccountId()));
                detailColumns.add("WRTOFFNO", DataValues.newString(pmt.getWrtOffNo()));
                detailColumns.add("ITEM", DataValues.newString(pmt.getItem()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(pmt.getOrganizationNo()));
                detailColumns.add("SOURCEORG", DataValues.newString(pmt.getSourceOrg()));
                detailColumns.add("TASKID", DataValues.newString(pmt.getTaskId()));
                detailColumns.add("WRTOFFPMTTYPE", DataValues.newString(pmt.getWrtOffPmtType()));
                detailColumns.add("PAIDBILLNO", DataValues.newString(pmt.getPaidBillNo()));
                detailColumns.add("WRTOFFITEM", DataValues.newString(pmt.getWrtOffItem()));
                detailColumns.add("PMTCODE", DataValues.newString(pmt.getPmtCode()));
                detailColumns.add("ACCTBILLNO", DataValues.newString(pmt.getAccountId()));
                detailColumns.add("TRANSFERREDDATA", DataValues.newString(pmt.getTransferredData()));
                detailColumns.add("MEMO", DataValues.newString(pmt.getMemo()));
                detailColumns.add("BNKDEPWDRAWCODE", DataValues.newString(pmt.getBnkDepWdrawCode()));
                detailColumns.add("CASHCHGCODE", DataValues.newString(pmt.getCashChgCode()));
                detailColumns.add("TRANSINCUSTCODE", DataValues.newString(pmt.getTransInCustCode()));
                detailColumns.add("TRANSINPMTBILLNO", DataValues.newString(pmt.getTransInPmtBillNo()));
                detailColumns.add("WRTOFFDIRECTION", DataValues.newString(pmt.getWrtOffDirection()));
                detailColumns.add("WRTOFFSUBJECT", DataValues.newString(pmt.getWrtOffSubject()));
                detailColumns.add("PAYDUEDATE", DataValues.newString(pmt.getPayDueDate()));
                detailColumns.add("RECEIVER", DataValues.newString(pmt.getReceiver()));
                detailColumns.add("SALERACCOUNT", DataValues.newString(pmt.getSalerAccount()));
                detailColumns.add("SALERACCOUNTCODE", DataValues.newString(pmt.getSalerAccountCode()));
                detailColumns.add("SALENAME", DataValues.newString(pmt.getSaleName()));
                detailColumns.add("FREECHARS1", DataValues.newString(pmt.getFreeChars1()));
                detailColumns.add("FREECHARS2", DataValues.newString(pmt.getFreeChars2()));
                detailColumns.add("FREECHARS3", DataValues.newString(pmt.getFreeChars3()));
                detailColumns.add("FREECHARS4", DataValues.newString(pmt.getFreeChars4()));
                detailColumns.add("FREECHARS5", DataValues.newString(pmt.getFreeChars5()));
                detailColumns.add("CURRENCY", DataValues.newString(pmt.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newString(pmt.getExRate()));
                detailColumns.add("FCYREVAMT", DataValues.newString(pmt.getFCYRevAmt()));
                detailColumns.add("LCYREVAMT", DataValues.newString(pmt.getLCYRevAmt()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLPMT",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(estList)){
            for (DCP_APDocCreateReq.EstList est : estList) {
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ACCOUNTID", DataValues.newString(est.getAccountId()));
                detailColumns.add("SOURCEORG", DataValues.newString(est.getSourceOrg()));
                detailColumns.add("APNO", DataValues.newString(apNo));
                detailColumns.add("ITEM", DataValues.newString(est.getItem()));
                detailColumns.add("ITEM2", DataValues.newString(est.getGlItem()));
                detailColumns.add("TASKID", DataValues.newString(est.getTaskId()));
                detailColumns.add("WRTOFFTYPE", DataValues.newString(est.getWrtOffType()));
                detailColumns.add("WRTOFFQTY", DataValues.newString(est.getWrtOffQty()));
                detailColumns.add("ESTBILLNO", DataValues.newString(est.getEstBillNo()));
                detailColumns.add("ESTBILLITEM", DataValues.newString(est.getEstBillItem()));
                detailColumns.add("PERIOD", DataValues.newString(est.getPeriod()));
                detailColumns.add("TAXRATE", DataValues.newString(est.getTaxRate()));
                detailColumns.add("WRTOFFAPSUBJECT", DataValues.newString(est.getWrtOffAPSubject()));
                detailColumns.add("WRTOFFBTAXSUBJECT", DataValues.newString(est.getWrtOffBTaxSubject()));
                detailColumns.add("WRTOFFTAXSUBJECT", DataValues.newString(est.getWrtOffTaxSubject()));
                detailColumns.add("WRTOFFPRCDIFFSUBJECT", DataValues.newString(est.getWrtOffPrcDiffSubject()));
                detailColumns.add("WRTOFFEXDIFFSUBJECT", DataValues.newString(est.getWrtOffExDiffSubject()));
                detailColumns.add("DEPARTNO", DataValues.newString(est.getDepartId()));
                detailColumns.add("TRADECUSTOMER", DataValues.newString(est.getTradeCustomer()));
                detailColumns.add("PMTCUSTOMER", DataValues.newString(est.getPmtCustomer()));
                detailColumns.add("CATEGORY", DataValues.newString(est.getCateGory()));
                detailColumns.add("EMPLOYEENO", DataValues.newString(est.getEmployeeNo()));
                detailColumns.add("FREECHARS1", DataValues.newString(est.getFreeChars1()));
                detailColumns.add("FREECHARS2", DataValues.newString(est.getFreeChars2()));
                detailColumns.add("FREECHARS3", DataValues.newString(est.getFreeChars3()));
                detailColumns.add("FREECHARS4", DataValues.newString(est.getFreeChars4()));
                detailColumns.add("FREECHARS5", DataValues.newString(est.getFreeChars5()));
                detailColumns.add("MEMO", DataValues.newString(est.getMemo()));
                detailColumns.add("FCYBILLPRICE", DataValues.newString(est.getFCYBillPrice()));
                detailColumns.add("EXRATE", DataValues.newString(est.getExRate()));
                detailColumns.add("FCYBTAMT", DataValues.newString(est.getFCYBTAmt()));
                detailColumns.add("FCYTAMT", DataValues.newString(est.getFCYTAmt()));
                detailColumns.add("FCYTATAMT", DataValues.newString(est.getFCYTATAmt()));
                detailColumns.add("FCYWRTOFFDIFFAMT", DataValues.newString(est.getFCYWrtOffDiffAmt()));
                detailColumns.add("LCYBILLPRICE", DataValues.newString(est.getLCYBillPrice()));
                detailColumns.add("LCYBTAMT", DataValues.newString(est.getLCYBTAmt()));
                detailColumns.add("LCYTAMT", DataValues.newString(est.getLCYTAmt()));
                detailColumns.add("LCYTATAMT", DataValues.newString(est.getLCYTATAmt()));
                detailColumns.add("LCYPRCDIFFAMT", DataValues.newString(est.getLCYPrcDiffAmt()));
                detailColumns.add("LCYEXDIFFAMT", DataValues.newString(est.getLCYExDiffAmt()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLESTDTL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("CORP", DataValues.newString(request.getCorp()));
        mainColumns.add("ACCOUNTID", DataValues.newString(request.getAccountId()));
        mainColumns.add("APNO", DataValues.newString(apNo));
        mainColumns.add("PDATE", DataValues.newString(request.getPDate()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(request.getOrganizationNo()));
        mainColumns.add("APTYPE", DataValues.newString(request.getApType()));
        mainColumns.add("ACCEMPLOYEENO", DataValues.newString(request.getEmployeeNo()));
        mainColumns.add("BIZPARTNERNO", DataValues.newString(request.getBizPartnerNo()));
        mainColumns.add("RECEIVER", DataValues.newString(request.getReceiver()));
        mainColumns.add("TASKID", DataValues.newString(request.getTaskId()));
        mainColumns.add("PAYDATENO", DataValues.newString(request.getPayDateNo()));
        mainColumns.add("PAYDUEDATE", DataValues.newString(request.getPayDueDate()));
        mainColumns.add("TAXCODE", DataValues.newString(request.getTaxCode()));
        mainColumns.add("TAXRATE", DataValues.newString(request.getTaxRate()));
        mainColumns.add("INCLTAX", DataValues.newString(request.getInclTax()));
        mainColumns.add("APPLICANT", DataValues.newString(request.getApplicant()));
        mainColumns.add("EMPLOYEENO", DataValues.newString(request.getEmployeeNo()));
        mainColumns.add("DEPARTNO", DataValues.newString(request.getDepartId()));
        mainColumns.add("SOURCETYPE", DataValues.newString(request.getSourceType()));
        mainColumns.add("SOURCENO", DataValues.newString(request.getSourceNo()));
        mainColumns.add("PENDOFFSETNO", DataValues.newString(request.getPendOffsetNo()));
        mainColumns.add("FEESUBJECTID", DataValues.newString(request.getFeeSubjectId()));
        mainColumns.add("APSUBJECTID", DataValues.newString(request.getApSubjectId()));
        mainColumns.add("GLNO", DataValues.newString(request.getGlNo()));
        mainColumns.add("GRPPMTNO", DataValues.newString(request.getGrpPmtNo()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("PAYLIST", DataValues.newString(request.getPayList()));
        mainColumns.add("CURRENCY", DataValues.newString(request.getCurrency()));
        mainColumns.add("LCYCURRENCY", DataValues.newString(request.getICYCurrency()));
        mainColumns.add("FCYCURRENCY", DataValues.newString(request.getFCYCurrency()));
        mainColumns.add("EXRATE", DataValues.newString(request.getExRate()));
        mainColumns.add("FCYBTAMT", DataValues.newString(request.getFCYBTAmt()));
        mainColumns.add("FCYTAMT", DataValues.newString(request.getFCYTAmt()));
        mainColumns.add("FCYREVAMT", DataValues.newString(request.getFCYRevAmt()));
        mainColumns.add("FCYTATAMT", DataValues.newString(request.getFCYTATAmt()));
        mainColumns.add("LCYBTAMT", DataValues.newString(request.getLCYBTAmt()));
        mainColumns.add("LCYTAMT", DataValues.newString(request.getLCYTAmt()));
        mainColumns.add("LCYREVAMT", DataValues.newString(request.getLCYRevAmt()));
        mainColumns.add("LCYTATAMT", DataValues.newString(request.getLCYTATAmt()));
        mainColumns.add("FCYPMTAMT", DataValues.newString(request.getFCYPmtAmt()));
        mainColumns.add("LCYPMTAMT", DataValues.newString(request.getLCYPmtAmt()));
        mainColumns.add("APDEPARTNO", DataValues.newString(request.getApDepart()));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_APBILL",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        this.doExecuteDataToDB();
        res.setApNo(apNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_APDocCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_APDocCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_APDocCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_APDocCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_APDocCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_APDocCreateReq>(){};
    }

    @Override
    protected DCP_APDocCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_APDocCreateRes();
    }

    private String getApNo(DCP_APDocCreateReq req) throws Exception{
        String apNo = "";
        String preFix="";
        //1：采购应付单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，
        // 7：采购暂估单；8：预付待抵单；9：员工借支待抵单，10-应付核销单
        switch (req.getRequest().getTaskId()) {
            case "1":
                preFix = "APZK";
                break;
            case "2":
                preFix = "APYF";
                break;
            case "3":
                preFix = "APQT";
                break;
            case "4":
                preFix = "APNB";
                break;
            case "5":
                preFix = "APBX";
                break;
            case "6":
                preFix = "APJK";
                break;
            case "7":
                preFix = "APZG";
                break;
            case "8":
                preFix = "APDD";
                break;
            case "9":
                preFix = "AOJZ";
                break;
            case "10":
                preFix = "APHX";
                break;
            }

        apNo=this.getOrderNO(req, preFix);

        return apNo;
    }

}


