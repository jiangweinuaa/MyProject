package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GoodsCustomerPriceDiscQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsCustomerPriceDiscQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_GoodsCustomerPriceDiscQuery extends SPosBasicService<DCP_GoodsCustomerPriceDiscQueryReq, DCP_GoodsCustomerPriceDiscQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_GoodsCustomerPriceDiscQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsCustomerPriceDiscQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsCustomerPriceDiscQueryReq>() {
        };
    }

    @Override
    protected DCP_GoodsCustomerPriceDiscQueryRes getResponseType() {
        return new DCP_GoodsCustomerPriceDiscQueryRes();
    }

    @Override
    protected DCP_GoodsCustomerPriceDiscQueryRes processJson(DCP_GoodsCustomerPriceDiscQueryReq req) throws Exception {
        DCP_GoodsCustomerPriceDiscQueryRes res = this.getResponseType();
        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {

            Map<String,Boolean> condition = Maps.newHashMap();
            condition.put("CUSTOMERNO",true);
            condition.put("PLUNO",true);
            condition.put("UNIT",true);

            List<Map<String, Object>> gData = MapDistinct.getMap(getData,condition);
            for (Map<String, Object> data : gData) {
                DCP_GoodsCustomerPriceDiscQueryRes.Datas oneData = res.new Datas();

                oneData.setCustGroupNo(StringUtils.toString(data.get("CUSTGROUPNO"), ""));
                oneData.setCustomerNo(StringUtils.toString(data.get("CUSTOMERNO"), ""));
                oneData.setBeginDate(StringUtils.toString(data.get("BEGINDATE"), ""));
                oneData.setLastModiTime(StringUtils.toString(data.get("LASTMODITIME"), ""));

                oneData.setEndDate(StringUtils.toString(data.get("ENDDATE"), ""));
                oneData.setPluNo(StringUtils.toString(data.get("PLUNO"), ""));
                oneData.setCategory(StringUtils.toString(data.get("CATEGORYID"), ""));
                oneData.setDiscRate(StringUtils.toString(data.get("DISCRATE"), ""));

                //这几个需要做进一步处理
                oneData.setUnit(StringUtils.toString(data.get("UNIT"), ""));
                oneData.setUnitName(data.get("UNAME").toString());
                oneData.setPrice(StringUtils.toString(data.get("PRICE"), ""));

                res.getDatas().add(oneData);
            }
        }

        res.setSuccess(true);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryDiscSql(DCP_GoodsCustomerPriceDiscQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "       SELECT DISTINCT CUSTOMERNO, PLUNO,CATEGORYID, DISCRATE " +
                        " FROM (SELECT DISTINCT CUSTOMERNO, CATEGORYID, DISCRATE " +
                        "      FROM (SELECT d.ID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                        "            FROM DCP_CUSTOMER_CATE_DISC a " +
                        "                     INNER JOIN DCP_CUSTGROUP b " +
                        "                                ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                        "                     INNER JOIN DCP_CUSTGROUP_DETAIL c " +
                        "                                on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '1'" +
                        "                     INNER JOIN DCP_TAGTYPE_DETAIL d" +
                        "                                ON d.EID = c.EID and TAGNO = c.ATTRID and d.TAGGROUPTYPE = 'CUST'" +
                        "                     WHERE a.STATUS='100' " +
                        "            UNION all " +
                        "            SELECT c.ATTRID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                        "            FROM DCP_CUSTOMER_CATE_DISC a " +
                        "                     INNER JOIN DCP_CUSTGROUP b " +
                        "                                ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                        "                     INNER JOIN DCP_CUSTGROUP_DETAIL c" +
                        "                                on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '2' " +
                        "                     WHERE a.STATUS='100' " +
                        "            UNION all " +
                        "            SELECT CUSTOMERNO, CATEGORYID, DISCRATE " +
                        "            FROM DCP_CUSTOMER_CATE_DISC a " +
                        "            WHERE a.CUSTOMERTYPE = '2' AND a.STATUS='100' ) a ");

        if (StringUtils.isNotEmpty(req.getRequest().getCustomerNo())) {
            sb.append(" WHERE CUSTOMERNO = '").append(req.getRequest().getCustomerNo()).append("'");
        }

        sb.append(" ) a")
                .append(" LEFT JOIN DCP_GOODS b ON a.CATEGORYID=b.CATEGORY");

        if (CollectionUtils.isNotEmpty(req.getRequest().getPluList())) {
            sb.append(" WHERE (1=2 ");
            for (DCP_GoodsCustomerPriceDiscQueryReq.PluList plu : req.getRequest().getPluList()) {
                sb.append(" OR PLUNO = '").append(plu.getPluNo()).append("'");
            }
            sb.append(" ) ");
        }

        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_GoodsCustomerPriceDiscQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        String bDate = DateFormatUtils.getNowPlainDate();

        querySql.append( "with disc as ( ")
                        .append(getQueryDiscSql(req))
                                .append(") ");


        querySql.append(" SELECT DISTINCT ORDERID,a.CUSTOMERNO,CUSTGROUPNO,a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME,NVL(disc.DISCRATE,0) DISCRATE" +
                        " ,NVL(disc.CATEGORYID,dg.CATEGORY) CATEGORYID,ul.UNAME FROM (")
                .append(
//                "--取出客户组中客户标签定价 " +
                        "       SELECT 3 ORDERID, d.ID as CUSTOMERNO,b.CUSTGROUPNO,a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                " INNER JOIN DCP_CUSTGROUP b ON a.CUSTOMERTYPE='1' and a.EID=b.EID and a.CUSTOMERNO=b.CUSTGROUPNO" +
                                " INNER JOIN DCP_CUSTGROUP_DETAIL c on b.EID=c.EID and b.CUSTGROUPNO=c.CUSTGROUPNO and c.ATTRTYPE='1'" +
                                " INNER JOIN DCP_TAGTYPE_DETAIL d ON d.EID=c.EID and TAGNO=c.ATTRID and d.TAGGROUPTYPE='CUST'" +
                                " WHERE a.STATUS='100' AND BEGINDATE<='"+bDate+"' AND a.ENDDATE>='" +bDate + "' " +
                                " UNION all " +
//                "--取出客户组中客户定价" +
                                " SELECT 2 ORDERID,c.ATTRID as CUSTOMERNO,b.CUSTGROUPNO,a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                " INNER JOIN DCP_CUSTGROUP b ON a.CUSTOMERTYPE='1' and a.EID=b.EID and a.CUSTOMERNO=b.CUSTGROUPNO" +
                                " INNER JOIN DCP_CUSTGROUP_DETAIL c on b.EID=c.EID and b.CUSTGROUPNO=c.CUSTGROUPNO and c.ATTRTYPE='2'" +
                                " WHERE a.STATUS='100' AND BEGINDATE<='"+bDate+"' AND a.ENDDATE>='" +bDate + "' " +
                                " UNION all " +
//                "--取出客户定价" +
                                " SELECT 1 ORDERID,a.CUSTOMERNO,CAST('' as NVARCHAR2(32)) CUSTGROUPNO, a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                " WHERE a.STATUS='100' AND BEGINDATE<='"+bDate+"' AND a.ENDDATE>='" +bDate + "' " +
                                " AND a.CUSTOMERTYPE='2'  ")
                .append(" ) a ")
                .append(" LEFT JOIN disc on a.CUSTOMERNO=disc.CUSTOMERNO and a.PLUNO=disc.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS dg on a.PLUNO=dg.PLUNO ")
                .append(" LEFT JOIN DCP_UNIT_LANG ul on ul.UNIT=a.UNIT AND ul.LANG_TYPE='").append(req.getLangType()).append("'")
        ;

        if (StringUtils.isNotEmpty(req.getRequest().getCustomerNo())) {
            querySql.append(" WHERE a.CUSTOMERNO = '").append(req.getRequest().getCustomerNo()).append("'");
        }

        //筛选品号
        if (CollectionUtils.isNotEmpty(req.getRequest().getPluList())) {
            querySql.append(" AND (1=2 ");
            for (DCP_GoodsCustomerPriceDiscQueryReq.PluList plu : req.getRequest().getPluList()) {
                querySql.append(" OR a.PLUNO = '").append(plu.getPluNo()).append("'");
            }
            querySql.append(")");
        }
        querySql.append(" ORDER BY ORDERID ASC,a.LASTMODITIME DESC ");

        return querySql.toString();
    }
}
