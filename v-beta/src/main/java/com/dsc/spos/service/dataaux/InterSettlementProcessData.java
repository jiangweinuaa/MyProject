package com.dsc.spos.service.dataaux;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.service.imp.json.DCP_InterSettlementProcess;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class InterSettlementProcessData {

    @Data
    @EqualsAndHashCode(callSuper = false)
    public class Route {
        int sortId;
        String startPoint;
        String endPoint;
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

    //按照结算路径生成结算底稿
    public void relationShipWithInterSettleRoute(List<DataProcessBean> dbs, List<Map<String, Object>> detail) throws Exception {
        TaxUtils taxUtils = new TaxUtils();

        List<Route> routeList = new ArrayList<>();
        for (Map<String, Object> oneData : detail) {
            Route route = new Route();
            route.setSortId(Integer.parseInt(oneData.get("SORTID").toString()));
            route.setStartPoint(oneData.get("STARTPOINT").toString());
            route.setStartPoint(oneData.get("ENDPOINT").toString());
            if (!routeList.contains(route)) {
                routeList.add(route);
            }
        }
        routeList = routeList.stream().sorted(Comparator.comparing(x -> x.sortId)).collect(Collectors.toList());

        for (Map<String, Object> oneData : detail) {

            String taxRate = StringUtils.toString(oneData.get("TAXRATE"), "");
            TaxUtils.Tax tax = null;
            if (StringUtils.isEmpty(taxRate)) {
                tax = taxUtils.getTax(
                        oneData.get("EID").toString(),
                        oneData.get("CORP").toString(),
                        oneData.get("PLUNO").toString()
                );
            }
            int inOutDir = getOutInDirection(oneData.get("BTYPE").toString());

            int type;

            //正向流程
            if (1 == Integer.parseInt(oneData.get("SORTID").toString())
                    || routeList.size() == Integer.parseInt(oneData.get("SORTID").toString())) {
                //原始单
                type = 0;
            } else {
                //联动单
                type = 1;
            }

            //写入单据信息
            insertIntoInterSettlement(tax,
                    type,
                    inOutDir * -1,
                    dbs,
                    oneData
            );

            //写入库存异动
            insertIntoStockChange(type, inOutDir, dbs, oneData);

            //写入单据信息
            insertIntoInterSettlement(tax,
                    type,
                    inOutDir,
                    dbs,
                    oneData
            );
            //写入库存异动
            insertIntoStockChange(type, inOutDir, dbs, oneData);


        }
    }

    private int getOutInDirection(String bType) {
        int dir = 1;
        if ("64".equals(bType) || "66".equals(bType)) {
            dir = -1;
        }

        return dir;
    }

    private String getReverseBType(String bType) {

        String returnType = "";
        if ("63".equals(bType)) {
            returnType = "65";
        } else if ("64".equals(bType)) {
            returnType = "66";
        } else if ("65".equals(bType)) {
            returnType = "63";
        } else if ("66".equals(bType)) {
            returnType = "64";
        }

        return returnType;
    }

    private void insertIntoInterSettlement(TaxUtils.Tax tax, int type, int outInDirection, List<DataProcessBean> dbs, Map<String, Object> oneData) {

        //todo 价格根据结算路径重新取价
        double preTaxamt = Double.parseDouble((oneData.get("PRETAXAMT").toString()));
        double amt = BigDecimalUtils.add(Double.parseDouble((oneData.get("PRETAXAMT").toString())), Double.parseDouble(oneData.get("TAXAMT").toString()));
        double billPrice = BigDecimalUtils.div(amt, Double.parseDouble(oneData.get("PQTY").toString()));

        ColumnDataValue dcp_intersettlement = new ColumnDataValue();

        String accountDate = DateFormatUtils.getPlainDate(oneData.get("BDATE").toString());
        dcp_intersettlement.add("EID", DataValues.newString(oneData.get("EID").toString()));

        dcp_intersettlement.add("YEAR", DataValues.newString(accountDate.substring(0, 4)));
        dcp_intersettlement.add("MONTH", DataValues.newString(accountDate.substring(4, 6)));

        if (outInDirection < 0) {
            String bType = getReverseBType(oneData.get("BTYPE").toString());
            dcp_intersettlement.add("BTYPE", DataValues.newString(bType));
            dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(bType));
        } else {
            dcp_intersettlement.add("BTYPE", DataValues.newString(oneData.get("BTYPE").toString()));
            dcp_intersettlement.add("INTERTRANSTYPE", DataValues.newString(oneData.get("BTYPE").toString()));
        }

        dcp_intersettlement.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(accountDate)));

        dcp_intersettlement.add("BILLNO", DataValues.newString(oneData.get("BILLNO").toString()));
        dcp_intersettlement.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
        dcp_intersettlement.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
        dcp_intersettlement.add("FEATURENO", DataValues.newString(oneData.get("FEATURENO").toString()));
        dcp_intersettlement.add("PRICEUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
        dcp_intersettlement.add("BILLQTY", DataValues.newString(oneData.get("PQTY").toString()));
//            dcp_intersettlement.add("FEE", DataValues.newString(oneData.get("PQTY").toString()));
        dcp_intersettlement.add("CURRENCY", DataValues.newString(oneData.get("CURRENCY").toString()));

        dcp_intersettlement.add("PRETAXAMT", DataValues.newString(preTaxamt));

        dcp_intersettlement.add("BILLPRICE", DataValues.newString(billPrice));

        if (null != tax) {
            dcp_intersettlement.add("TAXCODE", DataValues.newString(tax.getTaxCode()));
            dcp_intersettlement.add("TAXRATE", DataValues.newString(tax.getTaxRate()));
        } else {
            dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
            dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
        }
        dcp_intersettlement.add("AMT", DataValues.newString(amt));

        if (outInDirection < 0) {
            //出库取供货法人
            dcp_intersettlement.add("CORP", DataValues.newString(oneData.get("STARTPOINTCORP").toString()));
            dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneData.get("STARTPOINT").toString()));
            //出库取需求法人
            dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneData.get("ENDPOINT").toString()));
            dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneData.get("ENDPOINTCORP").toString()));
        } else {
            //入库取需求法人
            dcp_intersettlement.add("CORP", DataValues.newString(oneData.get("ENDPOINTCORP").toString()));
            dcp_intersettlement.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ENDPOINT").toString()));
            //入库取供货法人
            dcp_intersettlement.add("INTERTRADEORG", DataValues.newString(oneData.get("STARTPOINT").toString()));
            dcp_intersettlement.add("INTERTRADECORP", DataValues.newString(oneData.get("STARTPOINTCORP").toString()));
        }

        dcp_intersettlement.add("SUPPLYORG", DataValues.newString(oneData.get("STARTPOINT").toString()));
        dcp_intersettlement.add("SUPPLYCORP", DataValues.newString(oneData.get("STARTPOINTCORP").toString()));
        dcp_intersettlement.add("DEMANDORG", DataValues.newString(oneData.get("ENDPOINT").toString()));
        dcp_intersettlement.add("DEMANDCORP", DataValues.newString(oneData.get("ENDPOINTCORP").toString()));

        dcp_intersettlement.add("DIRECTION", DataValues.newString(oneData.get("DIRECTION").toString()));
        dcp_intersettlement.add("OUTINDIRECTION", DataValues.newString(outInDirection));


        dcp_intersettlement.add("STATUS", DataValues.newString(0));
        dcp_intersettlement.add("PROCESSNO", DataValues.newString(oneData.get("PROCESSNO").toString()));

        dcp_intersettlement.add("UNSETTAMT", DataValues.newString(amt));
        dcp_intersettlement.add("UNPOSTEDAMT", DataValues.newString(amt));
        dcp_intersettlement.add("SETTLEDAMT", DataValues.newString(0));
        dcp_intersettlement.add("POSTEDAMT", DataValues.newString(0));

        dcp_intersettlement.add("SOURCETYPE", DataValues.newString(type));
        dcp_intersettlement.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_intersettlement.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        dcp_intersettlement.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        dcp_intersettlement.add("SOURCENO", DataValues.newString(oneData.get("SOURCEBILLNO").toString()));
        dcp_intersettlement.add("SOURCENOSEQ", DataValues.newString(oneData.get("SOURCEITEM").toString()));
        dcp_intersettlement.add("DATATYPE", DataValues.newString(oneData.get("DATATYPE").toString()));

        dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLEMENT", dcp_intersettlement)));

    }

    private void insertIntoStockChange(int type, int outInDirection, List<DataProcessBean> dbs, Map<String, Object> oneData) {

        String accountDate = DateFormatUtils.getPlainDate(oneData.get("BDATE").toString());
        double preTaxamt = Double.parseDouble((oneData.get("PRETAXAMT").toString()));
        double amt = BigDecimalUtils.add(Double.parseDouble((oneData.get("PRETAXAMT").toString())), Double.parseDouble(oneData.get("TAXAMT").toString()));
        double billPrice = BigDecimalUtils.div(amt, Double.parseDouble(oneData.get("PQTY").toString()));

        if (0 == type && 1 == outInDirection) { //原始单入库不写
            return;
        }
        //原始单需增加一笔对应出库单库存异动。
        // 联动单全部写入
        List<Object> inputParameter1 = Lists.newArrayList();
        inputParameter1.add(oneData.get("EID").toString());
        inputParameter1.add(oneData.get("STARTPOINT").toString());

        inputParameter1.add("09");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
        inputParameter1.add(oneData.get("BILLNO").toString());                                                //P_BillNo	IN	VARCHAR2,	--单据号
        inputParameter1.add(oneData.get("ITEM").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
        inputParameter1.add(outInDirection);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
        inputParameter1.add(DateFormatUtils.getDate(accountDate));                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
        inputParameter1.add(oneData.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
        inputParameter1.add(oneData.get("FEATURENO").toString());          //P_FeatureNo	IN	VARCHAR2,	--特征码
        inputParameter1.add(oneData.get("IC_COST_WAREHOUSE"));                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
        inputParameter1.add(" ");        //P_BATCHNO	IN	VARCHAR2,	--批号
        inputParameter1.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
        inputParameter1.add(oneData.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
        inputParameter1.add(oneData.get("PQTY").toString());                                            //P_Qty		IN	NUMBER,		--交易数量
        inputParameter1.add(oneData.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
        double unitRatio = Double.parseDouble(oneData.get("UNITRATIO").toString());
        double pQty = Double.parseDouble(oneData.get("PQTY").toString());
        inputParameter1.add(BigDecimalUtils.mul(unitRatio, pQty));                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
        inputParameter1.add(oneData.get("UNITRATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
        inputParameter1.add(billPrice);                                                   //P_Price		IN	NUMBER,		--零售价
        inputParameter1.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
        inputParameter1.add(oneData.get("SUPPLYPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
        inputParameter1.add(oneData.get("SUPPLYAMT").toString());                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
        inputParameter1.add(DateFormatUtils.getDate(accountDate));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
        inputParameter1.add("");            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
        inputParameter1.add(DateFormatUtils.getDate(accountDate));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
        inputParameter1.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
        inputParameter1.add("内部结算联动");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
        inputParameter1.add("");                                            //P_UserID	IN	VARCHAR2	--操作员

        dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter1)));

    }

    //两角关系新增
    public void relationShip1Process(List<DataProcessBean> dbs, List<Map<String, Object>> detail, List<Map<String, Object>> issData, int type) throws Exception {
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


                in_dcp_intersettlement.add("TAXCODE", DataValues.newString(oneData.get("TAXCODE").toString()));
                in_dcp_intersettlement.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
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
    public void relationShip2Process(List<DataProcessBean> dbs, List<Map<String, Object>> detail, List<Map<String, Object>> issData) throws Exception {
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

}