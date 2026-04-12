package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MStockOutProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockAdjustProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeToMStockOutReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockTakeToMStockOutRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_StockTakeToMStockOut extends SPosAdvanceService<DCP_StockTakeToMStockOutReq, DCP_StockTakeToMStockOutRes> {

    @Override
    protected void processDUID(DCP_StockTakeToMStockOutReq req, DCP_StockTakeToMStockOutRes res) throws Exception {

        String eId = req.geteId();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String uptime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format( Calendar.getInstance().getTime());


        String stockTakeSql="select a.*,b.supprice,to_char(c.PRODUCTDATE,'yyyyMMdd') as PRODUCTDATE,to_char(c.LOSEDATE,'yyyyMMdd') as LOSEDATE,a.employeeid,a.departid," +
                " a.BASEQTY-a.REF_BASEQTY as splitqty " +
                " from DCP_STOCKTAKE_DETAIL a " +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join mes_batch c on c.eid=a.eid and c.batchno=a.batch_no and c.pluno=a.pluno and c.featureno=a.featureno  " +
                " left join dcp_stocktake d on a.eid=d.eid and a.organizationno=b.organizationno and a.stocktakeno=b.stocktakeno " +
                " where a.eid='"+req.geteId()+"' and a.stocktakeno='"+req.getRequest().getStockTakeNo()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.BASEQTY-a.REF_BASEQTY<>0 ";
        List<Map<String, Object>> getStockTakeData=this.doQueryData(stockTakeSql, null);
        if(CollUtil.isEmpty(getStockTakeData)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "盘点单"+req.getRequest().getStockTakeNo()+"无盈亏明细，无法进行分摊");

        }

        String employeeId = getStockTakeData.get(0).get("EMPLOYEEID").toString();
        String departId = getStockTakeData.get(0).get("DEPARTID").toString();

        List<DCP_StockTakeToMStockOutReq.DetailListLevel> detailList = req.getRequest().getDetailList();
        List<String> collect = detailList.stream().map(x -> x.getOOType() + "|" + x.getOOfNo() + "|" + x.getOOItem()).distinct().collect(Collectors.toList());
        if(collect.size()!=detailList.size()){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "任务单+项次不能重复，避免重复分摊");
        }

        //查询出detailList 对应的原料  分摊getStockTakeData
        //生产单位->配方单位->原料单位 需转换

        if(CollUtil.isNotEmpty(detailList)) {
            String bomNoStr = detailList.stream().map(x -> x.getBomNo()).collect(Collectors.joining(","));
            String versionNumStr = detailList.stream().map(x -> x.getVersionNum()).collect(Collectors.joining(","));
            bomNoStr += ",";
            versionNumStr += ",";

            Map<String, String> mapBomNo = new HashMap<String, String>();
            mapBomNo.put("BOMNO", bomNoStr);
            mapBomNo.put("VERSIONNUM", versionNumStr);
            MyCommon cm = new MyCommon();
            String withasSql_BOM = "";
            withasSql_BOM = cm.getFormatSourceMultiColWith(mapBomNo);
            mapBomNo = null;

            String sql_bom = " with p as (" + withasSql_BOM + ")" +
                    " select a.*,b.pluno,b.unit,b.batchqty from dcp_bom_material a " +
                    " inner join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno  " +
                    " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                    " where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> bomList = this.doQueryData(sql_bom, null);

            String sql_bom_v = " with p as (" + withasSql_BOM + ")" +
                    " select a.*,b.pluno,b.unit,b.batchqty from dcp_bom_material_v a " +
                    " inner join dcp_bom_v b on a.eid=b.eid and a.bomno=b.bomno and a.versionnum=b.versionnum " +
                    " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                    " where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> bomvList = this.doQueryData(sql_bom_v, null);

            //MES_BOM_SUBPROCESS_MATERIAL 上面有pluno
            String sql_bom_subprocess = " with p as (" + withasSql_BOM + ")" +
                    " select a.*,b.unit,b.versionnum,b.batchqty from MES_BOM_SUBPROCESS_MATERIAL a " +
                    " inner join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno  " +
                    " inner join p on p.bomno=a.bomno and b.versionnum=p.versionnum " +
                    " where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> bomSubList = this.doQueryData(sql_bom_subprocess, null);


            String sql_bom_subprocess_v = " with p as (" + withasSql_BOM + ")" +
                    " select a.*,b.unit,b.batchqty from MES_BOM_SUBPROCESS_M_V a " +
                    " inner join dcp_bom_v b on b.eid=a.eid and a.bomno=b.bomno and a.versionnum=b.versionnum " +
                    " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                    " where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> bomSubvList = this.doQueryData(sql_bom_subprocess_v, null);

            //生产单位->配方主件单位->原料单位
            List<DCP_StockTakeToMStockOutReq.DetailMaterialLevel> dms=new ArrayList<>();
            detailList.forEach(x->{
                x.setDetailMaterialLevel(new ArrayList<>());
                if("0".equals(x.getOOType())){
                    List<Map<String, Object>> filterBom1 = bomList.stream().filter(y -> y.get("BOMNO").toString().equals(x.getBomNo()) && y.get("VERSIONNUM").toString().equals(x.getVersionNum())).collect(Collectors.toList());
                    if(filterBom1.size()<=0){
                        filterBom1=bomvList.stream().filter(y -> y.get("BOMNO").toString().equals(x.getBomNo()) && y.get("VERSIONNUM").toString().equals(x.getVersionNum())).collect(Collectors.toList());
                    }
                    if(filterBom1.size()>0){
                        DCP_StockTakeToMStockOutReq.DetailMaterialLevel detailMaterialLevel = req.new DetailMaterialLevel();
                        detailMaterialLevel.setUnit(filterBom1.get(0).get("UNIT").toString());
                        detailMaterialLevel.setBatchQty(filterBom1.get(0).get("BATCHQTY").toString());
                        detailMaterialLevel.setMaterialPluNo(filterBom1.get(0).get("MATERIAL_PLUNO").toString());
                        detailMaterialLevel.setMaterialUnit(filterBom1.get(0).get("MATERIAL_UNIT").toString());
                        detailMaterialLevel.setMaterialQty(filterBom1.get(0).get("MATERIAL_QTY").toString());
                        detailMaterialLevel.setQty(filterBom1.get(0).get("QTY").toString());
                        detailMaterialLevel.setItem("");
                        detailMaterialLevel.setPItem("");
                        detailMaterialLevel.setSItem("");
                        detailMaterialLevel.setZItem("");
                        detailMaterialLevel.setProcessNo("");
                        detailMaterialLevel.setOoType(x.getOOType());
                        detailMaterialLevel.setOofNo(x.getOOfNo());
                        detailMaterialLevel.setOoItem(x.getOOItem());

                        Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), detailMaterialLevel.getUnit(), detailMaterialLevel.getMaterialUnit(), x.getProdQty());
                        String afterDecimal = unitCalculate.get("afterDecimal").toString();//生产数量转换为原料单位数量
                        Integer udLength = Integer.valueOf(unitCalculate.get("udLength").toString());
                        //任务-原料应耗用量[原料配方单位]=本月生产入库量prodQty[pUnit和配方主件单位不一致则换算成配方主件单位数量] * 耗用量 / (配方基数 * 底数)
                        BigDecimal divide = new BigDecimal(afterDecimal).multiply(new BigDecimal(detailMaterialLevel.getMaterialQty())).divide(new BigDecimal(detailMaterialLevel.getBatchQty()).multiply(new BigDecimal(detailMaterialLevel.getQty())), udLength, BigDecimal.ROUND_HALF_UP);
                        detailMaterialLevel.setProdMaterialQty(divide.toString());
                        dms.add(detailMaterialLevel);
                        x.getDetailMaterialLevel().add(detailMaterialLevel);
                    }
                }else if("1".equals(x.getOOType())){
                    List<Map<String, Object>> filterBom1 = bomSubList.stream().filter(y -> y.get("BOMNO").toString().equals(x.getBomNo()) && y.get("VERSIONNUM").toString().equals(x.getVersionNum())).collect(Collectors.toList());
                    if(filterBom1.size()<=0){
                        filterBom1=bomSubvList.stream().filter(y -> y.get("BOMNO").toString().equals(x.getBomNo()) && y.get("VERSIONNUM").toString().equals(x.getVersionNum())).collect(Collectors.toList());
                    }
                    if(filterBom1.size()>0){
                        DCP_StockTakeToMStockOutReq.DetailMaterialLevel detailMaterialLevel = req.new DetailMaterialLevel();
                        detailMaterialLevel.setUnit(filterBom1.get(0).get("UNIT").toString());
                        detailMaterialLevel.setBatchQty(filterBom1.get(0).get("BATCHQTY").toString());
                        detailMaterialLevel.setMaterialPluNo(filterBom1.get(0).get("MATERIAL_PLUNO").toString());
                        detailMaterialLevel.setMaterialUnit(filterBom1.get(0).get("MATERIAL_UNIT").toString());
                        detailMaterialLevel.setMaterialQty(filterBom1.get(0).get("MATERIAL_QTY").toString());
                        detailMaterialLevel.setQty(filterBom1.get(0).get("QTY").toString());
                        detailMaterialLevel.setItem(filterBom1.get(0).get("ITEM").toString());
                        detailMaterialLevel.setPItem(filterBom1.get(0).get("PITEM").toString());
                        detailMaterialLevel.setSItem(filterBom1.get(0).get("SITEM").toString());
                        detailMaterialLevel.setOoType(x.getOOType());
                        detailMaterialLevel.setOofNo(x.getOOfNo());
                        detailMaterialLevel.setOoItem(x.getOOItem());
                        detailMaterialLevel.setZItem(filterBom1.get(0).get("ITEM").toString());
                        detailMaterialLevel.setProcessNo(filterBom1.get(0).get("PROCESSNO").toString());

                        Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), detailMaterialLevel.getUnit(), detailMaterialLevel.getMaterialUnit(), x.getProdQty());
                        String afterDecimal = unitCalculate.get("afterDecimal").toString();//生产数量转换为原料单位数量
                        Integer udLength = Integer.valueOf(unitCalculate.get("udLength").toString());
                        //任务-原料应耗用量[原料配方单位]=本月生产入库量prodQty[pUnit和配方主件单位不一致则换算成配方主件单位数量] * 耗用量 / (配方基数 * 底数)
                        BigDecimal divide = new BigDecimal(afterDecimal).multiply(new BigDecimal(detailMaterialLevel.getMaterialQty())).divide(new BigDecimal(detailMaterialLevel.getBatchQty()).multiply(new BigDecimal(detailMaterialLevel.getQty())), udLength, BigDecimal.ROUND_HALF_UP);
                        detailMaterialLevel.setProdMaterialQty(divide.toString());
                        dms.add(detailMaterialLevel);
                        x.getDetailMaterialLevel().add(detailMaterialLevel);
                    }
                }
            });

            //算应总耗用量
            Map<String,BigDecimal> totalMaterialQty = new HashMap<>();
            for (DCP_StockTakeToMStockOutReq.DetailListLevel singleDetail:detailList){
                if(singleDetail.getDetailMaterialLevel().size()>0){
                    for (DCP_StockTakeToMStockOutReq.DetailMaterialLevel sm:singleDetail.getDetailMaterialLevel()){
                        String materialPluNo = sm.getMaterialPluNo();
                        BigDecimal bigDecimal = new BigDecimal(sm.getProdMaterialQty());
                        if(totalMaterialQty.containsKey(materialPluNo)){
                            totalMaterialQty.put(materialPluNo,totalMaterialQty.get(materialPluNo).add(bigDecimal));
                        }else{
                            totalMaterialQty.put(materialPluNo,bigDecimal);
                        }
                    }
                }
            }


            List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> singleStockTake:getStockTakeData){
                Map<String, Object> plu = new HashMap<String, Object>();
                plu.put("PLUNO", singleStockTake.get("PLUNO").toString());
                plu.put("PUNIT", singleStockTake.get("PUNIT").toString());
                plu.put("BASEUNIT", singleStockTake.get("BASEUNIT").toString());
                plu.put("UNITRATIO", singleStockTake.get("UNIT_RATIO").toString());
                plus.add(plu);
            }
            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(StaticInfo.dao, eId, req.getBELFIRM(), req.getOrganizationNO(), plus, "");


            String mstockoutNo = "";
            int mstockoutItem=0;
            int adjustItem=0;
            String adjustNo="";

            BigDecimal mtotCqty=new BigDecimal(0);
            BigDecimal mtotQty=new BigDecimal(0);
            BigDecimal mtotAmt=new BigDecimal(0);
            BigDecimal mtotDistriAmt=new BigDecimal(0);

            BigDecimal atotCqty=new BigDecimal(0);
            BigDecimal atotQty=new BigDecimal(0);
            BigDecimal atotAmt=new BigDecimal(0);
            BigDecimal atotDistriAmt=new BigDecimal(0);

            //根据盘点单去分配  剩下的给最后一个
            for (Map<String, Object> singleStockTake:getStockTakeData){
                String pluNo = singleStockTake.get("PLUNO").toString();
                String pQty = singleStockTake.get("SPLITQTY").toString();
                String pUnit = singleStockTake.get("PUNIT").toString();
                String supPrice = singleStockTake.get("SUPPRICE").toString();

                String price="0";

                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", pluNo);
                condiV.put("PUNIT", pUnit);
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                }

                int direction=1;
                BigDecimal bigDecimal = new BigDecimal(pQty);
                int i1 = new BigDecimal(pQty).compareTo(BigDecimal.ZERO);
                if(i1<0){
                    direction=-1;
                    bigDecimal=bigDecimal.multiply(new BigDecimal("-1"));
                }

                if(totalMaterialQty.containsKey(pluNo)){
                    BigDecimal allBigDecimal = totalMaterialQty.get(pluNo);
                    List<DCP_StockTakeToMStockOutReq.DetailMaterialLevel> dmsFilterRows = dms.stream().filter(x -> x.getMaterialPluNo().equals(pluNo)).collect(Collectors.toList());
                    BigDecimal totSplitQty=new BigDecimal(0);
                    for (int i=0;i<dmsFilterRows.size();i++){
                        mstockoutItem++;
                        DCP_StockTakeToMStockOutReq.DetailMaterialLevel dml = dmsFilterRows.get(i);
                        BigDecimal divide = bigDecimal.multiply(new BigDecimal(dml.getProdMaterialQty())).divide(allBigDecimal, 0, BigDecimal.ROUND_HALF_UP);//配方单位
                        if(i==dmsFilterRows.size()-1){
                            divide=bigDecimal.subtract(totSplitQty);
                        }else{
                            totSplitQty=totSplitQty.add(divide);
                        }

                        //Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), pluNo, pUnit, divide.toString());
                        //String baseUnit = baseMap.get("baseUnit").toString();
                        //String baseQty = baseMap.get("baseQty").toString();
                       // String unitRatio = baseMap.get("unitRatio").toString();
                        mtotQty=mtotQty.add(divide);
                        divide=divide.multiply(new BigDecimal(direction));

                        BigDecimal distriAmt = divide.multiply(new BigDecimal(supPrice));
                        BigDecimal amt = divide.multiply(new BigDecimal(price));
                        mtotAmt=mtotAmt.add(amt);
                        mtotDistriAmt=mtotDistriAmt.add(distriAmt);
                        mtotCqty=mtotCqty.add(new BigDecimal(1));

                        if(Check.Null(mstockoutNo)){
                            mstockoutNo=this.getOrderNO(req, "SCKL");
                        }

                        ColumnDataValue detailColumns=new ColumnDataValue();
                        detailColumns.add("EID", DataValues.newString(eId));
                        detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                        detailColumns.add("MSTOCKOUTNO", DataValues.newString(mstockoutNo));
                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(singleStockTake.get("ORGANIZATIONNO").toString()));
                        detailColumns.add("WAREHOUSE", DataValues.newString(""));
                        detailColumns.add("ITEM", DataValues.newString(mstockoutItem));
                        detailColumns.add("PLUNO", DataValues.newString(pluNo));
                        detailColumns.add("PUNIT", DataValues.newString(singleStockTake.get("BASEUNIT").toString()));
                        detailColumns.add("PQTY", DataValues.newString(divide));
                        detailColumns.add("BASEUNIT", DataValues.newString(singleStockTake.get("BASEUNIT").toString()));
                        detailColumns.add("BASEQTY", DataValues.newString(divide));
                        detailColumns.add("UNIT_RATIO", DataValues.newString("1"));
                        detailColumns.add("PRICE", DataValues.newString(price));
                        detailColumns.add("AMT", DataValues.newString(amt));
                        detailColumns.add("DISTRIPRICE", DataValues.newString(supPrice));
                        detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
                        detailColumns.add("BATCHNO", DataValues.newString(singleStockTake.get("BATCH_NO").toString()));
                        detailColumns.add("PRODDATE", DataValues.newString(singleStockTake.get("PRODUCTDATE").toString()));
                        detailColumns.add("EXPDATE", DataValues.newString(singleStockTake.get("LOSEDATE").toString()));
                        detailColumns.add("ISBUCKLE", DataValues.newString("Y"));
                        detailColumns.add("FEATURENO", DataValues.newString(singleStockTake.get("FEATURENO").toString()));
                        detailColumns.add("LOCATION", DataValues.newString(singleStockTake.get("LOCATION").toString()));
                        detailColumns.add("OTYPE", DataValues.newString("3"));
                        detailColumns.add("OFNO", DataValues.newString(singleStockTake.get("STOCKTAKENO").toString()));
                        detailColumns.add("OITEM", DataValues.newString(singleStockTake.get("ITEM").toString()));
                        detailColumns.add("OOTYPE", DataValues.newString(dml.getOoType()));
                        detailColumns.add("OOFNO", DataValues.newString(dml.getOofNo()));
                        detailColumns.add("OOITEM", DataValues.newString(dml.getOoItem()));
                        detailColumns.add("LOAD_DOCITEM", DataValues.newString(""));
                        detailColumns.add("PITEM", DataValues.newString(dml.getPItem()));
                        detailColumns.add("PROCESSNO", DataValues.newString(dml.getProcessNo()));
                        detailColumns.add("SITEM", DataValues.newString(dml.getSItem()));
                        detailColumns.add("ZITEM", DataValues.newString(dml.getZItem()));
                        detailColumns.add("OPQTY", DataValues.newString(""));

                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean detailib=new InsBean("DCP_MSTOCKOUT_DETAIL",detailColumnNames);
                        detailib.addValues(detailDataValues);
                        this.addProcessData(new DataProcessBean(detailib));




                    }

                    //会全部分摊完 直接更新
                    UptBean ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                    ub1.addUpdateValue("SHAREQTY", new DataValue(singleStockTake.get("SPLITQTY").toString(), Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(singleStockTake.get("ITEM").toString(), Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    ub1.addCondition("STOCKTAKENO", new DataValue(req.getRequest().getStockTakeNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                }
                else{
                    if(Check.Null(adjustNo)){
                        adjustNo =this.getOrderNO(req,"KCTZ");
                    }
                    adjustItem++;
                    atotCqty=atotCqty.add(new BigDecimal(1));
                    atotAmt=atotAmt.add(new BigDecimal(singleStockTake.get("AMT").toString()));
                    atotDistriAmt=atotDistriAmt.add(new BigDecimal(singleStockTake.get("DISTRIAMT").toString()));
                    atotQty=atotQty.add(new BigDecimal(singleStockTake.get("PQTY").toString()));
                    //调整单.
                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                    detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                    detailColumns.add("ADJUSTNO", DataValues.newString(adjustNo));
                    detailColumns.add("ITEM", DataValues.newString(adjustItem));
                    detailColumns.add("OITEM", DataValues.newString(singleStockTake.get("ITEM").toString()));
                    //detailColumns.add("PLU_BARCODE", DataValues.newString(detail.getpl()));
                    detailColumns.add("PLUNO", DataValues.newString(singleStockTake.get("PLUNO").toString()));
                    detailColumns.add("FEATURENO", DataValues.newString(singleStockTake.get("FEATURENO").toString()));
                    detailColumns.add("PQTY", DataValues.newDecimal(singleStockTake.get("SPLITQTY").toString()));
                    detailColumns.add("PUNIT", DataValues.newString(singleStockTake.get("BASEUNIT").toString()));
                    detailColumns.add("BASEUNIT", DataValues.newString(singleStockTake.get("BASEUNIT").toString()));
                    detailColumns.add("BASEQTY", DataValues.newDecimal(singleStockTake.get("SPLITQTY").toString()));
                    detailColumns.add("UNIT_RATIO", DataValues.newDecimal("1"));
                    detailColumns.add("PRICE", DataValues.newDecimal(singleStockTake.get("PRICE").toString()));
                    detailColumns.add("AMT", DataValues.newDecimal(singleStockTake.get("AMT").toString()));
                    detailColumns.add("DISTRIPRICE", DataValues.newDecimal(singleStockTake.get("DISTRIPRICE").toString()));
                    detailColumns.add("DISTRIAMT", DataValues.newDecimal(singleStockTake.get("DISTRIAMT").toString()));
                    detailColumns.add("WAREHOUSE", DataValues.newString(singleStockTake.get("WAREHOUSE").toString()));
                    detailColumns.add("BATCH_NO", DataValues.newString(singleStockTake.get("BATCH_NO").toString()));
                    detailColumns.add("PROD_DATE", DataValues.newString(singleStockTake.get("PRODUCTDATE").toString()));
                    detailColumns.add("BDATE", DataValues.newString(createDate));
                    detailColumns.add("TRAN_TIME", DataValues.newString(uptime));
                    detailColumns.add("LOCATION", DataValues.newString(singleStockTake.get("LOCATION").toString()));
                    detailColumns.add("EXPDATE", DataValues.newString(singleStockTake.get("LOSEDATE").toString()));
                    //detailColumns.add("CATEGORY", DataValues.newString(detail.getca()));
                    detailColumns.add("MEMO", DataValues.newString(""));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean detailib=new InsBean("DCP_ADJUST_DETAIL",detailColumnNames);
                    detailib.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(detailib));


                    UptBean ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                    ub1.addUpdateValue("SHAREQTY", new DataValue(singleStockTake.get("SPLITQTY").toString(), Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(singleStockTake.get("ITEM").toString(), Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    ub1.addCondition("STOCKTAKENO", new DataValue(req.getRequest().getStockTakeNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));


                }


            }

            if(Check.NotNull(mstockoutNo)){
                //生成扣料单单头

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                mainColumns.add("MSTOCKOUTNO", DataValues.newString(mstockoutNo));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                //mainColumns.add("WAREHOUSE", DataValues.newString(""));
                mainColumns.add("DOC_TYPE", DataValues.newString(""));
                mainColumns.add("BDATE", DataValues.newString(createDate));
                mainColumns.add("SDATE", DataValues.newString(createDate));
                mainColumns.add("OTYPE", DataValues.newString("3"));
                mainColumns.add("OFNO", DataValues.newString(req.getRequest().getStockTakeNo()));
                mainColumns.add("OOTYPE", DataValues.newString(""));
                mainColumns.add("OOFNO", DataValues.newString(""));
                mainColumns.add("MEMO", DataValues.newString(""));
                mainColumns.add("STATUS", DataValues.newInteger(0));
                mainColumns.add("ADJUSTSTATUS", DataValues.newString(2));
                mainColumns.add("OMSTOCKOUTNO", DataValues.newString(""));
                mainColumns.add("TOT_CQTY", DataValues.newDecimal(mtotCqty));
                mainColumns.add("TOT_PQTY", DataValues.newDecimal(mtotQty));
                mainColumns.add("TOT_AMT", DataValues.newDecimal(mtotAmt));
                mainColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(mtotDistriAmt));
                mainColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
                mainColumns.add("LOAD_DOCNO", DataValues.newString(""));
                mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATETIME", DataValues.newDate(createTime));
                mainColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                mainColumns.add("DEPARTID", DataValues.newString(departId));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MSTOCKOUT",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }

            if(Check.NotNull(adjustNo)){

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                mainColumns.add("ADJUSTNO", DataValues.newString(adjustNo));
                mainColumns.add("DOC_TYPE", DataValues.newString("2"));
                mainColumns.add("BDATE", DataValues.newString(createDate));
                mainColumns.add("OTYPE", DataValues.newString("2"));//?
                mainColumns.add("OFNO", DataValues.newString(req.getRequest().getStockTakeNo()));
                mainColumns.add("LOAD_DOCNO", DataValues.newString(""));
                mainColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
                mainColumns.add("WAREHOUSE", DataValues.newString(""));
                mainColumns.add("TOT_CQTY", DataValues.newDecimal(atotCqty));
                mainColumns.add("TOT_PQTY", DataValues.newDecimal(atotQty));
                mainColumns.add("TOT_AMT", DataValues.newDecimal(atotAmt));
                mainColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(atotDistriAmt));
                mainColumns.add("STATUS", DataValues.newString("0"));
                mainColumns.add("MEMO", DataValues.newString(""));
                mainColumns.add("PROCESS_STATUS", DataValues.newString("N"));
                mainColumns.add("UPDATE_TIME", DataValues.newString(uptime));
                mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
                mainColumns.add("TRAN_TIME", DataValues.newString(uptime));
                mainColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                mainColumns.add("DEPARTID", DataValues.newString(departId));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_ADJUST",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }

            this.doExecuteDataToDB();

            //审核对应的单据
            if(Check.NotNull(mstockoutNo)){
                ParseJson pj = new ParseJson();
                DCP_MStockOutProcessReq mstockReq=new DCP_MStockOutProcessReq();
                mstockReq.setServiceId("DCP_MStockOutProcess");
                mstockReq.setToken(req.getToken());
                DCP_MStockOutProcessReq.LevelRequest request = mstockReq.new LevelRequest();
                request.setMStockOutNo(mstockoutNo);
                request.setAccountDate(createDate);
                mstockReq.setRequest(request);

                String jsontemp= pj.beanToJson(mstockReq);

                //直接调用CRegisterDCP服务
                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_MStockOutProcessRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_MStockOutProcessRes>(){});
                if(resserver.isSuccess()==false)
                {
                    //删除单据
                    DelBean db1 = new DelBean("DCP_MSTOCKOUT");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    db1.addCondition("MSTOCKOUTNO", new DataValue(mstockoutNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_MSTOCKOUT_DETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    db2.addCondition("MSTOCKOUTNO", new DataValue(mstockoutNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    UptBean ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                    ub1.addUpdateValue("SHAREQTY", new DataValue("0", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    ub1.addCondition("STOCKTAKENO", new DataValue(req.getRequest().getStockTakeNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                    this.doExecuteDataToDB();

                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "扣料单审核失败！");
                }
                else{
                    //更新盘点单shareQty 放前面了

                }
            }

            if(Check.NotNull(adjustNo)){
                ParseJson pj = new ParseJson();
                DCP_StockAdjustProcessReq astockReq=new DCP_StockAdjustProcessReq();
                astockReq.setServiceId("DCP_StockAdjustProcess");
                astockReq.setToken(req.getToken());
                DCP_StockAdjustProcessReq.Request request = astockReq.new Request();
                request.setAdjustNo(mstockoutNo);
                request.setAccountDate(createDate);
                request.setOprType("post");
                astockReq.setRequest(request);

                String jsontemp= pj.beanToJson(astockReq);

                //直接调用CRegisterDCP服务
                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_MStockOutProcessRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_MStockOutProcessRes>(){});
                if(resserver.isSuccess()==false)
                {
                    //删除单据
                    DelBean db1 = new DelBean("DCP_ADJUST");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    db1.addCondition("ADJUSTNO", new DataValue(adjustNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_ADJUST_DETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    db2.addCondition("ADJUSTNO", new DataValue(adjustNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    UptBean ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                    ub1.addUpdateValue("SHAREQTY", new DataValue("0", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                    ub1.addCondition("STOCKTAKENO", new DataValue(req.getRequest().getStockTakeNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                    this.doExecuteDataToDB();

                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "扣料单审核失败！");
                }
                else{
                    //更新盘点单shareQty 放前面了

                }
            }

        }



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTakeToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTakeToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTakeToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockTakeToMStockOutReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockTakeToMStockOutReq> getRequestType() {
        return new TypeToken<DCP_StockTakeToMStockOutReq>() {
        };
    }

    @Override
    protected DCP_StockTakeToMStockOutRes getResponseType() {
        return new DCP_StockTakeToMStockOutRes();
    }
}

