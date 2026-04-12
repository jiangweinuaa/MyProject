package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_YestStockQueryReq;
import com.dsc.spos.json.cust.res.DCP_YestStockQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_YestStockQuery extends SPosBasicService<DCP_YestStockQueryReq, DCP_YestStockQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_YestStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_YestStockQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_YestStockQueryReq>(){};
    }

    @Override
    protected DCP_YestStockQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_YestStockQueryRes();
    }

    @Override
    protected DCP_YestStockQueryRes processJson(DCP_YestStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        DCP_YestStockQueryRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<>());
        //int totalRecords = 0; //总笔数
        //int totalPages = 0;

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String langType = req.getLangType();
        String warehouse = req.getRequest().getWarehouse();
        String bDate = req.getRequest().getBDate();

        //int pageSize=req.getPageSize();
        //int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        //startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        //startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        List<DCP_YestStockQueryReq.Datas> datas = req.getRequest().getDatas();
        StringBuffer sJoinPluNo=new StringBuffer("");
        StringBuffer sJoinFeatureNo=new StringBuffer("");
        for (DCP_YestStockQueryReq.Datas data:datas){
            String pluNo = data.getPluNo();
            String featureNo = data.getFeatureNo();
            sJoinPluNo.append(pluNo+",");
            sJoinFeatureNo.append(featureNo+",");
        }

        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("PLUNO", sJoinPluNo.toString());
        mapOrder.put("FEATURENO", sJoinFeatureNo.toString());
        MyCommon cm=new MyCommon();
        String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);
        StringBuffer stockSql=new StringBuffer();
        if(Check.NotNull(withasSql_mono)){
            stockSql.append(" with p as ("+withasSql_mono+") ");
        }
        stockSql.append(" select a.pluno,b.plu_name,a.featureno,c.featurename,a.warehouse,d.WAREHOUSE_NAME as warehousename," +
                " a.location,e.locationname,a.batchno,a.baseunit,f.uname ,sum(a.qty-nvl(a.LOCKQTY,0)) as qty,a.VALIDDATE as expDate,a.proddate  " +
                " from  MES_BATCH_STOCK_DETAIL a "
             );
        if(Check.NotNull(withasSql_mono)){
            stockSql.append(" inner join p on p.pluno=a.pluno and p.featureno=a.featureno ");
        }
        stockSql.append(" left join dcp_goods_lang b on a.pluno=b.pluno and a.eid=b.eid and b.lang_type='"+langType+"' " +
                " left join dcp_goods_feature_lang c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='"+langType+"' " +
                " left join dcp_warehouse_lang d on d.eid=a.eid and d.organizationno=a.organizationno and d.warehouse=a.warehouse and d.lang_type='"+langType+"' " +
                " left join dcp_location e on e.eid=a.eid and e.location=a.location " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.baseunit and f.lang_type='"+langType+"'" +
                "");


        stockSql.append(   " where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' " +
                " and a.warehouse='"+warehouse+"'");
        stockSql.append("group by a.pluno,b.plu_name,a.featureno,c.featurename,a.warehouse,d.warehouse_name,a.location,e.locationname,a.batchno,a.baseunit,f.uname,a.proddate,a.validdate ");

        List<Map<String, Object>> list = this.doQueryData(stockSql.toString(), null);

        StringBuffer sdSql=new StringBuffer();
        StringBuffer sdsSql=new StringBuffer();
        if(Check.NotNull(withasSql_mono)){
            sdSql.append(" with p as ("+withasSql_mono+") ");
            sdsSql.append(" with p as ("+withasSql_mono+") ");
        }
        sdSql.append(" select * from DCP_STOCK_DETAIL  a " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.warehouse='"+warehouse+"'" +
                " and to_char(a.bdate,'yyyyMMdd')>'"+bDate+"' ");
        List<Map<String, Object>> sdlist = this.doQueryData(sdSql.toString(), null);

        sdsSql.append(" select * from DCP_STOCK_DETAIL_STATIC  a " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.warehouse='"+warehouse+"'" +
                " and to_char(a.bdate,'yyyyMMdd')>'"+bDate+"' ");
        List<Map<String, Object>> sdslist = this.doQueryData(sdsSql.toString(), null);

        if(CollUtil.isNotEmpty(list)){
            //String num = list.get(0).get("NUM").toString();
            //totalRecords=Integer.parseInt(num);
            //算總頁數
            //totalPages = totalRecords / req.getPageSize();
            //totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> map:list){
                DCP_YestStockQueryRes.level1Elm lv1 = res.new level1Elm();
                String pluNo = map.get("PLUNO").toString();
                String pluName = map.get("PLU_NAME").toString();
                String featureNo = map.get("FEATURENO").toString();
                String featureName = map.get("FEATURENAME").toString();
                //String warehouse = map.get("WAREHOUSE").toString();
                String warehouseName = map.get("WAREHOUSENAME").toString();
                String location = map.get("LOCATION").toString();
                String locationName = map.get("LOCATIONNAME").toString();
                String batchNo = map.get("BATCHNO").toString();
                String baseUnit = map.get("BASEUNIT").toString();
                String baseUnitName = map.get("UNAME").toString();
                BigDecimal qty = new BigDecimal(map.get("QTY").toString());

                String prodDate = DateFormatUtils.getPlainDate( map.get("PRODDATE").toString());
                String expDate =  DateFormatUtils.getPlainDate(map.get("EXPDATE").toString());

                List<Map<String, Object>> collect1 = sdlist.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) &&
                        x.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
                List<Map<String, Object>> collect2 = sdslist.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) &&
                        x.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());

                BigDecimal diffQty=new BigDecimal(0);
                if(CollUtil.isNotEmpty(collect1)){
                    for (Map<String, Object> cc:collect1){
                        String ccBaseQty = cc.get("BASEQTY").toString();
                        String stockType = cc.get("STOCKTYPE").toString();
                        if(stockType.equals("1")){
                            diffQty=diffQty.add(new BigDecimal(ccBaseQty));
                        }else{
                            diffQty=diffQty.subtract(new BigDecimal(ccBaseQty));
                        }
                    }
                }
                if(CollUtil.isNotEmpty(collect2)){
                    for (Map<String, Object> cc:collect2){
                        String ccBaseQty = cc.get("BASEQTY").toString();
                        String stockType = cc.get("STOCKTYPE").toString();
                        if(stockType.equals("1")){
                            diffQty=diffQty.add(new BigDecimal(ccBaseQty));
                        }else{
                            diffQty=diffQty.subtract(new BigDecimal(ccBaseQty));
                        }
                    }
                }

                qty=qty.add(diffQty);
                lv1.setPluNo(pluNo);
                lv1.setPluName(pluName);
                lv1.setFeatureNo(featureNo);
                lv1.setFeatureName(featureName);
                lv1.setWarehouse(warehouse);
                lv1.setWarehouseName(warehouseName);
                lv1.setLocation(location);
                lv1.setLocationName(locationName);
                lv1.setBatchNo(batchNo);
                lv1.setBaseUnit(baseUnit);
                lv1.setBaseUnitName(baseUnitName);
                lv1.setQty(qty.toString());
                lv1.setProdDate(prodDate);
                lv1.setExpDate(expDate);
                res.getDatas().add(lv1);

            }

        }
        else
        {
           // totalRecords = 0;
            //totalPages = 0;
        }


        //res.setPageNumber(req.getPageNumber());
        //res.setPageSize(req.getPageSize());
        //res.setTotalRecords(totalRecords);
        //res.setTotalPages(totalPages);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_YestStockQueryReq req) throws Exception {
        return "";
    }

}
