package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjProcessReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjProcess extends SPosAdvanceService<DCP_CurInvCostAdjProcessReq, DCP_CurInvCostAdjProcessRes> {
    @Override
    protected void processDUID(DCP_CurInvCostAdjProcessReq req, DCP_CurInvCostAdjProcessRes res) throws Exception {
//        1. 来源类型为：2/3 使用执行：
//  ○ 执行时判断账套成本关账日期，执行期不得小于账套成本关账日；
//  ○ 成本关账日期不可大于现行年度期别的截止日期
//        2. 来源类型为：2：供应商往来调整单，抓取来源及逻辑
//        a. 涉及表：DCP_VENDORADJ 、DCP_VENDORADJ_DETAIL
//        b. 调用服务：/DCP_VendorAdjQuery
//        c. 条件：账套+对应组织+单据状态为【审核】+日期在执行当前期别内；
//        d. 表字段对应关系
//
//        3. 来源类型为：3：费用分摊；抓取来源及逻辑
//        a. 涉及表：DCP_BFEE、DCP_BFEE_DETAIL
//        b. 调用服务：/DCP_expenseQuery
//        c. 条件：账套+对应组织+单据状态为【审核】+
//                SETTACCPERIOD结算会计=本次执行当前期别；
//        d. 表字段对应关系
//        e. 同时费用单的费用编码分摊方式
//        FEEALLOCATION为：3.按单品分摊 为纳入本次计算数据内；调用服务：/DCP_FeeQuery

        String querySql = " SELECT a.*,b.COST_DOMAIN,b.COST_TAX_INCLUDED FROM DCP_ACOUNT_SETTING a " +
                " LEFT JOIN DCP_ORG b on a.eid=b.eid and a.CORP=b.ORGANIZATIONNO " +
                " WHERE a.eid='" + req.geteId() + "' AND a.ACCOUNTID='" + req.getRequest().getAccountID() + "'";
        List<Map<String, Object>> qData = doQueryData(querySql, null);


        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getAccountID() + "帐套未设置");
        } else {

            String costclosingdate = DateFormatUtils.getPlainDate(qData.get(0).get("COSTCLOSINGDATE").toString());
            String year = req.getRequest().getYear();
            String period = req.getRequest().getPeriod();

            if (Double.parseDouble(costclosingdate) > Double.parseDouble(year + period + "01")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "执行期不得小于账套成本关账日成本关账日期");
            }

        }

        String cost_tax_included = qData.get(0).get("COST_TAX_INCLUDED").toString();

        List<Map<String, Object>> vendorAdjData = null;
        List<Map<String, Object>> expenseData = null;
        String dataSource = req.getRequest().getDataSource();
        if ("2".equals(dataSource)) {
            vendorAdjData = doQueryData(getVendorAdjQuerySql(req), null);
        }
        if ("3".equals(dataSource)) {
            expenseData = doQueryData(getExpenseQuerySql(req), null);
        }

        if (CollectionUtils.isNotEmpty(vendorAdjData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("ADJUSTNO", true);

            List<Map<String, Object>> masterData = MapDistinct.getMap(vendorAdjData, distinct);

            for (Map<String, Object> oneCost : masterData) {

                ColumnDataValue delCondition = new ColumnDataValue();
                delCondition.add("EID", req.geteId());
                delCondition.add("REFERENCENO", oneCost.get("ADJUSTNO").toString());
                delCondition.add("DATASOURCE", req.getRequest().getDataSource());

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTADJ", delCondition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTDETAILADJ", delCondition)));

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneCost.get("EID"));
                condition.put("ADJUSTNO", oneCost.get("ADJUSTNO"));

                ColumnDataValue dcp_curinvcostadj = new ColumnDataValue();

                dcp_curinvcostadj.add("EID", DataValues.newString(req.geteId()));
                dcp_curinvcostadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                dcp_curinvcostadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                dcp_curinvcostadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                dcp_curinvcostadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
                dcp_curinvcostadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));

                dcp_curinvcostadj.add("REFERENCENO", DataValues.newString(oneCost.get("ADJUSTNO").toString()));

                dcp_curinvcostadj.add("STATUS", DataValues.newString("0"));

                dcp_curinvcostadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_curinvcostadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_curinvcostadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTADJ", dcp_curinvcostadj)));

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(vendorAdjData, condition, true);
                for (Map<String, Object> oneDetail : detail) {

                    ColumnDataValue dcp_curinvcostdetailadj = new ColumnDataValue();

                    dcp_curinvcostdetailadj.add("EID", DataValues.newString(req.geteId()));
                    dcp_curinvcostdetailadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                    dcp_curinvcostdetailadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                    dcp_curinvcostdetailadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                    dcp_curinvcostdetailadj.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
                    dcp_curinvcostdetailadj.add("CORP", DataValues.newString(req.getOrganizationNO()));
//                    dcp_curinvcostdetailadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
                    dcp_curinvcostdetailadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));

                    dcp_curinvcostdetailadj.add("REFERENCENO", DataValues.newString(oneDetail.get("ADJUSTNO").toString()));
                    dcp_curinvcostdetailadj.add("STATUS", DataValues.newString("0"));

                    dcp_curinvcostdetailadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                    dcp_curinvcostdetailadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                    dcp_curinvcostdetailadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

                    dcp_curinvcostdetailadj.add("ITEM", DataValues.newString(oneDetail.get("ITEM").toString()));
                    dcp_curinvcostdetailadj.add("COSTDOMAINID", DataValues.newString(req.getOrganizationNO()));
//                    dcp_curinvcostdetailadj.add("COSTDOMAINDIS", DataValues.newString(req.getOrganizationNO()));
                    dcp_curinvcostdetailadj.add("PLUNO", DataValues.newString(oneDetail.get("PLUNO").toString()));
                    dcp_curinvcostdetailadj.add("PLUNAME", DataValues.newString(oneDetail.get("PLU_NAME").toString()));
                    dcp_curinvcostdetailadj.add("FEATURENO", DataValues.newString(oneDetail.get("FEATURENO").toString()));
                    dcp_curinvcostdetailadj.add("MEMO", DataValues.newString(oneDetail.get("MEMO").toString()));
//                    dcp_curinvcostdetailadj.add("TOT_AMT", DataValues.newString(oneDetail.get("PRETAXAMT").toString()));

                    double material = BigDecimalUtils.sub(
                            Double.parseDouble(StringUtils.toString(oneDetail.get("ADJAMT"), "0"))
                            , Double.parseDouble(StringUtils.toString(oneDetail.get("ADJTAXAMT"), "0")));

                    dcp_curinvcostdetailadj.add("MATERIAL", DataValues.newString(material));

                    double preTaxMaterial = material;
                    if ("Y".equals(cost_tax_included)) {
                        preTaxMaterial = BigDecimalUtils.mul(
                                material,
                                1 + Double.parseDouble(oneDetail.get("TAXRATE").toString()));

                    }
                    dcp_curinvcostdetailadj.add("PRETAXMATERIAL", DataValues.newString(preTaxMaterial));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTDETAILADJ", dcp_curinvcostdetailadj)));

                }

            }

        }


        if (CollectionUtils.isNotEmpty(expenseData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("BFEENO", true);
            distinct.put("ORGANIZATIONNO", true);

            List<Map<String, Object>> masterData = MapDistinct.getMap(expenseData, distinct);


            for (Map<String, Object> oneCost : masterData) {

                ColumnDataValue delCondition = new ColumnDataValue();
                delCondition.add("EID", req.geteId());
                delCondition.add("REFERENCENO", oneCost.get("BFEENO").toString());
                delCondition.add("DATASOURCE", req.getRequest().getDataSource());

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTADJ", delCondition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTDETAILADJ", delCondition)));


                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneCost.get("EID"));
                condition.put("BFEENO", oneCost.get("BFEENO"));
                condition.put("ORGANIZATIONNO", oneCost.get("ORGANIZATIONNO"));

                ColumnDataValue dcp_curinvcostadj = new ColumnDataValue();

                dcp_curinvcostadj.add("EID", DataValues.newString(req.geteId()));
                dcp_curinvcostadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                dcp_curinvcostadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                dcp_curinvcostadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                dcp_curinvcostadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
                dcp_curinvcostadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));

                dcp_curinvcostadj.add("REFERENCENO", DataValues.newString(oneCost.get("BFEENO").toString()));

                dcp_curinvcostadj.add("STATUS", DataValues.newString("0"));

                dcp_curinvcostadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_curinvcostadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_curinvcostadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTADJ", dcp_curinvcostadj)));

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(expenseData, condition, true);
                for (Map<String, Object> oneDetail : detail) {

                    ColumnDataValue dcp_curinvcostdetailadj = new ColumnDataValue();

                    dcp_curinvcostdetailadj.add("EID", DataValues.newString(req.geteId()));
                    dcp_curinvcostdetailadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                    dcp_curinvcostdetailadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                    dcp_curinvcostdetailadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                    dcp_curinvcostdetailadj.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
                    dcp_curinvcostdetailadj.add("CORP", DataValues.newString(req.getOrganizationNO()));
//                    dcp_curinvcostdetailadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
                    dcp_curinvcostdetailadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));

                    dcp_curinvcostdetailadj.add("REFERENCENO", DataValues.newString(oneDetail.get("BFEENO").toString()));
                    dcp_curinvcostdetailadj.add("STATUS", DataValues.newString("0"));

                    dcp_curinvcostdetailadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                    dcp_curinvcostdetailadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                    dcp_curinvcostdetailadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

                    dcp_curinvcostdetailadj.add("ITEM", DataValues.newString(oneDetail.get("ITEM").toString()));
                    dcp_curinvcostdetailadj.add("COSTDOMAINID", DataValues.newString(req.getOrganizationNO()));
//                    dcp_curinvcostdetailadj.add("COSTDOMAINDIS", DataValues.newString(req.getOrganizationNO()));
                    dcp_curinvcostdetailadj.add("PLUNO", DataValues.newString(oneDetail.get("PLUNO").toString()));
                    dcp_curinvcostdetailadj.add("PLUNAME", DataValues.newString(oneDetail.get("PLU_NAME").toString()));
                    dcp_curinvcostdetailadj.add("FEATURENO", DataValues.newString(" "));
                    dcp_curinvcostdetailadj.add("MEMO", DataValues.newString(oneDetail.get("MEMO").toString()));

                    double material = Double.parseDouble(oneDetail.get("AMT").toString());
//                    dcp_curinvcostdetailadj.add("TOT_AMT", DataValues.newString(oneDetail.get("AMT").toString()));
                    dcp_curinvcostdetailadj.add("MATERIAL", DataValues.newString(material));

                    double preTaxMaterial = material;
                    if ("Y".equals(cost_tax_included)) {
                        preTaxMaterial = BigDecimalUtils.mul(
                                material,
                                1 + Double.parseDouble(oneDetail.get("TAXRATE").toString()));

                    }
                    dcp_curinvcostdetailadj.add("PRETAXMATERIAL", DataValues.newString(preTaxMaterial));


                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTDETAILADJ", dcp_curinvcostdetailadj)));

                }

            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }


    @Override
    protected String getQuerySql(DCP_CurInvCostAdjProcessReq req) throws Exception {
        return null;
    }

    private String getVendorAdjQuerySql(DCP_CurInvCostAdjProcessReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.*,gl1.PLU_NAME,dg.INPUTTAXCODE,tc.TAXRATE*0.01 TAXRATE ")
                .append(" FROM DCP_VENDORADJ a ")
                .append(" INNER JOIN DCP_VENDORADJ_DETAIL b on a.EID=b.eid and a.ADJUSTNO=b.ADJUSTNO ")
                .append(" LEFT JOIN DCP_GOODS dg on dg.eid=b.eid and dg.PLUNO=a.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_TAXCATEGORY tc on tc.eid=dg.eid and tc.TAXCODE=dg.INPUTTAXCODE ")
        ;

        // 条件： 账套+对应组织+单据状态为【审核】+日期在执行当前期别内
        querySql.append(" WHERE a.eid='").append(req.geteId()).append("' ");
        querySql.append(" AND a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("' ");
        querySql.append(" AND a.STATUS='2'");
        querySql.append(" AND a.BDATE like '%%").append(req.getRequest().getYear()).append(req.getRequest().getPeriod()).append("%%' ");


        return querySql.toString();
    }

    private String getExpenseQuerySql(DCP_CurInvCostAdjProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.*,gl1.PLU_NAME,dg.INPUTTAXCODE ,tc.TAXRATE*0.01 TAXRATE")
                .append(" FROM DCP_EXPSHEET a ")
                .append(" INNER JOIN DCP_EXPDETAIL b on a.EID=b.EID and a.BFEENO=b.BFEENO ")
                .append(" LEFT JOIN DCP_GOODS dg on dg.eid=b.eid and dg.PLUNO=a.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_TAXCATEGORY tc on tc.eid=dg.eid and tc.TAXCODE=dg.INPUTTAXCODE ")
        ;

        // 条件： 账套+对应组织+单据状态为【审核】+日期在执行当前期别内
        querySql.append(" WHERE a.eid='").append(req.geteId()).append("' ");
        querySql.append(" AND a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("' ");
        querySql.append(" AND a.STATUS='1'");
        querySql.append(" AND a.BDATE like '%%").append(req.getRequest().getYear()).append(req.getRequest().getPeriod()).append("%%' ");


        return querySql.toString();
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_CurInvCostAdjProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurInvCostAdjProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurInvCostAdjProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjProcessReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjProcessReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjProcessRes getResponseType() {
        return new DCP_CurInvCostAdjProcessRes();
    }
}
