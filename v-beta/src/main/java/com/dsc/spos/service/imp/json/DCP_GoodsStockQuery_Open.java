package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsStockQuery_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GoodsStockQuery_OpenReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_GoodsStockQuery_OpenReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsStockQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_GoodsStockQuery
 * 服务说明：商品库存查询
 * @author jinzma
 * @since  2020-04-21
 */
public class DCP_GoodsStockQuery_Open extends SPosBasicService<DCP_GoodsStockQuery_OpenReq,DCP_GoodsStockQuery_OpenRes>{

    @Override
    protected boolean isVerifyFail(DCP_GoodsStockQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        if (Check.Null(request.geteId())) {
            errMsg.append("企业编号不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsStockQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_GoodsStockQuery_OpenReq>(){};
    }

    @Override
    protected DCP_GoodsStockQuery_OpenRes getResponseType() {
        return new DCP_GoodsStockQuery_OpenRes();
    }

    @Override
    protected DCP_GoodsStockQuery_OpenRes processJson(DCP_GoodsStockQuery_OpenReq req) throws Exception {
        try {
            levelElm request = req.getRequest();
            String eId=request.geteId();
            String queryOrgId = request.getQueryOrgId();
            String queryType = request.getQueryType(); //DCP：查询门店管理库存 , ERP：查询ERP库存
            String warehouse = request.getWarehouse();
            String stockQtyType = request.getStockQtyType(); // 库存数量：0.库存大于零 1.库存不等于零 2.库存等于零 3.库存小于零 4.库存大于等于零 5.库存小于等于零
            String queryDate = request.getQueryDate();       //查询指定日期
            List<level1Elm> datas =request.getPluList();
            DCP_GoodsStockQuery_OpenRes res = this.getResponse();

            if (!Check.Null(queryType) && queryType.equals("ERP")){
                //根据请求商品关联总部库存表，汇总相同商品的可要货量返回
                getErpStock(eId,"","",stockQtyType,datas,res);
            }else if (!Check.Null(queryOrgId)){
                String sql=" select org_form,discentre,belfirm,out_cost_warehouse from dcp_org "
                        + " where eid='"+eId+"' and organizationno='"+queryOrgId+"' ";
                List<Map<String , Object>> getOrg = this.doQueryData(sql, null);
                if (getOrg != null && !getOrg.isEmpty()) {
                    String orgForm = getOrg.get(0).get("ORG_FORM").toString();
                    String discentre = getOrg.get(0).get("DISCENTRE").toString();
                    if (orgForm.equals("2") && discentre.equals("N")){
                        if (Check.Null(warehouse)){
                            warehouse = getOrg.get(0).get("OUT_COST_WAREHOUSE").toString();
                        }
                        queryType = "DCP";
                        //若ORG_FORM=2（门店） 且 DISCENTRE=N为门店，关联获取新零售库存表；
                        if (Check.Null(queryDate)){
                            //查询门店实时库存
                            getDcpStock(eId,queryOrgId,warehouse,stockQtyType,datas,res);
                        }else{
                            //查询门店指定日期库存
                            getDcpDateStock(eId,queryOrgId,warehouse,stockQtyType,queryDate,datas,res);
                        }
                    }else if (discentre.equals("Y")) {
                        //若DISCENTRE=Y，根据请求queryOrgId关联DCP_HEADQUARTER_STOCK.ORGANIZATIONNO获取该组织的可要货库存
                        getErpStock(eId,"",queryOrgId,stockQtyType,datas,res);
                    }else if (orgForm.equals("0") && discentre.equals("N")) {
                        //若ORG_FORM=0（公司）且 DISCENTRE=N，根据请求queryOrgId关联DCP_HEADQUARTER_STOCK.BELFIRM获取该公司的 所有配送中心的可要货库存和
                        getErpStock(eId,queryOrgId,"",stockQtyType,datas,res);
                    } else {
                        //啥也没查，直接就返回
                        DCP_GoodsStockQuery_OpenRes.levelElm d = res.new levelElm();
                        d.setPluList(new ArrayList<>());
                        res.setDatas(d);
                    }


                }
            }

            return res;
        }
        catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GoodsStockQuery_OpenReq req) throws Exception {
        return null;
    }

    //获取ERP库存
    private void getErpStock(String eId,String belFirm,String organizationNo,String stockQtyType,List<level1Elm>datas,DCP_GoodsStockQuery_OpenRes res) throws Exception{

        //批号不用处理，ERP未下传批号
        try {
            String withPlu = "";
            if (datas !=null && !datas.isEmpty()) {
                MyCommon mc = new MyCommon();
                Map<String,String> map = new HashMap<String, String>();
                String sJoinPlu="";
                String sJoinFeature="";
                for(level1Elm par :datas) {
                    sJoinPlu += par.getPluNo()+",";
                    if (Check.Null(par.getFeatureNo())){
                        sJoinFeature += ""+",";
                    }else{
                        sJoinFeature += par.getFeatureNo()+",";
                    }
                }
                map.put("PLUNO",sJoinPlu);
                map.put("FEATURENO",sJoinFeature);
                withPlu = mc.getFormatSourceMultiColWith(map);
            }

            String erpPluSql = getErpPluSql(withPlu,eId,belFirm,organizationNo,stockQtyType);
            String erpPluFeatureSql = getErpPluFeatureSql(withPlu,eId,belFirm,organizationNo,stockQtyType);

            List<Map<String , Object>> erpPlu = this.doQueryData(erpPluSql, null);
            List<Map<String , Object>> erpPluFeature = this.doQueryData(erpPluFeatureSql, null);
            DCP_GoodsStockQuery_OpenRes.levelElm lElm = res.new levelElm();
            lElm.setPluList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level1Elm>());
            if (erpPlu != null && !erpPlu.isEmpty()) {
                for (Map<String, Object> plu : erpPlu) {
                    DCP_GoodsStockQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
                    String pluNo = plu.get("PLUNO").toString();
                    String baseUnit = plu.get("BASEUNIT").toString();
                    String baseQty = plu.get("BASEQTY").toString();

                    oneLv1.setFeatureList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmFeature>());
                    oneLv1.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmBatch>());

                    //处理特征码
                    for (Map<String, Object> feature : erpPluFeature) {
                        if (pluNo.equals(feature.get("PLUNO").toString())) {
                            String featureNo = feature.get("FEATURENO").toString();
                            String featureBaseQty = feature.get("BASEQTY").toString();

                            DCP_GoodsStockQuery_OpenRes.level2ElmFeature level2ElmFeature = res.new level2ElmFeature();

                            level2ElmFeature.setFeatureNo(featureNo);
                            level2ElmFeature.setBaseQty(featureBaseQty);
                            level2ElmFeature.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level3ElmBatch>());

                            oneLv1.getFeatureList().add(level2ElmFeature);
                        }
                    }

                    oneLv1.setPluNo(pluNo);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseQty(baseQty);

                    lElm.getPluList().add(oneLv1);
                }
            }
            res.setDatas(lElm);
            res.setVersion("3.0");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceDescription("查询发生异常,异常原因 "+e.getMessage());
        }
    }

    //获取门店管理实时库存
    private void getDcpStock(String eId,String shopId,String warehouse,String stockQtyType,List<level1Elm>datas,DCP_GoodsStockQuery_OpenRes res) throws Exception{
        try
        {
            String withPlu = "";
            if (datas !=null && !datas.isEmpty())
            {
                MyCommon mc = new MyCommon();
                Map<String,String> map = new HashMap<String, String>();
                String sJoinPlu="";
                String sJoinFeature="";
                String sJoinBatch="";
                for(level1Elm par :datas)
                {
                    sJoinPlu += par.getPluNo()+",";
                    if (Check.Null(par.getFeatureNo())){
                        sJoinFeature += ""+",";
                    }else{
                        sJoinFeature += par.getFeatureNo()+",";
                    }
                    if (Check.Null(par.getBatchNo())){
                        sJoinBatch += ""+",";
                    }else{
                        sJoinBatch += par.getBatchNo()+",";
                    }
                }
                map.put("PLUNO",sJoinPlu);
                map.put("FEATURENO",sJoinFeature);
                map.put("BATCHNO",sJoinBatch);
                withPlu = mc.getFormatSourceMultiColWith(map);
            }

            StringBuffer sqlbuf = new StringBuffer();
            if (!Check.Null(withPlu))
            {
                sqlbuf.append(" with goods as ("+withPlu+") ");
            }
            sqlbuf.append(""
                    + " select a.pluno,a.featureno,a.batchno,max(a.proddate) as proddate,a.baseunit,sum(a.baseqty) as baseqty"
                    + " from ("
                    + " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.qty as baseqty"
                    + " from dcp_stock_day a"
                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"'"
                    + " union all"
                    + " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.baseqty*a.stocktype as baseqty"
                    + " from dcp_stock_detail a"
                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"' and"
                    + " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
                    + " ) a"
                    + " ");
            if (!Check.Null(withPlu))
            {
                sqlbuf.append(" inner join goods on a.pluno=goods.pluno"
                        + " and (trim(a.featureno)=trim(goods.featureno) or trim(goods.featureno) is null)"
                        + " and (trim(a.batchno)=trim(goods.batchno) or trim(goods.batchno) is null)");
            }
            sqlbuf.append(" where 1=1 ");
            if (!Check.Null(stockQtyType))
            {
                switch (stockQtyType) {
                    case "0":             //0.库存大于零
                        sqlbuf.append(" and a.baseqty>0 " );
                        break;
                    case "1":             //1.库存不等于零
                        sqlbuf.append(" and a.baseqty<>0 " );
                        break;
                    case "2":             //2.库存等于零
                        sqlbuf.append(" and a.baseqty=0 " );
                        break;
                    case "3":             // 3.库存小于零
                        sqlbuf.append(" and a.baseqty<0 " );
                        break;
                    case "4":             // 4.库存大于等于零
                        sqlbuf.append(" and a.baseqty>=0 " );
                        break;
                    case "5":             // 5.库存小于等于零
                        sqlbuf.append(" and a.baseqty<=0 " );
                        break;
                }
            }
            sqlbuf.append(" group by a.pluno,a.featureno,a.batchno,a.baseunit");
            sqlbuf.append(" order by a.pluno,a.featureno,a.batchno ");

            List<Map<String , Object>> stocks = this.doQueryData(sqlbuf.toString(), null);
            DCP_GoodsStockQuery_OpenRes.levelElm lElm = res.new levelElm();
            lElm.setPluList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level1Elm>());
            if (stocks != null && !stocks.isEmpty())
            {
                //库存返回解析
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("PLUNO", true);
                //调用过滤函数
                List<Map<String, Object>> getPlus=MapDistinct.getMap(stocks,condition);
                BigDecimal baseQtyB=new BigDecimal("0");
                for (Map<String, Object> plu : getPlus)
                {
                    DCP_GoodsStockQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
                    String pluNo = plu.get("PLUNO").toString();
                    String baseUnit = plu.get("BASEUNIT").toString();

                    oneLv1.setFeatureList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmFeature>());
                    oneLv1.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmBatch>());
                    //处理特征码
                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    List<Map<String, Object>> getFeatures=MapDistinct.getMap(stocks,condition);
                    for (Map<String, Object> feature : getFeatures)
                    {
                        if (pluNo.equals(feature.get("PLUNO").toString()))
                        {
                            String featureNo = feature.get("FEATURENO").toString();
                            if (!Check.Null(featureNo))
                            {
                                DCP_GoodsStockQuery_OpenRes.level2ElmFeature level2ElmFeature = res.new level2ElmFeature();
                                //处理批号
                                level2ElmFeature.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level3ElmBatch>() );
                                BigDecimal featureBaseQtyB = new BigDecimal("0");

                                for (Map<String, Object> batch : stocks) {
                                    if (pluNo.equals(batch.get("PLUNO").toString()) && featureNo.equals(batch.get("FEATURENO").toString()))
                                    {
                                        String batchNo = batch.get("BATCHNO").toString();
                                        featureBaseQtyB = featureBaseQtyB.add(new BigDecimal(batch.get("BASEQTY").toString()));
                                        if (!Check.Null(batchNo))
                                        {
                                            DCP_GoodsStockQuery_OpenRes.level3ElmBatch  level3ElmBatch = res.new level3ElmBatch();
                                            level3ElmBatch.setBatchNo(batchNo);
                                            level3ElmBatch.setBaseQty(batch.get("BASEQTY").toString());
                                            level2ElmFeature.getBatchList().add(level3ElmBatch);
                                        }
                                    }
                                }
                                level2ElmFeature.setFeatureNo(featureNo);
                                level2ElmFeature.setBaseQty(featureBaseQtyB.toString());
                                oneLv1.getFeatureList().add(level2ElmFeature);
                            }
                        }
                    }

                    //处理批号
                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("BATCHNO", true);
                    List<Map<String, Object>> getBatch=MapDistinct.getMap(stocks,condition);
                    for (Map<String, Object> batch : getBatch){
                        if (pluNo.equals(batch.get("PLUNO").toString()))
                        {
                            String batchNo = batch.get("BATCHNO").toString();
                            if (!Check.Null(batchNo))
                            {
                                DCP_GoodsStockQuery_OpenRes.level2ElmBatch level2ElmBatch = res.new level2ElmBatch();
                                BigDecimal batchBaseQtyB = new BigDecimal("0");

                                for (Map<String, Object> stock : stocks) {
                                    if (pluNo.equals(stock.get("PLUNO").toString()) && batchNo.equals(stock.get("BATCHNO").toString()))
                                    {
                                        batchBaseQtyB = batchBaseQtyB.add(new BigDecimal(stock.get("BASEQTY").toString()));
                                    }
                                }
                                level2ElmBatch.setBatchNo(batchNo);
                                level2ElmBatch.setBaseQty(batchBaseQtyB.toString());
                                oneLv1.getBatchList().add(level2ElmBatch);
                            }
                        }
                    }

                    //汇总库存数量
                    baseQtyB=new BigDecimal("0");
                    for (Map<String, Object> stock : stocks){
                        if (pluNo.equals(stock.get("PLUNO").toString()))
                        {
                            String baseQty = stock.get("BASEQTY").toString();
                            baseQtyB = baseQtyB.add(new BigDecimal(baseQty));
                        }
                    }
                    oneLv1.setPluNo(pluNo);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseQty(baseQtyB.toString());

                    lElm.getPluList().add(oneLv1);
                }
            }
            res.setDatas(lElm);
            res.setVersion("3.0");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceDescription("查询发生异常,异常原因 "+e.getMessage());
        }
    }

    //获取门店管理指定日期库存
    private void getDcpDateStock(String eId,String shopId,String warehouse,String stockQtyType,String queryDate,List<level1Elm>datas,DCP_GoodsStockQuery_OpenRes res) throws Exception{

        try
        {
            String withPlu = "";
            if (datas !=null && datas.isEmpty()==false)
            {
                MyCommon mc = new MyCommon();
                Map<String,String> map = new HashMap<String, String>();
                String sJoinPlu="";
                String sJoinFeature="";
                String sJoinBatch="";
                for(level1Elm par :datas)
                {
                    sJoinPlu += par.getPluNo()+",";
                    if (Check.Null(par.getFeatureNo())){
                        sJoinFeature += ""+",";
                    }else{
                        sJoinFeature += par.getFeatureNo()+",";
                    }
                    if (Check.Null(par.getBatchNo())){
                        sJoinBatch += ""+",";
                    }else{
                        sJoinBatch += par.getBatchNo()+",";
                    }
                }
                map.put("PLUNO",sJoinPlu);
                map.put("FEATURENO",sJoinFeature);
                map.put("BATCHNO",sJoinBatch);
                withPlu = mc.getFormatSourceMultiColWith(map);
            }

            StringBuffer sqlbuf = new StringBuffer();
            if (!Check.Null(withPlu))
            {
                sqlbuf.append(" with goods as ("+withPlu+") ");
            }
            sqlbuf.append(""
                    + " select a.pluno,a.featureno,a.batchno,max(a.proddate) as proddate,a.baseunit,sum(a.baseqty) as baseqty"
                    + " from ("
                    + " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.qty as baseqty"
                    + " from dcp_stock_day_static a"
                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"'"
                    + " and edate in (select max(edate) from dcp_stock_day_static where eid='"+eId+"' and organizationno='"+shopId+"' and edate<='"+queryDate+"') "
                    + " union all"
                    + " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.baseqty*a.stocktype as baseqty"
                    + " from dcp_stock_detail a"
                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"' "
                    + " and a.accountdate<=to_date('"+queryDate+"','yyyymmdd')"
                    + " and billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
                    + " ) a"
                    + " ");

            if (!Check.Null(withPlu)) {
                sqlbuf.append(" inner join goods on a.pluno=goods.pluno"
                        + " and (trim(a.featureno)=trim(goods.featureno) or trim(goods.featureno) is null)"
                        + " and (trim(a.batchno)=trim(goods.batchno) or trim(goods.batchno) is null)");
            }
            sqlbuf.append(" where 1=1 ");

            if (!Check.Null(stockQtyType)) {
                switch (stockQtyType) {
                    case "0":             //0.库存大于零
                        sqlbuf.append(" and a.baseqty>0 ");
                        break;
                    case "1":             //1.库存不等于零
                        sqlbuf.append(" and a.baseqty<>0 ");
                        break;
                    case "2":             //2.库存等于零
                        sqlbuf.append(" and a.baseqty=0 ");
                        break;
                    case "3":             // 3.库存小于零
                        sqlbuf.append(" and a.baseqty<0 ");
                        break;
                    case "4":             // 4.库存大于等于零
                        sqlbuf.append(" and a.baseqty>=0 ");
                        break;
                    case "5":             // 5.库存小于等于零
                        sqlbuf.append(" and a.baseqty<=0 ");
                        break;
                }
            }
            sqlbuf.append(" group by a.pluno,a.featureno,a.batchno,a.baseunit");
            sqlbuf.append(" order by a.pluno,a.featureno,a.batchno ");

            //调用公用方法，取ERP库存
            List<Map<String , Object>> stocks = this.doQueryData(sqlbuf.toString(), null);
            DCP_GoodsStockQuery_OpenRes.levelElm lElm = res.new levelElm();
            lElm.setPluList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level1Elm>());
            if (stocks != null && stocks.isEmpty()==false)
            {
                //库存返回解析
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("PLUNO", true);
                //调用过滤函数
                List<Map<String, Object>> getPlus=MapDistinct.getMap(stocks,condition);
                BigDecimal baseQtyB=new BigDecimal("0");
                for (Map<String, Object> plu : getPlus)
                {
                    DCP_GoodsStockQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
                    String pluNo = plu.get("PLUNO").toString();
                    String baseUnit = plu.get("BASEUNIT").toString();

                    oneLv1.setFeatureList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmFeature>());
                    oneLv1.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level2ElmBatch>());
                    //处理特征码
                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    List<Map<String, Object>> getFeatures=MapDistinct.getMap(stocks,condition);
                    for (Map<String, Object> feature : getFeatures)
                    {
                        if (pluNo.equals(feature.get("PLUNO").toString()))
                        {
                            String featureNo = feature.get("FEATURENO").toString();
                            if (!Check.Null(featureNo))
                            {
                                DCP_GoodsStockQuery_OpenRes.level2ElmFeature level2ElmFeature = res.new level2ElmFeature();
                                //处理批号
                                level2ElmFeature.setBatchList(new ArrayList<DCP_GoodsStockQuery_OpenRes.level3ElmBatch>() );
                                BigDecimal featureBaseQtyB = new BigDecimal("0");

                                for (Map<String, Object> batch : stocks) {
                                    if (pluNo.equals(batch.get("PLUNO").toString()) && featureNo.equals(batch.get("FEATURENO").toString()))
                                    {
                                        String batchNo = batch.get("BATCHNO").toString();
                                        featureBaseQtyB = featureBaseQtyB.add(new BigDecimal(batch.get("BASEQTY").toString()));
                                        if (!Check.Null(batchNo))
                                        {
                                            DCP_GoodsStockQuery_OpenRes.level3ElmBatch  level3ElmBatch = res.new level3ElmBatch();
                                            level3ElmBatch.setBatchNo(batchNo);
                                            level3ElmBatch.setBaseQty(batch.get("BASEQTY").toString());
                                            level2ElmFeature.getBatchList().add(level3ElmBatch);
                                        }
                                    }
                                }
                                level2ElmFeature.setFeatureNo(featureNo);
                                level2ElmFeature.setBaseQty(featureBaseQtyB.toString());
                                oneLv1.getFeatureList().add(level2ElmFeature);
                            }
                        }
                    }

                    //处理批号
                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("BATCHNO", true);
                    List<Map<String, Object>> getBatch=MapDistinct.getMap(stocks,condition);
                    for (Map<String, Object> batch : getBatch){
                        if (pluNo.equals(batch.get("PLUNO").toString()))
                        {
                            String batchNo = batch.get("BATCHNO").toString();
                            if (!Check.Null(batchNo))
                            {
                                DCP_GoodsStockQuery_OpenRes.level2ElmBatch level2ElmBatch = res.new level2ElmBatch();
                                BigDecimal batchBaseQtyB = new BigDecimal("0");

                                for (Map<String, Object> stock : stocks) {
                                    if (pluNo.equals(stock.get("PLUNO").toString()) && batchNo.equals(stock.get("BATCHNO").toString()))
                                    {
                                        batchBaseQtyB = batchBaseQtyB.add(new BigDecimal(stock.get("BASEQTY").toString()));
                                    }
                                }
                                level2ElmBatch.setBatchNo(batchNo);
                                level2ElmBatch.setBaseQty(batchBaseQtyB.toString());
                                oneLv1.getBatchList().add(level2ElmBatch);
                            }
                        }
                    }

                    //汇总库存数量
                    baseQtyB=new BigDecimal("0");
                    for (Map<String, Object> stock : stocks){
                        if (pluNo.equals(stock.get("PLUNO").toString()))
                        {
                            String baseQty = stock.get("BASEQTY").toString();
                            baseQtyB = baseQtyB.add(new BigDecimal(baseQty));
                        }
                    }
                    oneLv1.setPluNo(pluNo);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseQty(baseQtyB.toString());

                    lElm.getPluList().add(oneLv1);
                }
            }
            res.setDatas(lElm);
            res.setVersion("3.0");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceDescription("查询发生异常,异常原因 "+e.getMessage());
        }







    }

    private String getErpPluSql(String withPlu,String eId,String belFirm,String organizationNo,String stockQtyType) throws Exception{

        StringBuffer sqlbuf = new StringBuffer();
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" with goods as ("+withPlu+") ");
        }
        sqlbuf.append(""
                + " select a.pluno,a.baseunit,sum(a.baseqty) as baseqty "
                + " from ("
                + " select pluno,featureno,baseunit,availableqty as baseqty from dcp_headquarter_stock"
                + " where eid='"+eId+"'" );
        if (!Check.Null(belFirm)){
            sqlbuf.append(" and belfirm='"+belFirm+"'");
        }
        if (!Check.Null(organizationNo)){
            sqlbuf.append(" and organizationno='"+organizationNo+"'");
        }
        sqlbuf.append(" ) a ");
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" inner join (select pluno from goods group by pluno) b on a.pluno=b.pluno ");
        }
        sqlbuf.append(" where 1=1 ");

        if (!Check.Null(stockQtyType)) {
            switch (stockQtyType) {
                case "0":             //0.库存大于零
                    sqlbuf.append(" and a.baseqty>0 " );
                    break;
                case "1":             //1.库存不等于零
                    sqlbuf.append(" and a.baseqty<>0 " );
                    break;
                case "2":             //2.库存等于零
                    sqlbuf.append(" and a.baseqty=0 " );
                    break;
                case "3":             // 3.库存小于零
                    sqlbuf.append(" and a.baseqty<0 " );
                    break;
                case "4":             // 4.库存大于等于零
                    sqlbuf.append(" and a.baseqty>=0 " );
                    break;
                case "5":             // 5.库存小于等于零
                    sqlbuf.append(" and a.baseqty<=0 " );
                    break;
            }
        }
        sqlbuf.append(" group by a.pluno,a.baseunit");
        sqlbuf.append(" order by a.pluno");

        return sqlbuf.toString();
    }

    private String getErpPluFeatureSql(String withPlu,String eId,String belFirm,String organizationNo,String stockQtyType) throws Exception{

        StringBuffer sqlbuf = new StringBuffer();
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" with goods as ("+withPlu+") ");
        }
        sqlbuf.append(""
                + " select a.pluno,a.featureno,a.baseunit,sum(a.baseqty) as baseqty "
                + " from ("
                + " select pluno,featureno,baseunit,availableqty as baseqty from dcp_headquarter_stock"
                + " where eid='"+eId+"' and featureno<>' ' ");
        if (!Check.Null(belFirm)){
            sqlbuf.append(" and belfirm='"+belFirm+"'");
        }
        if (!Check.Null(organizationNo)){
            sqlbuf.append(" and organizationno='"+organizationNo+"'");
        }
        sqlbuf.append(" ) a ");
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" inner join goods on a.pluno=goods.pluno"
                    + " and (a.featureno=goods.featureno or trim(goods.featureno) is null)");
        }
        sqlbuf.append(" where 1=1 ");

        if (!Check.Null(stockQtyType)) {
            switch (stockQtyType) {
                case "0":             //0.库存大于零
                    sqlbuf.append(" and a.baseqty>0 " );
                    break;
                case "1":             //1.库存不等于零
                    sqlbuf.append(" and a.baseqty<>0 " );
                    break;
                case "2":             //2.库存等于零
                    sqlbuf.append(" and a.baseqty=0 " );
                    break;
                case "3":             // 3.库存小于零
                    sqlbuf.append(" and a.baseqty<0 " );
                    break;
                case "4":             // 4.库存大于等于零
                    sqlbuf.append(" and a.baseqty>=0 " );
                    break;
                case "5":             // 5.库存小于等于零
                    sqlbuf.append(" and a.baseqty<=0 " );
                    break;
            }
        }
        sqlbuf.append(" group by a.pluno,a.featureno,a.baseunit");

        return sqlbuf.toString();
    }

}
