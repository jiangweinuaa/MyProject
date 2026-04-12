package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ExpenseCreateReq;
import com.dsc.spos.json.cust.res.DCP_ExpenseCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ExpenseCreate extends SPosAdvanceService<DCP_ExpenseCreateReq, DCP_ExpenseCreateRes> {
    @Override
    protected void processDUID(DCP_ExpenseCreateReq req, DCP_ExpenseCreateRes res) throws Exception {

        String corp = req.getRequest().getCorp();
        //查询当前法人的关账日期
        String accSql = "select a.*,to_char(a.CLOSINGDATE,'yyyyMMdd') as CLOSINGDATE  from DCP_ACOUNT_SETTING a where a.eid='" + req.geteId() + "' and a.corp='" + corp + "'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(accList)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的法人账套" + corp);
        }
        String closingDate = accList.get(0).get("CLOSINGDATE").toString();
        String bDate = req.getRequest().getBdate();
        if (DateFormatUtils.compareDate(bDate , closingDate ) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于关账日期");
        }

        String bfeeNo;
        if ("0".equals(req.getRequest().getDoc_Type())) {
            bfeeNo = getOrderNO(req, corp, "GYJS");
        } else {
            bfeeNo = getOrderNO(req, corp, "KHJS");
        }

        DCP_ExpenseCreateReq.Request request = req.getRequest();

        double totAmt = 0;
        if (null != request.getDatas()) {
            for (DCP_ExpenseCreateReq.Datas detail : request.getDatas()) {
                ColumnDataValue dcp_expdetail = new ColumnDataValue();

                dcp_expdetail.add("EID", DataValues.newString(req.geteId()));
                dcp_expdetail.add("ORGANIZATIONNO", DataValues.newString(request.getOrganizationNo()));

                dcp_expdetail.add("BFEENO", DataValues.newString(bfeeNo));
                dcp_expdetail.add("ITEM", DataValues.newString(detail.getItem()));

                dcp_expdetail.add("FEE", DataValues.newString(detail.getFee()));

                dcp_expdetail.add("AMT", DataValues.newString(detail.getAmt()));
                totAmt += Double.parseDouble(detail.getAmt());
                dcp_expdetail.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
                dcp_expdetail.add("TAXRATE", DataValues.newString(detail.getTaxRate()));
                dcp_expdetail.add("STATUS", DataValues.newString("-1"));
                dcp_expdetail.add("CREATEOPID", DataValues.newString(request.getEmployeeNo()));
                dcp_expdetail.add("CREATEDEPTID", DataValues.newString(request.getDepartNo()));
                dcp_expdetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_expdetail.add("ORG_NAME", DataValues.newString(request.getOrg_Name()));
                dcp_expdetail.add("PRICECATEGORY", DataValues.newString(detail.getPriceCategory()));
                dcp_expdetail.add("FEENAME", DataValues.newString(detail.getFeeName()));
                String currency = detail.getCurrency();
                if (StringUtils.isEmpty(currency)) {
                    currency = req.getRequest().getCurrency();
                }
                dcp_expdetail.add("CURRENCY", DataValues.newString(currency));
                dcp_expdetail.add("TAXCODEINFO", DataValues.newString(detail.getTaxCodeInfo()));
                dcp_expdetail.add("FEETYPE", DataValues.newString(detail.getFeeType()));
                dcp_expdetail.add("STARTDATE", DataValues.newDate(DateFormatUtils.getDate(detail.getStartDate())));
                dcp_expdetail.add("ENDDATE", DataValues.newDate(DateFormatUtils.getDate(detail.getEndDate())));
                dcp_expdetail.add("SETTACCPERIOD", DataValues.newString(detail.getSettAccPeriod()));
                dcp_expdetail.add("CATEGORY", DataValues.newString(detail.getCategory()));
                dcp_expdetail.add("DEPARTNO", DataValues.newString(detail.getDepartNo()));
                dcp_expdetail.add("DEPARTNAME", DataValues.newString(detail.getDepartName()));
                dcp_expdetail.add("ISINSETTDOC", DataValues.newString(detail.getIsInSettDoc()));
                dcp_expdetail.add("EXPATTRITYPE", DataValues.newString(detail.getExpAttriType()));
                dcp_expdetail.add("EXPATTRIORG", DataValues.newString(detail.getExpAttriOrg()));
                dcp_expdetail.add("PLUNO", DataValues.newString(detail.getPluNo()));
                dcp_expdetail.add("PLUNAME", DataValues.newString(detail.getPluName()));
                dcp_expdetail.add("ISINVOCEINCL", DataValues.newString(detail.getIsInvoiceIncl()));
                dcp_expdetail.add("SOURCENO", DataValues.newString(detail.getSourceNo()));
                dcp_expdetail.add("SOURCENOSEQ", DataValues.newString(detail.getSourceNoSeq()));
                dcp_expdetail.add("MEMO", DataValues.newString(detail.getMeno()));


                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_EXPDETAIL", dcp_expdetail)));

            }

            ColumnDataValue dcp_expsheet = new ColumnDataValue();
            dcp_expsheet.add("EID", DataValues.newString(req.geteId()));
            dcp_expsheet.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
            dcp_expsheet.add("SHOPID", DataValues.newString(req.getShopId()));
            dcp_expsheet.add("BFEENO", DataValues.newString(bfeeNo));

            dcp_expsheet.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(request.getBdate())));
            dcp_expsheet.add("DOC_TYPE", DataValues.newString(request.getDoc_Type()));

            dcp_expsheet.add("TOT_AMT", DataValues.newString(request.getTot_Amt()));
            dcp_expsheet.add("STATUS", DataValues.newString("0"));
            dcp_expsheet.add("CORP", DataValues.newString(request.getCorp()));
            dcp_expsheet.add("ORG_NAME", DataValues.newString(request.getOrg_Name()));
            dcp_expsheet.add("SETTLEDBY", DataValues.newString(request.getSupplierNo()));
            dcp_expsheet.add("SETTMODE", DataValues.newString(request.getSettMode()));
            dcp_expsheet.add("PAYDATENO", DataValues.newString(request.getPayDateNo()));
            dcp_expsheet.add("BILLDATENO", DataValues.newString(request.getBillDateNo()));

            dcp_expsheet.add("SETTTYPE", DataValues.newString(request.getSettType()));
            dcp_expsheet.add("SOURCETYPE", DataValues.newString(request.getSourceType()));
            dcp_expsheet.add("SOURCENO", DataValues.newString(request.getSourceNo()));
            dcp_expsheet.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
            dcp_expsheet.add("NAME", DataValues.newString(req.getEmployeeName()));
            dcp_expsheet.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
            dcp_expsheet.add("DEPARTNAME", DataValues.newString(req.getDepartmentName()));
            dcp_expsheet.add("CURRENCY", DataValues.newString(request.getCurrency()));

            dcp_expsheet.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_expsheet.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_expsheet.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_EXPSHEET", dcp_expsheet)));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setBfeeNo(bfeeNo);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ExpenseCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ExpenseCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ExpenseCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ExpenseCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpenseCreateReq> getRequestType() {
        return new TypeToken<DCP_ExpenseCreateReq>() {
        };
    }

    @Override
    protected DCP_ExpenseCreateRes getResponseType() {
        return new DCP_ExpenseCreateRes();
    }
}
