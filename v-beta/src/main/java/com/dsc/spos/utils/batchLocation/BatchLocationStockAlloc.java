package com.dsc.spos.utils.batchLocation;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchLocationStockAlloc {

    public static List<WarehouseLocationPlu> batchLocationStockAlloc(BatchLocationPlu plu) throws Exception {

        List<BatchLocationPlu> batchLocationPluList = new ArrayList<>();
        batchLocationPluList.add(plu);

        return batchLocationStockAlloc(batchLocationPluList);
    }

    public static List<WarehouseLocationPlu> batchLocationStockAlloc(List<BatchLocationPlu> pluList) throws Exception {
        DsmDAO dao = StaticInfo.dao;

        //1.查询MES_BATCH_STOCK_DETAIL统计当前各个仓库和库位的实时库存，按日期升序，先进先出原则
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.EID,a.FEATURENO,a.ORGANIZATIONNO,a.PLUNO,a.WAREHOUSE,a.LOCATION,a.BATCHNO,a.BASEUNIT,a.PRODDATE,a.VALIDDATE," +
                        " s.STOCK,b.BATCHSORTTYPE,a.QTY,b.ISBATCH,c.ISLOCATION ")
                .append(" FROM MES_BATCH_STOCK_DETAIL a ")
                .append(" LEFT JOIN DCP_GOODS b ON a.eid = b.eid and a.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_WAREHOUSE c ON a.eid = c.eid and a.WAREHOUSE=c.WAREHOUSE ")
                .append(" LEFT JOIN ( SELECT EID,ORGANIZATIONNO,PLUNO,WAREHOUSE,FEATURENO,SUM(QTY) STOCK FROM DCP_STOCK GROUP BY EID,ORGANIZATIONNO,PLUNO,WAREHOUSE,FEATURENO ) s ON a.EID=s.EID and a.ORGANIZATIONNO=s.ORGANIZATIONNO and a.PLUNO=s.PLUNO and a.WAREHOUSE=s.WAREHOUSE AND a.FEATURENO=s.FEATURENO");

        querySql.append("   WHERE a.QTY>0 and ( 1=2 ");
//        querySql.append("   WHERE ( 1=2 ");
        for (BatchLocationPlu onePlu : pluList) {
            querySql.append("   OR ( a.PLUNO='").append(onePlu.getPluNo())
                    .append("' and a.WAREHOUSE='").append(onePlu.getWarehouse())
                    .append("' and a.FEATURENO='").append(StringUtils.toString(onePlu.getFeatureNo(), " "))
                    .append("') ");
        }
        querySql.append(")");

        StringBuilder unitConvertQuery = new StringBuilder(" SELECT EID,PLUNO,UNIT,UNITRATIO FROM DCP_GOODS_UNIT a WHERE 1=2 ");
        for (BatchLocationPlu onePlu : pluList) {
            unitConvertQuery.append(" OR ( a.PLUNO='").append(onePlu.getPluNo()).append("' and OUNIT='").append(onePlu.getPUnit()).append("') ");
        }
        List<Map<String, Object>> qData = dao.executeQuerySQL(querySql.toString(), null);
        List<Map<String, Object>> unitData = dao.executeQuerySQL(unitConvertQuery.toString(), null);

        List<WarehouseLocationPlu> warehouseLocationPluList = new ArrayList<>();

        for (BatchLocationPlu onePlu : pluList) {

            Map<String, Object> condition = Maps.newHashMap();
            condition.put("PLUNO", onePlu.getPluNo());
            condition.put("WAREHOUSE", onePlu.getWarehouse());
            condition.put("FEATURENO", StringUtils.toString(onePlu.getFeatureNo(), " "));

            List<Map<String, Object>> alloc = MapDistinct.getWhereMap(qData, condition, true);

            String isLocation = "N";
            String isBatch = "N";
            String batchSortType = "3";
            if (CollectionUtils.isNotEmpty(alloc)) {
                isLocation = StringUtils.toString(alloc.get(0).get("ISLOCATION"), "N");
                isBatch = StringUtils.toString(alloc.get(0).get("ISBATCH"), "N");
                batchSortType = StringUtils.toString(alloc.get(0).get("BATCHSORTTYPE"), "1"); //防止出现没有配置的情况
            }

            //增加判断逻辑，如果传入了批号和库位则优先取同批号库位的信息进行分配
            double nowPQty = Double.parseDouble(onePlu.getPQty());
            List<Map<String, Object>> sumAlloc = sumAndDistinct(alloc);

            if (StringUtils.isNotEmpty(onePlu.getBatchNo())) {
                String batchNo = onePlu.getBatchNo();
                String location = onePlu.getLocation();
                if (StringUtils.isEmpty(location)) {
                    location = " ";
                }
                condition.put("BATCHNO", batchNo);
                condition.put("LOCATION", location);

                List<Map<String, Object>> batchAlloc = MapDistinct.getWhereMap(qData, condition, true);

                if (CollectionUtils.isNotEmpty(batchAlloc)) {
                    Map<String, Object> oneBatch = batchAlloc.get(0);

                    double qty = Double.parseDouble(oneBatch.get("QTY").toString());

                    WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();
                    warehouseLocationPluList.add(warehouseLocationPlu);

                    warehouseLocationPlu.setId(onePlu.getId());
                    warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                    warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                    warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                    warehouseLocationPlu.setLocation(oneBatch.get("LOCATION").toString());

                    warehouseLocationPlu.setPUnit(onePlu.getPUnit());
                    warehouseLocationPlu.setProdDate(oneBatch.get("PRODDATE").toString());
                    warehouseLocationPlu.setValidDate(oneBatch.get("VALIDDATE").toString());

                    warehouseLocationPlu.setBatchNo(batchNo);
                    warehouseLocationPlu.setStock(String.valueOf(qty)); //库存量

                    if (qty >= nowPQty) {
                        warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                        oneBatch.put("QTY", qty - nowPQty);
                        nowPQty = 0;
                        continue; //分配结束 进行下一分配
                    } else {
                        nowPQty = nowPQty - qty;
                        warehouseLocationPlu.setAllocQty(String.valueOf(qty));

                        String finalLocation = location;

                        qData.removeIf(data -> onePlu.getWarehouse().equals(data.get("WAREHOUSE").toString())
                                && finalLocation.equals(data.get("LOCATION").toString())
                                && batchNo.equals(data.get("BATCHNO").toString())
                                && onePlu.getPluNo().equals(data.get("PLUNO").toString())
                                && onePlu.getFeatureNo().equals(data.get("FEATURENO").toString())
                        );

                        alloc.removeIf(data -> onePlu.getWarehouse().equals(data.get("WAREHOUSE").toString())
                                && finalLocation.equals(data.get("LOCATION").toString())
                                && batchNo.equals(data.get("BATCHNO").toString())
                                && onePlu.getPluNo().equals(data.get("PLUNO").toString())
                                && onePlu.getFeatureNo().equals(data.get("FEATURENO").toString())
                        );

                        sumAlloc.removeIf(data -> onePlu.getWarehouse().equals(data.get("WAREHOUSE").toString())
//                                && finalLocation.equals(data.get("LOCATION").toString())
//                                && batchNo.equals(data.get("BATCHNO").toString())
                                        && onePlu.getPluNo().equals(data.get("PLUNO").toString())
                                        && onePlu.getFeatureNo().equals(data.get("FEATURENO").toString())
                        );

                    }
                }
            }

            //判断商品是否启用库位，如果未启用库位且未启用批号则返回传入数量
            if ("N".equals(isLocation) && "N".equals(isBatch)) {
                WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();

                warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                warehouseLocationPlu.setAllocQty(onePlu.getPQty());
                warehouseLocationPlu.setPUnit(onePlu.getPUnit());
                warehouseLocationPlu.setId(onePlu.getId());
                warehouseLocationPlu.setBatchNo(" ");
                if (CollectionUtils.isNotEmpty(alloc)) {
                    warehouseLocationPlu.setStock(alloc.get(0).get("STOCK").toString()); //库存量
                } else {
                    warehouseLocationPlu.setStock("0"); //库存量
                }

                warehouseLocationPlu.setLocation(" ");
                warehouseLocationPlu.setProdDate("");
                warehouseLocationPlu.setValidDate("");

                warehouseLocationPlu.setSortId(0);

                warehouseLocationPluList.add(warehouseLocationPlu);

            } else if ("Y".equals(isLocation) && "N".equals(isBatch)) {
//                库位库存分配：按【品号+单位+仓库+库位】汇总库存量按库位编号从小到大排序后依次分配

                List<Map<String, Object>> sortedAlloc = sumAlloc.stream()
                        .sorted(Comparator.comparing(e -> MapUtils.getString(e, "LOCATION")))
                        .collect(Collectors.toList());
                for (Map<String, Object> map : sortedAlloc) {
                    double qty = Double.parseDouble(map.get("SQTY").toString());

                    WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();
                    warehouseLocationPluList.add(warehouseLocationPlu);

                    warehouseLocationPlu.setId(onePlu.getId());
                    warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                    warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                    warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                    warehouseLocationPlu.setLocation(map.get("LOCATION").toString());

                    warehouseLocationPlu.setPUnit(onePlu.getPUnit());
                    warehouseLocationPlu.setProdDate(map.get("PRODDATE").toString());
                    warehouseLocationPlu.setValidDate(map.get("VALIDDATE").toString());

                    warehouseLocationPlu.setBatchNo(" ");
                    warehouseLocationPlu.setStock(String.valueOf(qty)); //库存量

                    if (qty >= nowPQty) {
                        warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                        map.put("SQTY", qty - nowPQty);
                        nowPQty = 0;

                        break;
                    } else {
                        nowPQty = nowPQty - qty;
                        warehouseLocationPlu.setAllocQty(String.valueOf(qty));
                        sumAlloc.remove(map);
                        qData.removeIf(data -> onePlu.getWarehouse().equals(data.get("WAREHOUSE").toString())
                                && map.get("LOCATION").toString().equals(data.get("LOCATION").toString())
                                && onePlu.getPluNo().equals(data.get("PLUNO").toString())
                                && onePlu.getFeatureNo().equals(data.get("FEATURENO").toString())
                        );
                    }
                }
                if (nowPQty > 0) { //所有库位都分配完还没分完 则给一笔空库位的
                    WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();

                    warehouseLocationPlu.setId(onePlu.getId());
                    warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                    warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                    warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                    warehouseLocationPlu.setLocation("DEFAULTLOCATION");

                    warehouseLocationPlu.setPUnit(onePlu.getPUnit());
                    warehouseLocationPlu.setBatchNo(" ");

                    if (warehouseLocationPluList.contains(warehouseLocationPlu)) {
                        warehouseLocationPlu = warehouseLocationPluList.get(warehouseLocationPluList.indexOf(warehouseLocationPlu));

                        warehouseLocationPlu.setAllocQty(String.valueOf(Double.parseDouble(warehouseLocationPlu.getAllocQty()) + nowPQty));
                    } else {
                        warehouseLocationPluList.add(warehouseLocationPlu);

                        warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                        warehouseLocationPlu.setStock("0"); //库存量
                    }
                    warehouseLocationPlu.setProdDate("");
                    warehouseLocationPlu.setValidDate("");

                }


            } else if ("Y".equals(isBatch)) {
                //启用批号按批号冲减方式DCP_GOODS.BATCHSORTTYPE分配库存
//          1.先进先出(按生产日期)，根据生产日期PRODDATE 从小到大排序
//          2.先进先出(按有效日期)，根据有效日期EXPDATE 从小到大排序
//          3.人工指定，无批号排序逻辑，无需系统取出，根据前端传入存值

                if ("1".equals(batchSortType)) {
                    List<Map<String, Object>> sortedAlloc = alloc.stream()
//                            .filter(x -> StringUtils.isNotEmpty(x.get("QTY").toString()) && !"0".equals(x.get("QTY").toString()))
                            .sorted(Comparator.comparing(e -> MapUtils.getString(e, "PRODDATE")))
                            .collect(Collectors.toList());

                    for (Map<String, Object> map : sortedAlloc) {
                        double qty = Double.parseDouble(map.get("QTY").toString());

                        WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();
                        warehouseLocationPluList.add(warehouseLocationPlu);

                        warehouseLocationPlu.setId(onePlu.getId());
                        warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                        warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                        warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                        warehouseLocationPlu.setLocation(map.get("LOCATION").toString());

                        warehouseLocationPlu.setPUnit(onePlu.getPUnit());

                        warehouseLocationPlu.setBatchNo(map.get("BATCHNO").toString());
                        warehouseLocationPlu.setStock(String.valueOf(qty)); //库存量
                        warehouseLocationPlu.setProdDate(map.get("PRODDATE").toString());
                        warehouseLocationPlu.setValidDate(map.get("VALIDDATE").toString());

                        if (qty >= nowPQty) {
                            warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                            map.put("QTY", qty - nowPQty);
                            nowPQty = 0;
                            break;
                        } else {
                            nowPQty = nowPQty - qty;
                            warehouseLocationPlu.setAllocQty(String.valueOf(qty));
                            alloc.remove(map);
                            qData.remove(map);
                        }
                    }

                    if (nowPQty > 0) { //所有库位都分配完还没分完 则给一笔空库位的
                        WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();

                        warehouseLocationPlu.setId(onePlu.getId());
                        warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                        warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                        warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                        warehouseLocationPlu.setPUnit(onePlu.getPUnit());

                        if ("Y".equals(isLocation)) {
                            warehouseLocationPlu.setLocation("DEFAULTLOCATION");
                        } else {
                            warehouseLocationPlu.setLocation(" ");
                        }

                        warehouseLocationPlu.setBatchNo("DEFAULTBATCH");

                        if (warehouseLocationPluList.contains(warehouseLocationPlu)) {
                            warehouseLocationPlu = warehouseLocationPluList.get(warehouseLocationPluList.indexOf(warehouseLocationPlu));

                            warehouseLocationPlu.setAllocQty(String.valueOf(Double.parseDouble(warehouseLocationPlu.getAllocQty()) + nowPQty));
                        } else {
                            warehouseLocationPluList.add(warehouseLocationPlu);

                            warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                            warehouseLocationPlu.setStock("0"); //库存量
                        }
                        warehouseLocationPlu.setProdDate("");
                        warehouseLocationPlu.setValidDate("");

                    }

                } else if ("2".equals(batchSortType)) {
                    List<Map<String, Object>> sortedAlloc = alloc.stream()
//                            .filter(x -> StringUtils.isNotEmpty(x.get("QTY").toString()) && !"0".equals(x.get("QTY").toString()))
                            .sorted(Comparator.comparing(e -> MapUtils.getString(e, "VALIDDATE")))
                            .collect(Collectors.toList());
                    for (Map<String, Object> map : sortedAlloc) {
                        double qty = Double.parseDouble(map.get("QTY").toString());

                        WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();
                        warehouseLocationPluList.add(warehouseLocationPlu);

                        warehouseLocationPlu.setId(onePlu.getId());
                        warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                        warehouseLocationPlu.setFeatureNo(onePlu.getFeatureNo());
                        warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                        warehouseLocationPlu.setLocation(map.get("LOCATION").toString());

                        warehouseLocationPlu.setPUnit(onePlu.getPUnit());

                        warehouseLocationPlu.setBatchNo(map.get("BATCHNO").toString());
                        warehouseLocationPlu.setStock(String.valueOf(qty)); //库存量

                        warehouseLocationPlu.setProdDate(map.get("PRODDATE").toString());
                        warehouseLocationPlu.setValidDate(map.get("VALIDDATE").toString());

                        if (qty >= nowPQty) {
                            warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                            map.put("QTY", qty - nowPQty);
                            nowPQty = 0;

                            break;
                        } else {
                            nowPQty = nowPQty - qty;
                            warehouseLocationPlu.setAllocQty(String.valueOf(qty));
                            alloc.remove(map);
                            qData.remove(map);
                        }
                    }
                    if (nowPQty > 0) { //所有库位都分配完还没分完 则给一笔空库位的
                        WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();

                        warehouseLocationPlu.setId(onePlu.getId());
                        warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                        warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                        warehouseLocationPlu.setPUnit(onePlu.getPUnit());

                        if ("Y".equals(isLocation)) {
                            warehouseLocationPlu.setLocation("DEFAULTLOCATION");
                        } else {
                            warehouseLocationPlu.setLocation(" ");
                        }
                        warehouseLocationPlu.setBatchNo("DEFAULTBATCH");

                        if (warehouseLocationPluList.contains(warehouseLocationPlu)) {
                            warehouseLocationPlu = warehouseLocationPluList.get(warehouseLocationPluList.indexOf(warehouseLocationPlu));

                            warehouseLocationPlu.setAllocQty(String.valueOf(Double.parseDouble(warehouseLocationPlu.getAllocQty()) + nowPQty));
                        } else {
                            warehouseLocationPluList.add(warehouseLocationPlu);

                            warehouseLocationPlu.setAllocQty(String.valueOf(nowPQty));
                            warehouseLocationPlu.setStock("0"); //库存量
                        }

                        warehouseLocationPlu.setProdDate("");
                        warehouseLocationPlu.setValidDate("");

                    }

                } else if ("3".equals(batchSortType)) {
                    WarehouseLocationPlu warehouseLocationPlu = new WarehouseLocationPlu();

                    warehouseLocationPlu.setWarehouse(onePlu.getWarehouse());
                    warehouseLocationPlu.setPluNo(onePlu.getPluNo());
                    warehouseLocationPlu.setAllocQty(onePlu.getPQty());
                    warehouseLocationPlu.setPUnit(onePlu.getPUnit());
                    warehouseLocationPlu.setId(onePlu.getId());
                    warehouseLocationPlu.setBatchNo(onePlu.getBatchNo());
                    warehouseLocationPlu.setStock(alloc.get(0).get("STOCK").toString()); //库存量
                    warehouseLocationPlu.setLocation(onePlu.getLocation());
                    warehouseLocationPlu.setProdDate(onePlu.getProdDate());
                    warehouseLocationPlu.setValidDate(onePlu.getValidDate());
                    warehouseLocationPluList.add(warehouseLocationPlu);
                }

            }

        }

        return warehouseLocationPluList;
    }


    //根据商品和仓库合并
    private static List<Map<String, Object>> sumAndDistinct(List<Map<String, Object>> alloc) {

        Map<String, Boolean> condition = Maps.newHashMap();
//
//        List<Map<String, Object>> filterMap = alloc.stream()
//                .filter(x -> StringUtils.isNotEmpty(x.get("QTY").toString()) && !"0".equals(x.get("QTY").toString()))
//                .collect(Collectors.toList());

        condition.put("PLUNO", true);
        condition.put("FEATURENO", true);
        condition.put("WAREHOUSE", true);
        condition.put("LOCATION", true);

        List<Map<String, Object>> distinctAlloc = MapDistinct.getMap(alloc, condition);

        for (Map<String, Object> map : distinctAlloc) {
            Map<String, Object> mulCondition = Maps.newHashMap();
            mulCondition.put("PLUNO", map.get("PLUNO").toString());
            mulCondition.put("FEATURENO", map.get("FEATURENO").toString());
            mulCondition.put("WAREHOUSE", map.get("WAREHOUSE").toString());
            mulCondition.put("LOCATION", map.get("LOCATION").toString());

            List<Map<String, Object>> mul = MapDistinct.getWhereMap(alloc, mulCondition, true);
            double qty = 0;
            for (Map<String, Object> map2 : mul) {
                qty += Double.parseDouble(map2.get("QTY").toString());
            }
            map.put("SQTY", qty);
        }

        return distinctAlloc;
    }


}
