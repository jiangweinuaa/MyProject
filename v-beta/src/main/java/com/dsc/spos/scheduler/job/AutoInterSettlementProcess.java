package com.dsc.spos.scheduler.job;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.service.dataaux.InterSettlementProcessData;
import com.dsc.spos.service.imp.json.DCP_InterSettlementProcess;
import com.dsc.spos.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoInterSettlementProcess extends InitJob {
    Logger logger = LogManager.getLogger(AutoInterSettlementProcess.class.getName());
    static boolean bRun = false;//标记此服务是否正在执行中

    public String doExe() {
        String sReturnInfo = "";
        if (bRun) {
            logger.info("\r\n*********内部交易结算底稿AutoInterSettlementProcess正在执行中,本次调用取消:************\r\n");
            sReturnInfo = "内部交易结算底稿AutoInterSettlementProcess正在执行中！";
            return sReturnInfo;
        }
        bRun = true;
        try {
//            List<Map<String, Object>> qData = doQueryData(getQueryAllSql(), null);
            List<Map<String, Object>> qData = doQueryData(getQueryAllSql2(), null);
            if (CollectionUtils.isNotEmpty(qData)) {

                Map<String, Boolean> distinct = new HashMap<>();
                distinct.put("EID", true);
                distinct.put("BILLNO", true);

                List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);

                InterSettlementProcessData ispd = new InterSettlementProcessData();

                for (Map<String, Object> oneData : master) {
                    try {
                        Map<String, Object> condition = new HashMap<>();
                        condition.put("EID", oneData.get("EID"));
                        condition.put("BILLNO", oneData.get("BILLNO"));

                        List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);

                        List<DataProcessBean> dbs = new ArrayList<>();

                        String eid = oneData.get("EID").toString();
                        String billNo = oneData.get("BILLNO").toString();
//                    String docType = oneData.get("DOC_TYPE").toString();
//                    String stockInNo = oneData.get("STOCKINNO").toString();
//                    String shopId = oneData.get("SHOPID").toString();
//                    String organizationNo = oneData.get("ORGANIZATIONNO").toString();
//                    String outCorp = oneData.get("OUTCORP").toString();
//                    String inCorp = oneData.get("INCORP").toString();
//                    List<Map<String, Object>> issData = doQueryData(getQueryInterSettSetting(eid, docType, outCorp, inCorp), null);

//                    List<Map<String, Object>> detail = doQueryData(getQuerySql(eid, docType, stockInNo, shopId, organizationNo), null);
//                    if (CollectionUtils.isNotEmpty(issData)) {
//
//                        String relationship = issData.get(0).get("RELATIONSHIP").toString();
//                        if ("1".equals(relationship)) {
//                            ispd.relationShip1Process(dbs, detail, issData, 1);
//                        } else {
//                            ispd.relationShip2Process(dbs, detail, issData);
//                        }
//                    } else {
//                        ispd.relationShip1Process(dbs, detail, null, 1);
//                    }

                        ispd.relationShipWithInterSettleRoute(dbs, detail);

                        if (!dbs.isEmpty()) {
                            //修改原单isCreate
                            ColumnDataValue uptCondition = new ColumnDataValue();
                            ColumnDataValue uptValue = new ColumnDataValue();

                            uptCondition.add("EID", DataValues.newString(eid));
                            uptCondition.add("BILLNO", DataValues.newString(billNo));
//                    uptCondition.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
//                    uptCondition.add("SHOPID", DataValues.newString(shopId));

                            uptValue.add("ISCREATE", DataValues.newString("Y"));

                            dbs.add(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLE_DETAIL", uptCondition, uptValue)));

                            StaticInfo.dao.useTransactionProcessData(dbs); //执行到数据库

                        }
                    } catch (Exception e) {
                        //单支单据异常捕获，防止其它单子不执行
                        logger.error("\r\n******AutoInterSettlementProcess报错信息" + e.getMessage() + "\r\n******\r\n");
                        sReturnInfo = "错误信息:" + e.getMessage();
                    }

                }

            }
        } catch (Exception e) {
            logger.error("\r\n******AutoInterSettlementProcess报错信息" + e.getMessage() + "\r\n******\r\n");
            sReturnInfo = "错误信息:" + e.getMessage();
        } finally {
            bRun = false;//
            logger.info("\r\n*********AutoInterSettlementProcess定时调用End:************\r\n");
        }
        return sReturnInfo;
    }

    /*
    //两角关系新增
    protected void relationShip1Process(List<DataProcessBean> dbs, List<Map<String, Object>> detail, List<Map<String, Object>> issData, int type) throws Exception {
        TaxUtils taxUtils = new TaxUtils();

        String processNo;
        if (CollectionUtils.isEmpty(issData)) {
            processNo = "";
        } else {
            processNo = issData.get(0).get("PROCESSNO").toString();
        }
        for (Map<String, Object> oneData : detail) {

            String taxRate = StringUtils.toString(oneData.get("TAXRATE"), "");
            TaxUtils.Tax tax = null;
            if (StringUtils.isEmpty(taxRate)) {
                tax = taxUtils.getTax(
                        oneData.get("EID").toString(),
                        oneData.get("TRANSFER_SHOP").toString(),
                        oneData.get("PLUNO").toString()
                );
            }
            ColumnDataValue out_dcp_intersettlement = new ColumnDataValue();

            String docType = oneData.get("DOC_TYPE").toString();

            String accountDate = DateFormatUtils.getPlainDate(oneData.get("ACCOUNT_DATE").toString());
            out_dcp_intersettlement.add("EID", DataValues.newString(oneData.get("EID").toString()));

            out_dcp_intersettlement.add("YEAR", DataValues.newString(accountDate.substring(0, 4)));
            out_dcp_intersettlement.add("MONTH", DataValues.newString(accountDate.substring(4, 6)));

            out_dcp_intersettlement.add("BTYPE", DataValues.newString(docTypeToBType(docType)));
            out_dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(docTypeTonInterTransType(docType, type, -1)));
            out_dcp_intersettlement.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(accountDate)));

            out_dcp_intersettlement.add("BILLNO", DataValues.newString(oneData.get("STOCKINNO").toString()));
            out_dcp_intersettlement.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
            out_dcp_intersettlement.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
            out_dcp_intersettlement.add("FEATURENO", DataValues.newString(oneData.get("PLUNO").toString()));
            out_dcp_intersettlement.add("PRICEUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
            out_dcp_intersettlement.add("BILLQTY", DataValues.newString(oneData.get("PQTY").toString()));
//            out_dcp_intersettlement.add("FEE", DataValues.newString(oneData.get("PQTY").toString()));
            out_dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("OUTCURRENCY").toString()));

            out_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
            out_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
//            out_dcp_intersettlement.add("AMT", DataValues.newString(oneData.get("AMT").toString()));

            double amt = 0;
            if (null != tax) {
                amt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                        1 + tax.getTaxRate() / 100);

                out_dcp_intersettlement.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                out_dcp_intersettlement.add("TAXRATE", DataValues.newString(tax.getTaxRate()));

            } else {
                if (StringUtils.isEmpty(StringUtils.toString(oneData.get("TAXRATE"), ""))) {
                    amt = Double.parseDouble(oneData.get("AMT").toString());

                } else {
                    amt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                            1 + Double.parseDouble(oneData.get("TAXRATE").toString()) / 100);

                }

                out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            }
            out_dcp_intersettlement.add("AMT", DataValues.newString(amt));
            out_dcp_intersettlement.add("DEMANDORG", DataValues.newString(oneData.get("SHOPID").toString()));
            out_dcp_intersettlement.add("DEMANDCORP", DataValues.newString(oneData.get("INCORP").toString()));
            out_dcp_intersettlement.add("SUPPLYORG", DataValues.newString(oneData.get("TRANSFER_SHOP").toString()));
            out_dcp_intersettlement.add("SUPPLYCORP", DataValues.newString(oneData.get("OUTCORP").toString()));
            out_dcp_intersettlement.add("DIRECTION", DataValues.newString(-1));

            out_dcp_intersettlement.add("OUTINDIRECTION", DataValues.newString(-1));
            out_dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneData.get("TRANSFER_SHOP").toString()));
            out_dcp_intersettlement.add("CORP", DataValues.newString(oneData.get("OUTCORP").toString()));
            out_dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneData.get("SHOPID").toString()));
            out_dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneData.get("INCORP").toString()));

            out_dcp_intersettlement.add("STATUS", DataValues.newString(0));
            out_dcp_intersettlement.add("PROCESSNO", DataValues.newString(processNo));

            out_dcp_intersettlement.add("UNSETTAMT", DataValues.newString(amt));
            out_dcp_intersettlement.add("UNPOSTEDAMT", DataValues.newString(amt));
            out_dcp_intersettlement.add("SETTLEDAMT", DataValues.newString(0));
            out_dcp_intersettlement.add("POSTEDAMT", DataValues.newString(0));

            out_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(0));

            dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", out_dcp_intersettlement)));
            ColumnDataValue in_dcp_intersettlement = new ColumnDataValue();

            in_dcp_intersettlement.add("EID", DataValues.newString(oneData.get("EID").toString()));
            in_dcp_intersettlement.add("YEAR", DataValues.newString(accountDate.substring(0, 4)));
            in_dcp_intersettlement.add("MONTH", DataValues.newString(accountDate.substring(4, 6)));

            in_dcp_intersettlement.add("BTYPE", DataValues.newString(docTypeToBType(docType)));
            in_dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(docTypeTonInterTransType(docType, type, 1)));
            in_dcp_intersettlement.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(accountDate)));

            in_dcp_intersettlement.add("BILLNO", DataValues.newString(oneData.get("STOCKINNO").toString()));
            in_dcp_intersettlement.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
            in_dcp_intersettlement.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
            in_dcp_intersettlement.add("FEATURENO", DataValues.newString(oneData.get("PLUNO").toString()));
            in_dcp_intersettlement.add("PRICEUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
            in_dcp_intersettlement.add("BILLQTY", DataValues.newString(oneData.get("PQTY").toString()));
//            in_dcp_intersettlement.add("FEE", DataValues.newString(oneData.get("PQTY").toString()));
            in_dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("INCURRENCY").toString()));
//            in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
//            in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            in_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
            in_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
//            in_dcp_intersettlement.add("AMT", DataValues.newString(oneData.get("AMT").toString()));
            if (null != tax) {

                in_dcp_intersettlement.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                in_dcp_intersettlement.add("TAXRATE", DataValues.newString(tax.getTaxRate()));
            } else {


                out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            }
            in_dcp_intersettlement.add("AMT", DataValues.newString(amt));
            in_dcp_intersettlement.add("DEMANDORG", DataValues.newString(oneData.get("SHOPID").toString()));
            in_dcp_intersettlement.add("DEMANDCORP", DataValues.newString(oneData.get("INCORP").toString()));
            in_dcp_intersettlement.add("SUPPLYORG", DataValues.newString(oneData.get("TRANSFER_SHOP").toString()));
            in_dcp_intersettlement.add("SUPPLYCORP", DataValues.newString(oneData.get("OUTCORP").toString()));
            in_dcp_intersettlement.add("DIRECTION", DataValues.newString(-1));

            in_dcp_intersettlement.add("OUTINDIRECTION", DataValues.newString(1));
            in_dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneData.get("SHOPID").toString()));
            in_dcp_intersettlement.add("CORP", DataValues.newString(oneData.get("INCORP").toString()));
            in_dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneData.get("TRANSFER_SHOP").toString()));
            in_dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneData.get("OUTCORP").toString()));
            in_dcp_intersettlement.add("PROCESSNO", DataValues.newString(processNo));
            in_dcp_intersettlement.add("UNSETTAMT", DataValues.newString(amt));
            in_dcp_intersettlement.add("SETTLEDAMT", DataValues.newString(0));
            in_dcp_intersettlement.add("UNPOSTEDAMT", DataValues.newString(amt));
            in_dcp_intersettlement.add("POSTEDAMT", DataValues.newString(0));


            in_dcp_intersettlement.add("STATUS", DataValues.newString(0));
            in_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(0));

            dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", in_dcp_intersettlement)));
        }
    }

    //多角关系新增
    protected void relationShip2Process(List<DataProcessBean> dbs, List<Map<String, Object>> detail, List<Map<String, Object>> issData) throws Exception {
        TaxUtils taxUtils = new TaxUtils();

        //原单据按两角关系生成
        relationShip1Process(dbs, detail, issData, 2);

        for (Map<String, Object> oneRelation : issData) {

            for (Map<String, Object> oneData : detail) {
                String taxRate = StringUtils.toString(oneData.get("TAXRATE"), "");
                TaxUtils.Tax tax = null;
                if (StringUtils.isEmpty(taxRate)) {
                    tax = taxUtils.getTax(
                            oneData.get("EID").toString(),
                            oneData.get("TRANSFER_SHOP").toString(),
                            oneData.get("PLUNO").toString()
                    );
                }
                ColumnDataValue out_dcp_intersettlement = new ColumnDataValue();

                String docType = oneData.get("DOC_TYPE").toString();

                String accountDate = DateFormatUtils.getPlainDate(oneData.get("ACCOUNT_DATE").toString());
                out_dcp_intersettlement.add("EID", DataValues.newString(oneData.get("EID").toString()));

                out_dcp_intersettlement.add("YEAR", DataValues.newString(accountDate.substring(0, 4)));
                out_dcp_intersettlement.add("MONTH", DataValues.newString(accountDate.substring(4, 6)));

                out_dcp_intersettlement.add("BTYPE", DataValues.newString(docTypeToBType(docType)));
                out_dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(docTypeTonInterTransType(docType, 2, -1)));
                out_dcp_intersettlement.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(accountDate)));

                out_dcp_intersettlement.add("BILLNO", DataValues.newString(oneData.get("STOCKINNO").toString()));
                out_dcp_intersettlement.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
                out_dcp_intersettlement.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
                out_dcp_intersettlement.add("FEATURENO", DataValues.newString(oneData.get("PLUNO").toString()));
                out_dcp_intersettlement.add("PRICEUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
                out_dcp_intersettlement.add("BILLQTY", DataValues.newString(oneData.get("PQTY").toString()));
//            out_dcp_intersettlement.add("FEE", DataValues.newString(oneData.get("PQTY").toString()));
                out_dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("INCURRENCY").toString()));
//                out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
//                out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                out_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
                out_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
                double amt = 0;
                if (null != tax) {
                    amt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                            1 + tax.getTaxRate() / 100);

                    out_dcp_intersettlement.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                    out_dcp_intersettlement.add("TAXRATE", DataValues.newString(tax.getTaxRate()));

                } else {
                    if (StringUtils.isEmpty(StringUtils.toString(oneData.get("TAXRATE"), ""))) {
                        amt = Double.parseDouble(oneData.get("AMT").toString());

                    } else {
                        amt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                                1 + Double.parseDouble(oneData.get("TAXRATE").toString()) / 100);

                    }

                    out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                    out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                }

                out_dcp_intersettlement.add("AMT", DataValues.newString(amt));
                out_dcp_intersettlement.add("DEMANDORG", DataValues.newString(oneRelation.get("DEMANDOBJECT1").toString()));
                out_dcp_intersettlement.add("DEMANDCORP", DataValues.newString(oneRelation.get("INCORP").toString()));
                out_dcp_intersettlement.add("SUPPLYORG", DataValues.newString(oneRelation.get("SUPPLYOBJECT").toString()));
                out_dcp_intersettlement.add("SUPPLYCORP", DataValues.newString(oneRelation.get("OUTCORP").toString()));
                out_dcp_intersettlement.add("DIRECTION", DataValues.newString(-1));

                out_dcp_intersettlement.add("OUTINDIRECTION", DataValues.newString(-1));

                out_dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneRelation.get("SUPPLYOBJECT1").toString()));
                out_dcp_intersettlement.add("CORP", DataValues.newString(oneRelation.get("OUTCORP").toString()));
                out_dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneRelation.get("DEMANDOBJECT1").toString()));
                out_dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneRelation.get("INCORP").toString()));
                out_dcp_intersettlement.add("PROCESSNO", DataValues.newString(oneRelation.get("PROCESSNO").toString()));
                out_dcp_intersettlement.add("UNSETTAMT", DataValues.newString(amt));
                out_dcp_intersettlement.add("SETTLEDAMT", DataValues.newString(0));
                out_dcp_intersettlement.add("UNPOSTEDAMT", DataValues.newString(amt));
                out_dcp_intersettlement.add("POSTEDAMT", DataValues.newString(0));

                out_dcp_intersettlement.add("STATUS", DataValues.newString(0));
                out_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(1));

                dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", out_dcp_intersettlement)));
                ColumnDataValue in_dcp_intersettlement = new ColumnDataValue();

                in_dcp_intersettlement.add("EID", DataValues.newString(oneData.get("EID").toString()));
                in_dcp_intersettlement.add("YEAR", DataValues.newString(accountDate.substring(0, 4)));
                in_dcp_intersettlement.add("MONTH", DataValues.newString(accountDate.substring(4, 6)));

                in_dcp_intersettlement.add("BTYPE", DataValues.newString(docTypeToBType(docType)));
                in_dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(docTypeTonInterTransType(docType, 2, 1)));
                in_dcp_intersettlement.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(accountDate)));

                in_dcp_intersettlement.add("BILLNO", DataValues.newString(oneData.get("STOCKINNO").toString()));
                in_dcp_intersettlement.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
                in_dcp_intersettlement.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
                in_dcp_intersettlement.add("FEATURENO", DataValues.newString(oneData.get("PLUNO").toString()));
                in_dcp_intersettlement.add("PRICEUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
                in_dcp_intersettlement.add("BILLQTY", DataValues.newString(oneData.get("PQTY").toString()));
//            in_dcp_intersettlement.add("FEE", DataValues.newString(oneData.get("PQTY").toString()));
                in_dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("INCURRENCY").toString()));
//                in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
//                in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                in_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
                in_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
//                in_dcp_intersettlement.add("AMT", DataValues.newString(oneData.get("AMT").toString()));
                if (null != tax) {
                    in_dcp_intersettlement.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
                    in_dcp_intersettlement.add("TAXRATE", DataValues.newString(tax.getTaxRate()));
                } else {
                    in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                    in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));

                }

                in_dcp_intersettlement.add("AMT", DataValues.newString(amt));
                in_dcp_intersettlement.add("DEMANDORG", DataValues.newString(oneRelation.get("DEMANDOBJECT1").toString()));
                in_dcp_intersettlement.add("DEMANDCORP", DataValues.newString(oneRelation.get("INCORP").toString()));
                in_dcp_intersettlement.add("SUPPLYORG", DataValues.newString(oneRelation.get("SUPPLYOBJECT1").toString()));
                in_dcp_intersettlement.add("SUPPLYCORP", DataValues.newString(oneRelation.get("OUTCORP").toString()));
                in_dcp_intersettlement.add("DIRECTION", DataValues.newString(-1));

                in_dcp_intersettlement.add("OUTINDIRECTION", DataValues.newString(1));
                in_dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneRelation.get("DEMANDOBJECT1").toString()));
                in_dcp_intersettlement.add("CORP", DataValues.newString(oneRelation.get("INCORP").toString()));
                in_dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneRelation.get("SUPPLYOBJECT1").toString()));
                in_dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneRelation.get("OUTCORP").toString()));
                in_dcp_intersettlement.add("PROCESSNO", DataValues.newString(oneRelation.get("PROCESSNO").toString()));

                in_dcp_intersettlement.add("UNSETTAMT", DataValues.newString(amt));
                in_dcp_intersettlement.add("SETTLEDAMT", DataValues.newString(0));
                in_dcp_intersettlement.add("UNPOSTEDAMT", DataValues.newString(amt));
                in_dcp_intersettlement.add("POSTEDAMT", DataValues.newString(0));


                in_dcp_intersettlement.add("STATUS", DataValues.newString(0));
                in_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(1));

                dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", in_dcp_intersettlement)));
            }

        }
    }
     */

    private String getQueryAllSql2() throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT " +
                        " b.*,das1.CURRENCY " +
                        " ,a.PROCESSNO,a.VERSIONNUM,a.DIRECTION,a.STARTPOINT,a.ENDPOINT,a.SORTID " +
                        " ,a.BDATE,o1.CORP STARTPOINTCORP,o2.CORP ENDPOINTCORP,o3.IC_COST_WAREHOUSE " +
                        " ,dg.BASEUNIT,gu.UNITRATIO ")
                .append(" FROM DCP_INTERSETTLE_ROUTE a ")
                .append(" LEFT JOIN DCP_INTERSETTLE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO and a.PROCESSNO=b.PROCESSNO and a.VERSIONNUM=b.VERSIONNUM  ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING das1 on das1.eid=a.eid and a.CORP=das1.CORP and das1.ACCTTYPE='1' AND das1.STATUS='100' ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO=a.STARTPOINT ")
                .append(" LEFT JOIN DCP_ORG o2 on o2.eid=a.eid and o2.ORGANIZATIONNO=a.ENDPOINT ")
                .append(" LEFT JOIN DCP_ORG o3 on o3.eid=a.eid and o3.ORGANIZATIONNO=a.CORP ")
                .append(" LEFT JOIN DCP_GOODS dg on dg.eid=b.eid and dg.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_UNIT gu on gu.eid=b.eid and gu.PLUNO=b.PLUNO and gu.UNIT=dg.BASEUNIT and gu.OUNIT=b.PUNIT  ")
        ;
        querySql.append(" WHERE 1=1 ");

        querySql.append(" AND NVL(b.IS_SETTLEMENT,'N')='Y' ");
        querySql.append(" AND b.STATUS=2 ");

        querySql.append(" AND NVL(b.ISCREATE,'N')='N' ");

//        if (StringUtils.isNotEmpty(req.getRequest().getStockInNo())) {
//            querySql.append(" AND a.SOURCENO='").append(req.getRequest().getStockInNo()).append("'");
//        }

        return querySql.toString();

    }

    private String getQueryAllSql() throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.EID,a.SHOPID,a.ORGANIZATIONNO,a.STOCKINNO,a.DOC_TYPE,a.ACCOUNT_DATE,a.STATUS,a.ISCREATE,o1.CORP INCORP,o2.CORP OUTCORP  ")
                .append(" FROM DCP_STOCKIN a ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO=a.SHOPID ")
                .append(" LEFT JOIN DCP_ORG o2 on o2.eid=a.eid and o2.ORGANIZATIONNO=a.TRANSFER_SHOP ")
        ;
//        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" WHERE 1=1 ");
        querySql.append(" and a.DOC_TYPE in ('0','1','5') and a.status='2' and a.SHOPID<>a.TRANSFER_SHOP" +
                " and NVL(a.ISCREATE,'N')='N' ");
        querySql.append(" and o1.CORP<>o2.CORP ");

        querySql.append(" ORDER BY STOCKINNO ");

        return querySql.toString();
    }


    protected String getQueryRouteSql(String stockInNo) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT " +
                        " a.* ")
                .append(" FROM DCP_INTERSETTLE_ROUTE a ")
                .append(" LEFT JOIN DCP_INTERSETTLE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO and a.PROCESSNO=b.PROCESSNO and a.VERSIONNUM=b.VERSIONNUM  ")
        ;
//        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" WHERE 1=1 ");
        querySql.append(" AND a.BILLNO='").append(stockInNo).append("'");
        return querySql.toString();
    }

    protected String getQuerySql(String eid, String docType, String stockInNo, String shopId, String orgNo) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT " +
                        " a.DOC_TYPE,a.ACCOUNT_DATE,a.BDATE,a.LOAD_DOCNO" +
                        " ,a.SHOPID,a.TRANSFER_SHOP" +
                        " ,o1.CORP INCORP,o2.CORP OUTCORP,o3.CORP,b.* ")
                .append(" ,das1.CURRENCY INCURRENCY,das2.CURRENCY OUTCURRENCY ")
                .append(" ,dg1.TAXCODE,tc1.TAXRATE ")
                .append(" FROM DCP_STOCKIN a ")
                .append(" INNER JOIN DCP_STOCKIN_DETAIL b on a.eid=b.eid and a.STOCKINNO=b.STOCKINNO AND a.SHOPID=b.SHOPID AND a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO=a.SHOPID ")
                .append(" LEFT JOIN DCP_ORG o2 on o2.eid=a.eid and o2.ORGANIZATIONNO=a.TRANSFER_SHOP ")
                .append(" LEFT JOIN DCP_ORG o3 on o3.eid=a.eid and o3.ORGANIZATIONNO=a.ORGANIZATIONNO ")
                .append(" LEFT JOIN DCP_GOODS dg1 on dg1.eid=b.eid and dg1.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_TAXCATEGORY tc1 on tc1.eid=dg1.eid and tc1.TAXCODE=dg1.TAXCODE ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING das1 on das1.eid=o1.eid and o1.CORP=das1.CORP and das1.ACCTTYPE='1' AND das1.STATUS='100' ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING das2 on das1.eid=o2.eid and o2.CORP=das2.CORP and das2.ACCTTYPE='1' AND das2.STATUS='100' ")
        ;
        querySql.append(" WHERE a.EID='").append(eid).append("'");

        querySql.append("  and a.status='2' and a.SHOPID<>a.TRANSFER_SHOP and o1.CORP<>o2.CORP and NVL(a.ISCREATE,'N')='N' ");

        if (StringUtils.isNotEmpty(docType)) {
            querySql.append(" and a.DOC_TYPE ='").append(docType).append("'");
        } else {
            querySql.append(" and a.DOC_TYPE in ('0','1','5')");
        }

        if (StringUtils.isNotEmpty(shopId)) {
            querySql.append(" AND a.SHOPID='").append(shopId).append("'");
        }
        if (StringUtils.isNotEmpty(orgNo)) {
            querySql.append(" AND a.ORGANIZATIONNO='").append(orgNo).append("'");
        }

        if (StringUtils.isNotEmpty(stockInNo)) {
            querySql.append(" AND a.STOCKINNO='").append(stockInNo).append("'");
        }


        querySql.append(" ORDER BY b.ITEM ");

        return querySql.toString();
    }

    private String getQueryInterSettSetting(String eid, String docType, String outCorp, String inCorp) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.PROCESSNO,a.BTYPE,a.SUPPLYOBJECT,a.DEMANDOBJECT,a.VERSIONNUM,a.RELATIONSHIP ")
                .append(" ,b.SUPPLYOBJECT SUPPLYOBJECT1,b.DEMANDOBJECT1,o1.CORP INCORP,o2.CORP OUTCORP ")
                .append(" FROM DCP_INTERSETTSETTING a ")
                .append(" LEFT JOIN DCP_INTERSETTSETDETAIL b on a.eid=b.eid and a.PROCESSNO=b.PROCESSNO and a.VERSIONNUM=b.VERSIONNUM ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=b.eid and o1.ORGANIZATIONNO=b.DEMANDOBJECT1 ")
                .append(" LEFT JOIN DCP_ORG o2 on o2.eid=b.eid and o2.ORGANIZATIONNO=b.SUPPLYOBJECT ")
        ;

        querySql.append(" WHERE a.EID='").append(eid).append("'");
        querySql.append(" AND a.SUPPLYOBJECT='").append(outCorp).append("'");
        querySql.append(" AND a.DEMANDOBJECT='").append(inCorp).append("'");

        String bType = docTypeToBType(docType);
        if (StringUtils.isNotEmpty(bType)) {
            querySql.append(" AND a.BTYPE='").append(bType).append("'");
        }

        return querySql.toString();
    }

    private String docTypeToBType(String docType) {

        String bType = "";
        if ("0".equals(docType)) {
            bType = "1";
        } else if ("1".equals(docType)) {
            bType = "3";
        } else if ("5".equals(docType)) {
            bType = "3";
        }
        return bType;
    }


    private String docTypeTonInterTransType(String docType, int type, int dir) {
        String bType = "";
        if (dir > 0) {
            if ("0".equals(docType)) {
                if (type == 1) {
                    bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_22.getType();
                } else {
                    bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_11.getType();
                }

            } else if ("1".equals(docType)) {
                bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_32.getType();
            } else if ("5".equals(docType)) {
                bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_34.getType();
            }
        } else {
            if ("0".equals(docType)) {
                if (type == 1) {
                    bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_21.getType();
                } else {
                    bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_12.getType();
                }

            } else if ("1".equals(docType)) {
                bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_31.getType();
            } else if ("5".equals(docType)) {
                bType = DCP_InterSettlementProcess.InterTransType.INTER_TRANS_TYPE_33.getType();
            }
        }

        return bType;
    }

}
