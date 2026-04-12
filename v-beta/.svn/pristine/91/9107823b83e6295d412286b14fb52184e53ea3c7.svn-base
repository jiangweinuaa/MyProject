package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettBillStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettBillStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_InterSettBillStatusUpdate extends SPosAdvanceService<DCP_InterSettBillStatusUpdateReq, DCP_InterSettBillStatusUpdateRes> {

    private static final String OPTYPE_CONFIRM = "confirm";
    private static final String OPTYPE_UNCONFIRM = "unconfirm";
    private static final String OPTYPE_CANCEL = "cancel";


    @Override
    protected void processDUID(DCP_InterSettBillStatusUpdateReq req, DCP_InterSettBillStatusUpdateRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        String mStatus = qData.get(0).get("MSTATUS").toString();
        String opType = req.getRequest().getOpType();

        if (OPTYPE_CONFIRM.equals(opType)) {
            if (!"0".equals(mStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据无需审核！");
            }

            ColumnDataValue masterCondition = new ColumnDataValue();
            masterCondition.add("EID", req.geteId());
            masterCondition.add("BILLNO", req.getRequest().getBillNo());

            ColumnDataValue dcp_interSettBill = new ColumnDataValue();
            dcp_interSettBill.add("STATUS", "1");

            dcp_interSettBill.add("MODIFYBY", req.getEmployeeNo());
            dcp_interSettBill.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_interSettBill.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            dcp_interSettBill.add("CONFIRMBY", req.getEmployeeNo());
            dcp_interSettBill.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_interSettBill.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILL", masterCondition, dcp_interSettBill)));

            for (Map<String, Object> oneDetail : qData) {

                ColumnDataValue detailCond = new ColumnDataValue();
                ColumnDataValue dcp_interSettBillDetail = new ColumnDataValue();

                ColumnDataValue ofCond = new ColumnDataValue();
                ColumnDataValue ofValue = new ColumnDataValue();

                detailCond.add("EID", req.geteId());
                detailCond.add("BILLNO", req.getRequest().getBillNo());
                detailCond.add("ITEM", oneDetail.get("ITEM").toString());

                dcp_interSettBillDetail.add("STATUS", "1");
                double unSettAmt = BigDecimalUtils.sub(
                        Double.parseDouble(oneDetail.get("AMT").toString()),
                        Double.parseDouble(oneDetail.get("CURSETTLEDAMT").toString())
                );
                dcp_interSettBillDetail.add("UNSETTAMT", DataValues.newString(unSettAmt));
                dcp_interSettBillDetail.add("SETTLEDAMT", oneDetail.get("CURSETTLEDAMT").toString());

                ofCond.add("BILLNO", oneDetail.get("SOURCENO").toString());
                ofCond.add("ITEM", oneDetail.get("SOURCENOSEQ").toString());

                ofValue.add("STATUS", "2");

                ofValue.add("SETTLEDAMT", oneDetail.get("CURSETTLEDAMT").toString());
                ofValue.add("UNSETTAMT", DataValues.newString(unSettAmt));

                ofValue.add("MODIFYBY", req.getEmployeeNo());
                ofValue.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
                ofValue.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILLDETAIL", detailCond, dcp_interSettBillDetail)));
                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLEMENT", ofCond, ofValue)));

            }

        } else if (OPTYPE_UNCONFIRM.equals(opType)) {
            if (!"1".equals(mStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据无需取消审核！");
            }
            ColumnDataValue masterCondition = new ColumnDataValue();
            masterCondition.add("EID", req.geteId());
            masterCondition.add("BILLNO", req.getRequest().getBillNo());

            ColumnDataValue dcp_interSettBill = new ColumnDataValue();
            dcp_interSettBill.add("STATUS", "0");

            dcp_interSettBill.add("MODIFYBY", req.getEmployeeNo());
            dcp_interSettBill.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_interSettBill.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            dcp_interSettBill.add("CONFIRMBY", "");
            dcp_interSettBill.add("CONFIRM_DATE", "");
            dcp_interSettBill.add("CONFIRM_TIME", "");

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILL", masterCondition, dcp_interSettBill)));
            for (Map<String, Object> oneDetail : qData) {

                ColumnDataValue detailCond = new ColumnDataValue();
                ColumnDataValue dcp_interSettBillDetail = new ColumnDataValue();

                ColumnDataValue ofCond = new ColumnDataValue();
                ColumnDataValue ofValue = new ColumnDataValue();

                detailCond.add("EID", req.geteId());
                detailCond.add("BILLNO", req.getRequest().getBillNo());
                detailCond.add("ITEM", oneDetail.get("ITEM").toString());

                dcp_interSettBillDetail.add("STATUS", "0");
                double settAmt = BigDecimalUtils.sub(
                        Double.parseDouble(oneDetail.get("SETTLEDAMT").toString()),
                        Double.parseDouble(oneDetail.get("CURSETTLEDAMT").toString())
                );
                dcp_interSettBillDetail.add("UNSETTAMT", DataValues.newString(oneDetail.get("AMT").toString()));
                dcp_interSettBillDetail.add("SETTLEDAMT", DataValues.newString(settAmt));

                ofCond.add("BILLNO", oneDetail.get("SOURCENO").toString());
                ofCond.add("ITEM", oneDetail.get("SOURCENOSEQ").toString());

                ofValue.add("STATUS", "1");
                ofValue.add("SETTLEDAMT", DataValues.newString(settAmt));
                ofValue.add("UNSETTAMT", DataValues.newString(oneDetail.get("AMT").toString()));

                ofValue.add("MODIFYBY", req.getEmployeeNo());
                ofValue.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
                ofValue.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILLDETAIL", detailCond, dcp_interSettBillDetail)));
                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLEMENT", ofCond, ofValue)));

            }

        } else if (OPTYPE_CANCEL.equals(opType)) {
            if (!"0".equals(mStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据无法作废！");
            }
            ColumnDataValue masterCondition = new ColumnDataValue();
            masterCondition.add("EID", req.geteId());
            masterCondition.add("BILLNO", req.getRequest().getBillNo());

            ColumnDataValue dcp_interSettBill = new ColumnDataValue();
            dcp_interSettBill.add("STATUS", "-1");

            dcp_interSettBill.add("MODIFYBY", req.getEmployeeNo());
            dcp_interSettBill.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_interSettBill.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            dcp_interSettBill.add("CANCELBY", req.getEmployeeNo());
            dcp_interSettBill.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_interSettBill.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILL", masterCondition, dcp_interSettBill)));
            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILLDETAIL", masterCondition, dcp_interSettBill)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettBillStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettBillStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_InterSettBillStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_InterSettBillStatusUpdateRes getResponseType() {
        return new DCP_InterSettBillStatusUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_InterSettBillStatusUpdateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.STATUS MSTATUS, b.* FROM DCP_INTERSETTBILL a ")
                .append(" INNER JOIN DCP_INTERSETTBILLDETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" AND q.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        return querySql.toString();
    }
}
