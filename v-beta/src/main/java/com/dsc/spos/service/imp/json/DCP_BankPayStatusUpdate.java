package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BankPayStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BankPayStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.dataaux.ApBillData;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_BankPayStatusUpdate extends SPosAdvanceService<DCP_BankPayStatusUpdateReq, DCP_BankPayStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_BankPayStatusUpdateReq req, DCP_BankPayStatusUpdateRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无法查询到对应数据！");
        }
        String opType = req.getRequest().getOpType();
        ColumnDataValue uptCondition = new ColumnDataValue();
        ColumnDataValue uptData = new ColumnDataValue();

        uptCondition.add("EID", DataValues.newString(req.geteId()));
        uptCondition.add("CMNO", DataValues.newString(req.getRequest().getCmNo()));
        if (Constant.OPR_TYPE_CONFIRM.equals(opType)) {

            String taskId = qData.get(0).get("TASKID").toString();
            if ("6".equals(taskId)) {
                ApBillData apBillData = new ApBillData();
                apBillData.insertApBillFromDcpBankPay(this.pData, qData);
            }

            for (Map<String, Object> oneData : qData) {
                ColumnDataValue dcp_bankcashflows = new ColumnDataValue();

                dcp_bankcashflows.add("EID", DataValues.newString(oneData.get("EID").toString()));
                dcp_bankcashflows.add("CORP", DataValues.newString(oneData.get("CORP").toString()));
                dcp_bankcashflows.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
                dcp_bankcashflows.add("SOURCEORG", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
                dcp_bankcashflows.add("SOURCENO", DataValues.newString(oneData.get("CMNO").toString()));
                dcp_bankcashflows.add("SOURCENOSEQ", DataValues.newString(oneData.get("ITEM").toString()));
                dcp_bankcashflows.add("TASKID", DataValues.newString(oneData.get("TASKID").toString()));
                dcp_bankcashflows.add("ACCOUNTCODE", DataValues.newString(oneData.get("ACCOUNTCODE").toString()));
                dcp_bankcashflows.add("BIZPARTNERNO", DataValues.newString(oneData.get("BIZPARTNERNO").toString()));
                dcp_bankcashflows.add("ACCOUNTDATE", DataValues.newString(oneData.get("BDATE").toString()));
                dcp_bankcashflows.add("DIRECTION", DataValues.newString(oneData.get("CFCODE").toString()));
                dcp_bankcashflows.add("DEPWDRAWCODE", DataValues.newString(oneData.get("DEPWDRAWCODE").toString()));
                dcp_bankcashflows.add("CURRENCY", DataValues.newString(oneData.get("CURRENCY").toString()));
                dcp_bankcashflows.add("EXRATE", DataValues.newString("1"));
                dcp_bankcashflows.add("FCYAMT", DataValues.newString(oneData.get("FCYAMT").toString()));
                dcp_bankcashflows.add("LCYAMT", DataValues.newString(oneData.get("LCYAMT").toString()));
                dcp_bankcashflows.add("CLASSNO", DataValues.newString(oneData.get("CLASSNO").toString()));
                dcp_bankcashflows.add("BANKACCOUNT", DataValues.newString(oneData.get("PAYBANKCODE").toString()));
                dcp_bankcashflows.add("SALENAME", DataValues.newString(oneData.get("EMPLOYEENO").toString()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_BANKCASHFLOWS", dcp_bankcashflows)));
            }

            uptData.add("STATUS", DataValues.newString("2"));
            uptData.add("CONFIRMOPID", DataValues.newString(req.getEmployeeNo()));
            uptData.add("CONFIRMTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        } else {

            ColumnDataValue delCondition = new ColumnDataValue();
            delCondition.add("EID", DataValues.newString(req.geteId()));
            delCondition.add("SOURCENO", DataValues.newString(req.getRequest().getCmNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BANKCASHFLOWS", delCondition)));
            List<String> apNos = new ArrayList<>();
            for (Map<String, Object> map : qData) {
                //删除预估待抵单
                String pendoffsetno = map.get("PENDOFFSETNO").toString();
                if (Check.isNotEmpty(pendoffsetno) && !apNos.contains(pendoffsetno)) {
                    apNos.add(pendoffsetno);

                    ColumnDataValue delApBill = new ColumnDataValue();
                    delApBill.add("EID", DataValues.newString(req.geteId()));
                    delApBill.add("APNO", DataValues.newString(pendoffsetno));

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILL", delApBill)));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLDETAIL", delApBill)));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APPERD", delApBill)));
                }
            }

            if (Constant.OPR_TYPE_CANCEL.equals(opType)) {
                uptData.add("STATUS", DataValues.newString("-1"));
                uptData.add("CANCELOPID", DataValues.newString(req.getEmployeeNo()));
                uptData.add("CANCELTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            } else if (Constant.OPR_TYPE_UNCONFIRM.equals(opType)) {
                uptData.add("STATUS", DataValues.newString("0"));
                uptData.add("CONFIRMOPID", DataValues.newString(""));
                uptData.add("CONFIRMTIME", DataValues.newDate(null));

            }

        }

        uptData.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        uptData.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_BANKPAY", uptCondition, uptData)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected String getQuerySql(DCP_BankPayStatusUpdateReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.* ")
                .append(" FROM DCP_BANKPAY a ")
                .append(" LEFT JOIN DCP_BANKPAYDETAIL b ON a.EID = b.EID and a.CMNO=b.CMNO ")
        ;

        querySql.append(" WHERE a.EID = '").append(req.geteId()).append("' ");

        if (Check.isNotEmpty(req.getRequest().getCmNo())) {
            querySql.append(" AND a.CMNO = '").append(req.getRequest().getCmNo()).append("' ");
        }


        return querySql.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankPayStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankPayStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankPayStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BankPayStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankPayStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_BankPayStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_BankPayStatusUpdateRes getResponseType() {
        return new DCP_BankPayStatusUpdateRes();
    }
}
