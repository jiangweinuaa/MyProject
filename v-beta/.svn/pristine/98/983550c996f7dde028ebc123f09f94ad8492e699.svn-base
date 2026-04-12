package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_GoodsDeliveryQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsDeliveryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DCP_GoodsDeliveryQuery extends SPosBasicService<DCP_GoodsDeliveryQueryReq, DCP_GoodsDeliveryQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_GoodsDeliveryQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsDeliveryQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsDeliveryQueryReq>(){};
    }

    @Override
    protected DCP_GoodsDeliveryQueryRes getResponseType() {
        return new DCP_GoodsDeliveryQueryRes();
    }

    @Override
    protected DCP_GoodsDeliveryQueryRes processJson(DCP_GoodsDeliveryQueryReq req) throws Exception {
        DCP_GoodsDeliveryQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());

        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate d1=LocalDate.parse(beginDate,formatter);
        LocalDate d2=LocalDate.parse(endDate,formatter);
        BigDecimal  daysBetween = new BigDecimal(ChronoUnit.DAYS.between(d1, d2));

        List<Map<String,Object>> queryData = doQueryData(getQuerySql(req),null);

        res.setDatas(new ArrayList<>());
        if (queryData != null && !queryData.isEmpty()) {
            for (Map<String,Object> data : queryData) {

                DCP_GoodsDeliveryQueryRes.Datas datas = res.new Datas();

                datas.setPluNo(data.get("PLUNO").toString());
                datas.setFeatureNo(StringUtils.toString(data.get("FEATURENO")," "));
                datas.setTotDeliverQty(StringUtils.toString(data.get("BASEQTY")," "));

                BigDecimal totDecimal = new BigDecimal(datas.getTotDeliverQty());
                if(daysBetween.compareTo(BigDecimal.ZERO)!=0){
                    BigDecimal avg = totDecimal.divide(daysBetween, 2, BigDecimal.ROUND_HALF_UP);
                    datas.setAvgDeliverQty(avg.toString());
                }

                res.getDatas().add(datas);
            }
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_GoodsDeliveryQueryReq req) throws Exception {
        StringBuffer sb= new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        List<DCP_GoodsDeliveryQueryReq.PluList> pluList = req.getRequest().getPluList();
        StringBuffer sJoinPluno=new StringBuffer("");
        StringBuffer sJoinFeatureno=new StringBuffer("");

        String innerSql="";
        if(CollUtil.isNotEmpty(pluList)){
            for (DCP_GoodsDeliveryQueryReq.PluList plu : pluList){
                sJoinPluno.append(plu.getPluNo()+",");
                sJoinFeatureno.append(plu.getFeatureNo()+",");
            }

            Map<String, String> mapPlu= new HashMap<String, String>();
            mapPlu.put("PLUNO", sJoinPluno.toString());
            mapPlu.put("FEATURENO",sJoinFeatureno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapPlu);

            sb.append(" with p as ("+withasSql_mono+")");

            innerSql=" inner join p on p.pluno=a.pluno and p.featureno=a.featureno ";
        }


        sb.append("select pluno,featureno,sum(baseqty) as baseqty from ( select a.pluno,a.featureno,a.baseqty from DCP_STOCK_DETAIL a " +
                        innerSql+
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.billtype in ('20','04','39')" +
                " and to_char(a.ACCOUNTDATE,'yyyyMMdd')>='"+beginDate+"' "
                + " and to_char(a.ACCOUNTDATE,'yyyyMMdd')<='"+endDate+"' " +
                " union all" +
                " select a.pluno,a.featureno,a.baseqty from DCP_STOCK_DETAIL_static a " +
                innerSql+
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.billtype in ('20','04','39')" +
                " and to_char(a.ACCOUNTDATE,'yyyyMMdd')>='"+beginDate+"' "
                + " and to_char(a.ACCOUNTDATE,'yyyyMMdd')<='"+endDate+"' " +
                ") a group by pluno,featureno"
        );


        return sb.toString();
    }
}
