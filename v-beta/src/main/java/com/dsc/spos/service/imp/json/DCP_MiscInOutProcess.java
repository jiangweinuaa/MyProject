package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MiscInOutProcessReq;
import com.dsc.spos.json.cust.res.DCP_MiscInOutProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_MiscInOutProcess extends SPosAdvanceService<DCP_MiscInOutProcessReq, DCP_MiscInOutProcessRes> {
    @Override
    protected void processDUID(DCP_MiscInOutProcessReq req, DCP_MiscInOutProcessRes res) throws Exception {

//        1. 执行时先判断账套对应DCP_MISCINOUT内是否有数据，维度：账套+年度+期别+来源单号，只抓取未生生成数据；
//        2. 单据类型区分
//        a. 调用：DCP_StockInNewQuery 入库单查询，DCP_StockInDetailQuery入库单明细查询，对应【单据类型type】=1 杂项入库单
//        b. 调用：DCP_StockOutQueryV3-出库单查询&DCP_StockOutDetailQueryV3-出库单明细查询；对应【单据类型type】=2 杂项出库单
//        3. 抓取条件判断：
//        a. 按条件账套筛选法人组织下所有组织；
//        b. 单据状态status= 3 已签收
//        c. docType= 3 其他出库/3 其他入库；
//        d. dateType=accountDate记账日期 在年度期别自然月范围内；例如：202504 抓取 20250401-20250430
//        4. 字段来源参考
//        5. 科目调用：【品类设定】暂时处理为空；
//        6. 数据建立完成，反馈前端；
//        7. 需要考虑效能，建议生成时间控制在 1 分钟内；

        String date = req.getRequest().getYear() + req.getRequest().getPeriod();
        String type = req.getRequest().getBillType();

        //2.根据类型区分定义查询sql语句
        String querySql = "";

        if (StringUtils.isBlank(type) || "1".equals(type)) {
            querySql = getType1SqlQuery(req);
        }

        if (StringUtils.isBlank(type) || "2".equals(type)) {
            if (StringUtils.isNotEmpty(querySql)) {
                querySql = querySql + " UNION ALL " + getType2SqlQuery(req);
            } else {
                querySql = getType2SqlQuery(req);
            }

        }
        querySql = " SELECT * FROM ( " + querySql + " ) t ";

        List<Map<String, Object>> getData = doQueryData(querySql, null);

        if (CollectionUtils.isEmpty(getData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "没有可处理数据！");
        }
        List<Map<String, Object>> mcData = doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> costDoMain = doQueryData(getCostDoMainSqlQuery(req), null);
        for (Map<String, Object> map : getData) {

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", map.get("EID").toString());
            condition.put("ACCOUNTID", req.getRequest().getAccountID());
            condition.put("YEAR", req.getRequest().getYear());
            condition.put("PERIOD", req.getRequest().getPeriod());
            condition.put("TYPE", map.get("TYPE").toString());
            condition.put("REFERENCENO", map.get("BILLNO").toString());
            condition.put("ITEM", map.get("ITEM").toString());

            List<Map<String, Object>> exieted = MapDistinct.getWhereMap(mcData, condition, false);
            if (CollectionUtils.isNotEmpty(exieted)) {
                continue;
            }

            ColumnDataValue dcp_miscinout = new ColumnDataValue();
            dcp_miscinout.add("EID", map.get("EID").toString());
            dcp_miscinout.add("STATUS", "100");

            dcp_miscinout.add("CREATEOPID", req.getEmployeeNo());
            dcp_miscinout.add("CREATEDEPTID", req.getDepartmentNo());
            dcp_miscinout.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            dcp_miscinout.add("ACCOUNTID", req.getRequest().getAccountID());
            dcp_miscinout.add("YEAR", req.getRequest().getYear());
            dcp_miscinout.add("PERIOD", req.getRequest().getPeriod());
            dcp_miscinout.add("COST_CALCULATION", req.getRequest().getCost_Calculation());
            dcp_miscinout.add("CORP", req.getRequest().getCorp());
            dcp_miscinout.add("ITEM", map.get("ITEM").toString());

            dcp_miscinout.add("TYPE", map.get("TYPE").toString());
            dcp_miscinout.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(map.get("BDATE").toString())));
            dcp_miscinout.add("CONFIRM_DATE", DataValues.newDate(DateFormatUtils.getDate(map.get("ACCOUNT_DATE").toString())));
            dcp_miscinout.add("ORGANIZATIONNO", map.get("ORGANIZATIONNO").toString());

            if (CollectionUtils.isNotEmpty(costDoMain)) {
                dcp_miscinout.add("COSTDOMAINID", costDoMain.get(0).get("COSTDOMAINID").toString());
                dcp_miscinout.add("COSTDOMAINDIS", costDoMain.get(0).get("COSTDOMAIN").toString());
            }

            dcp_miscinout.add("REFERENCENO", map.get("BILLNO").toString());

            if ("1".equals(map.get("TYPE").toString())) {
                dcp_miscinout.add("INOUTCODE", "1");
            } else {
                dcp_miscinout.add("INOUTCODE", "-1");
            }
            dcp_miscinout.add("PRODNO", map.get("PLUNO").toString());
            dcp_miscinout.add("FEATURENO", map.get("FEATURENO").toString());
            dcp_miscinout.add("WAREHOUSENO", map.get("WAREHOUSE").toString());
            dcp_miscinout.add("POSITIONNO", map.get("LOCATION").toString());
            dcp_miscinout.add("BATCH", map.get("BATCH_NO").toString());

            dcp_miscinout.add("BILLTYPE", map.get("DOC_TYPE").toString());

            dcp_miscinout.add("BIZPARTNERNO", map.get("SUPPLIER").toString());
            dcp_miscinout.add("BIZPARTNERNAME", map.get("BIZPARTNERNAME").toString());
            dcp_miscinout.add("CATEGORY", map.get("CATEGORY").toString());

            //todo 后续改动
//            dcp_miscinout.add("INVSUBJECT", map.get("INVSUBJECT").toString());
//            dcp_miscinout.add("EXPCOSTSUBJECT", map.get("EXPCOSTSUBJECT").toString());
//            dcp_miscinout.add("INCOMESUBJECT", map.get("INCOMESUBJECT").toString());
            dcp_miscinout.add("TRANSCURRENCY", "CNY");
            dcp_miscinout.add("LOCALCURRENCY", "CNY");
            dcp_miscinout.add("EXRATE", "1");
            dcp_miscinout.add("TRANSUNIT", map.get("PUNIT").toString());
            dcp_miscinout.add("BASEUNIT", map.get("BASEUNIT").toString());
            dcp_miscinout.add("UNITRATIO", map.get("UNIT_RATIO").toString());
            dcp_miscinout.add("TRANSQTY", map.get("PQTY").toString());
            dcp_miscinout.add("QTY", DataValues.newString(BigDecimalUtils.mul(
                    Double.parseDouble(map.get("PQTY").toString()),
                    Double.parseDouble(map.get("UNIT_RATIO").toString())
            )));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_miscinout", dcp_miscinout)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    private String getAccountSettingQuerySql(DCP_MiscInOutProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();


        return querySql.toString();
    }

    private String getCostDoMainSqlQuery(DCP_MiscInOutProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT * FROM DCP_COSTDOMAIN a");
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("'");
        return querySql.toString();
    }


    private String getType1SqlQuery(DCP_MiscInOutProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT '1' as TYPE,a.EID,a.DOC_TYPE,a.BDATE,a.ACCOUNT_DATE,a.ORGANIZATIONNO" +
                "  ,a.STOCKINNO BILLNO,b.PLUNO,b.ITEM,b.FEATURENO,b.WAREHOUSE,b.MES_LOCATION LOCATION,dg.CATEGORY " +
                " ,b.BATCH_NO,dg.SUPPLIER,bz.SNAME BIZPARTNERNAME,b.UNIT_RATIO,b.PUNIT,b.BASEUNIT  " +
                " ,b.PQTY " +
                " FROM dcp_stockin a " +
                " INNER JOIN dcp_stockin_detail b on a.EID=b.EID and a.STOCKINNO=b.STOCKINNO and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO  " +
                " LEFT JOIN DCP_GOODS dg on dg.eid=b.eid and dg.PLUNO=b.PLUNO " +
                " LEFT JOIN DCP_BIZPARTNER bz on bz.eid=dg.eid and bz.BIZPARTNERNO=dg.SUPPLIER "
        );

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" and a.DOC_TYPE='3' AND a.STATUS='2' ");

        String date = req.getRequest().getYear() + req.getRequest().getPeriod();

        if (StringUtils.isNotEmpty(date)) {
            querySql.append(" AND a.ACCOUNT_DATE like '%%").append(date).append("%%'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");
        }


        return querySql.toString();
    }

    private String getType2SqlQuery(DCP_MiscInOutProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT '2' as TYPE,a.EID,a.DOC_TYPE,cast(a.BDATE as nvarchar2(8)),a.ACCOUNT_DATE,a.ORGANIZATIONNO" +
                "  ,a.STOCKOUTNO BILLNO,b.PLUNO,b.ITEM,b.FEATURENO,b.WAREHOUSE,b.LOCATION,dg.CATEGORY " +
                "  ,b.BATCHNO,dg.SUPPLIER,bz.SNAME BIZPARTNERNAME,b.UNITRATIO,b.PUNIT,b.BASEUNIT " +
                " ,b.PQTY " +
                " FROM DCP_STOCKOUT a " +
                " INNER JOIN dcp_stockout_batch b on a.EID=b.EID and a.STOCKOUTNO=b.STOCKOUTNO and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO  " +
                " LEFT JOIN DCP_GOODS dg on dg.eid=b.eid and dg.PLUNO=b.PLUNO " +
                " LEFT JOIN DCP_BIZPARTNER bz on bz.eid=dg.eid and bz.BIZPARTNERNO=dg.SUPPLIER "
        );

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" and a.DOC_TYPE='3' AND a.STATUS='2' ");

        String date = req.getRequest().getYear() + req.getRequest().getPeriod();

        if (StringUtils.isNotEmpty(date)) {
            querySql.append(" AND a.ACCOUNT_DATE like '%%").append(date).append("%%'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");
        }

        return querySql.toString();
    }


    protected String getQuerySql(DCP_MiscInOutProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT * FROM DCP_MISCINOUT a ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR='").append(req.getRequest().getYear()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBillType())) {
            querySql.append(" AND a.BILLTYPE='").append(req.getRequest().getBillType()).append("'");
        }

        return querySql.toString();
    }

//    private List<String> getOrgList(DCP_MiscInOutProcessReq req){
//        String querySql = " SELECT * FROM DCP_ORG a" +
//                " WHERE a.EID='"+req.geteId()+"'";
//    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_MiscInOutProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MiscInOutProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MiscInOutProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_MiscInOutProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_MiscInOutProcessReq> getRequestType() {
        return new TypeToken<DCP_MiscInOutProcessReq>() {
        };
    }

    @Override
    protected DCP_MiscInOutProcessRes getResponseType() {
        return new DCP_MiscInOutProcessRes();
    }
}
