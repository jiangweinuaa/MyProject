package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAutoAllocationReq;
import com.dsc.spos.json.cust.res.DCP_StockAutoAllocationRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_StockAutoAllocation
 * 服务说明：自动分配
 *
 * @author wangzyc 2021-03-16
 */
public class DCP_StockAutoAllocation extends SPosAdvanceService<DCP_StockAutoAllocationReq, DCP_StockAutoAllocationRes> {
    @Override
    protected void processDUID(DCP_StockAutoAllocationReq req, DCP_StockAutoAllocationRes res) throws Exception {
        /**
         * 服务逻辑：
         * 前端：pluList不传代表全部商品自动分配；pluList传代表指定商品自动分配
         *
         * 根据自动分配规则表，各渠道预留上架的商品范围、共享的商品范围更新；
         *  新预留上架的商品，库存根据分配规则自动上架数量；
         *  已预留上架的商品，库存根据分配规则自动增量更新数量；
         */
        DCP_StockAutoAllocationReq.level1Elm request = req.getRequest();
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

        String [] columns_STOCK = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","WAREHOUSE","BASEUNIT","QTY","LOCKQTY","ONLINEQTY","CREATEOPID"
                ,"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

        String sql = this.getPlunoRule(req);
        List<Map<String, Object>> datas = this.doQueryData(sql, null);

        sql = this.getUnit(req);
        List<Map<String, Object>> units = this.doQueryData(sql, null);

        // 拿到所有渠道
        Set<String> getchannels = new HashSet<>();

        List<StockChannel> stockChannels = new ArrayList<>();
        if(!CollectionUtils.isEmpty(datas)){
            for (Map<String, Object> data : datas) {
                String eid = data.get("EID").toString();
                String organizationno = data.get("ORGANIZATIONNO").toString();
                String pluno = data.get("PLUNO").toString();
                String featureno = data.get("FEATURENO").toString();
                String channelid = data.get("CHANNELID").toString();
                String sunit = data.get("SUNIT").toString();
                String ruletype = data.get("RULETYPE").toString();
                String allocationvalue = data.get("ALLOCATIONVALUE").toString();
                Double iallocationvalue  = Double.parseDouble(allocationvalue);
                Double baseQty = 0.0;
                getchannels.add(channelid);

                StockChannel stockChannel1 = new StockChannel();
                stockChannel1.setEId(eid);
                stockChannel1.setFeatureNo(featureno);
                stockChannel1.setSUnit(sunit);
                stockChannel1.setPluNo(pluno);
                stockChannel1.setOrganizationNo(organizationno);
                stockChannel1.setRuleType(ruletype);
                stockChannel1.setChannels(new ArrayList<StockChannel.channel>());

                //************************************ 查询该商品是否存在dcp_stock  Begin**********************
                String baseUnit = ""; // 基本单位
                String warehouse =""; // 仓库 出货仓
                Boolean existStock = false; // 是否存在 dcp_stock 表中 存在:true/ 不存在:false
                String warehouseOnlineqty = "0"; // 仓库预留数
                String qty = "0"; // 仓库 在库数
                String sql2 = this.getStock(req,organizationno,pluno,featureno);
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
                    sql2 = this.getPlunoInfo(eId, organizationno, pluno);
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
                double warehouseQty = Double.parseDouble(qty);
                double warehouseOnlineQty = Double.parseDouble(warehouseOnlineqty);
                stockChannel1.setWarehouse(warehouse);
                stockChannel1.setQTY(qty);
                stockChannel1.setWarehouseOnlineqty(warehouseOnlineqty);
                stockChannel1.setBaseunit(baseUnit);
                stockChannel1.setExistStock(existStock);
                //************************************ 查询该商品是否存在dcp_stock  Begin**********************

                // 查询商品在库存分配表中是否有记录
                sql  =this.getStockChannel(eId,organizationno,pluno,featureno,channelid,warehouse);
                List<Map<String, Object>> stockChannel = this.doQueryData(sql, null);

                StockChannel.channel channel = stockChannel1.new channel();
                channel.setExistStockChannelid(false);
                channel.setChannelId(channelid);

                Double iallocationValue = 0.0;
                Double bQty = 0.0;  // 现在基准单位数
                Double baseUnitQty = 0.0;

                Double yQTY = 0.0; // 预留数
                String direction = "1"; // 增加减少

                // 有记录
                if(!CollectionUtils.isEmpty(stockChannel)){
                    DelBean db2 = new DelBean("DCP_STOCK_CHANNEL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("ORGANIZATIONNO", new DataValue(organizationno, Types.VARCHAR));
                    db2.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
                    db2.addCondition("CHANNELID", new DataValue(channelid, Types.VARCHAR));
                    db2.addCondition("FEATURENO", new DataValue(featureno, Types.VARCHAR));
                    db2.addCondition("SUNIT", new DataValue(sunit, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    channel.setExistStockChannelid(true);

                    // 在库中已有记录 需要把之前记录拿出做比对，然后更新 写单据记录

                    Map<String, Object> stockChannelMap = stockChannel.get(0); // 查询出的库存分配表中的信息 精确查询 只有一条记录
                    String onlineqty = stockChannelMap.get("ONLINEQTY").toString();
                    String bonlineqty = stockChannelMap.get("BONLINEQTY").toString();
                    String lockqty = stockChannelMap.get("LOCKQTY").toString();
                    String blockqty = stockChannelMap.get("BLOCKQTY").toString();
                    int onlineqtyw = Integer.parseInt(onlineqty);
                    channel.setOnlineqty(onlineqty);
                    channel.setBOnlineqty(bonlineqty);
                    channel.setLockQty(lockqty);
                    channel.setBlockQty(blockqty);

                        if(ruletype.equals("0")){
                            iallocationValue = Double.parseDouble(allocationvalue);
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
                                bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, iallocationValue+""));;
                                unitConvert = PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, i1+"");
                                yQTY = Double.parseDouble(unitConvert);
                                if(warehouseOnlineQty==0){
                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, iallocationValue+""));
                                }

                                if(yQTY>0){
                                    direction ="1";
                                }else if(yQTY<0){
                                    direction ="-1";
                                    yQTY = yQTY * -1;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if(ruletype.equals("1")){
                            iallocationValue = Double.parseDouble(allocationvalue);
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
                                if(unit1.equals(sunit)){
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
                                unitConvert2 = PosPub.getUnitConvert(dao, eId, pluno, baseUnit, sunit, v + "");//
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
                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit,onlineqty+"" ));
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


                }else {
                    // 无记录
                    // 如果在库存没有记录 则插入一条库存分配记录
                        String onlineqty = "0";
                        String bonlineqty = "0";
                        int onlineqtyw = Integer.parseInt(onlineqty);
                        channel.setOnlineqty(onlineqty);
                        channel.setBOnlineqty(bonlineqty);
                        channel.setExistStockChannelid(false);


                        if(ruletype.equals("0")){
                            iallocationValue = Double.parseDouble(allocationvalue);
                            // 按数量
                            String unitConvert = "";
                            try {
                                Double i1 = iallocationValue-onlineqtyw;

                                // 计算基准单位
                                bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, iallocationValue+""));;
                                unitConvert = PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, i1+"");
                                yQTY = Double.parseDouble(unitConvert);
                                if(warehouseOnlineQty==0){
                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit, iallocationValue+""));
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
                        else if(ruletype.equals("1")){
                            iallocationValue = Double.parseDouble(allocationvalue);
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
                                if(unit1.equals(sunit)){
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
                                unitConvert2 = PosPub.getUnitConvert(dao, eId, pluno, baseUnit, sunit, v + "");//
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
                                    yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sunit, baseUnit,onlineqty+"" ));
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
                // 不存在 DCP_STOCK 表中数据
                if(stockChannel1.getExistStock()==false){
                    // 库存表中不存在的时候 插入一条数据
                    DataValue[] insValue2 = null;
                    insValue2 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(organizationno, Types.VARCHAR),

                            new DataValue(pluno, Types.VARCHAR),
                            new DataValue(featureno, Types.VARCHAR),
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
                    stockChannel1.setExistStock(true);
                    this.doExecuteDataToDB();
                }else {
                    UptBean ub2 = null;
                    ub2 = new UptBean("DCP_STOCK");
                    // condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationno, Types.VARCHAR));
                    ub2.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
                    ub2.addCondition("FEATURENO", new DataValue(featureno, Types.VARCHAR));
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
                stockChannel1.getChannels().add(channel);

                DataValue[] insValue2 = null;
                insValue2 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(channelid, Types.VARCHAR),
                        new DataValue(organizationno, Types.VARCHAR),

                        new DataValue(pluno, Types.VARCHAR),
                        new DataValue(sunit, Types.VARCHAR),
                        new DataValue(featureno, Types.VARCHAR),
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

                stockChannels.add(stockChannel1);
            }

            Set<StockChannel> set = new TreeSet<>(new Comparator<StockChannel>() {
                @Override
                public int compare(StockChannel sc1, StockChannel sc2) {
                    int count = 1;
                    if(StringUtils.equals(sc1.getOrganizationNo(), sc2.getOrganizationNo()) &&
                            StringUtils.equals(sc1.getPluNo(),sc2.getPluNo())&&
                            StringUtils.equals(sc1.getFeatureNo(),sc2.getFeatureNo())&&
                            StringUtils.equals(sc1.getSUnit(),sc2.getSUnit())&&
                            StringUtils.equals(sc1.getBaseunit(),sc2.getBaseunit())){
                        count = 0;
                    }
                    return count;
                }
            });
            set.addAll(stockChannels);
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
                for (StockChannel stockChannel : set) {
                    for (StockChannel gstockChannel : stockChannels) {
                        // 判断该商品是否存在规则
                        flag = true;
                        for (StockChannel.channel channel : gstockChannel.getChannels()) {
                            if(channel.getChannelId().equals(getchannel)){
                                if(Check.Null(gstockChannel.getWarehouse())){
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

                                totpqty+=sqty;
                                DataValue[] insValue2 = null;
                                insValue2 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(getchannel, Types.VARCHAR),
                                        new DataValue(billNo, Types.VARCHAR), // 没有
                                        new DataValue(billType, Types.VARCHAR), //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
                                        new DataValue(item, Types.VARCHAR),
                                        new DataValue(gstockChannel.getOrganizationNo(), Types.VARCHAR),

                                        new DataValue(gstockChannel.getPluNo(), Types.VARCHAR),
                                        new DataValue(gstockChannel.getSUnit(), Types.VARCHAR),
                                        new DataValue(gstockChannel.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(channel.getWarehouseId(), Types.VARCHAR),
                                        new DataValue(direction, Types.VARCHAR), //DIRECTION ,调整方向 ： 1增加，  -1 减少
                                        new DataValue(sqty, Types.VARCHAR),

                                        new DataValue(gstockChannel.getBaseunit(), Types.VARCHAR),
                                        new DataValue(bqty, Types.VARCHAR)

                                };
                                InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", billGoodsColumn);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));
                                item++;
                            }
                        }
                    }
                    totcqty++;
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
        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAutoAllocationReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAutoAllocationReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAutoAllocationReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAutoAllocationReq req) throws Exception {
        boolean isFail = false;
//        String dealType = req.getRequest().getDealType();
//        StringBuffer errMsg = new StringBuffer("");
//        if(Check.Null(dealType)){
//            errMsg.append("操作类型不可为空值, ");
//            isFail = true;
//        }
//        if (isFail){
//            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockAutoAllocationReq> getRequestType() {
        return new TypeToken<DCP_StockAutoAllocationReq>(){};
    }

    @Override
    protected DCP_StockAutoAllocationRes getResponseType() {
        return new DCP_StockAutoAllocationRes();
    }

    /**
     * 查询指定/所有商品规则
     * @param req
     * @return
     */
    private String getPlunoRule(DCP_StockAutoAllocationReq req){
        DCP_StockAutoAllocationReq.level1Elm request = req.getRequest();
        List<DCP_StockAutoAllocationReq.level2Elm> pluList = request.getPluList();
        String organizationNoStr = "",plunoStr="",featureStr="",sunitStr="";
        String org_form = req.getOrg_Form(); // 0:公司  2:门店
        if(!CollectionUtils.isEmpty(pluList)){
            int size = pluList.size();
            String[] organizationNoArray = new String[size] ;
            String[] plunoArray = new String[size] ;
            String[] featureNoArray = new String[size] ;
            String[] sunitArray = new String[size] ;
            int i=0;
            for (DCP_StockAutoAllocationReq.level2Elm lv2 : request.getPluList())
            {
                String organizationNo = "",pluNo = "",featureNo="",sunit="";
                if(!Check.Null(lv2.getOrganizationNo())){
                    organizationNo = lv2.getOrganizationNo();
                }
                if(!Check.Null(lv2.getPluNo())){
                    pluNo = lv2.getPluNo();
                }
                if(!Check.Null(lv2.getFeatureNo())){
                    featureNo = lv2.getFeatureNo();;
                }else{
                    featureNo = " ";
                }
                if(!Check.Null(lv2.getSUnit())){
                    sunit = lv2.getSUnit();
                }
                organizationNoArray[i] = organizationNo;
                plunoArray[i] = pluNo;
                featureNoArray[i] = featureNo;
                sunitArray[i] = sunit;
                i++;
            }
            organizationNoStr = getString(organizationNoArray);
            plunoStr = getString(plunoArray);
            featureStr = getString(featureNoArray);
            sunitStr = getString(sunitArray);
        }

        String sql = "";
        StringBuilder sqlBuf = new StringBuilder("");
        sqlBuf.append("SELECT EID,ORGANIZATIONNO,PLUNO,FEATURENO,SUNIT,CHANNELID,RULETYPE,ALLOCATIONVALUE " +
                " FROM DCP_STOCK_ALLOCATION_RULE " +
                " where CHANNELID <> ' ' and  eid = '"+req.geteId()+"'");
        // 组织类型是门店的情况下 分配所属门店
        if(org_form.equals("2")&&Check.Null(organizationNoStr)&&organizationNoStr.length()<2){
            sqlBuf.append(" AND ORGANIZATIONNO = '"+req.getOrganizationNO()+"'");
        }

        if(!Check.Null(organizationNoStr)&&organizationNoStr.length()>2){
            sqlBuf.append(" AND ORGANIZATIONNO IN ("+organizationNoStr+")");
        }
        if(!Check.Null(plunoStr)&&plunoStr.length()>2){
            sqlBuf.append(" AND PLUNO IN ("+plunoStr+")");
        }
        if(!Check.Null(featureStr)&&featureStr.length()>2){
            sqlBuf.append(" AND featureNo IN ("+featureStr+")");
        }
        if(!Check.Null(sunitStr)&&sunitStr.length()>2){
            sqlBuf.append(" AND SUNIT IN ("+sunitStr+")");
        }

        sql = sqlBuf.toString();
        return sql ;
    }

    /**
     * 根据条件 筛选查询 库存信息
     * @param eId
     * @param organizationno
     * @param pluno
     * @param featureno
     * @return
     */
    private String getStock(String eId,String organizationno,String pluno,String featureno){
        String sql = "";
        StringBuffer sqlBuf = new StringBuffer("");
        sqlBuf.append("SELECT ORGANIZATIONNO,PLUNO ,FEATURENO,WAREHOUSE,BASEUNIT,QTY,ONLINEQTY " +
                " FROM DCP_STOCK " +
                " where eid = '"+eId+"' and organizationno = '"+organizationno+"' and pluno = '"+pluno+"' and FEATURENO = '"+featureno+"'");
        sql =sqlBuf.toString();
        return sql;
    }

    /**
     * 根据条件 筛选查询 有无之前的库存信息
     * @param eId
     * @param organizationno
     * @param pluno
     * @param featureno
     * @return
     */
    private String getStockChannel(String eId,String organizationno,String pluno,String featureno,String channelid,String wareHouse){
        String sql = "";
        StringBuffer sqlBuf = new StringBuffer("");
        sqlBuf.append("SELECT ORGANIZATIONNO,PLUNO ,FEATURENO,WAREHOUSE,SUNIT,CHANNELID,ONLINEQTY,BASEUNIT,BONLINEQTY,LOCKQTY,BLOCKQTY " +
                " FROM DCP_STOCK_CHANNEL " +
                " where eid = '"+eId+"' and organizationno = '"+organizationno+"' and pluno = '"+pluno+"' and FEATURENO = '"+featureno+"' and CHANNELID = '"+channelid+"' " +
                " AND WAREHOUSE = '"+wareHouse+"'");
        sql =sqlBuf.toString();
        return sql;
    }
    /**
     * 获取单位预留几位
     * @param req
     * @return
     */
    private String getUnit(DCP_StockAutoAllocationReq req){
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
    private String queryMaxBillNo(DCP_StockAutoAllocationReq req) throws Exception{
        String billNo = "";
        String eId = req.geteId();
        String billType = "QDFH001";
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String bDate = sdf.format(now);

        String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' and billNo like 'QDFH"+bDate+"%%%' "
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
    private String getStock(DCP_StockAutoAllocationReq req, String organizationNO, String pluNo , String featureNo){
        String eId = req.geteId();
        String companyId = req.getBELFIRM();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");

        // 商品模板表
       /* sqlbuf.append(" "
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
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+organizationNO+"') "
                + " )");
        

        sqlbuf.append("SELECT a.pluno,a.FEATURENO,a.WAREHOUSE,a.BASEUNIT,a.QTY,a.ONLINEQTY FROM " +
                " dcp_stock a left JOIN goodstemplate b ON a.eid = b.eid AND b.pluno = a.pluno " +
                " INNER JOIN DCP_ORG c ON a.EID  = c.EID AND a.ORGANIZATIONNO  = c.ORGANIZATIONNO AND a.WAREHOUSE  = c.OUT_COST_WAREHOUSE" +
                " WHERE  b.eid = '"+eId+"' AND a.ORGANIZATIONNO = '"+organizationNO+"' AND b.PLUNO = '"+pluNo+"' and a.featureNo = '"+featureNo+"'");

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
