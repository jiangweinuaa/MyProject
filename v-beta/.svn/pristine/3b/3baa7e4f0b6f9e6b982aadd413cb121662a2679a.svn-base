package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ARDocCreateReq;
import com.dsc.spos.json.cust.req.DCP_ARDocUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ARDocUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_ARDocUpdate extends SPosAdvanceService<DCP_ARDocUpdateReq,DCP_ARDocUpdateRes> {
    @Override
    protected void processDUID(DCP_ARDocUpdateReq req, DCP_ARDocUpdateRes res) throws Exception {

        String arNo = req.getRequest().getArNo();
        ColumnDataValue dcp_arBill = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        condition.add("ARNO", DataValues.newString(arNo));

        dcp_arBill.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
        dcp_arBill.add("ARTYPE", DataValues.newString(req.getRequest().getArType()));
        dcp_arBill.add("PDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getPDate())));
        dcp_arBill.add("TASKID", DataValues.newString(req.getRequest().getTaskId()));
        dcp_arBill.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        dcp_arBill.add("ACCEMPLOYEENO", DataValues.newString(req.getRequest().getAccEmployeeNo()));
        dcp_arBill.add("BIZPARTNERNO", DataValues.newString(req.getRequest().getBizPartnerNo()));
        dcp_arBill.add("RECEIVER", DataValues.newString(req.getRequest().getReceiver()));
        dcp_arBill.add("PAYDATENO", DataValues.newString(req.getRequest().getPayDateNo()));
        dcp_arBill.add("PAYDUEDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getPayDateNo())));
        dcp_arBill.add("TAXCODE", DataValues.newString(req.getRequest().getTaxCode()));
        dcp_arBill.add("TAXRATE", DataValues.newString(req.getRequest().getTaxRate()));
        dcp_arBill.add("INCLTAX", DataValues.newString(req.getRequest().getInclTax()));
        dcp_arBill.add("EMPLOYEENO", DataValues.newString(req.getRequest().getEmployeeNo()));
        dcp_arBill.add("DEPARTNO", DataValues.newString(req.getRequest().getDepartId()));
        dcp_arBill.add("SOURCETYPE", DataValues.newString(req.getRequest().getSourceType()));
        dcp_arBill.add("SOURCENO", DataValues.newString(req.getRequest().getSourceNo()));
        dcp_arBill.add("PENDOFFSETNO", DataValues.newString(req.getRequest().getPendOffsetNo()));
//        dcp_arBill.add("ARSUBJECTID", DataValues.newString(req.getRequest().()));
//        dcp_arBill.add("REVSUBJECT", DataValues.newString(req.getRequest().getr()));
        dcp_arBill.add("GLNO", DataValues.newString(req.getRequest().getGlNo()));
        dcp_arBill.add("GRPPMTNO", DataValues.newString(req.getRequest().getGrpPmtNo()));
        dcp_arBill.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
        dcp_arBill.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
        dcp_arBill.add("EXRATE", DataValues.newString(req.getRequest().getExRate()));
        dcp_arBill.add("FCYBTAMT", DataValues.newString(req.getRequest().getFCYBTAmt()));
        dcp_arBill.add("FCYTAMT", DataValues.newString(req.getRequest().getFCYTAmt()));
        dcp_arBill.add("FCYREVAMT", DataValues.newString(req.getRequest().getFCYRevAmt()));
        dcp_arBill.add("FCYTATAMT", DataValues.newString(req.getRequest().getFCYTATAmt()));
        dcp_arBill.add("LCYBTAMT", DataValues.newString(req.getRequest().getLCYBTAmt()));
        dcp_arBill.add("LCYTAMT", DataValues.newString(req.getRequest().getLCYTAmt()));
        dcp_arBill.add("LCYREVAMT", DataValues.newString(req.getRequest().getLCYRevAmt()));
        dcp_arBill.add("LCYTATAMT", DataValues.newString(req.getRequest().getLCYTATAmt()));

        dcp_arBill.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
        dcp_arBill.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
        dcp_arBill.add("MODIFY_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_arBill.add("MODIFY_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ARBILL",condition,dcp_arBill)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLDETAIL",condition)));
        int item = 1;
        for (DCP_ARDocUpdateReq.ArBillList oneList : req.getRequest().getArBillList()) {
            ColumnDataValue dcp_arBillDetail = new ColumnDataValue();

            dcp_arBillDetail.add("EID", DataValues.newString(req.geteId()));
            dcp_arBillDetail.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            dcp_arBillDetail.add("ARNO", DataValues.newString(arNo));
            dcp_arBill.add("ITEM", DataValues.newString(item++));
            dcp_arBillDetail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
            dcp_arBillDetail.add("SOURCETYPE", DataValues.newString(req.getRequest().getSourceType()));
            dcp_arBillDetail.add("BIZPARTNERNO", DataValues.newString(oneList.getBizPartnerNo()));
            dcp_arBillDetail.add("RECEIVER", DataValues.newString(oneList.getReceiver()));
            dcp_arBillDetail.add("SOURCENO", DataValues.newString(oneList.getSourceNo()));
            dcp_arBillDetail.add("SOURCEITEM", DataValues.newString(oneList.getSourceItem()));
            dcp_arBillDetail.add("SOURCEORG", DataValues.newString(oneList.getSourceOrg()));
            dcp_arBillDetail.add("PLUNO", DataValues.newString(oneList.getPluNo()));
            dcp_arBillDetail.add("SPEC", DataValues.newString(oneList.getSpec()));
            dcp_arBillDetail.add("PRICEUNIT", DataValues.newString(oneList.getPriceUnit()));
            dcp_arBillDetail.add("QTY", DataValues.newString(oneList.getQty()));
            dcp_arBillDetail.add("OOFNO", DataValues.newString(oneList.getOofNo()));
            dcp_arBillDetail.add("OOITEM", DataValues.newString(oneList.getOoItem()));
            dcp_arBillDetail.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
            dcp_arBillDetail.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
            dcp_arBillDetail.add("CATEGORY", DataValues.newString(oneList.getCateGory()));
            dcp_arBillDetail.add("ISGIFT", DataValues.newString(oneList.getIsGift()));
            dcp_arBillDetail.add("BSNO", DataValues.newString(oneList.getBsNo()));
            dcp_arBillDetail.add("TAXCODE", DataValues.newString(req.getRequest().getTaxCode()));
            dcp_arBillDetail.add("TAXRATE", DataValues.newString(oneList.getTaxRate()));
//            dcp_arBillDetail.add("REVSUBJECT", DataValues.newString(req.getRequest().getre));
//            dcp_arBillDetail.add("ARSUBJECTID", DataValues.newString(oneList.getArNo()));
//            dcp_arBillDetail.add("TAXSUBJECTID", DataValues.newString(oneList.getArNo()));
            dcp_arBillDetail.add("DIRECTION", DataValues.newString(oneList.getDirection()));
            dcp_arBillDetail.add("ISREVEST", DataValues.newString(oneList.getIsRevEst()));
            dcp_arBillDetail.add("FREECHARS1", DataValues.newString(oneList.getFreeChars1()));
            dcp_arBillDetail.add("FREECHARS2", DataValues.newString(oneList.getFreeChars2()));
            dcp_arBillDetail.add("FREECHARS3", DataValues.newString(oneList.getFreeChars3()));
            dcp_arBillDetail.add("FREECHARS4", DataValues.newString(oneList.getFreeChars4()));
            dcp_arBillDetail.add("FREECHARS5", DataValues.newString(oneList.getFreeChars5()));
            dcp_arBillDetail.add("MEMO", DataValues.newString(oneList.getMemo()));
            dcp_arBillDetail.add("CURRENCY", DataValues.newString(oneList.getCurrency()));
            dcp_arBillDetail.add("BILLPRICE", DataValues.newString(oneList.getBillPrice()));
            dcp_arBillDetail.add("EXRATE", DataValues.newString(oneList.getExRate()));
            dcp_arBillDetail.add("FCYBTAMT", DataValues.newString(oneList.getFCYBTAmt()));
            dcp_arBillDetail.add("FCYTAMT", DataValues.newString(oneList.getFCYTAmt()));
            dcp_arBillDetail.add("FCYTATAMT", DataValues.newString(oneList.getFCYTATAmt()));
            dcp_arBillDetail.add("PRICE", DataValues.newString(oneList.getPrice()));
            dcp_arBillDetail.add("LCYBTAMT", DataValues.newString(oneList.getLCYBTAmt()));
            dcp_arBillDetail.add("LCYTAMT", DataValues.newString(oneList.getLCYTAmt()));
            dcp_arBillDetail.add("LCYTATAMT", DataValues.newString(oneList.getLCYTATAmt()));
//            dcp_arBillDetail.add("INVOICEQTY", DataValues.newString(oneList.getQty()));
//            dcp_arBillDetail.add("INVOICEAMT", DataValues.newString(oneList.geta()));
//            dcp_arBillDetail.add("INVCRNCYBTAMT", DataValues.newString(oneList.geta()));
//            dcp_arBillDetail.add("INVCRNCYATAMT", DataValues.newString(oneList.geta()));
//            dcp_arBillDetail.add("CLASSNO", DataValues.newString(oneList.getLCYTATAmt()));
//            dcp_arBillDetail.add("ADVSUBJECT", DataValues.newString(oneList.g()));
//            dcp_arBillDetail.add("CARDNO", DataValues.newString(oneList.getCateGory()));

            dcp_arBillDetail.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
            dcp_arBillDetail.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_arBillDetail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_arBillDetail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAIL",dcp_arBillDetail)));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARPERD",condition)));
        item = 1;
        for (DCP_ARDocUpdateReq.ArPerdList onePerd:req.getRequest().getArPerdList()){
            ColumnDataValue dcp_arPerd = new ColumnDataValue();

            dcp_arPerd.add("EID", DataValues.newString(req.geteId()));
            dcp_arPerd.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            dcp_arPerd.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_arPerd.add("ARNO", DataValues.newString(arNo));
            dcp_arPerd.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
            dcp_arPerd.add("ITEM", DataValues.newString(item++));

            dcp_arPerd.add("INSTPMTSEQ", DataValues.newString(onePerd.getInstPmtSeq()));
            dcp_arPerd.add("PAYTYPE", DataValues.newString(onePerd.getPayType()));
            dcp_arPerd.add("PAYDUEDATE", DataValues.newDate(onePerd.getPayDueDate()));
            dcp_arPerd.add("BILLDUEDATE", DataValues.newDate(onePerd.getBillDueDate()));
//            dcp_arPerd.add("INVOICENUMBER", DataValues.newString(onePerd.getinv()));
//            dcp_arPerd.add("INVOICECODE", DataValues.newString(onePerd.getinv()));
//            dcp_arPerd.add("ACCORG", DataValues.newString(onePerd.geta()));
//            dcp_arPerd.add("BDATE", DataValues.newString(onePerd.geta()));
//            dcp_arPerd.add("ACCDATE", DataValues.newString(onePerd.geta()));
//            dcp_arPerd.add("PAYDATE", DataValues.newString(onePerd.geta()));
//            dcp_arPerd.add("DEDDATE", DataValues.newString(onePerd.geta()));
            dcp_arPerd.add("CURRENCY", DataValues.newString(onePerd.getCurrency()));
            dcp_arPerd.add("EXRATE", DataValues.newString(onePerd.getExRate()));
            dcp_arPerd.add("FCYREVSEDRATE", DataValues.newString(onePerd.getFCYRevsedRate()));
            dcp_arPerd.add("REVALADJNUM", DataValues.newString(onePerd.getRevalAdjNum()));
            dcp_arPerd.add("FCYTATAMT", DataValues.newString(onePerd.getFCYTATAmt()));
            dcp_arPerd.add("FCYPMTREVAMT", DataValues.newString(onePerd.getFCYPmtRevAmt()));
//            dcp_arPerd.add("LCYREVALADJNUM", DataValues.newString(onePerd.getlcyv()));
            dcp_arPerd.add("LCYTATAMT", DataValues.newString(onePerd.getLCYTATAmt()));
            dcp_arPerd.add("LCYPMTREVAMT", DataValues.newString(onePerd.getLCYPmtRevAmt()));
            dcp_arPerd.add("PAYDATENO", DataValues.newString(onePerd.getPayDateNo()));
            dcp_arPerd.add("PMTCATEGORY", DataValues.newString(onePerd.getPmtCategory()));
            dcp_arPerd.add("PONO", DataValues.newString(onePerd.getPoNo()));
            dcp_arPerd.add("ARSUBJECTID", DataValues.newString(onePerd.getArSubjectId()));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARPERD",dcp_arPerd)));
        }
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLDETAILSUM",condition)));
        for (DCP_ARDocUpdateReq.ArBillSumList oneSum:req.getRequest().getArBillSumList()){

            ColumnDataValue dcp_arBillDetailSum = new ColumnDataValue();
            dcp_arBillDetailSum.add("EID", DataValues.newString(req.geteId()));
            dcp_arBillDetailSum.add("APNO", DataValues.newString(arNo));
            dcp_arBillDetailSum.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            dcp_arBillDetailSum.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
            dcp_arBillDetailSum.add("ITEM", DataValues.newString(oneSum.getItem()));
            dcp_arBillDetailSum.add("BIZPARTNERNO", DataValues.newString(oneSum.getBizPartnerNo()));
            dcp_arBillDetailSum.add("RECEIVER", DataValues.newString(oneSum.getReceiver()));
            dcp_arBillDetailSum.add("SOURCETYPE", DataValues.newString(oneSum.getSourceType()));
            dcp_arBillDetailSum.add("SOURCENO", DataValues.newString(oneSum.getSourceNo()));
            dcp_arBillDetailSum.add("SOURCEORG", DataValues.newString(oneSum.getSourceOrg()));
            dcp_arBillDetailSum.add("FEE", DataValues.newString(oneSum.getFee()));
            dcp_arBillDetailSum.add("DEPARTNO", DataValues.newString(oneSum.getDepartId()));
            dcp_arBillDetailSum.add("CATEGORY", DataValues.newString(oneSum.getCateGory()));
            dcp_arBillDetailSum.add("ISGIFT", DataValues.newString(oneSum.getIsGift()));
            dcp_arBillDetailSum.add("TAXRATE", DataValues.newString(oneSum.getTaxRate()));
//            dcp_arBillDetailSum.add("FEESUBJECTID", DataValues.newString(oneSum.getf()));
            dcp_arBillDetailSum.add("ARSUBJECTID", DataValues.newString(oneSum.getArSubjectId()));
//            dcp_arBillDetailSum.add("TAXSUBJECTID", DataValues.newString(oneSum.getta()));
            dcp_arBillDetailSum.add("DIRECTION", DataValues.newString(oneSum.getDirection()));
            dcp_arBillDetailSum.add("ISREVEST", DataValues.newString(oneSum.getIsRevEst()));
            dcp_arBillDetailSum.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
            dcp_arBillDetailSum.add("FREECHARS1", DataValues.newString(oneSum.getFreeChars1()));
            dcp_arBillDetailSum.add("FREECHARS2", DataValues.newString(oneSum.getFreeChars2()));
            dcp_arBillDetailSum.add("FREECHARS3", DataValues.newString(oneSum.getFreeChars3()));
            dcp_arBillDetailSum.add("FREECHARS4", DataValues.newString(oneSum.getFreeChars4()));
            dcp_arBillDetailSum.add("FREECHARS5", DataValues.newString(oneSum.getFreeChars5()));
            dcp_arBillDetailSum.add("CURRENCY", DataValues.newString(oneSum.getCurrency()));
            dcp_arBillDetailSum.add("EXRATE", DataValues.newString(oneSum.getExRate()));
            dcp_arBillDetailSum.add("FCYBTAMT", DataValues.newString(oneSum.getFCYBTAmt()));
            dcp_arBillDetailSum.add("FCYTAMT", DataValues.newString(oneSum.getFCYTAmt()));
            dcp_arBillDetailSum.add("FCYTATAMT", DataValues.newString(oneSum.getFCYTATAmt()));
            dcp_arBillDetailSum.add("LCYBTAMT", DataValues.newString(oneSum.getLCYBTAmt()));
            dcp_arBillDetailSum.add("LCYTAMT", DataValues.newString(oneSum.getLCYTAmt()));
            dcp_arBillDetailSum.add("LCYTATAMT", DataValues.newString(oneSum.getLCYTATAmt()));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAILSUM",dcp_arBillDetailSum)));

        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ARDocUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ARDocUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ARDocUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ARDocUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ARDocUpdateReq> getRequestType() {
        return new TypeToken<DCP_ARDocUpdateReq>(){};
    }

    @Override
    protected DCP_ARDocUpdateRes getResponseType() {
        return new DCP_ARDocUpdateRes();
    }
}
