package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApWrtOffCreateReq;
import com.dsc.spos.json.cust.res.DCP_ApWrtOffCreateRes;
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

public class DCP_ApWrtOffCreate extends SPosAdvanceService<DCP_ApWrtOffCreateReq, DCP_ApWrtOffCreateRes> {

    @Override
    protected void processDUID(DCP_ApWrtOffCreateReq req, DCP_ApWrtOffCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();

        String createDateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());


        String wrtOffNo = this.getOrderNO(req, "HX");

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
        mainColumns.add("WRTOFFNO", DataValues.newString(wrtOffNo));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
        mainColumns.add("STATUS", DataValues.newString("0"));
        mainColumns.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        mainColumns.add("ACCEMPLOYEENO", DataValues.newString(req.getRequest().getAccEmployeeNo()));
        mainColumns.add("BDATE", DataValues.newString(req.getRequest().getBDate()));
        mainColumns.add("TASKID", DataValues.newString(req.getRequest().getTaskId()));
        mainColumns.add("BIZPARTNERNO", DataValues.newString(req.getRequest().getBizPartnerNo()));
        mainColumns.add("RECEIVER", DataValues.newString(req.getRequest().getReceiver()));
        mainColumns.add("SOURCENO", DataValues.newString(req.getRequest().getSourceNo()));
        //mainColumns.add("GRPPMTNO", DataValues.newString(req.getRequest()()));
        mainColumns.add("GLNO", DataValues.newString(req.getRequest().getGlNo()));
        mainColumns.add("FCYDRTATAMT", DataValues.newDecimal(req.getRequest().getFCYDRTATAmt()));
        mainColumns.add("FCYCRTATAMT", DataValues.newDecimal(req.getRequest().getFCYCRTATAmt()));
        mainColumns.add("LCYDRTATAMT", DataValues.newDecimal(req.getRequest().getLCYDRTATAmt()));
        mainColumns.add("LCYCRTATAMT", DataValues.newDecimal(req.getRequest().getLCYCRTATAmt()));
        mainColumns.add("ISAUTOWRITEOFF", DataValues.newString("N"));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_APWRTOFF",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_ApWrtOffCreateReq.ApWFListLevel> apWFList = req.getRequest().getApWFList();
        if(CollUtil.isNotEmpty(apWFList)){
            for (DCP_ApWrtOffCreateReq.ApWFListLevel item : apWFList){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                detailColumns.add("CREATE_DATE", DataValues.newString(createDate));
                detailColumns.add("CREATE_TIME", DataValues.newString(createTime));
                detailColumns.add("STATUS", DataValues.newString("0"));
                detailColumns.add("CORP", DataValues.newString(req.getRequest().getCorp()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
                detailColumns.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
                detailColumns.add("WRTOFFNO", DataValues.newString(wrtOffNo));
                detailColumns.add("ITEM", DataValues.newInteger(item.getItem()));
                detailColumns.add("TASKID", DataValues.newString(item.getTaskId()));
                detailColumns.add("WRTOFFTYPE", DataValues.newString(item.getWrtOffType()));
                detailColumns.add("SOURCEORG", DataValues.newString(item.getSourceOrg()));
                detailColumns.add("WRTOFFBILLNO", DataValues.newString(item.getWrtOffBillNo()));
                detailColumns.add("WRTOFFBILLITEM", DataValues.newInteger(item.getWrtOffBillitem()));
                detailColumns.add("INSTPMTSEQ", DataValues.newInteger(item.getInstPmtSeq()));
                detailColumns.add("MEMO", DataValues.newString(item.getMemo()));
                detailColumns.add("BSNO", DataValues.newString(item.getBsNo()));
                detailColumns.add("WRTOFFDIRECTION", DataValues.newString(item.getWrtOffDirection()));
                detailColumns.add("APSUBJECTID", DataValues.newString(item.getApSubjectId()));
                detailColumns.add("EMPLOYEENO", DataValues.newString(item.getEmployeeNo()));
                detailColumns.add("DEPARTNO", DataValues.newString(item.getDepartId()));
                detailColumns.add("CATEGORY", DataValues.newString(item.getCateGory()));
                detailColumns.add("SECREFNO", DataValues.newString(item.getSecRefNo()));
                detailColumns.add("GLNO", DataValues.newString(item.getGlNo()));
                detailColumns.add("PAYDUEDATE", DataValues.newString(item.getPayDueDate()));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(item.getBizPartnerNo()));
                detailColumns.add("RECEIVER", DataValues.newString(item.getReceiver()));
                detailColumns.add("FREECHARS1", DataValues.newString(item.getFreeChars1()));
                detailColumns.add("FREECHARS2", DataValues.newString(item.getFreeChars2()));
                detailColumns.add("FREECHARS3", DataValues.newString(item.getFreeChars3()));
                detailColumns.add("FREECHARS4", DataValues.newString(item.getFreeChars4()));
                detailColumns.add("FREECHARS5", DataValues.newString(item.getFreeChars5()));
                detailColumns.add("CURRENCY", DataValues.newString(item.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newDecimal(item.getExRate()));
                detailColumns.add("FCYREVAMT", DataValues.newDecimal(item.getFCYRevAmt()));
                detailColumns.add("LCYREVAMT", DataValues.newDecimal(item.getLCYRevAmt()));
                detailColumns.add("FCYBTAXWRTOFFAMT", DataValues.newDecimal(item.getFCYBTaxWrtOffAmt()));
                detailColumns.add("LCYBTAXWRTOFFAMT", DataValues.newDecimal(item.getLCYBTaxWrtOffAmt()));
                detailColumns.add("INVOICENUMBER", DataValues.newString(item.getInvoiceNumber()));
                detailColumns.add("INVOICECODE", DataValues.newString(item.getInvoiceCode()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLWRTOFF",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        List<DCP_ApWrtOffCreateReq.PmtListLevel> pmtList = req.getRequest().getPmtList();
        if(CollUtil.isNotEmpty(pmtList)){
            for (DCP_ApWrtOffCreateReq.PmtListLevel item : pmtList){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                detailColumns.add("CREATE_DATE", DataValues.newString(createDate));
                detailColumns.add("CREATE_TIME", DataValues.newString(createTime));
                detailColumns.add("STATUS", DataValues.newString("0"));
                detailColumns.add("CORP", DataValues.newString(req.getRequest().getCorp()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
                detailColumns.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
                detailColumns.add("WRTOFFNO", DataValues.newString(wrtOffNo));
                detailColumns.add("ITEM", DataValues.newInteger(item.getItem()));
                detailColumns.add("TASKID", DataValues.newString(item.getTaskId()));
                //detailColumns.add("WRTOFFTYPE", DataValues.newString(item.getwr()));
                detailColumns.add("SOURCEORG", DataValues.newString(item.getSourceOrg()));
                detailColumns.add("WRTOFFBILLNO", DataValues.newString(item.getWrtOffNo()));
                detailColumns.add("WRTOFFBILLITEM", DataValues.newInteger(item.getWrtOffItem()));
                //detailColumns.add("INSTPMTSEQ", DataValues.newInteger(item.geti()));
                detailColumns.add("MEMO", DataValues.newString(item.getMemo()));
                detailColumns.add("PAIDBILLNO", DataValues.newString(item.getPaidBillNo()));

                detailColumns.add("PMTCODE", DataValues.newString(item.getPmtCode()));
                detailColumns.add("ACCTBILLNO", DataValues.newString(item.getAccountBillNo()));
                detailColumns.add("TRANSFERREDDATA", DataValues.newString(item.getTransferredData()));
                detailColumns.add("BNKDEPWDRAWCODE", DataValues.newString(item.getBnkDepWdrawCode()));
                detailColumns.add("CASHCHGCODE", DataValues.newString(item.getCashChgCode()));
                detailColumns.add("TRANSINCUSTCODE", DataValues.newString(item.getTransInCustCode()));
                detailColumns.add("TRANSINPMTBILLNO", DataValues.newString(item.getTransInPmtBillNo()));
                detailColumns.add("WRTOFFDIRECTION", DataValues.newString(item.getWrtOffDirection()));
                detailColumns.add("WRTOFFSUBJECT", DataValues.newString(item.getWrtOffSubject()));
                detailColumns.add("PAYDUEDATE", DataValues.newString(item.getPayDueDate()));
                detailColumns.add("RECEIVER", DataValues.newString(item.getReceiver()));
                detailColumns.add("SALERACCOUNT", DataValues.newString(item.getSalerAccount()));
                detailColumns.add("SALERACCOUNTCODE", DataValues.newString(item.getSalerAccountCode()));
                detailColumns.add("SALENAME", DataValues.newString(item.getSaleName()));
                detailColumns.add("FREECHARS1", DataValues.newString(item.getFreeChars1()));
                detailColumns.add("FREECHARS2", DataValues.newString(item.getFreeChars2()));
                detailColumns.add("FREECHARS3", DataValues.newString(item.getFreeChars3()));
                detailColumns.add("FREECHARS4", DataValues.newString(item.getFreeChars4()));
                detailColumns.add("FREECHARS5", DataValues.newString(item.getFreeChars5()));
                detailColumns.add("CURRENCY", DataValues.newString(item.getCurrency()));
                detailColumns.add("EXRATE", DataValues.newDecimal(item.getExRate()));
                detailColumns.add("FCYREVAMT", DataValues.newDecimal(item.getFCYRevAmt()));
                detailColumns.add("LCYREVAMT", DataValues.newDecimal(item.getLCYRevAmt()));



                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_APBILLPMT",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApWrtOffCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApWrtOffCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApWrtOffCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApWrtOffCreateReq req) throws Exception {
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
    protected TypeToken<DCP_ApWrtOffCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ApWrtOffCreateReq>(){};
    }

    @Override
    protected DCP_ApWrtOffCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ApWrtOffCreateRes();
    }

}


