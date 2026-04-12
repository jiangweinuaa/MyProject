package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockInMaterialBatchRefreshReq;
import com.dsc.spos.json.cust.res.DCP_PStockInMaterialBatchRefreshRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PStockInMaterialBatchRefresh  extends SPosAdvanceService<DCP_PStockInMaterialBatchRefreshReq, DCP_PStockInMaterialBatchRefreshRes>
{
    @Override
    protected void processDUID(DCP_PStockInMaterialBatchRefreshReq req, DCP_PStockInMaterialBatchRefreshRes res) throws Exception {
        // TODO Auto-generated method stub
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();;
        String pStockInNO = req.getRequest().getPStockInNo();
        String shopId = req.getShopId();
        String docType = req.getRequest().getDocType();
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");


        DelBean db4 = new DelBean("DCP_PSTOCKOUT_BATCH");
        db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db4.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
        db4.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));


        String materialSql="select * from DCP_PSTOCKIN_MATERIAL a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' ";
        List<Map<String, Object>> materialList=this.doQueryData(materialSql,null);

        List<String> materialWarehouseList = materialList.stream().map(x -> x.get("WAREHOUSE").toString()).distinct().collect(Collectors.toList());
        List<String> materialPluNoList = materialList.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());

        String detailSql="select * from dcp_pstockin_detail a where a.eid='"+eId+"' and a.pstockinno='"+pStockInNO+"' and a.organizationno='"+organizationNO+"' ";
        List<Map<String, Object>> detailList=this.doQueryData(detailSql,null);
        List<String> detailWarehouseList = detailList.stream().map(x -> x.get("WAREHOUSE").toString()).distinct().collect(Collectors.toList());
        List<String> detailPluNoList = detailList.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());


        if("1".equals(docType)||"0".equals(docType)){

            StringBuffer sJoinWarehouse=new StringBuffer("");
            for (String thisWarehouse : materialWarehouseList){
                sJoinWarehouse.append(thisWarehouse+",");
            }
            Map<String, String> mapWarehouse=new HashMap<String, String>();
            mapWarehouse.put("WAREHOUSE", sJoinWarehouse.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono1=cm.getFormatSourceMultiColWith(mapWarehouse);

            if (withasSql_mono1.equals(""))
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
            }

            String warehouseSql="with p as ("+withasSql_mono1+") "+
                    " select a.ISLOCATION,a.warehouse " +
                    " from DCP_WAREHOUSE a " +
                    " inner join p on p.warehouse=a.warehouse "+
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                    ;
            List<Map<String, Object>> getWarehouseData=this.doQueryData(warehouseSql, null);


            StringBuffer sJoinPluNo=new StringBuffer("");
            for (String thisPluno : materialPluNoList){
                sJoinPluNo.append(thisPluno+",");
            }
            Map<String, String> mapPluNo=new HashMap<String, String>();
            mapPluNo.put("PLUNO", sJoinPluNo.toString());

            String withasSql_mono2=cm.getFormatSourceMultiColWith(mapPluNo);

            if (withasSql_mono1.equals(""))
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
            }

            String pluNoSql="with p as ("+withasSql_mono2+") "+
                    " select a.ISBATCH,a.PLUNO,a.BATCHSORTTYPE " +
                    " from DCP_GOODS a " +
                    " inner join p on p.pluno=a.pluno "+
                    " where a.eid='"+req.geteId()+"'  "
                    ;
            List<Map<String, Object>> getPluData=this.doQueryData(pluNoSql, null);

            //MES_BATCH_STOCK_DETAIL
            String stockSql="with p as ("+withasSql_mono2+") "+
                    " select a.*,to_char(a.PRODDATE,'yyyyMMdd') as proddatestr,to_char(a.VALIDDATE,'yyyyMMdd') as VALIDDATESTR " +
                    " from MES_BATCH_STOCK_DETAIL a " +
                    " inner join p on p.pluno=a.pluno "+
                    " where a.eid='"+req.geteId()+"' and a.qty>0 "
                    ;
            List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

            List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> stockInfos = getStockData.stream().map(x -> {
                DCP_PStockInMaterialBatchRefreshReq.StockInfo stockInfo = req.new StockInfo();
                stockInfo.setPluNo(x.get("PLUNO").toString());
                stockInfo.setFeatureNo(x.get("FEATURENO").toString());
                stockInfo.setWarehouse(x.get("WAREHOUSE").toString());
                stockInfo.setBatchNo(x.get("BATCHNO").toString());
                stockInfo.setLocation(x.get("LOCATION").toString());
                stockInfo.setBaseUnit(x.get("BASEUNIT").toString());
                stockInfo.setQty(x.get("QTY").toString());
                stockInfo.setLockQty(x.get("LOCKQTY").toString());
                stockInfo.setProdDate(x.get("PRODDATESTR").toString());
                stockInfo.setValidDate(x.get("VALIDDATESTR").toString());
                return stockInfo;
            }).collect(Collectors.toList());

            int detailItem=0;
                for (Map<String, Object> oneDataMaterial : materialList) {
                    String material_item = oneDataMaterial.get("ITEM").toString();
                    BigDecimal material_pqty = new BigDecimal(oneDataMaterial.get("PQTY").toString());
                    String material_warehouse = oneDataMaterial.get("WAREHOUSE").toString();
                    String material_pluNo = oneDataMaterial.get("PLUNO").toString();
                    String featureNo = oneDataMaterial.get("FEATURENO").toString();
                    String pUnit = oneDataMaterial.get("PUNIT").toString();
                    String unitRatio = oneDataMaterial.get("UNIT_RATIO").toString();
                    String baseUnit = oneDataMaterial.get("BASEUNIT").toString();

                    String location = oneDataMaterial.get("LOCATION").toString();
                    String batchNo = oneDataMaterial.get("BATCH_NO").toString();
                    String expDate = oneDataMaterial.get("EXPDATE").toString();
                    String pQty = oneDataMaterial.get("PQTY").toString();
                    String baseQty = oneDataMaterial.get("BASEQTY").toString();
                    String prodDate = oneDataMaterial.get("PROD_DATE").toString();

                    List<Map<String, Object>> singlePluInfos = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(material_pluNo)).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(singlePluInfos)){
                        String isBatch = singlePluInfos.get(0).get("ISBATCH").toString();
                        String batchSortType = singlePluInfos.get(0).get("BATCHSORTTYPE").toString();
                        if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                            List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(material_pluNo) && x.getWarehouse().equals(material_warehouse)).collect(Collectors.toList());
                            if("1".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getProdDate()));
                            }
                            if("2".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getValidDate()));
                            }

                            for(DCP_PStockInMaterialBatchRefreshReq.StockInfo oneStock : singlePluStock){
                                detailItem++;
                                BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                if(material_pqty.compareTo(BigDecimal.ZERO)<=0){
                                    continue;//分完了
                                }
                                BigDecimal fpQty=new BigDecimal("0");
                                if(stockQty.compareTo(material_pqty)>=0){
                                    fpQty=material_pqty;
                                }else{
                                    fpQty=stockQty;
                                }
                                if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                    continue;
                                }

                                material_pqty=material_pqty.subtract(fpQty);

                                Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, pUnit);
                                BigDecimal thisPQty = fpQty.divide(new BigDecimal(unitRatio), pUnitUdlength);

                                //存表
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString(detailItem+""));
                                mbColumns.add("OITEM",DataValues.newString(material_item));
                                mbColumns.add("PLUNO",DataValues.newString(material_pluNo));
                                mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                                mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                                mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                                mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                                mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));

                                //stockInfos 扣减数量
                                for (DCP_PStockInMaterialBatchRefreshReq.StockInfo oldInfo :stockInfos){
                                    if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                            oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                        oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                    }
                                }
                            }


                        }
                        else{
                            //仓库
                            List<Map<String, Object>> warehouseInfos = getWarehouseData.stream().filter(x -> x.get("WAREHOUSE").toString().equals(material_warehouse)).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(warehouseInfos)){
                                String islocation = warehouseInfos.get(0).get("ISLOCATION").toString();
                                if("N".equals(islocation)){
                                    detailItem++;
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(detailItem+""));
                                    mbColumns.add("OITEM",DataValues.newString(material_item));
                                    mbColumns.add("PLUNO",DataValues.newString(material_pluNo));
                                    mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                    mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                    mbColumns.add("PQTY",DataValues.newString(pQty));
                                    mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                    mbColumns.add("BASEQTY",DataValues.newString(baseQty));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(material_warehouse));
                                    mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                    mbColumns.add("BATCHNO",DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                                    mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                    mbColumns.add("EXPDATE",DataValues.newString(expDate));


                                    String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                    DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                    ibmb.addValues(mbDataValues);
                                    this.addProcessData(new DataProcessBean(ibmb));

                                }
                                else {
                                    List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(material_pluNo) && x.getWarehouse().equals(material_warehouse)).sorted(Comparator.comparing(x -> x.getLocation())).collect(Collectors.toList());
                                    for(DCP_PStockInMaterialBatchRefreshReq.StockInfo oneStock : singlePluStock){
                                        detailItem++;
                                        BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                        if(material_pqty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;//分完了
                                        }
                                        BigDecimal fpQty=new BigDecimal("0");
                                        if(stockQty.compareTo(material_pqty)>=0){
                                            fpQty=material_pqty;
                                        }else{
                                            fpQty=stockQty;
                                        }
                                        if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;
                                        }

                                        material_pqty=material_pqty.subtract(fpQty);

                                        Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, pUnit);
                                        BigDecimal thisPQty = fpQty.divide(new BigDecimal(unitRatio), pUnitUdlength);

                                        //存表
                                        ColumnDataValue mbColumns=new ColumnDataValue();
                                        mbColumns.add("EID", DataValues.newString(eId));
                                        mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                        mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                        mbColumns.add("ITEM",DataValues.newString(detailItem+""));
                                        mbColumns.add("OITEM",DataValues.newString(material_item));
                                        mbColumns.add("PLUNO",DataValues.newString(material_pluNo));
                                        mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                        mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                        mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                        mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                        mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                        mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                        mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                                        mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                                        mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                                        mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                                        mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                                        String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                        DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                        ibmb.addValues(mbDataValues);
                                        this.addProcessData(new DataProcessBean(ibmb));

                                        //stockInfos 扣减数量
                                        for (DCP_PStockInMaterialBatchRefreshReq.StockInfo oldInfo :stockInfos){
                                            if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                                    oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                                oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }


                }

                this.doExecuteDataToDB();
        }

        else  if("2".equals(docType)){

            StringBuffer sJoinWarehouse=new StringBuffer("");
            for (String thisWarehouse : detailWarehouseList){
                sJoinWarehouse.append(thisWarehouse+",");
            }
            Map<String, String> mapWarehouse=new HashMap<String, String>();
            mapWarehouse.put("WAREHOUSE", sJoinWarehouse.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono1=cm.getFormatSourceMultiColWith(mapWarehouse);

            if (withasSql_mono1.equals(""))
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
            }

            String warehouseSql="with p as ("+withasSql_mono1+") "+
                    " select a.ISLOCATION,a.warehouse " +
                    " from DCP_WAREHOUSE a " +
                    " inner join p on p.warehouse=a.warehouse "+
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                    ;
            List<Map<String, Object>> getWarehouseData=this.doQueryData(warehouseSql, null);


            StringBuffer sJoinPluNo=new StringBuffer("");
            for (String thisPluno : detailPluNoList){
                sJoinPluNo.append(thisPluno+",");
            }
            Map<String, String> mapPluNo=new HashMap<String, String>();
            mapPluNo.put("PLUNO", sJoinPluNo.toString());

            String withasSql_mono2=cm.getFormatSourceMultiColWith(mapPluNo);

            if (withasSql_mono1.equals(""))
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
            }

            String pluNoSql="with p as ("+withasSql_mono2+") "+
                    " select a.ISBATCH,a.PLUNO,a.BATCHSORTTYPE " +
                    " from DCP_GOODS a " +
                    " inner join p on p.pluno=a.pluno "+
                    " where a.eid='"+req.geteId()+"'  "
                    ;
            List<Map<String, Object>> getPluData=this.doQueryData(pluNoSql, null);

            //MES_BATCH_STOCK_DETAIL
            String stockSql="with p as ("+withasSql_mono2+") "+
                    " select a.*,to_char(a.proddate,'yyyyMMdd') proddates,to_char(a.validdate,'yyyyMMdd') validdates  " +
                    " from MES_BATCH_STOCK_DETAIL a " +
                    " inner join p on p.pluno=a.pluno "+
                    " where a.eid='"+req.geteId()+"'  and a.QTY>0  "
                    ;
            List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

            List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> stockInfos = getStockData.stream().map(x -> {
                DCP_PStockInMaterialBatchRefreshReq.StockInfo stockInfo = req.new StockInfo();
                stockInfo.setPluNo(x.get("PLUNO").toString());
                stockInfo.setFeatureNo(x.get("FEATURENO").toString());
                stockInfo.setWarehouse(x.get("WAREHOUSE").toString());
                stockInfo.setBatchNo(x.get("BATCHNO").toString());
                stockInfo.setLocation(x.get("LOCATION").toString());
                stockInfo.setBaseUnit(x.get("BASEUNIT").toString());
                stockInfo.setQty(x.get("QTY").toString());
                stockInfo.setLockQty(x.get("LOCKQTY").toString());
                stockInfo.setProdDate(x.get("PRODDATES").toString());
                stockInfo.setValidDate(x.get("VALIDDATES").toString());
                return stockInfo;
            }).collect(Collectors.toList());

            for (Map<String, Object> oneDataDetail : detailList) {
                String item = oneDataDetail.get("ITEM").toString();
                BigDecimal pqty = new BigDecimal(oneDataDetail.get("PQTY").toString());
                String warehouse = oneDataDetail.get("WAREHOUSE").toString();
                String pluNo = oneDataDetail.get("PLUNO").toString();
                String featureNo = oneDataDetail.get("FEATURENO").toString();
                String pUnit = oneDataDetail.get("PUNIT").toString();
                String unitRatio = oneDataDetail.get("UNIT_RATIO").toString();
                String baseUnit = oneDataDetail.get("BASEUNIT").toString();

                String location = oneDataDetail.get("LOCATION").toString();
                String batchNo = oneDataDetail.get("BATCH_NO").toString();
                String expDate = oneDataDetail.get("EXPDATE").toString();
                String pQty = oneDataDetail.get("PQTY").toString();
                String baseQty = oneDataDetail.get("BASEQTY").toString();
                String prodDate = oneDataDetail.get("PROD_DATE").toString();

                List<Map<String, Object>> singlePluInfos = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(singlePluInfos)){
                    String isBatch = singlePluInfos.get(0).get("ISBATCH").toString();
                    String batchSortType = singlePluInfos.get(0).get("BATCHSORTTYPE").toString();
                    if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                        List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(pluNo) && x.getWarehouse().equals(warehouse)).collect(Collectors.toList());
                        if("1".equals(batchSortType)){
                            singlePluStock.sort(Comparator.comparing(x -> x.getProdDate()));
                        }
                        if("2".equals(batchSortType)){
                            singlePluStock.sort(Comparator.comparing(x -> x.getValidDate()));
                        }

                        int detailItem=0;
                        for(DCP_PStockInMaterialBatchRefreshReq.StockInfo oneStock : singlePluStock){
                            detailItem++;
                            BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                            if(pqty.compareTo(BigDecimal.ZERO)<=0){
                                continue;//分完了
                            }
                            BigDecimal fpQty=new BigDecimal("0");
                            if(stockQty.compareTo(pqty)>=0){
                                fpQty=pqty;
                            }else{
                                fpQty=stockQty;
                            }
                            if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                continue;
                            }

                            pqty=pqty.subtract(fpQty);

                            Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, pUnit);
                            BigDecimal thisPQty = fpQty.divide(new BigDecimal(unitRatio), pUnitUdlength);

                            //存表
                            ColumnDataValue mbColumns=new ColumnDataValue();
                            mbColumns.add("EID", DataValues.newString(eId));
                            mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                            mbColumns.add("ITEM",DataValues.newString(detailItem+""));
                            mbColumns.add("OITEM",DataValues.newString(item));
                            mbColumns.add("PLUNO",DataValues.newString(pluNo));
                            mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                            mbColumns.add("PUNIT",DataValues.newString(pUnit));
                            mbColumns.add("PQTY",DataValues.newString(thisPQty));
                            mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                            mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                            mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                            mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                            mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                            mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                            mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                            mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                            String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                            DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                            ibmb.addValues(mbDataValues);
                            this.addProcessData(new DataProcessBean(ibmb));

                            //stockInfos 扣减数量
                            for (DCP_PStockInMaterialBatchRefreshReq.StockInfo oldInfo :stockInfos){
                                if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                        oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                    oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                }
                            }
                        }


                    }else{
                        //仓库
                        List<Map<String, Object>> warehouseInfos = getWarehouseData.stream().filter(x -> x.get("WAREHOUSE").toString().equals(warehouse)).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(warehouseInfos)){
                            String islocation = warehouseInfos.get(0).get("ISLOCATION").toString();
                            if("N".equals(islocation)){
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString("1"));
                                mbColumns.add("OITEM",DataValues.newString(item));
                                mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                mbColumns.add("PQTY",DataValues.newString(pQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                mbColumns.add("BASEQTY",DataValues.newString(baseQty));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                mbColumns.add("BATCHNO",DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                                mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                mbColumns.add("EXPDATE",DataValues.newString(expDate));


                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));

                            }
                            else {
                                int detailItem=0;
                                List<DCP_PStockInMaterialBatchRefreshReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(pluNo) && x.getWarehouse().equals(warehouse)).sorted(Comparator.comparing(x -> x.getLocation())).collect(Collectors.toList());
                                for(DCP_PStockInMaterialBatchRefreshReq.StockInfo oneStock : singlePluStock){
                                    detailItem++;
                                    BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                    if(pqty.compareTo(BigDecimal.ZERO)<=0){
                                        continue;//分完了
                                    }
                                    BigDecimal fpQty=new BigDecimal("0");
                                    if(stockQty.compareTo(pqty)>=0){
                                        fpQty=pqty;
                                    }else{
                                        fpQty=stockQty;
                                    }
                                    if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                        continue;
                                    }

                                    pqty=pqty.subtract(fpQty);

                                    Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, pUnit);
                                    BigDecimal thisPQty = fpQty.divide(new BigDecimal(unitRatio), pUnitUdlength);

                                    //存表
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(detailItem+""));
                                    mbColumns.add("OITEM",DataValues.newString(item));
                                    mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                    mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                    mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                    mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                    mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                    mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                                    mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                                    mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                                    mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                                    mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                                    String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                    DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                    ibmb.addValues(mbDataValues);
                                    this.addProcessData(new DataProcessBean(ibmb));

                                    //stockInfos 扣减数量
                                    for (DCP_PStockInMaterialBatchRefreshReq.StockInfo oldInfo :stockInfos){
                                        if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                                oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                            oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                        }
                                    }
                                }

                            }
                        }
                    }

                }


            }

            this.doExecuteDataToDB();
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockInMaterialBatchRefreshReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockInMaterialBatchRefreshReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockInMaterialBatchRefreshReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockInMaterialBatchRefreshReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String pStockInNO = req.getRequest().getPStockInNo();
        if (Check.Null(pStockInNO))
        {
            isFail = true;
            errMsg.append("完工入库单单号不可为空值, ");
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PStockInMaterialBatchRefreshReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PStockInMaterialBatchRefreshReq>(){};
    }

    @Override
    protected DCP_PStockInMaterialBatchRefreshRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PStockInMaterialBatchRefreshRes();
    }

}
