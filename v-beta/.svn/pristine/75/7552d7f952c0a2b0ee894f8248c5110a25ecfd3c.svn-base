package com.dsc.spos.progress.imp;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_HrExpStatProcessReq;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HrExpStatProcess extends ProgressService<DCP_HrExpStatProcessReq> {

    private static final int MAX_STEP = 8;

    public HrExpStatProcess(DCP_HrExpStatProcessReq req) {
        super(req);
        this.setMaxStep(MAX_STEP);
        setType(ProgressType.ProgressType_G);
    }

    //执行步骤如下
//        1.  调用DCP_Acount_SetingQuery：应检查计算的年度+期别在成本关账日期之后;如小于关账日期，则提示不可执行；
//        2. 执行时先将账套+成本计算类型+年度+期别已存在的数据删除；
//        3. 【工时费用统计表】取值
//        a. 抓取费用金额
//  ○ 抓取条件：帐套+年度+期别
//  ○ 分组条件：帐套+年度+期别+成本中心+分摊类型
//        ALLOCTYPE
//  ○  抓取来源： DCP_COSTALLOC成本要素分摊费用金额来源DCP_COSTALLOC成本要素分摊
//  ○ 抓取对象：抓取分摊金额SUM（AMT）
//  ○ 存入对象：存入费用总额TOTEXPENSE
//        b. 抓取分摊指标数
//        抓取条件：账套+法人+年度+期别+成本中心
//        分组条件：账套+法人+成本中心
//        抓取来源：DCP_WOPRODREPSTAT工单报工统计分摊指数来源DCP_WOPRODREPSTAT工单报工统计
//        抓取对象：
//        ⅰ. 实际工时sum(ACTHOURS)
//        ⅱ. 实际机时sum(ACTMACHINEHRS)
//        ⅲ. 标准工时sum(STDHOURS)
//        ⅳ. 标准机时 sum(STDMACHINEHRS)
//                ⅴ. 入库数量sum（INVQTY）
//        ⅵ. 入库金额sum（INVAMOUNT）
//        存入对象：
//  ○ 如果"分摊方式"="3标准工时*分摊权数,"，则分摊基础指标总数ALLOCBASESUM=标准工时SUM（STDHOURS），则标准产能STDCAPACITY=标准工时SUM（STDHOURS）
//  ○ 如果"分摊方式="4实际工时*分摊权数,"，则分摊基础指标总数=实际工时SUM（ACTHOURS），则标准产能=实际工时SUM（ACTHOURS)
//  ○ 如果"分摊方式"="5.标准机时*分摊权数"，则分摊基础指标总数=标准机时SUM（STDMACHINEHRS），则标准产能=标准机时SUM（STDMACHINEHRS)
//  ○ 如果"分摊方式"="1.入库金额*分摊权数,,"，则分摊基础指标总数=入库金额SUM（INVAMOUNT），则标准产能=入库金额SUM（INVAMOUNT）
//  ○ 如果"分摊方式"="2产出数量*分摊权数"，则分摊基础指标总数=入库数量SUM（INVQTY），则标准产能=入库数量SUM（INVQTY）
//        c. 计算分摊费用和单位成本
//                单位成本UNITCOST=分摊金额/分摊基础指标总数
//        d.  分摊金额ALLOCAMOUT= 单位成本* 分摊基础总和
//        e. 闲置费用= 费用总和 - 分摊金额
//        f. 工时费用统计工时费用统计-计算后格式
    @Override
    public void runProgress() throws Exception {

        DsmDAO dao = StaticInfo.dao;

        //1.检查年度+期别是否小于关账日期
        incStep("正在查询关账日期"); //1

        String querySql = " SELECT * FROM DCP_ACOUNT_SETTING WHERE EID='%s' and ACCOUNTID='%s'  ";
        List<Map<String, Object>> data = dao.executeQuerySQL(String.format(querySql, getReq().geteId(), getReq().getRequest().getAccountID()), null);

        String closingDate = "";
        if (null != data && !data.isEmpty()) {
            closingDate = StringUtils.toString(data.get(0).get("CLOSING_DATE"), "");
        }

        String period = getReq().getRequest().getPeriod();
        if (period.length() < 2) {
            period = "0" + period;
        }
        getReq().getRequest().setPeriod(period);
        String currDate = getReq().getRequest().getYear() + getReq().getRequest().getPeriod();
        if (StringUtils.isNotEmpty(closingDate) && DateFormatUtils.compareDate(currDate, closingDate) < 0) {
            throw new RuntimeException("当前计算的年度期别:" + period + "小于关账日期:" + closingDate + ",不可执行");
        }

        incStep("正在检查当前年度期别是否已执行");//2

        querySql = " SELECT * FROM DCP_HREXPSTAT WHERE EID='%s' and ACCOUNTID='%s'  " +
                " and YEAR=%s and PERIOD=%s  " +
                " and CORP='%s'  ";
        if (StringUtils.isNotEmpty(getReq().getRequest().getAllocType())) {
            querySql += " and ALLOCTYPE=%s ";

            querySql = String.format(querySql,
                    getReq().geteId(),
                    getReq().getRequest().getAccountID(),
                    getReq().getRequest().getYear(),
                    Integer.parseInt(getReq().getRequest().getPeriod()),
                    getReq().getRequest().getCorp(),
                    getReq().getRequest().getAllocType()
            );
        } else {
            querySql = String.format(querySql,
                    getReq().geteId(),
                    getReq().getRequest().getAccountID(),
                    getReq().getRequest().getYear(),
                    Integer.parseInt(getReq().getRequest().getPeriod()),
                    getReq().getRequest().getCorp()
            );
        }

        data = dao.executeQuerySQL(querySql, null);

        if (null != data && !data.isEmpty()) {
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", getReq().geteId());
            condition.add("ACCOUNTID", getReq().getRequest().getAccountID());
            condition.add("YEAR", getReq().getRequest().getYear());
            condition.add("PERIOD", DataValues.newInteger(getReq().getRequest().getPeriod()));
            condition.add("CORP", getReq().getRequest().getCorp());

            if (StringUtils.isNotEmpty(getReq().getRequest().getAllocType())) {
                condition.add("ALLOCTYPE", getReq().getRequest().getAllocType());
            }

            this.addProcessBean(new DataProcessBean(DataBeans.getDelBean("dcp_hrexpstat", condition)));
            this.addProcessBean(new DataProcessBean(DataBeans.getDelBean("dcp_hrexpdetail", condition)));
        }


        //获取金额小数取位
        //获取currency信息

//        if(Check.NotNull(getReq().getRequest().getCurrency())) {
//            String currencySql = "select * from DCP_CURRENCY where eid='" + getReq().geteId() + "' and" +
//                    " CURRENCY='" + request.getCurrency() + "' ";
//            List<Map<String, Object>> currencies = dao.executeQuerySQL(currencySql, null);
//            if (currencies.size() <= 0) {
//                DocSubmitStop.endStop(key_redis);
//                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "币种:"+request.getCurrency()+"不存在!");
//            } else {
//                amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
//            }
//        }
        incStep("正在抓取金额小数取位");//3
        //抓取金额小数取位
        int amountDigit = 4;
        int priceDigit = 2;
        querySql = " SELECT  b.AMOUNTDIGIT,b.COSTAMOUNTDIGIT,b.COSTPRICEDIGIT " +
                " FROM DCP_ACOUNT_SETTING a " +
                " LEFT JOIN DCP_CURRENCY b on a.eid=b.eid and a.CURRENCY=b.CURRENCY and NATION='CN' " +
                " WHERE a.EID='" + getReq().geteId() + "'" +
                " AND a.ACCOUNTID='" + getReq().getRequest().getAccountID() + "'";
        List<Map<String, Object>> currencyInfo = dao.executeQuerySQL(
                querySql
                , null);
        if (CollectionUtils.isNotEmpty(currencyInfo)) {
            amountDigit = Integer.parseInt(currencyInfo.get(0).get("COSTAMOUNTDIGIT").toString());
            priceDigit = Integer.parseInt(currencyInfo.get(0).get("COSTPRICEDIGIT").toString());
        }

        incStep("正在抓取分摊指标数");//4
        querySql = " SELECT EID,ACCOUNTID,ACCOUNT,YEAR,PERIOD,COSTCENTERNO,ALLOCTYPE,ALLOCFORMULA,SUM(AMT) AMT " +
                "  FROM DCP_COSTALLOC WHERE EID='%s' and ACCOUNTID='%s' and YEAR=%s and PERIOD=%s " +
                "  GROUP BY EID,ACCOUNTID,ACCOUNT,YEAR,PERIOD,COSTCENTERNO,ALLOCTYPE,ALLOCFORMULA ";

        querySql = String.format(querySql,
                getReq().geteId(),
                getReq().getRequest().getAccountID(),
                getReq().getRequest().getYear(),
                Integer.parseInt(getReq().getRequest().getPeriod()),
                getReq().getRequest().getCostCenterNo()
        );
        List<Map<String, Object>> dcp_costalloc = dao.executeQuerySQL(
                querySql
                , null);
        if (CollectionUtils.isEmpty(dcp_costalloc)) {
            throw new RuntimeException("查询费用金额信息失败，请检查成本要素分摊设置是否正确");
        }

        incStep("正在抓取费用金额");//5

        querySql = " SELECT EID,CORP,COSTCENTERNO," +
                " SUM(ACTHOURS) SACTHOURS," +
                " SUM(ACTMACHINEHRS) SACTMACHINEHRS," +
                " SUM(STDHOURS) SSTDHOURS," +
                " SUM(STDMACHINEHRS) SSTDMACHINEHRS," +
                " SUM(INVQTY) SINVQTY," +
                " SUM(INVAMOUNT) SINVAMOUNT " +
                " FROM DCP_WOPRODREPSTAT " +
                " WHERE EID='%s' and CORP='%s' and to_char(WDATE,'yyyymm')='%s'  " +
                " GROUP BY EID,CORP,COSTCENTERNO ";

        List<Map<String, Object>> dcp_woprodrepstat = dao.executeQuerySQL(
                String.format(querySql,
                        getReq().geteId(),
                        getReq().getRequest().getCorp(),
                        getReq().getRequest().getYear() + getReq().getRequest().getPeriod(),
                        getReq().getRequest().getCostCenterNo()
                ), null);

        incStep("正在抓取工单信息");//6
        querySql = " SELECT EID,CORP,COSTCENTERNO,TASKID, " +
                " SUM(ACTHOURS) ACTHOURS," +
                " SUM(ACTMACHINEHRS) ACTMACHINEHRS," +
                " SUM(STDHOURS) STDHOURS," +
                " SUM(STDMACHINEHRS) STDMACHINEHRS," +
                " SUM(INVQTY) INVQTY," +
                " SUM(INVAMOUNT) INVAMOUNT " +
                " FROM DCP_WOPRODREPSTAT " +
                " WHERE EID='%s' and CORP='%s' and to_char(WDATE,'yyyymm')='%s'  " +
                " GROUP BY EID,CORP,COSTCENTERNO,TASKID " +
                " ORDER BY TASKID ";

        querySql = String.format(querySql,
                getReq().geteId(),
                getReq().getRequest().getCorp(),
                getReq().getRequest().getYear() + getReq().getRequest().getPeriod(),
                getReq().getRequest().getCostCenterNo()
        );
        List<Map<String, Object>> workDetail = dao.executeQuerySQL(
                querySql, null);


        incStep("正在将计算结果转换为工时费用统计");//6

        String costCenterNo = getReq().getRequest().getCostCenterNo();
        Map<String, Object> condition = new HashMap<>();
        condition.put("COSTCENTERNO", costCenterNo);
        if (StringUtils.isNotEmpty(getReq().getRequest().getAllocType())) {
            condition.put("ALLOCTYPE", getReq().getRequest().getAllocType());
        }

        String allocType = "";
        String allocFormula = "";

        double amt = 0;
        List<Map<String, Object>> costAlloc = MapDistinct.getWhereMap(dcp_costalloc, condition, true);
        for (Map<String, Object> oneAlloc : costAlloc) {
            allocType = oneAlloc.get("ALLOCTYPE").toString();
            allocFormula = oneAlloc.get("ALLOCFORMULA").toString();
            amt = Double.parseDouble(oneAlloc.get("AMT").toString());

            for (Map<String, Object> woProdStat : dcp_woprodrepstat) {

                ColumnDataValue dcp_hrexpstat = new ColumnDataValue();
                dcp_hrexpstat.add("EID", DataValues.newString(getReq().geteId()));
                dcp_hrexpstat.add("CORP", DataValues.newString(getReq().getRequest().getCorp()));
                dcp_hrexpstat.add("ACCOUNTID", DataValues.newString(getReq().getRequest().getAccountID()));
                dcp_hrexpstat.add("ACCOUNT", DataValues.newString(getReq().getRequest().getAccount()));
                dcp_hrexpstat.add("YEAR", DataValues.newString(getReq().getRequest().getYear()));
                dcp_hrexpstat.add("PERIOD", DataValues.newString(getReq().getRequest().getPeriod()));
                dcp_hrexpstat.add("COSTCENTERNO", DataValues.newString(costCenterNo));
                dcp_hrexpstat.add("ALLOCTYPE", DataValues.newString(allocType));
                dcp_hrexpstat.add("COST_CALCULATION", DataValues.newString(getReq().getRequest().getCost_Calculation()));

                dcp_hrexpstat.add("STATUS", DataValues.newString("100"));

                dcp_hrexpstat.add("TOTEXPENSE", DataValues.newString(amt));
                //  ○ 如果"分摊方式"="1.入库金额*分摊权数,,"，则分摊基础指标总数=入库金额SUM（INVAMOUNT），则标准产能=入库金额SUM（INVAMOUNT）
                //  ○ 如果"分摊方式"="2产出数量*分摊权数"，则分摊基础指标总数=入库数量SUM（INVQTY），则标准产能=入库数量SUM（INVQTY）
                //  ○ 如果"分摊方式"="3标准工时*分摊权数,"，则分摊基础指标总数ALLOCBASESUM=标准工时SUM（STDHOURS），则标准产能STDCAPACITY=标准工时SUM（STDHOURS）
                //  ○ 如果"分摊方式="4实际工时*分摊权数,"，则分摊基础指标总数=实际工时SUM（ACTHOURS），则标准产能=实际工时SUM（ACTHOURS)
                //  ○ 如果"分摊方式"="5.标准机时*分摊权数"，则分摊基础指标总数=标准机时SUM（STDMACHINEHRS），则标准产能=标准机时SUM（STDMACHINEHRS)
                String allocBaseSum = "0";
                String stdCapacity = "0";
                if ("1".equals(allocFormula)) {
                    allocBaseSum = woProdStat.get("SINVAMOUNT").toString();
                    stdCapacity = woProdStat.get("SINVAMOUNT").toString();
                } else if ("2".equals(allocFormula)) {
                    allocBaseSum = woProdStat.get("SINVQTY").toString();
                    stdCapacity = woProdStat.get("SINVQTY").toString();
                } else if ("3".equals(allocFormula)) {
                    allocBaseSum = woProdStat.get("SSTDHOURS").toString();
                    stdCapacity = woProdStat.get("SSTDHOURS").toString();
                } else if ("4".equals(allocFormula)) {
                    allocBaseSum = woProdStat.get("SACTHOURS").toString();
                    stdCapacity = woProdStat.get("SACTHOURS").toString();
                } else if ("5".equals(allocFormula)) {
                    allocBaseSum = woProdStat.get("SSTDMACHINEHRS").toString();
                    stdCapacity = woProdStat.get("SSTDMACHINEHRS").toString();
                }

                dcp_hrexpstat.add("ALLOCFORMULA", DataValues.newString(allocFormula));
                dcp_hrexpstat.add("ALLOCBASESUM", DataValues.newString(allocBaseSum));
                dcp_hrexpstat.add("STDCAPACITY", DataValues.newString(stdCapacity));
//                dcp_hrexpstat.add("STDCAPACITY", DataValues.newString(stdCapacity));

//        单位成本UNITCOST=分摊金额/分摊基础指标总数
//        d.  分摊金额ALLOCAMOUT= 单位成本* 分摊基础总和单位成本UNITCOST=分摊金额/分摊基础指标总数
////        d.  分摊金额ALLOCAMOUT= 单位成本* 分摊基础总和

                //分摊基础指标总数
//                String sumQty = woProdStat.get("SINVQTY").toString();
                double unitCost = BigDecimalUtils.div(
                        amt, Double.parseDouble(allocBaseSum),
                        priceDigit);
                double allocAmount =
                        BigDecimalUtils.round(amt, amountDigit);
//                        BigDecimalUtils.mul(
//                        unitCost, Double.parseDouble(sumQty),
//                        amountDigit);
                dcp_hrexpstat.add("UNITCOST", DataValues.newString(unitCost));
                dcp_hrexpstat.add("ALLOCAMOUT", DataValues.newString(allocAmount));
                dcp_hrexpstat.add("IDLEEXPENSE", DataValues.newString(0 ));

                dcp_hrexpstat.add("CREATEOPID", DataValues.newString(getReq().getEmployeeNo()));
                dcp_hrexpstat.add("CREATEDEPTID", DataValues.newString(getReq().getDepartmentNo()));
                dcp_hrexpstat.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("dcp_hrexpstat", dcp_hrexpstat)));

                List<Map<String, Object>> workList = MapDistinct.getWhereMap(workDetail, condition, true);

                double allocAmtSum = 0;
                double allocAmt;
                for (int i = 0; i < workList.size(); i++) {
                    Map<String, Object> map = workList.get(i);
                    ColumnDataValue dcp_hrexpdetail = new ColumnDataValue();

                    dcp_hrexpdetail.add("EID", DataValues.newString(getReq().geteId()));
                    dcp_hrexpdetail.add("CORP", DataValues.newString(getReq().getRequest().getCorp()));
                    dcp_hrexpdetail.add("ACCOUNTID", DataValues.newString(getReq().getRequest().getAccountID()));
                    dcp_hrexpdetail.add("ACCOUNT", DataValues.newString(getReq().getRequest().getAccount()));
                    dcp_hrexpdetail.add("YEAR", DataValues.newString(getReq().getRequest().getYear()));
                    dcp_hrexpdetail.add("PERIOD", DataValues.newString(getReq().getRequest().getPeriod()));
                    dcp_hrexpdetail.add("COSTCENTERNO", DataValues.newString(getReq().getRequest().getCostCenterNo()));
                    dcp_hrexpdetail.add("ALLOCTYPE", DataValues.newString(getReq().getRequest().getAllocType()));
                    dcp_hrexpdetail.add("COST_CALCULATION", DataValues.newString(getReq().getRequest().getCost_Calculation()));

                    dcp_hrexpdetail.add("STATUS", DataValues.newString("100"));

                    dcp_hrexpdetail.add("ALLOCTYPE", DataValues.newString(allocType));
                    dcp_hrexpdetail.add("TASKID", DataValues.newString(map.get("TASKID").toString()));
                    dcp_hrexpdetail.add("ALLOCFORMULA", DataValues.newString(allocFormula));

                    String qty = map.get("INVQTY").toString(); //分摊数量给数量
                    if ("1".equals(allocFormula)) {
                        qty = map.get("INVAMOUNT").toString();
                    } else if ("2".equals(allocFormula)) {
                        qty = map.get("INVQTY").toString();
                    } else if ("3".equals(allocFormula)) {
                        qty = map.get("STDHOURS").toString();
                    } else if ("4".equals(allocFormula)) {
                        qty = map.get("ACTHOURS").toString();
                    } else if ("5".equals(allocFormula)) {
                        qty = map.get("STDMACHINEHRS").toString();
                    }
                    allocAmt = BigDecimalUtils.mul(Double.parseDouble(qty), unitCost, amountDigit);
                    allocAmtSum += allocAmt;
                    if (i + 1 == workList.size()) { //最后一笔进行尾差调整
                        if (allocAmount - allocAmtSum != 0) {
                            allocAmt = allocAmt + (allocAmount - allocAmtSum);
                        }
                    }
                    dcp_hrexpdetail.add("ALLOCAMOUT", DataValues.newString(allocAmt));
                    dcp_hrexpdetail.add("ALLOCQTY", DataValues.newString(qty));
                    dcp_hrexpdetail.add("UNITCOST", DataValues.newString(unitCost));

                    dcp_hrexpdetail.add("CREATEOPID", DataValues.newString(getReq().getEmployeeNo()));
                    dcp_hrexpdetail.add("CREATEDEPTID", DataValues.newString(getReq().getDepartmentNo()));
                    dcp_hrexpdetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                    this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("DCP_HREXPDETAIL", dcp_hrexpdetail)));
                }

            }

        }

        incStep("正在进行数据持久化");
        dao.useTransactionProcessData(this.getPData());

        incStep("执行成功！");
    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void afterRun() {

    }

}
