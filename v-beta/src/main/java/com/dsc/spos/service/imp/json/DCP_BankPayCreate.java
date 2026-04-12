package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BankPayCreateReq;
import com.dsc.spos.json.cust.res.DCP_BankPayCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_BankPayCreate extends SPosAdvanceService<DCP_BankPayCreateReq, DCP_BankPayCreateRes> {


    @Override
    protected void processDUID(DCP_BankPayCreateReq req, DCP_BankPayCreateRes res) throws Exception {


        String billNo = getOrderNO(req, "CMZF");

        for (DCP_BankPayCreateReq.CmList item : req.getRequest().getCmList()) {
            ColumnDataValue dcp_bankPayDetail = new ColumnDataValue();

            dcp_bankPayDetail.add("EID", DataValues.newString(req.geteId()));
            dcp_bankPayDetail.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_bankPayDetail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
            dcp_bankPayDetail.add("CMNO", DataValues.newString(billNo));
            dcp_bankPayDetail.add("ITEM", DataValues.newString(item.getItem()));
//            dcp_bankPayDetail.add("SOURCEORG", DataValues.newString(item.getSourceNo()));
            dcp_bankPayDetail.add("TASKID", DataValues.newString(item.getTaskId()));
            dcp_bankPayDetail.add("SOURCENO", DataValues.newString(item.getSourceNo()));
            dcp_bankPayDetail.add("SOURCENOSEQ", DataValues.newString(item.getSourceNoSeq()));
            dcp_bankPayDetail.add("BIZPARTNERNO", DataValues.newString(req.getRequest().getBizPartnerNo()));
            dcp_bankPayDetail.add("PAYEMPLOYEE", DataValues.newString(req.getRequest().getPayEmployee()));
            dcp_bankPayDetail.add("CLASSNO", DataValues.newString(item.getClassNo()));
            dcp_bankPayDetail.add("CFCODE", DataValues.newString(item.getCfCode()));
            dcp_bankPayDetail.add("DEPWDRAWCODE", DataValues.newString(item.getDepWdrawCode()));
            dcp_bankPayDetail.add("OFFSETSUBJECT", DataValues.newString(item.getOffsetSubject()));
            dcp_bankPayDetail.add("FCYAMT", DataValues.newString(item.getFCYAmt()));
            dcp_bankPayDetail.add("LCYAMT", DataValues.newString(item.getLCYAmt()));
            dcp_bankPayDetail.add("EXRATE", DataValues.newString(item.getExRate()));
            dcp_bankPayDetail.add("PENDOFFSETNO", DataValues.newString(item.getPendOffsetNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_BANKPAYDETAIL", dcp_bankPayDetail)));
        }

        ColumnDataValue dcp_bankPay = new ColumnDataValue();
        dcp_bankPay.add("EID", DataValues.newString(req.geteId()));
        dcp_bankPay.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        dcp_bankPay.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        dcp_bankPay.add("CMNO", DataValues.newString(billNo));
        dcp_bankPay.add("BDATE", DataValues.newString(req.getRequest().getBDate()));
//        dcp_bankPay.add("CLASSNO", DataValues.newString(req.getRequest().getBDate()));
        dcp_bankPay.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
        dcp_bankPay.add("ACCOUNTCODE", DataValues.newString(req.getRequest().getAccountCode()));
        dcp_bankPay.add("BIZPARTNERNO", DataValues.newString(req.getRequest().getBizPartnerNo()));
        dcp_bankPay.add("PAYEMPLOYEE", DataValues.newString(req.getRequest().getPayEmployee()));
        dcp_bankPay.add("DEPWDRAWCODE", DataValues.newString(req.getRequest().getDepWdrawCode()));
//        dcp_bankPay.add("CFCODE", DataValues.newString(req.getRequest().getCmNo()));
//        dcp_bankPay.add("PAYDUEDATE", DataValues.newString(req.getRequest().getCmNo()));
//        dcp_bankPay.add("PAYDATE", DataValues.newString(req.getRequest().getp()));
//        dcp_bankPay.add("ARNO", DataValues.newString(req.getRequest().get()));
//        dcp_bankPay.add("CLASSTYPE", DataValues.newString(req.getRequest().getc()));
//        dcp_bankPay.add("PAYBANKCODE", DataValues.newString(req.getRequest().getPayEmployee()));
//        dcp_bankPay.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
//        dcp_bankPay.add("EXRATE", DataValues.newString(req.getRequest().gete()));
//        dcp_bankPay.add("FCYAMT", DataValues.newString(req.getRequest().getfc()));
//        dcp_bankPay.add("LCYAMT", DataValues.newString(req.getRequest().getfc()));
//        dcp_bankPay.add("GLNO", DataValues.newString(req.getRequest().getfc()));
//        dcp_bankPay.add("ISENTY", DataValues.newString(req.getRequest().getfc()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_BANKPAY", dcp_bankPay)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankPayCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankPayCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankPayCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BankPayCreateReq req) throws Exception {

        StringBuilder errorMsg = new StringBuilder();
        //        "taskId": "1：采购应付单；2：内部应付单，3：预付采购单，4：员工报销单，5：员工借支单，6：无来源预付单，7：其他应付单",
        for (DCP_BankPayCreateReq.CmList item : req.getRequest().getCmList()) {
            if (!"6".equals(item.getTaskId()) && Check.isEmpty(item.getSourceNo())) {
                errorMsg.append("来源单号不能为空");
            }

        }
        if (errorMsg.length() > 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_BankPayCreateReq> getRequestType() {
        return new TypeToken<DCP_BankPayCreateReq>() {
        };
    }

    @Override
    protected DCP_BankPayCreateRes getResponseType() {
        return new DCP_BankPayCreateRes();
    }
}
