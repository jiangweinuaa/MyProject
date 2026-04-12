package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dsc.spos.json.cust.req.DCP_BatchStockDistributionReq;
import com.dsc.spos.json.cust.res.DCP_BatchStockDistributionRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DCP_BatchStockDistribution extends SPosBasicService<DCP_BatchStockDistributionReq, DCP_BatchStockDistributionRes> {

    @Override
    protected boolean isVerifyFail(DCP_BatchStockDistributionReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_BatchStockDistributionReq> getRequestType() {
        return new TypeToken<DCP_BatchStockDistributionReq>(){};
    }

    @Override
    protected DCP_BatchStockDistributionRes getResponseType() {
        return new DCP_BatchStockDistributionRes();
    }

    @Override
    protected DCP_BatchStockDistributionRes processJson(DCP_BatchStockDistributionReq req) throws Exception {
        DCP_BatchStockDistributionRes res = this.getResponse();



        DCP_BatchStockDistributionReq.RequestLevel request = req.getRequest();
        String pluNo = request.getPluNo();
        String featureNo = request.getFeatureNo();
        String batchNo = request.getBatchNo();
        Boolean inStockOnly = request.getInStockOnly();
        List<String> locationList = request.getLocation();
        List<String> organizationNoList = request.getOrganizationNo();
        List<String> warehouseList = request.getWarehouse();

        //品号批号唯一

        MyCommon mc = new MyCommon();
        String withLocation = "";
        if (locationList !=null && locationList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinLocation = "";
            for(String s :locationList) {
                sJoinLocation += s +",";
            }
            map.put("LOCATION", sJoinLocation);
            withLocation = mc.getFormatSourceMultiColWith(map);
        }

        String withOrganizationNo = "";
        if (organizationNoList !=null && organizationNoList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinOrganizationNo = "";
            for(String s :organizationNoList) {
                sJoinOrganizationNo += s +",";
            }
            map.put("ORGANIZATIONNO", sJoinOrganizationNo);
            withOrganizationNo = mc.getFormatSourceMultiColWith(map);
        }

        String withWarehouse = "";
        if (warehouseList !=null && warehouseList.size()>0 ) {
            Map<String,String> map = new HashMap<>();
            String sJoinP = "";
            for(String s :warehouseList) {
                sJoinP += s +",";
            }
            map.put("WAREHOUSE", sJoinP);
            withWarehouse = mc.getFormatSourceMultiColWith(map);
        }

        StringBuffer sb=new StringBuffer("");
        if(Check.NotNull(withLocation)){
            sb.append(" with location a ("+withLocation+") ");
        }
        if(Check.NotNull(withOrganizationNo)){
            sb.append(" with org as ("+withOrganizationNo+") ");
        }
        if(Check.NotNull(withWarehouse)){
            sb.append(" with warehouse as ("+withWarehouse+") ");
        }

        sb.append("select a.pluno,a.featureno,a.batchno,c.plu_name as pluname,b.wunit,b.baseunit," +
                " d.location,nvl(d.qty,0) qty,e.featurename,b.wunit,b.baseunit ,f.uname as wunitname,g.uname as baseunitname," +
                " h.unitratio,d.organizationno,i.org_name as organizationname,d.warehouse,j.warehouse_name as warehousename," +
                " d.location,k.locationname,nvl(d.LOCKQTY,0) as lockqty " +
                " from mes_batch a" +
                " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " inner join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join MES_BATCH_STOCK_DETAIL d on d.eid=a.eid  and d.batchno=a.batchno and d.pluno=a.pluno and d.featureno=a.featureno  " +
                " left join dcp_goods_feature_lang e on e.eid=a.eid and e.pluno=a.pluno and e.featureno=a.featureno and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=b.wunit and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=b.baseunit and g.lang_type='"+req.getLangType()+"'  " +
                " left join dcp_goods_unit h on h.eid=a.eid and h.unit=b.wunit and h.ounit=b.baseunit and h.pluno=b.pluno" +
                " left join dcp_org_lang i on i.eid=a.eid and i.organizationno=d.organizationno and i.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang j on j.eid=a.eid and j.organizationno=i.organizationno and j.warehouse=d.warehouse and j.lang_type='"+req.getLangType()+"' " +
                " left join dcp_location k on k.eid=a.eid and k.organizationno=d.organizationno and k.warehouse=d.warehouse and k.location=d.location" +
                "  ");
        if(Check.NotNull(withLocation)){
            sb.append("inner join location location a on d.location=location.location ");
        }
        if(Check.NotNull(withOrganizationNo)){
            sb.append("inner join org org on org.organizationno=d.organizationno ");
        }
        if(Check.NotNull(withWarehouse)){
            sb.append("inner join warehouse warehouse  on d.warehouse=warehouse.warehouse ");
        }
        sb.append(" where a.eid='"+req.geteId()+"' and a.pluno='"+pluNo+"' and a.batchno='"+batchNo+"'" +
                " ");
        if(Check.NotNull(featureNo)){
            sb.append(" and a.featureno='"+featureNo+"' ");
        }
        if(inStockOnly!=null&&inStockOnly){
            sb.append(" and nvl(d.qty,0)>0 ");
        }

        List<Map<String, Object>> list = this.doQueryData(sb.toString(), null);

        Map<String,List<Map<String, Object>>> map = list.stream()
                .collect(Collectors.groupingBy(item -> item.get("PLUNO").toString() + item.get("FEATURENO").toString() + item.get("BATCHNO").toString()));
        for (String key : map.keySet()){
            List<Map<String, Object>> filterRows1 = map.get(key);
            Map<String, Object> sm1 = filterRows1.get(0);
            DCP_BatchStockDistributionRes.DatasLevel datasLevel = res.new DatasLevel();
            datasLevel.setPluNo(sm1.get("PLUNO").toString());
            datasLevel.setPluName(sm1.get("PLUNAME").toString());
            datasLevel.setFeatureNo(sm1.get("FEATURENO").toString());
            datasLevel.setFeatureName(sm1.get("FEATURENAME").toString());
            datasLevel.setBatchNo(sm1.get("BATCHNO").toString());
            datasLevel.setWUnit(sm1.get("WUNIT").toString());
            datasLevel.setBaseUnit(sm1.get("BASEUNIT").toString());
            datasLevel.setUnitRatio(sm1.get("UNITRATIO").toString());
            datasLevel.setWUnitName(sm1.get("WUNITNAME").toString());
            datasLevel.setBaseUnitName(sm1.get("BASEUNITNAME").toString());

            BigDecimal totStockQty=BigDecimal.ZERO;
            BigDecimal top5StockQtyPercent=BigDecimal.ZERO;
            BigDecimal orgCnt=BigDecimal.ZERO;
            BigDecimal warehouseCnt=BigDecimal.ZERO;

            datasLevel.setDetail(new ArrayList<>());

            Map<String, List<Map<String, Object>>> map2 = filterRows1.stream().collect(Collectors.groupingBy(item -> item.get("ORGANIZATIONNO").toString()));
            for (String key2 : map2.keySet()){
                List<Map<String, Object>> filterRows2 = map2.get(key2);
                Map<String, Object> sm2 = filterRows2.get(0);

                DCP_BatchStockDistributionRes.DetailLevel detailLevel = res.new DetailLevel();
                detailLevel.setOrganizationNo(sm2.get("ORGANIZATIONNO").toString());
                detailLevel.setOrganizationName(sm2.get("ORGANIZATIONNAME").toString());

                BigDecimal orgQty=new BigDecimal(0);
                BigDecimal orgLockQty=new BigDecimal(0);
                BigDecimal orgOnLineQty=new BigDecimal(0);
                BigDecimal orgAvailableQty=new BigDecimal(0);


                detailLevel.setChildren(new ArrayList<>());

                Map<String, List<Map<String, Object>>> map3 = filterRows2.stream().collect(Collectors.groupingBy(item -> item.get("WAREHOUSE").toString()));
                for (String key3 : map3.keySet()){
                    List<Map<String, Object>> filterRows3 = map3.get(key3);
                    Map<String, Object> sm3 = filterRows3.get(0);
                    DCP_BatchStockDistributionRes.ChildrenLevel1 childrenLevel1 = res.new ChildrenLevel1();
                    childrenLevel1.setWarehouseNo(sm3.get("WAREHOUSE").toString());
                    childrenLevel1.setWarehouseName(sm3.get("WAREHOUSENAME").toString());
                    BigDecimal wareQty=new BigDecimal(0);
                    BigDecimal wareLockQty=new BigDecimal(0);
                    BigDecimal wareOnLineQty=new BigDecimal(0);
                    BigDecimal wareAvailableQty=new BigDecimal(0);
                    childrenLevel1.setChildren(new ArrayList<>());
                    Map<String, List<Map<String, Object>>> map4 = filterRows3.stream().collect(Collectors.groupingBy(item -> item.get("LOCATION").toString()));
                    for (String key4 : map4.keySet()){
                        List<Map<String, Object>> filterRows4 = map4.get(key4);
                        Map<String, Object> sm4 = filterRows4.get(0);
                        DCP_BatchStockDistributionRes.ChildrenLevel2 childrenLevel2 = res.new ChildrenLevel2();
                        childrenLevel2.setLocation(sm4.get("LOCATION").toString());
                        childrenLevel2.setLocationName(sm4.get("LOCATIONNAME").toString());
                        BigDecimal loQty=new BigDecimal(sm4.get("QTY").toString());
                        BigDecimal loLockQty=new BigDecimal(sm4.get("LOCKQTY").toString());
                        BigDecimal loOnLineQty=new BigDecimal("0");
                        BigDecimal loAvailableQty=loQty.subtract(loLockQty);

                        childrenLevel2.setQty(loQty.toString());
                        childrenLevel2.setLockQty(loLockQty.toString());
                        childrenLevel2.setOnLineQty(loOnLineQty.toString());
                        childrenLevel2.setAvailableQty(loAvailableQty.toString());

                        wareQty=wareQty.add(loQty);
                        wareLockQty=wareLockQty.add(loLockQty);
                        wareOnLineQty=wareOnLineQty.add(loOnLineQty);
                        childrenLevel1.getChildren().add(childrenLevel2);
                    }

                    wareAvailableQty=wareQty.subtract(wareLockQty);
                    childrenLevel1.setQty(wareQty.toString());
                    childrenLevel1.setLockQty(wareLockQty.toString());
                    childrenLevel1.setOnLineQty(wareOnLineQty.toString());
                    childrenLevel1.setAvailableQty(wareAvailableQty.toString());
                    detailLevel.getChildren().add(childrenLevel1);
                    orgQty=orgQty.add(wareQty);
                    orgLockQty=orgLockQty.add(wareLockQty);
                    orgOnLineQty=orgOnLineQty.add(wareOnLineQty);
                }
                orgAvailableQty=orgQty.subtract(orgLockQty);
                detailLevel.setQty(orgQty.toString());
                detailLevel.setLockQty(orgLockQty.toString());
                detailLevel.setOnLineQty(orgOnLineQty.toString());
                detailLevel.setAvailableQty(orgAvailableQty.toString());
                datasLevel.getDetail().add(detailLevel);

                totStockQty=totStockQty.add(orgQty);

            }

            datasLevel.setTotStockQty(totStockQty.toString());
            orgCnt=new BigDecimal(map2.size());
            datasLevel.setOrgCnt(orgCnt.toString());

            int warehouseSize = filterRows1.stream().map(x -> x.get("WAREHOUSE").toString()).distinct().collect(Collectors.toList()).size();
            warehouseCnt=new BigDecimal(warehouseSize);
            datasLevel.setWarehouseCnt(warehouseCnt.toString());

            List<DCP_BatchStockDistributionRes.DetailLevel> details = datasLevel.getDetail();

            // 按 qty 降序排序
            details.sort((o1, o2) -> new BigDecimal(o2.getQty()).compareTo(new BigDecimal(o1.getQty())));

            // 计算前5个 qty 的和（注意 size 不足5的情况）
            BigDecimal top5Sum = IntStream.range(0, Math.min(5, details.size()))
                    .mapToObj(details::get)
                    .map(detail -> new BigDecimal(detail.getQty()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 计算所有 qty 的总和
            BigDecimal totalSum = details.stream()
                    .map(detail -> new BigDecimal(detail.getQty()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 防止除以0
            BigDecimal ratio = BigDecimal.ZERO;
            if (totalSum.compareTo(BigDecimal.ZERO) != 0) {
                ratio = top5Sum.divide(totalSum, 4, RoundingMode.HALF_UP); // 保留4位小数
            }
            top5StockQtyPercent=ratio.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
            datasLevel.setTop5StockQtyPercent(top5StockQtyPercent.toString());
            res.setDatas(datasLevel);
        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_BatchStockDistributionReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();


        return sqlbuf.toString();
    }
}


