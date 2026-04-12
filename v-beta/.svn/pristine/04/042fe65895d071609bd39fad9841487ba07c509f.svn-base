package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAllocationRuleUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockAllocationRuleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_StockAllocationRuleUpdate
 * 服务说明：分配规则设置：添加/修改/批量修改
 * @author wangzyc 2021-03-15
 */
public class DCP_StockAllocationRuleUpdate extends SPosAdvanceService<DCP_StockAllocationRuleUpdateReq, DCP_StockAllocationRuleUpdateRes> {
    @Override
    protected void processDUID(DCP_StockAllocationRuleUpdateReq req, DCP_StockAllocationRuleUpdateRes res) throws Exception {
        /** 服务逻辑：
         *  添加：
         *  判断该商品是否存在规则，如果不存在规则则添加，如果存在该规则则修改，添加的时候，只添加DCP_STOCK_ALLOCATION_RULE（库存分配规则）
         *
         *  修改：
         *  如果该商品规则已存在规则且 dealType 等于0：
         *  先更新规则表(DCP_STOCK_ALLOCATION_RULE)，然后更新库存分配表(DCP_STOCK_CHANNEL)，
         *  并在分配单据表中添加记录（DCP_STOCK_CHANNEL_BILL，DCP_STOCK_CHANNEL_BILLGOODS）
         *
         *  如果该商品规则已存在规则且 dealType 等于1：
         *  先查询库存分配表中商品分配信息，拿到预留数，去增量更新DCP_STOCK 表中预留数
         *  然后更新规则表中数据，删除库存分配表中明细，且根据分配规则自动上架数量，并添加分配单据表中记录
         *
         *  2021/5/14 增加逻辑 商品如果不在 dcp_stock 表中存在 则写入一条分配记录 仓库为组织默认出货仓
         *  注：只要更改DCP_STOCK_CHANNEL 表数据，就要去更新分配单据表中记录
         *
         *
         **/
        DCP_StockAllocationRuleUpdateReq.level1ELm request = req.getRequest();
        List<DCP_StockAllocationRuleUpdateReq.level2ELm> pluList = request.getPluList();
        String dealType = request.getDealType();
        
        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = df.format(cal.getTime());
        String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
                "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","DEALTYPE"};
        
        String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
                "SUNIT","FEATURENO","WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};
        
        String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","WAREHOUSE",
                "ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY","CREATEOPID","CREATEOPNAME","CREATETIME"};
        
        String[] columns_ALLOCATION_RULE = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","SUNIT","CHANNELID","RULETYPE",
                "ALLOCATIONVALUE","CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
        
        String [] columns_STOCK = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","WAREHOUSE","BASEUNIT","QTY","LOCKQTY","ONLINEQTY","CREATEOPID"
                ,"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
        
        try {
            String sql = "";
            
            // 拿到所有渠道
            Set<String> getchannels = this.getchannels(req);
            
            // 拿到商品单位值预留几位
            sql = getUnit(req);
            List<Map<String, Object>> units = this.doQueryData(sql, null);
            
            List<StockChannel> stockChannels = new ArrayList<>();
            
            
            if(!CollectionUtils.isEmpty(pluList)){
                
                for (DCP_StockAllocationRuleUpdateReq.level2ELm plu:pluList){
             
                    String organizationNo = plu.getOrganizationNo();
                    String pluNo = plu.getPluNo();
                    String featureNo = Check.Null(plu.getFeatureNo()) ?" ":plu.getFeatureNo();
                    String sUnit = plu.getSUnit();
                    String ruleType = plu.getRuleType();
                    List<DCP_StockAllocationRuleUpdateReq.level3ELm> channelList = plu.getChannelList();
                    StockChannel stockChannel = new StockChannel();
                    stockChannel.setEId(eId);
                    stockChannel.setFeatureNo(featureNo);
                    stockChannel.setSUnit(sUnit);
                    stockChannel.setPluNo(pluNo);
                    stockChannel.setOrganizationNo(organizationNo);
                    stockChannel.setRuleType(ruleType);
                    
                    //************************************ 查询该商品是否存在规则 Begin**********************
                    boolean existPlunoRule = false; // 是否存在商品规则
                    String sql2 = existPlunoRule(stockChannel);
                    List<Map<String, Object>> existRule = null;
                    try {
                        existRule = this.doQueryData(sql2, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(!CollectionUtils.isEmpty(existRule)){
                        existPlunoRule = true;
                    }
                    stockChannel.setExistRule(existPlunoRule);
                    //************************************ 查询该商品是否存在规则 End**********************
                    
                    //************************************ 查询该商品是否存在dcp_stock  Begin**********************
                    String baseUnit = ""; // 基本单位
                    String warehouse =""; // 仓库 出货仓
                    Boolean existStock = false; // 是否存在 dcp_stock 表中 存在:true/ 不存在:false
                    String warehouseOnlineqty = "0"; // 仓库预留数
                    String qty = "0"; // 仓库 在库数
                    sql2 = this.getStock(req,organizationNo,pluNo,featureNo);
                    List<Map<String, Object>> getStock = null;
                    try {
                        // 查询是否存在 dcp_stock 表中
                        getStock = this.doQueryData(sql2, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(!CollectionUtils.isEmpty(getStock)){
                        // 存在则从中获取相关信息
                        Map<String, Object> getStockMap = getStock.get(0);
                        baseUnit = getStockMap.get("BASEUNIT").toString();
                        warehouse = getStockMap.get("WAREHOUSE").toString();
                        warehouseOnlineqty = getStockMap.get("ONLINEQTY").toString();
                        qty = getStockMap.get("QTY").toString();
                        existStock = true;
                    }else {
                        // 如果该商品不在 dcp_stock 表中存在 则去查询对应的资料
                        sql2 = this.getPlunoInfo(eId, organizationNo, pluNo);
                        List<Map<String, Object>> getPluNoInfo = null;
                        try {
                            getPluNoInfo = this.doQueryData(sql2, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(!CollectionUtils.isEmpty(getPluNoInfo)){
                            baseUnit = getPluNoInfo.get(0).get("BASEUNIT").toString();
                            warehouse = getPluNoInfo.get(0).get("OUT_COST_WAREHOUSE").toString();
                        }
                        existStock = false;
                    }
                    
                    //【ID1035601】【红鑫光-3.0】库存分配保存报错：error executing work  by jinzma 20230823
                    if (Check.Null(warehouse)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "组织:"+organizationNo+"  未设置默认仓库,请检查组织基本资料表(DCP_ORG)");
                    }
                    
                    
                    double warehouseQty = Double.parseDouble(qty);
                    double warehouseOnlineQty = Double.parseDouble(warehouseOnlineqty);
                    stockChannel.setWarehouse(warehouse);
                    stockChannel.setQTY(qty);
                    stockChannel.setWarehouseOnlineqty(warehouseOnlineqty);
                    stockChannel.setBaseunit(baseUnit);
                    stockChannel.setExistStock(existStock);
                    //************************************ 查询该商品是否存在dcp_stock  Begin**********************
                    
                    //************************************ 查询该商品在库存分配表中的记录 DCP_STOCK_CHANNEL  Begin**********************
                    // 查询商品在库存分配表中的记录
                    sql2 = getPluNoChannel(stockChannel);
                    List<Map<String, Object>> pluChannels = null;
                    try {
                        pluChannels = this.doQueryData(sql2, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //************************************ 查询该商品在库存分配表中的记录 DCP_STOCK_CHANNEL  End**********************
                    
                    stockChannel.setChannels(new ArrayList<StockChannel.channel>());
                    
                    // 存在规则则添加，不存在则修改
                    if(!existPlunoRule&&dealType.equals("0")){
                        // 添加
                        DataValue[] insValue1 = null;
                        insValue1 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(organizationNo, Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(sUnit, Types.VARCHAR),
                                new DataValue(" ", Types.VARCHAR),
                                new DataValue(ruleType, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(createTime, Types.DATE),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(createTime, Types.DATE)
                        };
                        InsBean ib1 = new InsBean("DCP_STOCK_ALLOCATION_RULE", columns_ALLOCATION_RULE);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1));
                    }else if(existPlunoRule&&dealType.equals("0")&&!CollectionUtils.isEmpty(channelList)){
                        /**
                         * 先删除规则表中数据
                         */
                        DelBean db1 = new DelBean("DCP_STOCK_ALLOCATION_RULE");
                        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        db1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                        db1.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));

//                        /**
//                         * 删除库存分配表中记录
//                         */
//                        for (DCP_StockAllocationRuleUpdateReq.level3ELm level3ELm : channelList) {
//                            DelBean db2 = new DelBean("DCP_STOCK_CHANNEL");
//                            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//                            db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
//                            db2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//                            db2.addCondition("CHANNELID", new DataValue(level3ELm.getChannelId(), Types.VARCHAR));
//                            db2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
//                            db2.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
//                            this.addProcessData(new DataProcessBean(db2));
//                        }
                        
                        /**
                         * 添加库存分配表
                         */
                        for (DCP_StockAllocationRuleUpdateReq.level3ELm level3ELm : channelList) {
                            String channelId = level3ELm.getChannelId();
                            String allocationValue = level3ELm.getAllocationValue();
                            DataValue[] insValue1 = null;
                            insValue1 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(organizationNo, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(featureNo, Types.VARCHAR),
                                    new DataValue(sUnit, Types.VARCHAR),
                                    new DataValue(channelId, Types.VARCHAR),
                                    new DataValue(ruleType, Types.VARCHAR),
                                    new DataValue(allocationValue, Types.VARCHAR),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(opName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(opName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE)
                            };
                            InsBean ib1 = new InsBean("DCP_STOCK_ALLOCATION_RULE", columns_ALLOCATION_RULE);
                            ib1.addValues(insValue1);
                            this.addProcessData(new DataProcessBean(ib1));
                            
                            StockChannel.channel channel = stockChannel.new channel();
                            channel.setAllocationValue("0");
                            channel.setChannelId(channelId);
                            channel.setExistStockChannelid(false);
                            if(!CollectionUtils.isEmpty(pluChannels)){
                                // 存在库存分配表中
                                for (Map<String, Object> pluChannel : pluChannels) {
                                    String channelid = pluChannel.get("CHANNELID").toString();
                                    if(channelId.equals(channelid)){
//                                        String onlineqty = pluChannel.get("ONLINEQTY").toString();
//                                        String lockqty = pluChannel.get("LOCKQTY").toString();
//                                        String blockqty = pluChannel.get("BLOCKQTY").toString();
//                                        channel.setOnlineqty(onlineqty);
//                                        channel.setLockQty(lockqty);
//                                        channel.setBlockQty(blockqty);
                                        channel.setExistStockChannelid(true);
                                    }
                                }
                            }
                            //  新预留上架的商品，库存为0； 已预留上架的商品，库存不更新
                            if(channel.getExistStockChannelid() == false){
                                // 写入库存分配表中记录
                                DataValue[] insValue2 = null;
                                insValue2 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(channelId, Types.VARCHAR),
                                        new DataValue(organizationNo, Types.VARCHAR),
                                        
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(sUnit, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                        new DataValue(warehouse, Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR), //ONLINEQTY
                                        new DataValue(channel.getLockQty(), Types.VARCHAR), //LOCKQTY
                                        new DataValue(baseUnit, Types.VARCHAR), //baseUnit
                                        
                                        new DataValue("0" , Types.VARCHAR),//BONLINEQTY
                                        new DataValue(channel.getBlockQty(), Types.VARCHAR),//BLOCKQTY
                                        new DataValue(opNo, Types.VARCHAR),
                                        new DataValue(opName, Types.VARCHAR),
                                        new DataValue(createTime, Types.DATE)
                                };
                                InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));
                                
                                stockChannel.getChannels().add(channel);
                            }
                            
                        }
                        if(!CollectionUtils.isEmpty(stockChannel.getChannels())){
                            stockChannels.add(stockChannel);
                        }
                    }else if(existPlunoRule&&dealType.equals("1")&&!CollectionUtils.isEmpty(channelList)){
                        // 修改商品的库存分配 并同步到dcp_stock 表中
                        /**
                         * 先删除规则表中数据
                         */
                        DelBean db1 = new DelBean("DCP_STOCK_ALLOCATION_RULE");
                        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        db1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                        db1.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));
                        
                        /**
                         * 删除库存分配表中记录
                         */
                        for (DCP_StockAllocationRuleUpdateReq.level3ELm level3ELm : channelList) {
                            DelBean db2 = new DelBean("DCP_STOCK_CHANNEL");
                            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                            db2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                            db2.addCondition("CHANNELID", new DataValue(level3ELm.getChannelId(), Types.VARCHAR));
                            db2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                            db2.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(db2));
                        }
                        
                        
                        /**
                         * 添加库存分配表
                         */
                        for (DCP_StockAllocationRuleUpdateReq.level3ELm level3ELm : channelList) {
                            String channelId = level3ELm.getChannelId();
                            String allocationValue = level3ELm.getAllocationValue();
                            DataValue[] insValue1 = null;
                            insValue1 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(organizationNo, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(featureNo, Types.VARCHAR),
                                    new DataValue(sUnit, Types.VARCHAR),
                                    new DataValue(channelId, Types.VARCHAR),
                                    new DataValue(ruleType, Types.VARCHAR),
                                    new DataValue(allocationValue, Types.VARCHAR),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(opName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(opName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE)
                            };
                            InsBean ib1 = new InsBean("DCP_STOCK_ALLOCATION_RULE", columns_ALLOCATION_RULE);
                            ib1.addValues(insValue1);
                            this.addProcessData(new DataProcessBean(ib1));

//                            if(!stockChannel.getExistStock()){
//                                // 库存表中不存在的时候 插入一条数据
//                                DataValue[] insValue2 = null;
//                                insValue2 = new DataValue[]{
//                                        new DataValue(eId, Types.VARCHAR),
//                                        new DataValue(organizationNo, Types.VARCHAR),
//
//                                        new DataValue(pluNo, Types.VARCHAR),
//                                        new DataValue(featureNo, Types.VARCHAR),
//                                        new DataValue(warehouse, Types.VARCHAR),
//                                        new DataValue(baseUnit, Types.VARCHAR),
//                                        new DataValue("0", Types.VARCHAR), // QTY
//                                        new DataValue("0", Types.VARCHAR), // LOCKQTY
//                                        new DataValue("0", Types.VARCHAR), // ONLINEQTY
//                                        new DataValue(opNo, Types.VARCHAR),
//                                        new DataValue(opName, Types.VARCHAR),
//                                        new DataValue(createTime, Types.DATE),
//                                        new DataValue(opNo, Types.VARCHAR),
//                                        new DataValue(opName, Types.VARCHAR),
//                                        new DataValue(createTime, Types.DATE)
//                                };
//                                InsBean ib2 = new InsBean("DCP_STOCK", columns_STOCK);
//                                ib2.addValues(insValue2);
//                                this.addProcessData(new DataProcessBean(ib2));
//                                stockChannel.setExistStock(true);
//                            }
                            
                            StockChannel.channel channel = stockChannel.new channel();
                            channel.setExistStockChannelid(false);
                            channel.setChannelId(channelId);
                            Double iallocationValue = 0.0;
                            Double bQty = 0.0;  // 现在基准单位数
                            Double baseUnitQty = 0.0;
                            
                            Double yQTY = 0.0; // 预留数
                            String direction = "1"; // 增加减少
                            if(!CollectionUtils.isEmpty(pluChannels)){
                                for (Map<String, Object> pluChannel : pluChannels) {
                                    
                                    String channelid = pluChannel.get("CHANNELID").toString();
                                    if(channelid.equals(channelId)){
                                        String onlineqty = pluChannel.get("ONLINEQTY").toString();
                                        String bonlineqty = pluChannel.get("BONLINEQTY").toString();
                                        String lockqty = pluChannel.get("LOCKQTY").toString();
                                        String blockqty = pluChannel.get("BLOCKQTY").toString();
                                        int onlineqtyw = Integer.parseInt(onlineqty);
                                        channel.setOnlineqty(onlineqty);
                                        channel.setBOnlineqty(bonlineqty);
                                        channel.setLockQty(lockqty);
                                        channel.setBlockQty(blockqty);
                                        channel.setExistStockChannelid(true);
                                        
                                        {
                                            
                                            if(ruleType.equals("0")){
                                                iallocationValue = Double.parseDouble(allocationValue);
                                                // 按数量
                                                String unitConvert = "";
                                                try {
                                                    Double i1 = iallocationValue-onlineqtyw;
                                                    if(i1>0){
                                                        direction ="1";
                                                    }else if(i1<0){
                                                        direction ="-1";
                                                    }
                                                    // 计算基准单位
                                                    bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));;
                                                    unitConvert = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, i1+"");
                                                    yQTY = Double.parseDouble(unitConvert);
                                                    if(warehouseOnlineQty==0){
                                                        yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));
                                                    }
                                                    
                                                    if(yQTY<0){
                                                        yQTY =  yQTY*-1;
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else if(ruleType.equals("1")){
                                                iallocationValue = Double.parseDouble(allocationValue);
                                                // 按比例
                                                double v = warehouseQty * (iallocationValue * 0.01);
                                                // 保留几位
                                                String baseUnitRetain = "0";
                                                String sUnitRetain = "0";
                                                
                                                for (Map<String, Object> unit : units) {
                                                    String unit1 = unit.get("UNIT").toString();
                                                    Integer udlength = Integer.parseInt(unit.get("UDLENGTH").toString());
                                                    StringBuffer reta = new StringBuffer("0");
                                                    if(unit1.equals(baseUnit)){
                                                        if(udlength!=0){
                                                            reta.append(".");
                                                            for(int i2 =0;i2<=udlength;i2++){
                                                                reta.append("0");
                                                            }
                                                        }
                                                        baseUnitRetain = reta.toString();
                                                        
                                                    }
                                                    if(unit1.equals(sUnit)){
                                                        if(udlength!=0){
                                                            reta.append(".");
                                                            for(int i2 =0;i2<=udlength;i2++){
                                                                reta.append("0");
                                                            }
                                                        }
                                                        sUnitRetain =  reta.toString();
                                                    }
                                                    
                                                }
                                                // 现在的基准单位数量
                                                String strBaseUnit = new DecimalFormat(baseUnitRetain).format(v);
                                                // 现在的销售单位数量
                                                String strsUnit = "0";
                                                String unitConvert2 ="1";
                                                try {
                                                    unitConvert2 = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, sUnit, v + "");//
                                                    strsUnit = new DecimalFormat(sUnitRetain).format(Double.parseDouble(unitConvert2));
                                                    bQty = Double.parseDouble(strBaseUnit);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                iallocationValue = Double.parseDouble(strsUnit) ;
                                                baseUnitQty = Double.parseDouble(strBaseUnit);
                                                yQTY = baseUnitQty - Double.parseDouble(bonlineqty); // 多少
                                                
                                                if(yQTY>0){
                                                    direction ="1";
                                                }else if(yQTY<0){
                                                    direction ="-1";
                                                    yQTY = yQTY * -1;
                                                }
                                                if(warehouseQty==0){
                                                    try {
                                                        yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit,onlineqty+"" ));
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                
                            }else{
                                
                                // 如果dealType ==1 库存分配表中不存在数据
                                for (DCP_StockAllocationRuleUpdateReq.level3ELm level4ELm : channelList)
                                {
                                    if(level4ELm.getChannelId().equals(channelId)){
                                        String onlineqty = "0";
                                        String bonlineqty = "0";
                                        int onlineqtyw = Integer.parseInt(onlineqty);
                                        channel.setOnlineqty(onlineqty);
                                        channel.setBOnlineqty(bonlineqty);
                                        channel.setExistStockChannelid(false);
                                        
                                        
                                        if(ruleType.equals("0")){
                                            iallocationValue = Double.parseDouble(allocationValue);
                                            // 按数量
                                            String unitConvert = "";
                                            try {
                                                Double i1 = iallocationValue-onlineqtyw;
                                                
                                                // 计算基准单位
                                                bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));;
                                                unitConvert = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, i1+"");
                                                yQTY = Double.parseDouble(unitConvert);
                                                if(warehouseQty==0){
                                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));
                                                }
                                                
                                                if(yQTY>0){
                                                    direction ="1";
                                                }else if(yQTY<0){
                                                    direction ="-1";
                                                    yQTY = i1*-1;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else if(ruleType.equals("1")){
                                            iallocationValue = Double.parseDouble(allocationValue);
                                            // 按比例
                                            double v = warehouseQty * (iallocationValue * 0.01);
                                            // 保留几位
                                            String baseUnitRetain = "";
                                            String sUnitRetain = "";
                                            
                                            for (Map<String, Object> unit : units) {
                                                String unit1 = unit.get("UNIT").toString();
                                                Integer udlength = Integer.parseInt(unit.get("UDLENGTH").toString());
                                                StringBuffer reta = new StringBuffer("0");
                                                if(unit1.equals(baseUnit)){
                                                    if(udlength!=0){
                                                        reta.append(".");
                                                        for(int i2 =0;i2<=udlength;i2++){
                                                            reta.append("0");
                                                        }
                                                    }
                                                    baseUnitRetain = reta.toString();
                                                    
                                                }
                                                if(unit1.equals(sUnit)){
                                                    if(udlength!=0){
                                                        reta.append(".");
                                                        for(int i2 =0;i2<=udlength;i2++){
                                                            reta.append("0");
                                                        }
                                                    }
                                                    sUnitRetain =  reta.toString();
                                                }
                                                
                                            }
                                            // 现在的基准单位数量
                                            String strBaseUnit = new DecimalFormat(baseUnitRetain).format(v);
                                            // 现在的销售单位数量
                                            String strsUnit = "";
                                            String unitConvert2 ="";
                                            try {
                                                unitConvert2 = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, sUnit, v + "");//
                                                strsUnit = new DecimalFormat(sUnitRetain).format(Double.parseDouble(unitConvert2));
                                                bQty = Double.parseDouble(strBaseUnit);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            iallocationValue = Double.parseDouble(strsUnit) ;
                                            baseUnitQty = Double.parseDouble(strBaseUnit);
                                            yQTY = baseUnitQty - Double.parseDouble(bonlineqty); // 多少
                                            
                                            if(warehouseQty==0){
                                                try {
                                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit,onlineqty+"" ));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if(yQTY>0){
                                                direction ="1";
                                            }else if(yQTY<0){
                                                direction ="-1";
                                                yQTY = yQTY * -1;
                                            }
                                        }
                                        
                                        
                                    }
                                    
                                }
                            }
                            
                            if(stockChannel.getExistStock()==false){
                                // 库存表中不存在的时候 插入一条数据
                                DataValue[] insValue2 = null;
                                insValue2 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(organizationNo, Types.VARCHAR),
                                        
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                        new DataValue(warehouse, Types.VARCHAR),
                                        new DataValue(baseUnit, Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR), // QTY
                                        new DataValue("0", Types.VARCHAR), // LOCKQTY
                                        new DataValue(yQTY, Types.VARCHAR), // ONLINEQTY
                                        new DataValue(opNo, Types.VARCHAR),
                                        new DataValue(opName, Types.VARCHAR),
                                        new DataValue(createTime, Types.DATE),
                                        new DataValue(opNo, Types.VARCHAR),
                                        new DataValue(opName, Types.VARCHAR),
                                        new DataValue(createTime, Types.DATE)
                                };
                                InsBean ib2 = new InsBean("DCP_STOCK", columns_STOCK);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));
                                stockChannel.setExistStock(true);
                            }else {
                                UptBean ub2 = null;
                                ub2 = new UptBean("DCP_STOCK");
                                // condition
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                                ub2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                                ub2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                                ub2.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                                
                                if(direction.equals("1")){ // 1：增加 ，  -1：减少
                                    ub2.addUpdateValue("ONLINEQTY", new DataValue(yQTY, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));
                                }
                                if(direction.equals("-1")){
                                    ub2.addUpdateValue("ONLINEQTY", new DataValue(yQTY, Types.VARCHAR, DataValue.DataExpression.SubSelf));
                                }
                                ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
                                ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
                                ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub2));
                            }
                            
                            channel.setAllocationValue(iallocationValue+"");
                            channel.setBQty(bQty+"");
                            stockChannel.getChannels().add(channel);
                            
                            DataValue[] insValue2 = null;
                            insValue2 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(channelId, Types.VARCHAR),
                                    new DataValue(organizationNo, Types.VARCHAR),
                                    
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(sUnit, Types.VARCHAR),
                                    new DataValue(featureNo, Types.VARCHAR),
                                    new DataValue(warehouse, Types.VARCHAR),
                                    new DataValue(iallocationValue, Types.VARCHAR), //ONLINEQTY
                                    new DataValue(channel.getLockQty(), Types.VARCHAR), //LOCKQTY
                                    new DataValue(baseUnit, Types.VARCHAR), //baseUnit
                                    
                                    new DataValue(bQty , Types.VARCHAR),//BONLINEQTY
                                    new DataValue(channel.getBlockQty(), Types.VARCHAR),//BLOCKQTY
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(opName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE)
                            };
                            InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));
                            
                        }
                        stockChannels.add(stockChannel);
                    }
                };
                
                // ********************************* 如果更改了DCP_STOCK_CHANNEL 要去写库存分配单据记录 *************************************************
                String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
                String billNo = this.queryMaxBillNo(req);
                int createbillNo = 0;
                for (String getchannel : getchannels) {
                    int item = 1;
                    int totcqty = 0;
                    int totpqty = 0;
                    boolean flag =false;  // 是否写单头
                    if(createbillNo>0){
                        long i;
                        billNo = billNo.substring(4, billNo.length());
                        i = Long.parseLong(billNo) + 1;
                        billNo = i + "";
                        billNo = "QDFH" + billNo;
                    }
                    
                    for (StockChannel stockChannel : stockChannels) {
                        // 判断该商品是否存在规则
                        if(stockChannel.getExistRule()) {
                            flag = true;
                            String warehouse = stockChannel.getWarehouse();
                            for (StockChannel.channel channel : stockChannel.getChannels()) {
                                if(channel.getChannelId().equals(getchannel)){
                                    if(Check.Null(warehouse)){
                                        flag = false;
                                    }
                                    String direction = "1";
                                    Double sqty = 0.0;
                                    Double bqty = 0.0;
                                    Double allocationValue = Double.parseDouble(channel.getAllocationValue());
                                    Double bQty = Double.parseDouble(Check.Null(channel.getBQty())==true?"0":channel.getBQty());
                                    Double onlineqty = Double.parseDouble(Check.Null(channel.getOnlineqty())==true?"0":channel.getOnlineqty());
                                    Double bOnlineqty = Double.parseDouble(Check.Null(channel.getBOnlineqty())==true?"0":channel.getBOnlineqty());
                                    if((allocationValue-onlineqty)>0){
                                        direction = "1";
                                        sqty = allocationValue-onlineqty;
                                    }
                                    if(onlineqty-allocationValue>0){
                                        direction = "-1";
                                        sqty = onlineqty-allocationValue;
                                    }
                                    if(bQty-bOnlineqty>0){
                                        bqty = bQty-bOnlineqty;
                                    }
                                    if(bOnlineqty-bQty>0){
                                        bqty = bOnlineqty-bQty;
                                    }
                                    if(!channel.getExistStockChannelid()&&dealType.equals("0")){
                                        sqty = 0.0;
                                        bqty = 0.0;
                                    }
                                    totpqty+=sqty;
                                    DataValue[] insValue2 = null;
                                    insValue2 = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(getchannel, Types.VARCHAR),
                                            new DataValue(billNo, Types.VARCHAR), // 没有
                                            new DataValue(billType, Types.VARCHAR), //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
                                            new DataValue(item, Types.VARCHAR),
                                            new DataValue(stockChannel.getOrganizationNo(), Types.VARCHAR),
                                            
                                            new DataValue(stockChannel.getPluNo(), Types.VARCHAR),
                                            new DataValue(stockChannel.getSUnit(), Types.VARCHAR),
                                            new DataValue(stockChannel.getFeatureNo(), Types.VARCHAR),
                                            new DataValue(warehouse, Types.VARCHAR),
                                            new DataValue(direction, Types.VARCHAR), //DIRECTION ,调整方向 ： 1增加，  -1 减少
                                            new DataValue(sqty, Types.VARCHAR),
                                            
                                            new DataValue(stockChannel.getBaseunit(), Types.VARCHAR),
                                            new DataValue(bqty, Types.VARCHAR)
                                        
                                    };
                                    InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", billGoodsColumn);
                                    ib2.addValues(insValue2);
                                    this.addProcessData(new DataProcessBean(ib2));
                                    item++;
                                }
                            }
                        }
                        if(!CollectionUtils.isEmpty(stockChannel.getChannels())){
                            totcqty++;
                        }
                    }
                    if(flag){
                        DataValue[] insValue3 = null;
                        insValue3 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(getchannel, Types.VARCHAR),
                                new DataValue(billNo, Types.VARCHAR),
                                new DataValue(billType, Types.VARCHAR),
                                new DataValue(totpqty, Types.VARCHAR), //总数量
                                new DataValue(totcqty, Types.VARCHAR), // 种类数（有多少个商品 ，即item）
                                
                                new DataValue("", Types.VARCHAR),//memo
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(createTime, Types.DATE),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(createTime, Types.DATE),
                                new DataValue("1", Types.VARCHAR), //dealType  0:新增   1：调整   2：删除
                        };
                        InsBean ib3 = new InsBean("DCP_STOCK_CHANNEL_BILL", billColumn);
                        ib3.addValues(insValue3);
                        this.addProcessData(new DataProcessBean(ib3));
                        createbillNo++;
                    }
                }
            }
            
            
            //【ID1034458】【罗森尼娜3.3.0.5】库存分配逻辑bug  by jinzma 20230815
            for (DCP_StockAllocationRuleUpdateReq.level2ELm plu:pluList){
                if (!CollectionUtils.isEmpty(plu.getChannelList())){
                    String featureNo = plu.getFeatureNo();
                    if (Check.Null(featureNo)){
                        featureNo = " ";
                    }
                    String channelSql = " select channelid from dcp_stock_channel"
                            + " where eid='"+eId+"' and organizationno='"+plu.getOrganizationNo()+"' "
                            + " and pluno='"+plu.getPluNo()+"' and featureno='"+featureNo+"' ";
                    List<Map<String,Object>> channels = this.doQueryData(channelSql,null);
                    if (!CollectionUtils.isEmpty(channels)){
                        for (Map<String,Object>channel:channels){
                            if (plu.getChannelList().stream().noneMatch(a->a.getChannelId().equals(channel.get("CHANNELID").toString()))){
                              //删除dcp_stock_channel
                                DelBean db = new DelBean("DCP_STOCK_CHANNEL");
                                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db.addCondition("ORGANIZATIONNO", new DataValue(plu.getOrganizationNo(), Types.VARCHAR));
                                db.addCondition("PLUNO", new DataValue(plu.getPluNo(), Types.VARCHAR));
                                db.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                                db.addCondition("CHANNELID", new DataValue(channel.get("CHANNELID").toString(), Types.VARCHAR));
                                
                                this.addProcessData(new DataProcessBean(db));
                            }
                        }
                    }
                }
            }
            
            
            this.doExecuteDataToDB();
            
            
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败："+e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAllocationRuleUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAllocationRuleUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAllocationRuleUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_StockAllocationRuleUpdateReq req) throws Exception {
        boolean isFail = false;
        
        StringBuffer errMsg = new StringBuffer("");
        DCP_StockAllocationRuleUpdateReq.level1ELm request = req.getRequest();
        List<DCP_StockAllocationRuleUpdateReq.level2ELm> pluList = request.getPluList();
        
        if(CollectionUtils.isEmpty(pluList)){
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(request.getDealType())){
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_StockAllocationRuleUpdateReq.level2ELm pluInfo : pluList)
        {
            List<DCP_StockAllocationRuleUpdateReq.level3ELm> channelList = pluInfo.getChannelList();
            if(CollectionUtils.isEmpty(channelList))
            {
                //不敢加判断
               /* errMsg.append("商品编码:"+pluInfo.getPluNo()+"对应的渠道列表不可为空值, ");
                isFail = true;*/
            }
            else
            {
                //存在重复的渠道id，前端是根据DCP_ChannelQuery查询有返回重复的。
                List<String> channeIdList = new ArrayList<>();
                for (int i = channelList.size() - 1; i >= 0; i--)
                {
                    DCP_StockAllocationRuleUpdateReq.level3ELm oneLv3 = channelList.get(i);
                    String curChannelId = oneLv3.getChannelId();
                    if (channeIdList.contains(curChannelId))
                    {
                        channelList.remove(i);
                        continue;
                    }
                    else
                    {
                        channeIdList.add(curChannelId);
                    }

                    if (oneLv3.getAllocationValue()==null||oneLv3.getAllocationValue().isEmpty())
                    {
                        oneLv3.setAllocationValue("0");
                    }
                    else
                    {
                        try
                        {
                            double qty = Double.parseDouble(oneLv3.getAllocationValue());
                        }
                        catch (Exception e)
                        {
                            oneLv3.setAllocationValue("0");
                        }
                    }
                }

            }
        }


        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_StockAllocationRuleUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockAllocationRuleUpdateReq>(){};
    }
    
    @Override
    protected DCP_StockAllocationRuleUpdateRes getResponseType() {
        return new DCP_StockAllocationRuleUpdateRes();
    }
    
    /**
     * 查询是否存在该商品规则
     * @param stockChannel
     * @return
     */
    private String existPlunoRule(StockChannel stockChannel){
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.EID, a.ORGANIZATIONNO, a.PLUNO, a.FEATURENO, a.SUNIT " +
                "  FROM DCP_STOCK_ALLOCATION_RULE a WHERE EID = '"+stockChannel.getEId()+"' " +
                " and ORGANIZATIONNO = '"+stockChannel.getOrganizationNo()+"' and PLUNO = '"+stockChannel.getPluNo()+"' " +
                " and FEATURENO = '"+stockChannel.getFeatureNo()+"' and SUNIT = '"+stockChannel.getSUnit()+"'");
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 查询商品在库存分配表中现有的记录
     * @param stockChannel
     * @return
     */
    private String getPluNoChannel(StockChannel stockChannel){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.EID, a.ORGANIZATIONNO, a.CHANNELID, a.PLUNO, a.FEATURENO ,a.WAREHOUSE,a.ONLINEQTY,a.LOCKQTY,a.BASEUNIT," +
                "a.BONLINEQTY ,a.BLOCKQTY, a.SUNIT " +
                "FROM DCP_STOCK_CHANNEL a  " +
                " INNER JOIN DCP_ORG b ON a.EID  = b.EID AND a.ORGANIZATIONNO  = b.ORGANIZATIONNO AND a.WAREHOUSE  = b.OUT_COST_WAREHOUSE " +
                " where a.EID = '"+stockChannel.getEId()+"' and a.ORGANIZATIONNO = '"+stockChannel.getOrganizationNo()+"' and a.PLUNO ='"+stockChannel.getPluNo()+"' " +
                " And a.featureNo = '"+stockChannel.getFeatureNo()+"' And a.SUNIT = '"+stockChannel.getSUnit()+"'");
        sql = sqlbuf.toString();
        return sql;
    }
    
    
    /**
     * 拿到所有的渠道
     * @return
     */
    private Set<String> getchannels(DCP_StockAllocationRuleUpdateReq req){
        List<DCP_StockAllocationRuleUpdateReq.level2ELm> pluList = req.getRequest().getPluList();
        Set<String> channels = new HashSet<>();
        for (DCP_StockAllocationRuleUpdateReq.level2ELm level2ELm : pluList) {
            for (DCP_StockAllocationRuleUpdateReq.level3ELm level3ELm : level2ELm.getChannelList()) {
                channels.add(level3ELm.getChannelId());
            }
        }
        return channels;
    }
    
    /**
     * 获取单位预留几位
     * @param req
     * @return
     */
    private String getUnit(DCP_StockAllocationRuleUpdateReq req){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT UNIT,UDLENGTH FROM DCP_UNIT  WHERE EID = '"+req.geteId()+"' and status = '100'");
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 获取最新单据号
     * @param req
     * @return
     * @throws Exception
     */
    private String queryMaxBillNo(DCP_StockAllocationRuleUpdateReq req) throws Exception{
        String billNo = "";
        String eId = req.geteId();
        String billType = "QDFH001";
//        String channelId = req.getRequest().getPluList().get(0).getChannelId();

//		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String bDate = sdf.format(now);
        
        String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' and billNo like 'QDFH"+bDate+"%%%' "
//				 + " and  channelId = '"+channelId+"' "
                ;
        
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        
        if (getQData != null && getQData.isEmpty() == false) {
            
            billNo = (String) getQData.get(0).get("BILLNO");
            
            if (billNo != null && billNo.length() > 0) {
                long i;
                billNo = billNo.substring(4, billNo.length());
                i = Long.parseLong(billNo) + 1;
                billNo = i + "";
                billNo = "QDFH" + billNo;
                
            } else {
                billNo = "QDFH" + bDate + "00001";
            }
        } else {
            billNo = "QDFH" + bDate + "00001";
        }
        
        return billNo;
    }
    
    protected String getString(String[] str)
    {
        String str2 = "";
        for (String s:str)
        {
            str2 = str2 + "'" + s + "'"+ ",";
        }
        if (str2.length()>0)
        {
            str2=str2.substring(0,str2.length()-1);
        }
        
        return str2;
    }
    
    /**
     * 查询商品有无在 dcp_stock
     * @param
     * @return
     */
    private String getStock(DCP_StockAllocationRuleUpdateReq req,String organizationNO,String pluNo ,String featureNo){
        String eId = req.geteId();
        String companyId = req.getBELFIRM();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        
        // 商品模板表
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+organizationNO+"'"
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/
        
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        
        //以下注释，实在不能理解，查库存干嘛要去关联商品模板表，这个就是SA或者PR对系统不熟悉造成的，给后人造成困扰  by jinzma 20230625
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+organizationNO+"') "
                + " )");*/
        
        sqlbuf.append("SELECT a.pluno,a.FEATURENO,a.WAREHOUSE,a.BASEUNIT,a.QTY,a.ONLINEQTY FROM " +
                " dcp_stock a " +   //left JOIN goodstemplate b ON a.eid = b.eid AND b.pluno = a.pluno
                " INNER JOIN DCP_ORG c ON a.EID  = c.EID AND a.ORGANIZATIONNO  = c.ORGANIZATIONNO AND a.WAREHOUSE  = c.OUT_COST_WAREHOUSE" +
                " WHERE  a.eid = '"+eId+"' AND a.ORGANIZATIONNO = '"+organizationNO+"' AND a.PLUNO = '"+pluNo+"' and a.featureNo = '"+featureNo+"'");
        
        sql = sqlbuf.toString();
        return  sql;
    }
    
    /**
     * 如果不存在 dcp_stock 表中 则查询商品相关的信息
     * @param eId
     * @param organizationNO
     * @param pluNo
     * @return
     */
    private String getPlunoInfo(String eId,String organizationNO ,String pluNo ){
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.PLUNO ,a.BASEUNIT ,b.OUT_COST_WAREHOUSE FROM dcp_goods a " +
                "LEFT JOIN DCP_ORG b ON a.EID  = b.EID  " +
                "WHERE a.EID = '"+eId+"' AND a.PLUNO = '"+pluNo+"' AND b.ORGANIZATIONNO = '"+organizationNO+"'");
        sql = sqlbuf.toString();
        return  sql;
    }
    
}


/**
 * 渠道库存接口 相关的数据传输实体类
 */
@Data
class StockChannel{
    private String eId;
    private String organizationNo;
    private String pluNo;
    private String featureNo;
    private String sUnit;
    private String baseunit;
    private List<channel> channels; // 渠道列表
    
    private String ruleType; // 规则类型
    private Boolean existRule; // 是否存在库中规则
    //
    private String warehouse;
    private String warehouseOnlineqty; // 仓库预留数
    private String QTY; // 取到商品的总库存数
    private Boolean existStock; // 是否存在库中
    
    @Data
    public class channel{
        private String channelId;
        private String warehouseId;
        private String onlineqty;       // 原销售单位预留数
        private String allocationValue = "0"; // 现销售单位预留数
        private String bOnlineqty;      // 原基准单位预留数
        private String bQty;            // 现基准单位预留数
        private Boolean existStockChannelid; // 是否存在分配表中
        
        private String lockQty;         // 锁定数
        private String blockQty;         // 基准单位锁定数
        
    }
    
}
