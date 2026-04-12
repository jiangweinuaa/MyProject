package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettlementProcessReq;
import com.dsc.spos.json.cust.res.DCP_InterSettlementProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.dataaux.InterSettlementProcessData;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_InterSettlementProcess extends SPosAdvanceService<DCP_InterSettlementProcessReq, DCP_InterSettlementProcessRes> {

    //useless
    @Getter
    public enum InterTransType {
        INTER_TRANS_TYPE_11("11", "采购入库"),
        INTER_TRANS_TYPE_12("11", "采购仓退"),
        INTER_TRANS_TYPE_21("21", "销售出货"),
        INTER_TRANS_TYPE_22("22", "销售退货"),
        INTER_TRANS_TYPE_31("31", "调拨出货"),
        INTER_TRANS_TYPE_32("32", "调拨入库"),
        INTER_TRANS_TYPE_33("33", "退仓出库"),
        INTER_TRANS_TYPE_34("34", "退仓入库");

        InterTransType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private String type;
        private String name;
    }

    @Override
    protected void processDUID(DCP_InterSettlementProcessReq req, DCP_InterSettlementProcessRes res) throws Exception {

//        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> qData = doQueryData(getQueryRouteSql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }
        Map<String, Boolean> distinct = new HashMap<>();
        distinct.put("EID", true);
        distinct.put("BILLNO", true);

        List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);
        InterSettlementProcessData ispd = new InterSettlementProcessData();
        for (Map<String, Object> oneData : master) {

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", oneData.get("EID"));
            condition.put("BILLNO", oneData.get("BILLNO"));

            List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);

            ispd.relationShipWithInterSettleRoute(this.pData, detail);

            String eid = oneData.get("EID").toString();
            String billNo = oneData.get("BILLNO").toString();

            //修改原单isCreate
            ColumnDataValue uptCondition = new ColumnDataValue();
            ColumnDataValue uptValue = new ColumnDataValue();

            uptCondition.add("EID", DataValues.newString(eid));
            uptCondition.add("BILLNO", DataValues.newString(billNo));
//                    uptCondition.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
//                    uptCondition.add("SHOPID", DataValues.newString(shopId));

            uptValue.add("ISCREATE", DataValues.newString("Y"));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLE_DETAIL", uptCondition, uptValue)));


        }


//        Map<String, Boolean> distinct = new HashMap<>();
//        distinct.put("EID", true);
//        distinct.put("STOCKINNO", true);
//        List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);
//
//        String stockInNo = "";
//        String docType = req.getRequest().getDocType();
//
//        for (Map<String, Object> map : master) {
//            stockInNo = map.get("STOCKINNO").toString();
//            docType = map.get("DOC_TYPE").toString();
//            Map<String, Object> con = new HashMap<>();
//            con.put("EID", map.get("EID"));
//            con.put("STOCKINNO", map.get("STOCKINNO"));
//
//            List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, con, true);
//            String outCorp = map.get("OUTCORP").toString();
//            String inCorp = map.get("INCORP").toString();
//            List<Map<String, Object>> issData = doQueryData(getQueryInterSettSetting(req, docType, outCorp, inCorp), null);
//
//            if (CollectionUtils.isNotEmpty(issData)) {
//
//                String relationship = issData.get(0).get("RELATIONSHIP").toString();
//                if ("1".equals(relationship)) {
//
//                    ispd.relationShip1Process(this.pData, detail, issData, 1);
//
////                    relationShip1Process(detail, 1);
//                } else {
//                    ispd.relationShip2Process(this.pData, detail, issData);
//
////                    relationShip2Process(detail, issData);
//                }
//            } else {
//                ispd.relationShip1Process(this.pData, detail, null, 1);
//
//            }

//            //修改原单isCreate
//            ColumnDataValue condition = new ColumnDataValue();
//            ColumnDataValue dcp_stockIn = new ColumnDataValue();
//
//            condition.add("EID", DataValues.newString(map.get("EID").toString()));
//            condition.add("STOCKINNO", DataValues.newString(map.get("STOCKINNO").toString()));
//            condition.add("ORGANIZATIONNO", DataValues.newString(map.get("ORGANIZATIONNO").toString()));
//            condition.add("SHOPID", DataValues.newString(map.get("SHOPID").toString()));
//
//            dcp_stockIn.add("ISCREATE", DataValues.newString("Y"));
//
//            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKIN", condition, dcp_stockIn)));

//        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }


    /*
    //两角关系新增
    protected void relationShip1Process(List<Map<String, Object>> detail, int type) {

        for (Map<String, Object> oneData : detail) {

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
            out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
            out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            out_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
            out_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
            out_dcp_intersettlement.add("AMT", DataValues.newString(
                    BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                            1 + Double.parseDouble(oneData.get("TAXRATE").toString()))
            ));
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
            out_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(0));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", out_dcp_intersettlement)));
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
            in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
            in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
            in_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
            in_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
            in_dcp_intersettlement.add("AMT", DataValues.newString(
                    BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                            1 + Double.parseDouble(oneData.get("TAXRATE").toString()))
            ));
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

            in_dcp_intersettlement.add("STATUS", DataValues.newString(0));
            in_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(0));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", in_dcp_intersettlement)));
        }
    }

    //多角关系新增
    protected void relationShip2Process(List<Map<String, Object>> detail, List<Map<String, Object>> issData) {

        //原单据按两角关系生成
        relationShip1Process(detail, 2);

        for (Map<String, Object> oneRelation : issData) {

            for (Map<String, Object> oneData : detail) {
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
                out_dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("OUTCURRENCY").toString()));
                out_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                out_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                out_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
                out_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
                out_dcp_intersettlement.add("AMT", DataValues.newString(
                        BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                                1 + Double.parseDouble(oneData.get("TAXRATE").toString()))
                ));
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

                out_dcp_intersettlement.add("STATUS", DataValues.newString(0));
                out_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(1));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", out_dcp_intersettlement)));
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
                in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                in_dcp_intersettlement.add("BILLPRICE", DataValues.newString(oneData.get("DISTRIPRICE").toString()));
                in_dcp_intersettlement.add("PRETAXAMT", DataValues.newString(oneData.get("DISTRIAMT").toString()));
                in_dcp_intersettlement.add("AMT", DataValues.newString(
                        BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIAMT").toString()),
                                1 + Double.parseDouble(oneData.get("TAXRATE").toString()))
                ));
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

                in_dcp_intersettlement.add("STATUS", DataValues.newString(0));
                in_dcp_intersettlement.add("SOURCETYPE", DataValues.newString(1));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", in_dcp_intersettlement)));

            }

        }

    }
    */

    protected String getQueryRouteSql(DCP_InterSettlementProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT " +
                        " b.*,das1.CURRNECY " +
                        " ,a.PROCESSNO,a.VERSIONNUM,a.DIRECTION,a.STARTPOINT,a.ENDPOINT ")
                .append(" FROM DCP_INTERSETTLE_ROUTE a ")
                .append(" LEFT JOIN DCP_INTERSETTLE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO and a.PROCESSNO=b.PROCESSNO and a.VERSIONNUM=b.VERSIONNUM  ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING das1 on das1.eid=o1.eid and a.CORP=das1.CORP and das1.ACCTTYPE='1' AND das1.STATUS='100' ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND NVL(b.IS_SETTLEMENT,'N')='Y' ");
        querySql.append(" AND b.STATUS=2 ");

        querySql.append(" AND NVL(b.ISCREATE,'N')='N' ");

        if (StringUtils.isNotEmpty(req.getRequest().getStockInNo())) {
            querySql.append(" AND a.SOURCENO='").append(req.getRequest().getStockInNo()).append("'");
        }

        return querySql.toString();
    }

    protected String getQuerySql(DCP_InterSettlementProcessReq req) throws Exception {
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
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append("  and a.status='2' and a.SHOPID<>a.TRANSFER_SHOP and o1.CORP<>o2.CORP and NVL(a.ISCREATE,'N')='N' ");

        if (StringUtils.isNotEmpty(req.getRequest().getDocType())) {
            querySql.append(" and a.DOC_TYPE ='").append(req.getRequest().getDocType()).append("'");
        } else {
            querySql.append(" and a.DOC_TYPE in ('0','1','5')");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStockInNo())) {
            querySql.append(" AND a.STOCKINNO='").append(req.getRequest().getStockInNo()).append("'");
        }

        querySql.append(" ORDER BY a.STOCKINNO,b.ITEM ");

        return querySql.toString();
    }

    private String getQueryInterSettSetting(DCP_InterSettlementProcessReq req, String docType, String outCorp, String inCorp) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.PROCESSNO,a.BTYPE,a.SUPPLYOBJECT,a.DEMANDOBJECT,a.VERSIONNUM,a.RELATIONSHIP ")
                .append(" ,b.SUPPLYOBJECT SUPPLYOBJECT1,b.DEMANDOBJECT1,o1.CORP INCORP,o2.CORP OUTCORP ")

                .append(" FROM DCP_INTERSETTSETTING a ")
                .append(" LEFT JOIN DCP_INTERSETTSETDETAIL b on a.eid=b.eid and a.PROCESSNO=b.PROCESSNO and a.VERSIONNUM=b.VERSIONNUM ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=b.eid and o1.ORGANIZATIONNO=b.DEMANDOBJECT1 ")
                .append(" LEFT JOIN DCP_ORG o2 on o2.eid=b.eid and o2.ORGANIZATIONNO=b.SUPPLYOBJECT ")

        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
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


    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettlementProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettlementProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettlementProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettlementProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettlementProcessReq> getRequestType() {
        return new TypeToken<DCP_InterSettlementProcessReq>() {
        };
    }

    @Override
    protected DCP_InterSettlementProcessRes getResponseType() {
        return new DCP_InterSettlementProcessRes();
    }


}
