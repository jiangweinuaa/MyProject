package com.dsc.spos.utils.price;


import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoProdGetPriceUtils {


    public static List<GoodsPrice> getGoodsPrice(String accountId, List<ProdGoods> plus, String bData) throws Exception {

        List<GoodsPrice> goodsPriceList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(plus)) {
            for (ProdGoods pg : plus) {
                GoodsPrice goodsPrice = new GoodsPrice();
                goodsPrice.setPluNo(pg.getPluNo());
                goodsPrice.setOrgNo(pg.getOrgNo());
                goodsPrice.setQty(pg.getQty());
                goodsPriceList.add(goodsPrice);
            }
        }

        getCurAvgPrice(accountId, goodsPriceList);
        getSupPriceTemplatePrice(goodsPriceList, bData);
        getPurchaseTemplatePrice(goodsPriceList);

        return goodsPriceList;

    }


    private static void getCurAvgPrice(String accountId, List<GoodsPrice> goodsPrice) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.ACCOUNTID,a.COSTDOMAINID, a.YEAR, a.PERIOD, a.PLUNO, CURAVGPRICE ")
                .append(" FROM DCP_CURINVCOSTSTAT a ")
        ;

        querySql.append(" WHERE ( 1=2 ");
        for (GoodsPrice goods : goodsPrice) {
            querySql.append(" OR ( a.PLUNO='").append(goods.getPluNo()).append("'")
                    .append(" AND a.ACCOUNTID='").append(accountId).append("' ")
                    .append(" AND a.COSTDOMAINID='").append(goods.getOrgNo()).append("' ")
                    .append(" ) ");
        }
        querySql.append(" ) ");

        querySql.append(" AND EXISTS(SELECT * " +
                "             FROM (SELECT EID, " +
                "                          PLUNO, " +
                "                          ACCOUNTID, " +
                "                          a.COSTDOMAINID, " +
                "                          MAX(cast(YEAR as NVARCHAR2(4)) || LPAD(cast(PERIOD as nvarchar2(2)), 2, '0')) YEARPERIOD\n" +
                "                   FROM DCP_CURINVCOSTSTAT a " +
                "                   WHERE a.ACCOUNTID<>'UNKNOW' " +
                "                   GROUP BY EID, PLUNO, ACCOUNTID,a.COSTDOMAINID " +
                "                   ) t " +
                "             WHERE a.EID = t.EID " +
                "               and a.ACCOUNTID = t.ACCOUNTID " +
                "               and cast(a.YEAR as NVARCHAR2(4)) || LPAD(cast(a.PERIOD as nvarchar2(2)), 2, '0') = t.YEARPERIOD\n" +
                "               and a.PLUNO = t.PLUNO " +
                "               and a.COSTDOMAINID=t.COSTDOMAINID " +
                "             ) ")
        ;

        List<Map<String, Object>> qData = StaticInfo.dao.executeQuerySQL(querySql.toString(), null);

        if (CollectionUtils.isNotEmpty(qData)) {
            for (GoodsPrice goods : goodsPrice) {
                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", goods.getPluNo());
                condition.put("COSTDOMAINID", goods.getOrgNo());
                List<Map<String, Object>> oneData = MapDistinct.getWhereMap(qData, condition, false);

                if (CollectionUtils.isNotEmpty(oneData)) {
                    double curAvgPrice = Double.parseDouble(oneData.get(0).get("CURAVGPRICE").toString());

                    GoodsPrice.Price price = goods.new Price();
                    price.setSort(1);
                    price.setType(PriceType.CurAvgPrice);
                    price.setPrice(curAvgPrice);
                    goods.getPrices().add(price);
                }
            }
        }
    }

    private static void getSupPriceTemplatePrice(List<GoodsPrice> goodsPrice, String bDate) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.PLUNO,PRICE " +
                " FROM DCP_SUPPRICETEMPLATE_PRICE a " +
                " WHERE STATUS='100'  ")
        ;

        if (StringUtils.isNotEmpty(bDate)) {
            querySql.append("AND to_char(a.BEGINDATE,'yyyyMMdd')<='").append(bDate).append("' ");
            querySql.append("AND to_char(a.ENDDATE,'yyyyMMdd')>='").append(bDate).append("' ");
        }

        querySql.append(" AND ( 1=2 ");

        for (GoodsPrice goods : goodsPrice) {
            querySql.append(" OR a.PLUNO='").append(goods.getPluNo()).append("'");
        }
        querySql.append(")");


        List<Map<String, Object>> qData = StaticInfo.dao.executeQuerySQL(querySql.toString(), null);

        if (CollectionUtils.isNotEmpty(qData)) {
            for (GoodsPrice goods : goodsPrice) {
                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", goods.getPluNo());
                List<Map<String, Object>> oneData = MapDistinct.getWhereMap(qData, condition, false);

                if (CollectionUtils.isNotEmpty(oneData)) {
                    double supPrice = Double.parseDouble(oneData.get(0).get("PRICE").toString());

                    GoodsPrice.Price price = goods.new Price();
                    price.setSort(2);
                    price.setType(PriceType.SupPriceTemplatePrice);
                    price.setPrice(supPrice);
                    goods.getPrices().add(price);
                }
            }
        }
    }

    private static void getPurchaseTemplatePrice(List<GoodsPrice> goodsPrice) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.PLUNO,b.BQTY,b.EQTY,b.PURPRICE " +
                " FROM DCP_PURCHASETEMPLATE_GOODS  a " +
                " LEFT JOIN DCP_PURCHASETEMPLATE_PRICE b on a.EID=b.EID and a.PURTEMPLATENO=b.PURTEMPLATENO and a.ITEM=b.ITEM " +
                " WHERE STATUS='100' ")
        ;

        querySql.append(" AND ( 1=2 ");

        for (GoodsPrice goods : goodsPrice) {
            querySql.append(" OR (a.PLUNO='").append(goods.getPluNo()).append("'")
                    .append(" AND b.BQTY>=").append(goods.getQty())
                    .append(" AND b.EQTY<=").append(goods.getQty())
                    .append(") ");
            ;

        }
        querySql.append(")");

        List<Map<String, Object>> qData = StaticInfo.dao.executeQuerySQL(querySql.toString(), null);

        if (CollectionUtils.isNotEmpty(qData)) {
            for (GoodsPrice goods : goodsPrice) {
                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", goods.getPluNo());
                List<Map<String, Object>> oneData = MapDistinct.getWhereMap(qData, condition, false);

                if (CollectionUtils.isNotEmpty(oneData)) {
                    double purPrice = Double.parseDouble(oneData.get(0).get("PRICE").toString());

                    GoodsPrice.Price price = goods.new Price();
                    price.setSort(3);
                    price.setType(PriceType.PurchaseTemplatePrice);
                    price.setPrice(purPrice);
                    goods.getPrices().add(price);
                }
            }
        }
    }


}
