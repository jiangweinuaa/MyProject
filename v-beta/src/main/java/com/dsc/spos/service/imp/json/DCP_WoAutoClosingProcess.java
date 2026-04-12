package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WoAutoClosingProcessReq;
import com.dsc.spos.json.cust.res.DCP_WoAutoClosingProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_WoAutoClosingProcess extends SPosAdvanceService<DCP_WoAutoClosingProcessReq, DCP_WoAutoClosingProcessRes> {
    @Override
    protected void processDUID(DCP_WoAutoClosingProcessReq req, DCP_WoAutoClosingProcessRes res) throws Exception {

        List<Map<String, Object>> accountList = doQueryData(getQueryAcountSql(req), null);

        if (CollectionUtils.isEmpty(accountList)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "请先设置对应法人的主帐套信息！！");
        }

        List<Map<String, Object>> taskList = doQueryData(getQuerySql(req), null);
//        b. 3：将已完工（预计产量= 完工入库量）的工单结案
//        ⅰ. MES_BATCHTASK配料状态 =Y配料完成
//        ⅱ. PRODUCTSTATUS=Y已完工或指定完工
//        ⅲ. 生产数量=完工入库数+报废数+不合格数，入库数量＞0，用派工工单关联发料表、领导单、完工入库单的单据都需要已经完成审核过账；
//        ⅳ. 将MES_BATCHTASK表的状态更新为 M 成本结案
//        ⅴ. 生管状态：更新为：自动结案
//        ⅵ. 将执行画面上成本关账日期更新至：MES_BATCHTASK中成本关账日字段COSTCLOSEDATE
        if ("3".equals(req.getRequest().getType()) || "1".equals(req.getRequest().getType())) {
            for (Map<String, Object> taskMap : taskList) {

                double productQty = Double.parseDouble(taskMap.get("PRODUCTQTY").toString());
                double qty = Double.parseDouble(taskMap.get("PQTY").toString());
                
                if ("Y".equals(taskMap.get("PRODUCTSTATUS").toString()) && productQty >= qty) {
                    ColumnDataValue uptCondition = new ColumnDataValue();
                    ColumnDataValue uptValue = new ColumnDataValue();

                    uptCondition.add("EID", DataValues.newString(taskMap.get("EID").toString()));
                    uptCondition.add("ORGANIZATIONNO", DataValues.newString(taskMap.get("ORGANIZATIONNO").toString()));
                    uptCondition.add("BATCHTASKNO", DataValues.newString(taskMap.get("BATCHTASKNO").toString()));
                    uptCondition.add("PLUNO", DataValues.newString(taskMap.get("PLUNO").toString()));

                    uptValue.add("COSTCLOSEDATE", DataValues.newDate(DateFormatUtils.getDate(req.getRequest().getCostCloseDate())));
                    uptValue.add("BATCHSTATUS", DataValues.newDate(DateFormatUtils.getDate("M")));
                    uptValue.add("PMCLOSESTATUS", DataValues.newDate(DateFormatUtils.getDate("1")));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_BATCHTASK", uptCondition, uptValue)));
                }

            }
        }
        if ("2".equals(req.getRequest().getType()) || "1".equals(req.getRequest().getType())) {
            for (Map<String, Object> taskMap : taskList) {

                double productQty = Double.parseDouble(taskMap.get("PRODUCTQTY").toString());
                double qty = Double.parseDouble(taskMap.get("PQTY").toString());


                if ("Y".equals(taskMap.get("PRODUCTSTATUS").toString()) && productQty >= qty) {
                    ColumnDataValue uptCondition = new ColumnDataValue();
                    ColumnDataValue uptValue = new ColumnDataValue();

                    uptCondition.add("EID", DataValues.newString(taskMap.get("EID").toString()));
                    uptCondition.add("ORGANIZATIONNO", DataValues.newString(taskMap.get("ORGANIZATIONNO").toString()));
                    uptCondition.add("BATCHTASKNO", DataValues.newString(taskMap.get("BATCHTASKNO").toString()));
                    uptCondition.add("PLUNO", DataValues.newString(taskMap.get("PLUNO").toString()));

                    uptValue.add("COSTCLOSEDATE", DataValues.newDate(DateFormatUtils.getDate(req.getRequest().getCostCloseDate())));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_BATCHTASK", uptCondition, uptValue)));
                }

            }


        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WoAutoClosingProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WoAutoClosingProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WoAutoClosingProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WoAutoClosingProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoAutoClosingProcessReq> getRequestType() {
        return new TypeToken<DCP_WoAutoClosingProcessReq>() {
        };
    }

    private String getQueryAcountSql(DCP_WoAutoClosingProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT *  ")
                .append(" FROM DCP_ACOUNT_SETTING a")
        ;

        querySql.append(" WHERE a.eid='").append(req.geteId()).append("'");

        querySql.append(" AND a.STAUTS='100' and a.ACCTTYPE='1' ");

        querySql.append(" AND a.CORP='").append(req.getCorp()).append("'");


        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_WoAutoClosingProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.* ")
                .append(" FROM MES_BATCHTASK a ")
                .append(" LEFT JOIN DCP_ORG o1 on a.eid=o1.eid and a.ORGANIZATIONNO=o1.ORGANIZATIONNO ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND o1.CORP='").append(req.getCorp()).append("'");

        if (Check.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }
        String period = req.getRequest().getPeriod();
        if (Check.isNotEmpty(period) && period.length() == 1) {
            period = "0" + period;
        }
        String year = req.getRequest().getYear();
        if (Check.isNotEmpty(year) && Check.isNotEmpty(period)) {
            querySql.append(" AND a.BDATE like '").append(year).append(period).append("%%'");

        }

        querySql.append(" AND a.PRODUCTSTATUS<>'N' and a.PRODUCTSTATUS<>'M' ");

        return querySql.toString();
    }

    @Override
    protected DCP_WoAutoClosingProcessRes getResponseType() {
        return new DCP_WoAutoClosingProcessRes();
    }
}
