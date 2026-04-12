package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ExpenseCopyReq;
import com.dsc.spos.json.cust.res.DCP_ExpenseCopyRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ExpenseCopy extends SPosAdvanceService<DCP_ExpenseCopyReq, DCP_ExpenseCopyRes> {
    @Override
    protected void processDUID(DCP_ExpenseCopyReq req, DCP_ExpenseCopyRes res) throws Exception {


        String sourceBFeeNo = req.getRequest().getSourceBfeeNo();
        String targetBFeeNo = req.getRequest().getTargetBfeeNo();

        if (StringUtils.isBlank(targetBFeeNo)) {
            targetBFeeNo = getOrderNO(req, "KHJS");
        } 

        String querySql = " SELECT " +
                " a.SHOPID,a.BDATE,a.MEMO,a.ORG_NAME,a.DOC_TYPE,a.TOT_AMT," +
                " a.TAXRATE,a.TAXCODE ,a.CORP,a.SETTLEDBY,a.SETTMODE,a.SETTTYPE," +
                " a.SOURCETYPE,a.SOURCENO,a.SOURCENOSEQ, b.* " +
                " FROM DCP_EXPSHEET a" +
                "  INNER JOIN DCP_EXPDETAIL b on a.EID=b.EID and a.BFEENO=b.BFEENO " +
                " WHERE a.EID='" + req.geteId() + "'" + " AND a.BFEENO='" + sourceBFeeNo + "'";

        List<Map<String, Object>> qDatas = this.doQueryData(querySql, null);

        if (CollectionUtils.isNotEmpty(qDatas)) {
            Map<String, Object> qDataMap = qDatas.get(0);

            ColumnDataValue dcp_expsheet = new ColumnDataValue();

            dcp_expsheet.add("EID", DataValues.newString(req.geteId()));
            dcp_expsheet.add("ORGANIZATIONNO", DataValues.newString(qDataMap.get("ORGANIZATIONNO").toString()));
            dcp_expsheet.add("SHOPID", DataValues.newString(qDataMap.get("SHOPID").toString()));
            dcp_expsheet.add("BFEENO", DataValues.newString(targetBFeeNo));

            dcp_expsheet.add("BDATE", DataValues.newString(qDataMap.get("BDATE").toString()));
            dcp_expsheet.add("DOC_TYPE", DataValues.newString(qDataMap.get("DOC_TYPE").toString()));

            dcp_expsheet.add("TOT_AMT", DataValues.newString(qDataMap.get("TOT_AMT").toString()));
            dcp_expsheet.add("STATUS", DataValues.newString("0"));
            dcp_expsheet.add("CORP", DataValues.newString(qDataMap.get("CORP").toString()));
            dcp_expsheet.add("ORG_NAME", DataValues.newString(qDataMap.get("ORG_NAME").toString()));
            dcp_expsheet.add("SETTLEDBY", DataValues.newString(qDataMap.get("SETTLEDBY").toString()));
            dcp_expsheet.add("SETTMODE", DataValues.newString(qDataMap.get("SETTMODE").toString()));
            dcp_expsheet.add("SETTTYPE", DataValues.newString(qDataMap.get("SETTTYPE").toString()));
            dcp_expsheet.add("PAYDATENO", DataValues.newString(qDataMap.get("PAYDATENO").toString()));
            dcp_expsheet.add("BILLDATENO", DataValues.newString(qDataMap.get("BILLDATENO").toString()));
            dcp_expsheet.add("SOURCETYPE", DataValues.newString(qDataMap.get("SOURCETYPE").toString()));
            dcp_expsheet.add("SOURCENO", DataValues.newString(qDataMap.get("SOURCENO").toString()));

            dcp_expsheet.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
            dcp_expsheet.add("NAME", DataValues.newString(req.getEmployeeName()));
            dcp_expsheet.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
            dcp_expsheet.add("DEPARTNAME", DataValues.newString(req.getDepartmentName()));

            dcp_expsheet.add("CURRENCY", DataValues.newString(qDataMap.get("CURRENCY").toString()));

            dcp_expsheet.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_expsheet.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_expsheet.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_EXPSHEET", dcp_expsheet)));

            for (Map<String, Object> oneData : qDatas) {
                ColumnDataValue dcp_expdetail = new ColumnDataValue();

                dcp_expdetail.add("EID", DataValues.newString(req.geteId()));
                dcp_expdetail.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));

                dcp_expdetail.add("BFEENO", DataValues.newString(targetBFeeNo));
                dcp_expdetail.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));

                dcp_expdetail.add("FEE", DataValues.newString(oneData.get("FEE").toString()));
                dcp_expdetail.add("AMT", DataValues.newString(oneData.get("AMT").toString()));
                dcp_expdetail.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                dcp_expdetail.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                dcp_expdetail.add("STATUS", DataValues.newString("-1"));

                dcp_expdetail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_expdetail.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_expdetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_expdetail.add("ORG_NAME", DataValues.newString(oneData.get("ORG_NAME").toString()));
                dcp_expdetail.add("PRICECATEGORY", DataValues.newString(oneData.get("PRICECATEGORY").toString()));
                dcp_expdetail.add("FEENAME", DataValues.newString(oneData.get("FEENAME").toString()));
                dcp_expdetail.add("CURRENCY", DataValues.newString(oneData.get("CURRENCY").toString()));
                dcp_expdetail.add("TAXCODEINFO", DataValues.newString(oneData.get("TAXCODEINFO").toString()));
                dcp_expdetail.add("FEETYPE", DataValues.newString(oneData.get("FEETYPE").toString()));
                dcp_expdetail.add("STARTDATE", DataValues.newDate(DateFormatUtils.getDate(oneData.get("STARTDATE").toString())));
                dcp_expdetail.add("ENDDATE", DataValues.newDate(DateFormatUtils.getDate(oneData.get("ENDDATE").toString() )));
                dcp_expdetail.add("SETTACCPERIOD", DataValues.newString(oneData.get("SETTACCPERIOD").toString()));
                dcp_expdetail.add("CATEGORY", DataValues.newString(oneData.get("CATEGORY").toString()));
                dcp_expdetail.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                dcp_expdetail.add("DEPARTNAME", DataValues.newString(req.getDepartmentName()));
                dcp_expdetail.add("ISINSETTDOC", DataValues.newString(oneData.get("ISINSETTDOC").toString()));
                dcp_expdetail.add("EXPATTRITYPE", DataValues.newString(oneData.get("EXPATTRITYPE").toString()));
                dcp_expdetail.add("EXPATTRIORG", DataValues.newString(oneData.get("EXPATTRIORG").toString()));
                dcp_expdetail.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
                dcp_expdetail.add("PLUNAME", DataValues.newString(oneData.get("PLUNAME").toString()));
                dcp_expdetail.add("ISINVOCEINCL", DataValues.newString(oneData.get("ISINVOCEINCL").toString()));
                dcp_expdetail.add("SOURCENO", DataValues.newString(oneData.get("SOURCENO").toString()));
                dcp_expdetail.add("SOURCENOSEQ", DataValues.newString(oneData.get("SOURCENOSEQ").toString()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_EXPDETAIL", dcp_expdetail)));

            }

        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
//        res.setBfeeNo(bfeeNo);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ExpenseCopyReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ExpenseCopyReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ExpenseCopyReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ExpenseCopyReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpenseCopyReq> getRequestType() {
        return new TypeToken<DCP_ExpenseCopyReq>() {
        };
    }

    @Override
    protected DCP_ExpenseCopyRes getResponseType() {
        return new DCP_ExpenseCopyRes();
    }
}
