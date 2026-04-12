package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostOpnStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CostOpnStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostOpnStatusUpdate extends SPosAdvanceService<DCP_CostOpnStatusUpdateReq, DCP_CostOpnStatusUpdateRes> {


    @Override
    protected void processDUID(DCP_CostOpnStatusUpdateReq req, DCP_CostOpnStatusUpdateRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        String oStatus = qData.get(0).get("STATUS").toString();
        String eid = qData.get(0).get("EID").toString();
        if (Constant.OPR_TYPE_CONFIRM.equals(req.getRequest().getOpType())) {
            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可审核！");
            }
            //审核写入dcp_curinvcoststat

            for (Map<String, Object> q : qData) {
                ColumnDataValue dcp_curInvCostStat = new ColumnDataValue();
                dcp_curInvCostStat.add("EID", DataValues.newString(q.get("EID").toString()));
                dcp_curInvCostStat.add("ACCOUNTID", DataValues.newString(q.get("ACCOUNTID").toString()));
                dcp_curInvCostStat.add("COSTDOMAINID", DataValues.newString(q.get("COSTDOMAINID").toString()));
                dcp_curInvCostStat.add("COST_CALCULATION", DataValues.newString(q.get("COST_CALCULATION").toString()));
                dcp_curInvCostStat.add("CORP", DataValues.newString(q.get("CORP").toString()));
                dcp_curInvCostStat.add("YEAR", DataValues.newString(q.get("YEAR").toString()));
                dcp_curInvCostStat.add("PERIOD", DataValues.newString(q.get("PERIOD").toString()));
                dcp_curInvCostStat.add("PLUNO", DataValues.newString(q.get("PRODNO").toString()));
                dcp_curInvCostStat.add("FEATURENO", DataValues.newString(q.get("FEATURENO").toString()));
//                dcp_curInvCostStat.add("CURRENCY", DataValues.newString(q.get("CURRENCY").toString()));
                dcp_curInvCostStat.add("ENDINGBALQTY", DataValues.newString(q.get("QTY").toString()));
                dcp_curInvCostStat.add("ENDINGBALAMT", DataValues.newString(q.get("TOT_AMT").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_MAT", DataValues.newString(q.get("MATERIAL").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_LABOR", DataValues.newString(q.get("LABOR").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OEM", DataValues.newString(q.get("OEM").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OH1", DataValues.newString(q.get("EXP1").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OH2", DataValues.newString(q.get("EXP2").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OH3", DataValues.newString(q.get("EXP3").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OH4", DataValues.newString(q.get("EXP4").toString()));
                dcp_curInvCostStat.add("ENDINGBAL_OH5", DataValues.newString(q.get("EXP5").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE", DataValues.newString(q.get("AVG_PRICE").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_MAT", DataValues.newString(q.get("AVG_PRICE_MATERIAL").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_LABOR", DataValues.newString(q.get("AVG_PRICE_LABOR").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OEM", DataValues.newString(q.get("AVG_PRICE_OEM").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OH1", DataValues.newString(q.get("AVG_PRICE_EXP1").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OH2", DataValues.newString(q.get("AVG_PRICE_EXP2").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OH3", DataValues.newString(q.get("AVG_PRICE_EXP3").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OH4", DataValues.newString(q.get("AVG_PRICE_EXP4").toString()));
                dcp_curInvCostStat.add("CURAVGPRICE_OH5", DataValues.newString(q.get("AVG_PRICE_EXP5").toString()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTSTAT", dcp_curInvCostStat)));

            }

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue oValue = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eid));
            condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

            oValue.add("STATUS", DataValues.newString(1));

            oValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            oValue.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INIINVCOSTOPN", condition, oValue)));

        } else if (Constant.OPR_TYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
            if (!"1".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可反审核！");
            }

            ColumnDataValue delCondition = new ColumnDataValue();
            delCondition.add("EID", DataValues.newString(eid));
            delCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
//            delCondition.add("COSTDOMAINID", DataValues.newString(q.get("COSTDOMAINID").toString()));
//            delCondition.add("COST_CALCULATION", DataValues.newString(q.get("COST_CALCULATION").toString()));
//            delCondition.add("CORP", DataValues.newString(q.get("CORP").toString()));
            delCondition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            delCondition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTSTAT", delCondition)));

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue oValue = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eid));
            condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

            oValue.add("STATUS", DataValues.newString(0));

            oValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            oValue.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INIINVCOSTOPN", condition, oValue)));


        } else if (Constant.OPR_TYPE_CANCEL.equals(req.getRequest().getOpType())) {
            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可作废！");
            }

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue oValue = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eid));
            condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

            oValue.add("STATUS", DataValues.newString(-1));

            oValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            oValue.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INIINVCOSTOPN", condition, oValue)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostOpnStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostOpnStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostOpnStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostOpnStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostOpnStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_CostOpnStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_CostOpnStatusUpdateRes getResponseType() {
        return new DCP_CostOpnStatusUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_CostOpnStatusUpdateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.*,b.CORP ")
                .append(" FROM DCP_INIINVCOSTOPN a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
        ;

        if (StringUtils.isEmpty(req.getRequest().getEId())) {
            querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        } else {
            querySql.append(" WHERE a.EID='").append(req.getRequest().getEId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR=").append(req.getRequest().getYear());
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD=").append(req.getRequest().getPeriod());
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS=").append(req.getRequest().getStatus());
        }

        return querySql.toString();
    }
}
