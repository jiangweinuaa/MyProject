package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ExpSheetStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ExpSheetStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ExpSheetStatusUpdate extends SPosAdvanceService<DCP_ExpSheetStatusUpdateReq, DCP_ExpSheetStatusUpdateRes> {
    @Override
    protected void processDUID(DCP_ExpSheetStatusUpdateReq req, DCP_ExpSheetStatusUpdateRes res) throws Exception {


        String querySql = " SELECT * FROM DCP_EXPSHEET WHERE eid='" + req.geteId() + "' AND BFEENO='" + req.getRequest().getBfeeNo() + "'";
        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在!");
        }

        String docType = qData.get(0).get("DOC_TYPE").toString();

        String totAmt = StringUtils.toString(qData.get(0).get("TOT_AMT").toString(), "0");
        if (Double.parseDouble(totAmt) <= 0) {
            if ("0".equals(docType)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "金额小于等于 0，请重新检查【供应商费用单】");
            } else {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "金额小于等于 0，请重新检查【大客户费用单】");
            }

        }

        String oStatus = qData.get(0).get("STATUS").toString();
        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_expdetail = new ColumnDataValue();
        ColumnDataValue dcp_expsheet = new ColumnDataValue();

        String status = "0";
        if ("confirm".equals(req.getRequest().getOpType())) {
            status = "1";
            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不可审核!");
            }
            dcp_expsheet.add("CONFIRMBY", req.getEmployeeNo());
            dcp_expsheet.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_expsheet.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

            List<Map<String, Object>> qDetail = doQueryData(getQueryDetailSql(req), null);
            String bDate = DateFormatUtils.getPlainDate(qDetail.get(0).get("BDATE").toString());
            for (Map<String, Object> detail : qDetail) {
                String settAccPeriod = detail.get("SETTACCPERIOD").toString().replaceAll("-", "");
                if (!bDate.contains(settAccPeriod)) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "结算会计期:" + settAccPeriod + "与单据日期:" + bDate + "不符!单据不可审核!");
                }

            }


            for (Map<String, Object> detail : qDetail) {
                ColumnDataValue dcp_settleData = new ColumnDataValue();

                dcp_settleData.add("EID", detail.get("EID").toString());
//                dcp_settleData.add("ORGANIZATIONNO", detail.get("ORGANIZATIONNO").toString());
//                dcp_settleData.add("ORGANIZATIONNO", detail.get("EXPATTRIORG").toString());//组织改为取结算组织
                dcp_settleData.add("ORGANIZATIONNO", detail.get("PAYORGNO").toString());
                dcp_settleData.add("BDATE", detail.get("BDATE").toString());
                dcp_settleData.add("BTYPE", "3");
                dcp_settleData.add("BILLNO", detail.get("BFEENO").toString());
                dcp_settleData.add("ITEM", detail.get("ITEM").toString());
                if ("0".equals(docType)) {
                    dcp_settleData.add("BIZTYPE", "1");
                } else {
                    dcp_settleData.add("BIZTYPE", "2");
                }

                dcp_settleData.add("BIZPARTNERNO", detail.get("SETTLEDBY").toString());
//                dcp_settleData.add("PAYORGNO", detail.get("PAYORGNO").toString());
                dcp_settleData.add("PAYORGNO", detail.get("EXPATTRIORG").toString());
                dcp_settleData.add("BILLDATENO", detail.get("BILLDATENO").toString());
                dcp_settleData.add("PAYDATENO", detail.get("PAYDATENO").toString());
                dcp_settleData.add("BILLDATE", detail.get("BDATE").toString());
                dcp_settleData.add("PAYDATE", detail.get("BDATE").toString());
                String date = detail.get("BDATE").toString();

                dcp_settleData.add("YEAR", date.substring(0, 4));
                dcp_settleData.add("MONTH", date.substring(4, 6));
                dcp_settleData.add("CURRENCY", detail.get("CURRENCY").toString());
                dcp_settleData.add("TAXCODE", detail.get("TAXCODE").toString());
                dcp_settleData.add("TAXRATE", detail.get("CTAXRATE").toString());
                String feeType = detail.get("FEETYPE").toString();
                if ("0".equals(docType)) {
                    if ("0".equals(feeType)) {
                        dcp_settleData.add("DIRECTION", "1");
                    } else {
                        dcp_settleData.add("DIRECTION", "-1");
                    }

                } else {
                    if ("0".equals(feeType)) {
                        dcp_settleData.add("DIRECTION", "-1");
                    } else {
                        dcp_settleData.add("DIRECTION", "1");
                    }
                }

                dcp_settleData.add("BILLAMT", detail.get("AMT").toString());

                int amountDigit = Integer.parseInt(detail.get("AMOUNTDIGIT").toString());
                double amt = Double.parseDouble(detail.get("AMT").toString());
                double taxRate = Double.parseDouble(detail.get("CTAXRATE").toString());
                double taxAmt = BigDecimalUtils.mul(
                        Double.parseDouble(StringUtils.toString(detail.get("AMT"), "0")),
                        Double.parseDouble(StringUtils.toString(detail.get("CTAXRATE"), "0")));
                dcp_settleData.add("TAXAMT", DataValues.newString(taxAmt));

                //税前金额 = 金额 / (1 + 税率)
                double preTaxAmt = BigDecimalUtils.div(
                        amt,
                        1 + taxRate,
                        amountDigit
                );

                dcp_settleData.add("PRETAXAMT", DataValues.newString(preTaxAmt));

                dcp_settleData.add("UNSETTLEAMT", detail.get("AMT").toString());
//                dcp_settleData.add("SETTLEAMT", detail.get("AMT").toString());
//                dcp_settleData.add("PAIDAMT", detail.get("AMT").toString());
                dcp_settleData.add("UNPAIDAMT", detail.get("AMT").toString());
//                dcp_settleData.add("BILLQTY", detail.get("BILLQTY").toString());
//                dcp_settleData.add("BILLPRICE", detail.get("BILLPRICE").toString());
//                dcp_settleData.add("PRICEUNIT", detail.get("PRICEUNIT").toString());
                dcp_settleData.add("DEPARTID", detail.get("DEPARTID").toString());
                dcp_settleData.add("CATEGORY", detail.get("CATEGORY").toString());
                dcp_settleData.add("PLUNO", detail.get("PLUNO").toString());
                dcp_settleData.add("FEATURENO", detail.get("FEATURENO").toString());
                dcp_settleData.add("STATUS", "0");
                dcp_settleData.add("FEE", detail.get("FEE").toString());

//                dcp_settleData.add("APQTY", detail.get("APQTY").toString());
//                dcp_settleData.add("APAMT", detail.get("APAMT").toString());
//                dcp_settleData.add("UNAPAMT", detail.get("UNAPAMT").toString());

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SETTLEDATA", dcp_settleData)));
            }

        } else if ("unConfirm".equals(req.getRequest().getOpType())) {
            status = "0";
            if (!"1".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不可反审!");
            }

            ColumnDataValue deleteCondition = new ColumnDataValue();
            deleteCondition.add("BILLNO", req.getRequest().getBfeeNo());
            deleteCondition.add("BTYPE", "3");

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SETTLEDATA", deleteCondition)));

        } else if ("cancel".equals(req.getRequest().getOpType())) {
            status = "3";
            dcp_expsheet.add("CANCELBY", req.getEmployeeNo());
            dcp_expsheet.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_expsheet.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());
            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不可反审!");
            }
        }


        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BFEENO", DataValues.newString(req.getRequest().getBfeeNo()));

        dcp_expsheet.add("STATUS", DataValues.newString(status));
        dcp_expdetail.add("STATUS", DataValues.newString(status));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_EXPSHEET", condition, dcp_expsheet)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_EXPDETAIL", condition, dcp_expdetail)));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    protected String getQueryDetailSql(DCP_ExpSheetStatusUpdateReq req) throws Exception {
        String querySql = " SELECT b.*,a.BDATE,a.SETTLEDBY,a.ORGANIZATIONNO PAYORGNO,a.BILLDATENO,a.PAYDATENO " +
                " ,' ' AS FEATURENO,a.DEPARTNO DEPARTID,tc.TAXRATE/100 CTAXRATE,e.PRICEDIGIT,e.AMOUNTDIGIT " +
                " FROM DCP_EXPSHEET a " +
                " INNER JOIN DCP_EXPDETAIL b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.BFEENO=b.BFEENO " +
//                " LEFT JOIN DCP_FEE ON f on f.eid=b.eid and f.FEE=a.FEE  " +
                " LEFT JOIN DCP_GOODS c on b.eid=c.eid and c.PLUNO=b.PLUNO " +
                " LEFT JOIN DCP_TAXCATEGORY tc on tc.eid=b.eid and tc.TAXCODE=b.TAXCODE " +
                " LEFT JOIN DCP_CURRENCY e on e.eid=a.eid and e.CURRENCY=a.CURRENCY " +
                " WHERE a.eid='" + req.geteId() + "'";
        if (StringUtils.isNotEmpty(req.getRequest().getBfeeNo())) {
            querySql = querySql +
                    " AND a.BFEENO='" + req.getRequest().getBfeeNo() + "'";
        }

        return querySql;
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_ExpSheetStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ExpSheetStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ExpSheetStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ExpSheetStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpSheetStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ExpSheetStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ExpSheetStatusUpdateRes getResponseType() {
        return new DCP_ExpSheetStatusUpdateRes();
    }
}
