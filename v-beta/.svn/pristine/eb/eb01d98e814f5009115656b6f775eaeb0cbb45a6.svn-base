package com.dsc.spos.progress.imp;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_invMthClosingProcessReq;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class InvMthClosingProcess extends ProgressService<DCP_invMthClosingProcessReq> {

    private static final int MAX_STEP = 6;

    public InvMthClosingProcess(DCP_invMthClosingProcessReq req) {
        super(req);
        setType(ProgressType.ProgressType_B);
        this.setMaxStep(MAX_STEP);
    }

    @Override
    public void runProgress() throws Exception {
        DsmDAO dao = StaticInfo.dao;

        String eId = getReq().geteId();
        String corp = getReq().getRequest().getCorp();
        String corp_Name = getReq().getRequest().getCorp_Name();
        String accountID = getReq().getRequest().getAccountID();
        String account = getReq().getRequest().getAccount();
        String cost_Calculation = getReq().getRequest().getCost_Calculation();
        String year = getReq().getRequest().getYear();
        String period = getReq().getRequest().getPeriod();

        //1. 条件：账套+年度+期别，调用/DCP_OrgOpenQry按账套法人对应所有有效组织，对应年度期别对应自然月1 日-31 日在DCP_STOCK_DETAIL中数据；
        //2. 调用DCP_CostExecuteCreate更新DCP_CostExecute成本执行和DCP_CostExecuteDetail 成本执行明细表；
        // 【组织作为一个执行进度，每个循环里按执行过程划分不同阶段对应的进度比】
        //3. 再次执行时，DCP_CostExecuteCreate的主任务id 不更新，只更新对应的参数；
        //5. 如果对应抓取数据为空，给值空格
        //6. 上期期末有库存，本期未发生异动，将上期期末库存写入到本期期末库存数量；


        incStep(); //1
        setStepDescription("正在按账套法人对应所有有效组织");

        String orgSql="select a.* from dcp_org a " +
                " left join dcp_org_lang b on a.corp=b.organizationno and a.eid=b.eid and b.lang_type='"+getReq().getLangType()+"' " +
                " inner join DCP_ACOUNT_SETTING c on c.eid=a.eid and c.corp=a.corp " +
                " where a.eid='"+eId+"' and a.corp='"+corp+"' and c.ACCOUNTID='"+accountID+"' ";
        if(Check.NotNull(corp_Name)){
            orgSql+=" and b.org_name like '%"+corp_Name+"%'";
        }
        if(Check.NotNull( account)){
            orgSql+=" and c.account='"+account+"'";
        }

        List<Map<String, Object>> orglist = dao.executeQuerySQL(orgSql, null);
        List<String> orgs = orglist.stream().map(x -> x.get("ORGANIZATIONNO").toString()).distinct().collect(Collectors.toList());

        List<String> rkTypeList= Arrays.asList("14","05","06","17","08","30","35","36","38","33","63","64");
        List<String> xhTypeList= Arrays.asList("20","21","22","23","65","66");
        List<String> lyTypeList=Arrays.asList("15","31","11","37","32","34","07");
        List<String> dbTypeList=Arrays.asList("01","02","04","16","18","19","03","09","12","13","41","42");
        List<String> tzTypeList=Arrays.asList("10");

        if(CollUtil.isNotEmpty(orgs)){

            //删除数据
            incStep(); //1
            setStepDescription("正在删除本期库存月结");
            //根据组织删除
            for (String org:orgs){
                DelBean db1 = new DelBean("DCP_INVMTHCLOSING");
                db1.addCondition("YEAR", new DataValue(year, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("MONTH", new DataValue(period, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(org, Types.VARCHAR));
                this.addProcessBean(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_ACCTSTOCKMTHSTAT");
                db2.addCondition("YEAR", new DataValue(year, Types.VARCHAR));
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("PERIOD", new DataValue(period, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(org, Types.VARCHAR));
                this.addProcessBean(new DataProcessBean(db2));
            }


            String orgSqlIn = orgs.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));

            //年度+期别 查库存异动明细
            String beginDate=year+""+period+"01";
            String endDate=year+""+period+"31"; //最多到31号

            String preYear="";
            String preMonth="";
            if(period.equals("01")){
                preYear=Integer.parseInt(year)-1+"";
                preMonth="12";
            }else{
                preYear=year;
                preMonth=Integer.parseInt(period)-1+"";
            }

            String stockDetailSql=" select a.*,nvl(b.udlength,0) as udlength,nvl(a.BATCHNO,' ') as BATCHNOSTR,nvl(a.LOCATION,' ') AS LOCATIONSTR,NVL(C.IS_COST,'N') as isCost  " +
                    " from DCP_STOCK_DETAIL a" +
                    " left join dcp_unit b on a.eid=b.eid and a.unit=b.unit " +
                    " inner join dcp_warehouse c on c.eid=a.eid and c.organizationno=a.organizationno and c.warehouse=a.warehouse " +
                    " where a.eid='"+eId+"' " +
                    " and a.organizationno in ("+orgSqlIn+") " +
                    " and to_char(a.bdate,'yyyyMMdd')>='"+beginDate+"' " +
                    " and to_char(a.bdate,'yyyyMMdd')<='"+endDate+"' " ;
            List<Map<String, Object>> stockDetailList = dao.executeQuerySQL(stockDetailSql, null);
            List<String> stockOrgList = stockDetailList.stream().map(x -> x.get("ORGANIZATIONNO").toString()).distinct().collect(Collectors.toList());

            incStep();//3
            setStepDescription("正在写入本期期末库存数量");

            List<MonthClosingDetail> detailList = stockDetailList.stream().map(x -> {
                MonthClosingDetail detail = new MonthClosingDetail();
                detail.setOrganizationNo(x.get("ORGANIZATIONNO").toString());
                detail.setEId(x.get("EID").toString());
                detail.setPluNo(x.get("PLUNO").toString());
                detail.setFeatureNo(x.get("FEATURENO").toString());
                detail.setBatchNo(x.get("BATCHNOSTR").toString());
                detail.setWarehouse(x.get("WAREHOUSE").toString());
                detail.setLocation(x.get("LOCATIONSTR").toString());
                detail.setBaseUnit(x.get("BASEUNIT").toString());
                return detail;
            }).distinct().collect(Collectors.toList());

            for (MonthClosingDetail detail : detailList){
                String organizationNo = detail.getOrganizationNo();
                String pluNo = detail.getPluNo();
                String featureNo = detail.getFeatureNo();
                String batchNo = detail.getBatchNo();
                String warehouse = detail.getWarehouse();
                String location = detail.getLocation();
                String baseUnit = detail.getBaseUnit();

                List<Map<String, Object>> collect = stockDetailList.stream().filter(x -> x.get("ORGANIZATIONNO").equals(organizationNo)
                        && x.get("PLUNO").equals(pluNo) && x.get("FEATURENO").equals(featureNo)
                        && x.get("BATCHNOSTR").equals(batchNo) && x.get("WAREHOUSE").equals(warehouse)
                        && x.get("LOCATIONSTR").equals(location)&&x.get("BASEUNIT").equals(baseUnit)).collect(Collectors.toList());

                BigDecimal curRinQty=new BigDecimal(0);
                BigDecimal curSalesQty=new BigDecimal(0);
                BigDecimal curIssueQty=new BigDecimal(0);
                BigDecimal curTransferQty=new BigDecimal(0);
                BigDecimal curAdjustQty=new BigDecimal(0);
                BigDecimal endQty=new BigDecimal(0);

                BigDecimal refCurRinQty=new BigDecimal(0);
                BigDecimal refCurSalesQty=new BigDecimal(0);
                BigDecimal refCurIssueQty=new BigDecimal(0);
                BigDecimal refCurTransferQty=new BigDecimal(0);
                BigDecimal refCurAdjustQty=new BigDecimal(0);
                BigDecimal refEndQty=new BigDecimal(0);

                for (Map<String, Object> map : collect){
                    BigDecimal baseQty = new BigDecimal(Check.Null(map.get("BASEQTY").toString())?"0":map.get("BASEQTY").toString());
                    BigDecimal unitRatio = new BigDecimal(Check.Null(map.get("UNITRATIO").toString())?"1":map.get("UNITRATIO").toString());
                    BigDecimal stockType = new BigDecimal(map.get("STOCKTYPE").toString());//1 -1
                    Integer udLength = Integer.valueOf(map.get("UDLENGTH").toString());
                    String billType = map.get("BILLTYPE").toString();
                    //入库
                    if(rkTypeList.contains(billType)){
                        curRinQty=curRinQty.add(baseQty.multiply(stockType));
                        refCurRinQty=refCurRinQty.add(baseQty.multiply(stockType).divide(unitRatio, udLength, RoundingMode.HALF_UP));
                    }
                    //销货
                    if(xhTypeList.contains(billType)){
                        curSalesQty=curSalesQty.add(baseQty.multiply(stockType));
                        refCurSalesQty=refCurSalesQty.add(baseQty.multiply(stockType).divide(unitRatio, udLength, RoundingMode.HALF_UP));

                    }
                    //领用
                    if(lyTypeList.contains(billType)){
                        curIssueQty=curIssueQty.add(baseQty.multiply(stockType));
                        refCurIssueQty=refCurIssueQty.add(baseQty.multiply(stockType).divide(unitRatio, udLength, RoundingMode.HALF_UP));

                    }
                    //调拨
                    if(dbTypeList.contains(billType)){
                        curTransferQty=curTransferQty.add(baseQty.multiply(stockType));
                        refCurTransferQty=refCurTransferQty.add(baseQty.multiply(stockType).divide(unitRatio, udLength, RoundingMode.HALF_UP));

                    }

                    //调整
                    if(tzTypeList.contains(billType)){
                        curAdjustQty=curAdjustQty.add(baseQty.multiply(stockType));
                        refCurAdjustQty=refCurAdjustQty.add(baseQty.multiply(stockType).divide(unitRatio, udLength, RoundingMode.HALF_UP));

                    }


                }

                endQty=curRinQty.add(curSalesQty).add(curIssueQty).add(curTransferQty).add(curAdjustQty);
                refEndQty=refCurRinQty.add(refCurSalesQty).add(refCurIssueQty).add(refCurTransferQty).add(refCurAdjustQty);

                ColumnDataValue dcp_monthClosing = new ColumnDataValue();
                dcp_monthClosing.add("EID", DataValues.newString(getReq().geteId()));
                dcp_monthClosing.add("YEAR", DataValues.newString(getReq().getRequest().getYear()));
                dcp_monthClosing.add("MONTH", DataValues.newString(getReq().getRequest().getPeriod()));
                dcp_monthClosing.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                dcp_monthClosing.add("PLUNO", DataValues.newString(pluNo));
                dcp_monthClosing.add("FEATURENO", DataValues.newString(featureNo));
                dcp_monthClosing.add("LOCATION", DataValues.newString(Check.Null(location)?" ":location));
                dcp_monthClosing.add("WAREHOUSE", DataValues.newString(warehouse));
                dcp_monthClosing.add("BATCH_NO", DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                dcp_monthClosing.add("WUNIT", DataValues.newString(baseUnit));
                dcp_monthClosing.add("CURINQTY", DataValues.newString(curRinQty));
                dcp_monthClosing.add("CURSALESQTY", DataValues.newString(curSalesQty));
                dcp_monthClosing.add("CURISSUEQTY", DataValues.newString(curIssueQty));
                dcp_monthClosing.add("CURTRANSFERQTY", DataValues.newString(curTransferQty));
                dcp_monthClosing.add("CURADJQTY", DataValues.newString(curAdjustQty));
                dcp_monthClosing.add("ENDQTY", DataValues.newString(endQty));
                dcp_monthClosing.add("REF_CURINQTY", DataValues.newString(refCurRinQty));
                dcp_monthClosing.add("REF_CURSALEQTY", DataValues.newString(refCurSalesQty));
                dcp_monthClosing.add("REF_CURISSUEQTY", DataValues.newString(refCurIssueQty));
                dcp_monthClosing.add("REF_CURTRANSFERQTY", DataValues.newString(refCurTransferQty));
                dcp_monthClosing.add("REF_CURAJDQTY", DataValues.newString(refCurAdjustQty));
                dcp_monthClosing.add("REF_ENDQTY", DataValues.newString(refEndQty));
                dcp_monthClosing.add("CREATEBY", DataValues.newString(getReq().getEmployeeNo()));
                dcp_monthClosing.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                dcp_monthClosing.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

                this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("DCP_INVMTHCLOSING",dcp_monthClosing)));


            }

            //账套库存月统计档
            List<Map<String, Object>> costFilter = stockDetailList.stream().filter(x -> "Y".equals(x.get("ISCOST").toString())).collect(Collectors.toList());

            if (costFilter.size() > 0){

                //查找上期结存
                String statSql="select * from DCP_ACCTSTOCKMTHSTAT a " +
                        " where a.eid='"+eId+"' and a.accountid='"+accountID+"' " +
                        " and a.organizationno in ("+orgSqlIn+") " +
                        " and a.year='"+preYear+"' and a.period='"+preMonth+"' ";
                List<Map<String, Object>> statList = dao.executeQuerySQL(statSql, null);

                List<MonthClosingDetail> costDetailList = costFilter.stream().map(x -> {
                    MonthClosingDetail detail = new MonthClosingDetail();
                    detail.setOrganizationNo(x.get("ORGANIZATIONNO").toString());
                    detail.setEId(x.get("EID").toString());
                    detail.setPluNo(x.get("PLUNO").toString());
                    detail.setFeatureNo(x.get("FEATURENO").toString());
                    detail.setBatchNo(x.get("BATCHNOSTR").toString());
                    detail.setWarehouse(x.get("WAREHOUSE").toString());
                    detail.setLocation(x.get("LOCATIONSTR").toString());
                    detail.setBaseUnit(x.get("BASEUNIT").toString());
                    return detail;
                }).distinct().collect(Collectors.toList());

                for (MonthClosingDetail detail : costDetailList) {
                    String organizationNo = detail.getOrganizationNo();
                    String pluNo = detail.getPluNo();
                    String featureNo = detail.getFeatureNo();
                    String batchNo = detail.getBatchNo();
                    String warehouse = detail.getWarehouse();
                    String location = detail.getLocation();
                    String baseUnit = detail.getBaseUnit();

                    List<Map<String, Object>> collect = costFilter.stream().filter(x -> x.get("ORGANIZATIONNO").equals(organizationNo)
                            && x.get("PLUNO").equals(pluNo) && x.get("FEATURENO").equals(featureNo)
                            && x.get("BATCHNOSTR").equals(batchNo) && x.get("WAREHOUSE").equals(warehouse)
                            && x.get("LOCATIONSTR").equals(location) && x.get("BASEUNIT").equals(baseUnit)).collect(Collectors.toList());

                    //"00-期初 01-配送收货 02-调拨收货 03-退货出库
                    //--04-调拨出库  06-自采退货 07-报损出库
                    //--08-完工入库 09-配送差异调整 10-盘点盈亏调整
                    //--11-加工原料倒扣 12-在途加 13-在途减 14-其它入库
                    //--15-其它出库 16-调拨收货差异 17-退货差异调整
                    //--18-移仓出库 19-移仓收货 20-销售出库 21-销退入库
                    //--22-销订 23-退订 30-组合入库 31-组合原料出库
                    //--32-拆解出库 33-拆解原料入库 34-报损差异调整
                    //--35-成品转换合并  36-原料转换合并  37-转换拆解出库
                    //--38-原料转换拆解入库"

                    BigDecimal lastBalQty=new BigDecimal(0);
                    BigDecimal curPurInQty=new BigDecimal(0);  //本期采购入库数量 05 06 63 64
                    curPurInQty = collect.stream().filter(x -> "05".equals(x.get("BILLTYPE").toString()) || "06".equals(x.get("BILLTYPE").toString()) || "63".equals(x.get("BILLTYPE").toString()) || "64".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curOutSourcInQty=new BigDecimal(0);//本期委外入库数量
                    BigDecimal curWOInQty=new BigDecimal(0);//本期工单入库数量  08 30 33 43
                    curWOInQty = collect.stream().filter(x -> "08".equals(x.get("BILLTYPE").toString()) || "30".equals(x.get("BILLTYPE").toString()) || "33".equals(x.get("BILLTYPE").toString()) || "43".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curReWOOutQty=new BigDecimal(0);//本期返工领出数量
                    BigDecimal curReWOInQty=new BigDecimal(0);//本期返工入库数量
                    BigDecimal curMiscInQty=new BigDecimal(0);//本期杂项入库数量
                    BigDecimal curAdjInQty=new BigDecimal(0);//本期调整入库数量  34
                    curAdjInQty = collect.stream().filter(x -> "34".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curCancelInQty=new BigDecimal(0);//本期销退入库数量
                    BigDecimal curTransInQty=new BigDecimal(0);//本期其他入库数量  14
                    curTransInQty = collect.stream().filter(x -> "14".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curWOOutQty=new BigDecimal(0);//本期工单领用数量  11  31  32  39
                    curWOOutQty = collect.stream().filter(x -> "11".equals(x.get("BILLTYPE").toString()) || "31".equals(x.get("BILLTYPE").toString()) || "32".equals(x.get("BILLTYPE").toString()) || "39".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curSalesQty=new BigDecimal(0);//本期销货数量  20  65
                    curSalesQty = collect.stream().filter(x -> "20".equals(x.get("BILLTYPE").toString()) || "65".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curReturnQty=new BigDecimal(0);//本期销退数量  21 66
                    curReturnQty = collect.stream().filter(x -> "21".equals(x.get("BILLTYPE").toString()) || "66".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curMiscOutQty=new BigDecimal(0);//本期杂发数量 70 71
                    curMiscOutQty = collect.stream().filter(x -> "70".equals(x.get("BILLTYPE").toString()) || "71".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal curInvAdjQty=new BigDecimal(0);//本期盘盈亏数量 10
                    curInvAdjQty = collect.stream().filter(x -> "10".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal curTransOutQty=new BigDecimal(0);//本期其他出库数量 15
                    curTransOutQty = collect.stream().filter(x -> "15".equals(x.get("BILLTYPE").toString()))
                            .map(y -> {
                                BigDecimal qty = new BigDecimal(y.get("BASEQTY").toString());
                                BigDecimal stockType = new BigDecimal(y.get("STOCKTYPE").toString());
                                BigDecimal multiply = qty.multiply(stockType);
                                return multiply;
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal endingBalQty=new BigDecimal(0);//期末结存数量


                    List<Map<String, Object>> yetCollection = statList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)
                            &&x.get("ORGANIZATIONNO").toString().equals(organizationNo)
                                    && x.get("FEATURENO").toString().equals(featureNo)
                                    && x.get("BATCH_NO").toString().equals(batchNo)
                                    && x.get("LOCATION").toString().equals(location)
                                    && x.get("BASEUNIT").toString().equals(baseUnit))
                            .collect(Collectors.toList());
                    if(yetCollection.size()>0){
                        lastBalQty=new BigDecimal(yetCollection.get(0).get("ENDINGBALQTY").toString());
                    }

                    //  ENDINGBALQTY 期末结存数量=上期结存数量+本期采购入库数量
                    //  +本期委外入库数量+本期工单入库数量
                    //  +本期返工领出数量+本期返工入库数量
                    //  +本期杂项入库数量+本期调整入库数量+本期调整入库数量
                    //  +本期销退入库数量+本期其他入库数量+本期工单领用数量
                    //  -本期销货数量-本期销退数量-本期杂发数量-本期盘盈亏数量-本期其他出库数量
                    endingBalQty=lastBalQty.add(curPurInQty)
                            .add(curOutSourcInQty)
                            .add(curWOInQty).add(curReWOOutQty).add(curReWOInQty)
                            .add(curMiscInQty).add(curAdjInQty).add(curCancelInQty)
                            .add(curTransInQty).add(curWOOutQty)
                            .subtract(curSalesQty).subtract(curReturnQty).subtract(curMiscOutQty)
                            .subtract(curInvAdjQty).subtract(curTransOutQty);

                    ColumnDataValue mthstat = new ColumnDataValue();
                    mthstat.add("EID", DataValues.newString(getReq().geteId()));
                    mthstat.add("ACCOUNTID", DataValues.newString(accountID));
                    mthstat.add("YEAR", DataValues.newString(getReq().getRequest().getYear()));
                    mthstat.add("PERIOD", DataValues.newString(getReq().getRequest().getPeriod()));
                    mthstat.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                    mthstat.add("PLUNO", DataValues.newString(pluNo));
                    mthstat.add("FEATURENO", DataValues.newString(featureNo));
                    mthstat.add("LOCATION", DataValues.newString(Check.Null(location)?" ":location));
                    mthstat.add("WAREHOUSE", DataValues.newString(warehouse));
                    mthstat.add("BATCH_NO", DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                    mthstat.add("BASEUNIT", DataValues.newString(baseUnit));
                    mthstat.add("CURPURINQTY", DataValues.newDecimal(curPurInQty));
                    mthstat.add("CUROUTSOURCINQTY", DataValues.newDecimal(curOutSourcInQty));
                    mthstat.add("CURWOINQTY", DataValues.newDecimal(curWOInQty));
                    mthstat.add("CURREWOOUTQTY", DataValues.newDecimal(curReWOOutQty));
                    mthstat.add("CURREWOINQTY", DataValues.newDecimal(curReWOInQty));
                    mthstat.add("CURMISCINQTY", DataValues.newDecimal(curMiscInQty));
                    mthstat.add("CURADJINQTY", DataValues.newDecimal(curAdjInQty));
                    mthstat.add("CURCANCELINQTY", DataValues.newDecimal(curCancelInQty));
                    mthstat.add("CURTRANSINQTY", DataValues.newDecimal(curTransInQty));
                    mthstat.add("CURWOOUTQTY", DataValues.newDecimal(curWOOutQty));
                    mthstat.add("CURSALESQTY", DataValues.newDecimal(curSalesQty));
                    mthstat.add("CURRETURNQTY", DataValues.newDecimal(curReturnQty));
                    mthstat.add("CURMISCOUTQTY", DataValues.newDecimal(curMiscOutQty));
                    mthstat.add("CURINVADJQTY", DataValues.newDecimal(curInvAdjQty));
                    mthstat.add("CURTRANSOUTQTY", DataValues.newDecimal(curTransOutQty));
                    mthstat.add("ENDINGBALQTY", DataValues.newDecimal(endingBalQty));
                    mthstat.add("LASTBALQTY", DataValues.newDecimal(lastBalQty));
                    mthstat.add("CREATEOPID", DataValues.newString(getReq().getOpNO()));
                    mthstat.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                    this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("DCP_ACCTSTOCKMTHSTAT",mthstat)));
                }



            }

            incStep();//4
            setStepDescription("正在将上期期末库存写入到本期期末库存数量");
            orgs.removeAll(stockOrgList);
            if(orgs.size()>0){

                String lastOrgSqlIn = orgs.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
                String invMthClosingSql=" select * from DCP_INVMTHCLOSING where eid='"+eId+"' " +
                        " and organizationno in ("+lastOrgSqlIn+") " +
                        " and year='"+preYear+"' " +
                        " and month='"+preMonth+"' " ;
                List<Map<String, Object>> invMthClosingList = dao.executeQuerySQL(invMthClosingSql, null);
                if(invMthClosingList.size()>0){
                    for(Map<String, Object> invMthClosing:invMthClosingList){
                        ColumnDataValue dcp_monthClosing = new ColumnDataValue();
                        dcp_monthClosing.add("EID", DataValues.newString(getReq().geteId()));
                        dcp_monthClosing.add("YEAR", DataValues.newString(getReq().getRequest().getYear()));
                        dcp_monthClosing.add("MONTH", DataValues.newString(getReq().getRequest().getPeriod()));
                        dcp_monthClosing.add("ORGANIZATIONNO", DataValues.newString(invMthClosing.get("ORGANIZATIONNO").toString()));
                        dcp_monthClosing.add("PLUNO", DataValues.newString(invMthClosing.get("PLUNO").toString()));
                        dcp_monthClosing.add("FEATURENO", DataValues.newString(invMthClosing.get("FEATURENO").toString()));
                        dcp_monthClosing.add("LOCATION", DataValues.newString(Check.Null(invMthClosing.get("LOCATION").toString())?" ":invMthClosing.get("LOCATION").toString()));
                        dcp_monthClosing.add("WAREHOUSE", DataValues.newString(invMthClosing.get("WAREHOUSE").toString()));
                        dcp_monthClosing.add("BATCH_NO", DataValues.newString(Check.Null(invMthClosing.get("BATCH_NO").toString())?" ":invMthClosing.get("BATCH_NO").toString()));
                        dcp_monthClosing.add("WUNIT", DataValues.newString(invMthClosing.get("WUNIT").toString()));
                        dcp_monthClosing.add("CURINQTY", DataValues.newString(invMthClosing.get("CURINQTY").toString()));
                        dcp_monthClosing.add("CURSALESQTY", DataValues.newString(invMthClosing.get("CURSALESQTY").toString()));
                        dcp_monthClosing.add("CURISSUEQTY", DataValues.newString(invMthClosing.get("CURISSUEQTY").toString()));
                        dcp_monthClosing.add("CURTRANSFERQTY", DataValues.newString(invMthClosing.get("CURTRANSFERQTY").toString()));
                        dcp_monthClosing.add("CURADJQTY", DataValues.newString(invMthClosing.get("CURADJQTY").toString()));
                        dcp_monthClosing.add("ENDQTY", DataValues.newString(invMthClosing.get("ENDQTY").toString()));
                        dcp_monthClosing.add("REF_CURINQTY", DataValues.newString(invMthClosing.get("REF_CURINQTY").toString()));
                        dcp_monthClosing.add("REF_CURSALEQTY", DataValues.newString(invMthClosing.get("REF_CURSALEQTY").toString()));
                        dcp_monthClosing.add("REF_CURISSUEQTY", DataValues.newString(invMthClosing.get("REF_CURISSUEQTY").toString()));
                        dcp_monthClosing.add("REF_CURTRANSFERQTY", DataValues.newString(invMthClosing.get("REF_CURTRANSFERQTY").toString()));
                        dcp_monthClosing.add("REF_CURAJDQTY", DataValues.newString(invMthClosing.get("REF_CURAJDQTY").toString()));
                        dcp_monthClosing.add("REF_ENDQTY", DataValues.newString(invMthClosing.get("REF_ENDQTY").toString()));
                        dcp_monthClosing.add("CREATEBY", DataValues.newString(getReq().getEmployeeNo()));
                        dcp_monthClosing.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                        dcp_monthClosing.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

                        this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("DCP_INVMTHCLOSING",dcp_monthClosing)));

                    }
                }
            }

        }
        else{
            //查不到组织
            incStep();//2
            incStep();//3
            incStep();//4
        }






        incStep();//5
        setStepDescription("正在进行数据持久化");
        dao.useTransactionProcessData(this.getPData());

        incStep();//6
        setStepDescription("执行成功！");
    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void afterRun() {

    }

    @Data
    public class MonthClosingDetail{
        private String eId;
        private String organizationNo;
        private String pluNo;
        private String featureNo;
        private String location;
        private String batchNo;
        private String warehouse;
        private String baseUnit;
    }

}
