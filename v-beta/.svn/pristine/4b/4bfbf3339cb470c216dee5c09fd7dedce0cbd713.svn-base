package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDataProcessReq;
import com.dsc.spos.json.cust.res.DCP_CostDataProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostDataProcess extends SPosAdvanceService<DCP_CostDataProcessReq, DCP_CostDataProcessRes> {


    @Getter
    public enum CostType {
        WORK_ORDER_ISSUE(1, "工单发料凭证"),
        WORK_ORDER_RECEIPT(2, "工单入库凭证"),
        INVENTORY_COST_ADJUSTMENT(3, "库存成本调整凭证"),
        INVENTORY_COUNT_PROFIT_LOSS(4, "盘盈亏成本凭证"),
        SALES_COST(6, "销货成本凭证"),
        MISCELLANEOUS_IN_OUT(7, "杂项进出成本凭证"),
        BALANCE_ADJUSTMENT(10, "结存调整成本凭证");

        private final int code;           // 编号
        private final String description; // 描述

        CostType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static CostType fromCode(int code) {
            for (CostType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }

    }

    private String getQueryCurInvMovDetailSql(DCP_CostDataProcessReq req, CostType costType) {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT ")
                .append(" a.ACCOUNTID,a.YEAR,a.PERIOD,a.PLUNO,a.TASKID," +
                        "    SUM(a.QTY) QTY," +
                        "    SUM(a.TOT_AMT) AMOUNT," +
                        "    SUM(a.MATERIAL) MATERIAL, " +
                        "    SUM(a.LABOR) LABOR, " +
                        "    SUM(a.OEM) OEM, " +
                        "    SUM(a.EXP1) EXP1, " +
                        "    SUM(a.EXP2) EXP2, " +
                        "    SUM(a.EXP3) EXP3, " +
                        "    SUM(a.EXP4) EXP4, " +
                        "    SUM(a.EXP5) EXP5")
                .append(" FROM DCP_CURINVMOVDETAIL a ")
        ;

        if (costType == CostType.INVENTORY_COST_ADJUSTMENT) {
            querySql.append(" LEFT JOIN DCP_CURINVCOSTADJ b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID and a.YEAR=b.YEAR and a.PERIOD=b.PERIOD and a.DATASOURCE='1' and a.TASKID=b.REFERENCENO ");
        }

        querySql.append("WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (Check.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR=").append(req.getRequest().getYear());
        }

        if (Check.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD=").append(req.getRequest().getPeriod());
        }

        switch (costType) {
            case WORK_ORDER_ISSUE:
                querySql.append(" AND a.BTYPE in ('11','32') ");
                break;
            case WORK_ORDER_RECEIPT:
                querySql.append(" AND a.BTYPE in ('08','33') ");
                break;
            case INVENTORY_COST_ADJUSTMENT:
                querySql.append(" AND a.BTYPE in ('61') ");
                break;
            case INVENTORY_COUNT_PROFIT_LOSS:
                querySql.append(" AND a.BTYPE in ('10') ");
                break;
            case SALES_COST:
                querySql.append(" AND a.BTYPE in ('20','21','65','66') ");
                break;
            case MISCELLANEOUS_IN_OUT:
                querySql.append(" AND a.BTYPE in ('14','15') ");
                break;
        }

        querySql.append(" GROUP BY ACCOUNTID,YEAR,PERIOD,PLUNO,TASKID");


        return querySql.toString();
    }

    private String getQueryCurinvCostStatSql(DCP_CostDataProcessReq req, CostType costType) {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT ")
                .append(" ACCOUNTID,YEAR,PERIOD,PLUNO,COSTDOMAINID ")
                .append(" SUM(a.QTY) QTY,")
                .append(" FROM DCP_CURINVCOSTSTAT a ")
        ;
        querySql.append("WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (Check.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR=").append(req.getRequest().getYear());
        }

        if (Check.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD=").append(req.getRequest().getPeriod());
        }

        querySql.append(" GROUP BY ACCOUNTID,YEAR,PERIOD,PLUNO,COSTDOMAINID");

        return querySql.toString();
    }

    protected void workOrderIssueProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.WORK_ORDER_ISSUE;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }

    protected void workOrderReceiptProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.WORK_ORDER_RECEIPT;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }

    protected void inventoryCostAdjustmentProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.INVENTORY_COST_ADJUSTMENT;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }


    protected void inventoryCountProfitLossProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.INVENTORY_COUNT_PROFIT_LOSS;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }

    protected void salesCostProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.SALES_COST;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }

    protected void miscellaneousInOutProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.MISCELLANEOUS_IN_OUT;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);

    }

    private void balanceAdjustmentProcess(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

        CostType costType = CostType.BALANCE_ADJUSTMENT;

        List<Map<String, Object>> qData = doQueryData(getQueryCurInvMovDetailSql(req, costType), null);
        insertIntoCostData(req, costType, qData);
    }

    protected void insertIntoCostData(DCP_CostDataProcessReq req, CostType costType, List<Map<String, Object>> qData) throws Exception {
        if (CollectionUtils.isNotEmpty(qData)) {

            String costNo = getOrderNO(req, "CBPZ");

            int item = 0;
            for (Map<String, Object> d : qData) {
                ++item;
                ColumnDataValue dcp_costdetailout = new ColumnDataValue();
                dcp_costdetailout.add("EID", DataValues.newString(req.geteId()));
                dcp_costdetailout.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));

                dcp_costdetailout.add("COSTNO", DataValues.newString(costNo));
                dcp_costdetailout.add("ITEM", DataValues.newString(item));
                dcp_costdetailout.add("BILLNO", DataValues.newString(d.get("TASKID").toString()));
                dcp_costdetailout.add("BTYPE", DataValues.newString(d.get("BTYPE").toString()));
                dcp_costdetailout.add("CTYPE", DataValues.newString(d.get("CTYPE").toString()));
                dcp_costdetailout.add("PLUNO", DataValues.newString(d.get("PLUNO").toString()));
                dcp_costdetailout.add("FEATURENO", DataValues.newString(d.get("FEATURENO").toString()));
//                dcp_costdetailout.add("SUBJECTID", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("MEMO", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("COSTDOMAINID", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("BSNO", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("COSTCENTER", DataValues.newString(req.geteId()));
                dcp_costdetailout.add("QTY", DataValues.newString(d.get("QTY").toString()));
                dcp_costdetailout.add("TOT_AMT", DataValues.newString(d.get("TOT_AMT")));
                dcp_costdetailout.add("MATERIAL", DataValues.newString(d.get("MATERIAL")));
                dcp_costdetailout.add("LABOR", DataValues.newString(d.get("LABOR")));
                dcp_costdetailout.add("OEM", DataValues.newString(d.get("OEM")));
                dcp_costdetailout.add("EXP1", DataValues.newString(d.get("EXP1")));
                dcp_costdetailout.add("EXP2", DataValues.newString(d.get("EXP2")));
                dcp_costdetailout.add("EXP3", DataValues.newString(d.get("EXP3")));
                dcp_costdetailout.add("EXP4", DataValues.newString(d.get("EXP4")));
                dcp_costdetailout.add("EXP5", DataValues.newString(d.get("EXP5")));

                dcp_costdetailout.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                dcp_costdetailout.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
//                dcp_costdetailout.add("FREECHARS1", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("FREECHARS2", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("FREECHARS3", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("FREECHARS4", DataValues.newString(req.geteId()));
//                dcp_costdetailout.add("FREECHARS5", DataValues.newString(req.geteId()));
                dcp_costdetailout.add("CATEGORY", DataValues.newString(req.geteId()));
                dcp_costdetailout.add("ORGANIZATIONNO", DataValues.newString(req.geteId()));
                dcp_costdetailout.add("CORP", DataValues.newString(req.geteId()));

                dcp_costdetailout.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_costdetailout.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_costdetailout.add("STATUS", DataValues.newString(0));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTDETAILOUT", dcp_costdetailout)));

                ColumnDataValue dcp_costdetailin = new ColumnDataValue();
                dcp_costdetailin.add("EID", DataValues.newString(req.geteId()));
                dcp_costdetailin.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));

                dcp_costdetailin.add("COSTNO", DataValues.newString(costNo));
                dcp_costdetailin.add("ITEM", DataValues.newString(item));
                dcp_costdetailin.add("BILLNO", DataValues.newString(d.get("TASKID").toString()));
                dcp_costdetailin.add("BTYPE", DataValues.newString(d.get("BTYPE").toString()));
                dcp_costdetailin.add("CTYPE", DataValues.newString(d.get("CTYPE").toString()));
                dcp_costdetailin.add("PLUNO", DataValues.newString(d.get("PLUNO").toString()));
                dcp_costdetailin.add("FEATURENO", DataValues.newString(d.get("FEATURENO").toString()));
//                dcp_costdetailin.add("SUBJECTID", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("MEMO", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("COSTDOMAINID", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("BSNO", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("COSTCENTER", DataValues.newString(req.geteId()));
                dcp_costdetailin.add("QTY", DataValues.newString(d.get("QTY").toString()));
                dcp_costdetailin.add("TOT_AMT", DataValues.newString(d.get("TOT_AMT")));
                dcp_costdetailin.add("MATERIAL", DataValues.newString(d.get("MATERIAL")));
                dcp_costdetailin.add("LABOR", DataValues.newString(d.get("LABOR")));
                dcp_costdetailin.add("OEM", DataValues.newString(d.get("OEM")));
                dcp_costdetailin.add("EXP1", DataValues.newString(d.get("EXP1")));
                dcp_costdetailin.add("EXP2", DataValues.newString(d.get("EXP2")));
                dcp_costdetailin.add("EXP3", DataValues.newString(d.get("EXP3")));
                dcp_costdetailin.add("EXP4", DataValues.newString(d.get("EXP4")));
                dcp_costdetailin.add("EXP5", DataValues.newString(d.get("EXP5")));

                dcp_costdetailin.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                dcp_costdetailin.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
//                dcp_costdetailin.add("FREECHARS1", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("FREECHARS2", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("FREECHARS3", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("FREECHARS4", DataValues.newString(req.geteId()));
//                dcp_costdetailin.add("FREECHARS5", DataValues.newString(req.geteId()));
                dcp_costdetailin.add("CATEGORY", DataValues.newString(req.geteId()));
                dcp_costdetailin.add("ORGANIZATIONNO", DataValues.newString(req.geteId()));
                dcp_costdetailin.add("CORP", DataValues.newString(req.geteId()));

                dcp_costdetailin.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_costdetailin.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_costdetailin.add("STATUS", DataValues.newString(0));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTDETAILIN", dcp_costdetailin)));

            }
            ColumnDataValue dcp_costdata = new ColumnDataValue();
            dcp_costdata.add("EID", DataValues.newString(req.geteId()));
            dcp_costdata.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_costdata.add("COSTNO", DataValues.newString(costNo));
            dcp_costdata.add("BDATE", DataValues.newString(req.getRequest().getAccountID()));
            dcp_costdata.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getAccountID()));
            dcp_costdata.add("COSTTYPE", DataValues.newString(costType.getCode()));
            dcp_costdata.add("COST_CALCULATION", DataValues.newString(costType.getCode()));
            dcp_costdata.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            dcp_costdata.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            dcp_costdata.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
            dcp_costdata.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
            dcp_costdata.add("GLNO", DataValues.newString(req.getDepartmentNo()));
            dcp_costdata.add("GDATE", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTDATA", dcp_costdata)));

        }

    }

    @Override
    protected void processDUID(DCP_CostDataProcessReq req, DCP_CostDataProcessRes res) throws Exception {

//        产逻辑需按【单据类型】顺序执行生产单据：1-工单发料凭证-->2-工单入库凭证-->3-库存成本调整凭证-->4-盘盈亏成本凭证-->6-销货成本凭证 -->7-杂项进出成本凭证-->10-结存调整成本凭证
        workOrderIssueProcess(req, res);
        workOrderReceiptProcess(req, res);
        inventoryCostAdjustmentProcess(req, res);
        inventoryCountProfitLossProcess(req, res);
        salesCostProcess(req, res);
        miscellaneousInOutProcess(req, res);
        balanceAdjustmentProcess(req, res);


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDataProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataProcessReq> getRequestType() {
        return new TypeToken<DCP_CostDataProcessReq>() {
        };
    }

    @Override
    protected DCP_CostDataProcessRes getResponseType() {
        return new DCP_CostDataProcessRes();
    }

    @Override
    protected String getQuerySql(DCP_CostDataProcessReq req) throws Exception {
        return super.getQuerySql(req);
    }
}
