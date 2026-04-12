package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettProcessReq;
import com.dsc.spos.json.cust.res.DCP_InterSettProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DCP_InterSettProcess extends SPosAdvanceService<DCP_InterSettProcessReq, DCP_InterSettProcessRes> {
    @Override
    protected void processDUID(DCP_InterSettProcessReq req, DCP_InterSettProcessRes res) throws Exception {


        String isTaxRateSplit = req.getRequest().getIsTaxRateSplit();

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {

            Map<String, List<Map<String, Object>>> taxRate = new HashMap<>();
            if ("Y".equals(isTaxRateSplit)) {  //按税率分组
                taxRate = qData.stream().collect(Collectors.groupingBy(
                        x -> x.get("TAXRATE").toString()
                ));
            }

            if (!taxRate.isEmpty()) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : taxRate.entrySet()) {
                    insertNewBill(req, entry.getValue());
                }
            } else {
                insertNewBill(req, qData);
            }

//            将DCP_INTERSETTLEMENT结算底稿更新为：STATUS=1  对账中
            for (Map<String, Object> q : qData) {
                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue dcp_interSettlement = new ColumnDataValue();

                condition.add("EID", q.get("EID").toString());
                condition.add("BILLNO", q.get("BILLNO").toString());
                condition.add("ITEM", q.get("ITEM").toString());

                dcp_interSettlement.add("STATUS", "1");

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean(
                        "DCP_INTERSETTLEMENT", condition,
                        dcp_interSettlement
                )));
            }

            this.doExecuteDataToDB();
        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    //            e. 账务类型：应付=  11.采购入库,12.采购仓退,33.退仓出库,32.调拨入库
//                    应收=21.销售出货,22.销售退货,31.调拨出货34.退仓入库
    private final String[] PAY_ARRAY_TYPE = new String[]{"11", "12", "33", "32"};
    private final String[] RECEIVE_ARRAY_TYPE = new String[]{"21", "22", "31", "34"};

    private void insertNewBill(DCP_InterSettProcessReq req, List<Map<String, Object>> qData) throws Exception {
        String preFix = "NBJY";
        String bDate = req.getRequest().getEndDate();

        String billNo = getOrderNOWithDate(req, preFix, bDate);

        double amt = 0;
        int item = 1;
        List<String> receiveArray = Arrays.asList(RECEIVE_ARRAY_TYPE);

        for (Map<String, Object> oneData : qData) {
            ColumnDataValue dcp_interSettBillDetail = new ColumnDataValue();

            dcp_interSettBillDetail.add("EID", DataValues.newString(req.geteId()));
            dcp_interSettBillDetail.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_interSettBillDetail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_interSettBillDetail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

            dcp_interSettBillDetail.add("STATUS", DataValues.newString("0"));

            dcp_interSettBillDetail.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
            dcp_interSettBillDetail.add("BILLNO", DataValues.newString(billNo));
            dcp_interSettBillDetail.add("ITEM", DataValues.newString(item++));
            dcp_interSettBillDetail.add("BTYPE", DataValues.newString(oneData.get("BTYPE").toString()));
            dcp_interSettBillDetail.add("INTERTRANSTYPE", DataValues.newString(oneData.get("INTERTRANSTYPE").toString()));
            dcp_interSettBillDetail.add("SOURCENO", DataValues.newString(oneData.get("BILLNO").toString()));
            dcp_interSettBillDetail.add("SOURCENOSEQ", DataValues.newString(oneData.get("ITEM").toString()));
            dcp_interSettBillDetail.add("RDATE", DataValues.newDate(DateFormatUtils.getDate(oneData.get("BDATE").toString())));
            dcp_interSettBillDetail.add("FEE", DataValues.newString(oneData.get("FEE").toString()));
            dcp_interSettBillDetail.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
            dcp_interSettBillDetail.add("CURRENCY", DataValues.newString(oneData.get("CURRENCY").toString()));
            dcp_interSettBillDetail.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            dcp_interSettBillDetail.add("DIRECTION", DataValues.newString(oneData.get("DIRECTION").toString()));
            dcp_interSettBillDetail.add("PRETAXAMT", DataValues.newString(oneData.get("PRETAXAMT").toString()));
            dcp_interSettBillDetail.add("AMT", DataValues.newString(oneData.get("AMT").toString()));
            dcp_interSettBillDetail.add("UNSETTAMT", DataValues.newString(oneData.get("UNSETTAMT").toString()));
            dcp_interSettBillDetail.add("SETTLEDAMT", DataValues.newString("0"));
            dcp_interSettBillDetail.add("CURSETTLEDAMT", DataValues.newString(oneData.get("AMT").toString()));
            dcp_interSettBillDetail.add("INVORG", DataValues.newString(oneData.get("INVORG").toString()));
            dcp_interSettBillDetail.add("INVCORP", DataValues.newString(oneData.get("INVCORP").toString()));
            dcp_interSettBillDetail.add("WAREHOUSE", DataValues.newString(oneData.get("IC_COST_WAREHOUSE").toString()));
            dcp_interSettBillDetail.add("BIZPARTNERNO", DataValues.newString(oneData.get("DEMANDCORP").toString()));
            dcp_interSettBillDetail.add("INTERTRADEORG", DataValues.newString(oneData.get("INTERTRADEORG").toString()));
            dcp_interSettBillDetail.add("INTERTRADECORP", DataValues.newString(oneData.get("INTERTRADECORP").toString()));
            String year = oneData.get("YEAR").toString();
            String period = oneData.get("MONTH").toString();
            if (period.length() < 2) {
                period = "0" + period;
            }
            dcp_interSettBillDetail.add("SETTACCPERIOD", DataValues.newString(year + period));
            dcp_interSettBillDetail.add("YEAR", DataValues.newString(oneData.get("YEAR").toString()));
            dcp_interSettBillDetail.add("PERIOD", DataValues.newString(oneData.get("MONTH").toString()));
            dcp_interSettBillDetail.add("ACCOUNTORG", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
            dcp_interSettBillDetail.add("CORP", DataValues.newString(oneData.get("CORP").toString()));
//            e. 账务类型：应付=  11.采购入库,12.采购仓退,33.退仓出库,32.调拨入库
//                    应收=21.销售出货,22.销售退货,31.调拨出货34.退仓入库

            String bType = oneData.get("BTYPE").toString();
            String glType = "1";
            if (receiveArray.contains(bType)) {
                glType = "2";
            }

            dcp_interSettBillDetail.add("GLTYPE", DataValues.newString(glType));
            dcp_interSettBillDetail.add("INOUTCODE", DataValues.newString(oneData.get("OUTINDIRECTION").toString()));
            dcp_interSettBillDetail.add("QTY", DataValues.newString(oneData.get("BILLQTY").toString()));
            dcp_interSettBillDetail.add("PRICE", DataValues.newString(oneData.get("BILLPRICE").toString()));
            dcp_interSettBillDetail.add("BASEUNIT", DataValues.newString(oneData.get("PRICEUNIT").toString()));
            amt += Double.parseDouble(oneData.get("AMT").toString());
            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTBILLDETAIL", dcp_interSettBillDetail)));

        }

        Map<String, Object> oneData = qData.get(0);
        String year = oneData.get("YEAR").toString();
        String period = oneData.get("MONTH").toString();
        if (period.length() < 2) {
            period = "0" + period;
        }
        ColumnDataValue dcp_interSettBill = new ColumnDataValue();
        dcp_interSettBill.add("EID", DataValues.newString(req.geteId()));
        dcp_interSettBill.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        dcp_interSettBill.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_interSettBill.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

        dcp_interSettBill.add("STATUS", DataValues.newString("0"));
        dcp_interSettBill.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        dcp_interSettBill.add("BILLNO", DataValues.newString(billNo));
        dcp_interSettBill.add("BDATE", DataValues.newString(bDate));
        dcp_interSettBill.add("BIZPARTNER1", DataValues.newString(req.getRequest().getBizpartner1()));
        dcp_interSettBill.add("BIZPARTNER2", DataValues.newString(req.getRequest().getBizpartner2()));
        dcp_interSettBill.add("SETTACCPERIOD", DataValues.newString(year + period));
        dcp_interSettBill.add("STARTDATE", DataValues.newDate(req.getRequest().getStartDate()));
        dcp_interSettBill.add("ENDDATE", DataValues.newDate(req.getRequest().getEndDate()));
        dcp_interSettBill.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
        dcp_interSettBill.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
        dcp_interSettBill.add("AMT", DataValues.newString(amt));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTBILL", dcp_interSettBill)));

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettProcessReq> getRequestType() {
        return new TypeToken<DCP_InterSettProcessReq>() {
        };
    }


    @Override
    protected DCP_InterSettProcessRes getResponseType() {
        return new DCP_InterSettProcessRes();
    }

//    private String getSumData(DCP_InterSettProcessReq req) throws Exception {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(" SELECT a.EID,SUM(AMT) amt ")
//
//        if ("Y".equals(req.getRequest().getIsTaxRateSplit())){
//
//
//
//        }else {
//
//        }
//
//        return sb.toString();
//
//    }

    @Override
    protected String getQuerySql(DCP_InterSettProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.*,o1.CORP INVCORP,a.ORGANIZATIONNO INVORG,o1.IC_COST_WAREHOUSE  ")
                .append(" FROM DCP_INTERSETTLEMENT a ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO=a.ORGANIZATIONNO ")

        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND a.STATUS='0'");


        if (StringUtils.isNotEmpty(req.getRequest().getBizpartner1())) {
            querySql.append(" AND a.CORP='").append(req.getRequest().getBizpartner1()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBizpartner2())) {
            querySql.append(" AND a.INTERTRADECORP='").append(req.getRequest().getBizpartner2()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStartDate())) {
            querySql.append(" AND TO_CHAR(a.BDATE,'YYYYMMDD')>='").append(DateFormatUtils.getPlainDate(req.getRequest().getStartDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            querySql.append(" AND TO_CHAR(a.BDATE,'YYYYMMDD')<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }

        return querySql.toString();
    }
}
