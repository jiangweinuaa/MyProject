package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_StockAllocationRuleUpdateReq;
import com.dsc.spos.json.cust.req.DCP_N_StockAllocationRuleUpdateReq.*;
import com.dsc.spos.json.cust.res.DCP_N_StockAllocationRuleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 服务函数：DCP_N_StockAllocationRuleUpdate
 * 服务说明：N_分配规则设置修改
 * @author jinzma
 * @since  2024-04-22
 */
public class DCP_N_StockAllocationRuleUpdate extends SPosAdvanceService<DCP_N_StockAllocationRuleUpdateReq, DCP_N_StockAllocationRuleUpdateRes> {
    @Override
    protected void processDUID(DCP_N_StockAllocationRuleUpdateReq req, DCP_N_StockAllocationRuleUpdateRes res) throws Exception {
        /** 旧版服务逻辑说明(DCP_StockAllocationRuleUpdate服务)：
         *  添加：
         *  判断该商品是否存在规则，如果不存在规则则添加，如果存在该规则则修改，添加的时候，只添加DCP_STOCK_ALLOCATION_RULE（库存分配规则）
         *
         *  修改：
         *  如果该商品规则已存在规则且 dealType 等于0：0库存不自动分配
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
         *

         //DCP_N_StockAllocationRuleUpdate  小程序合并 重新整理了旧版的逻辑，说明如下
         1、商品是否存在规则表   dcp_stock_allocation_rule
         2、商品是否存在库存表   dcp_stock
         存在获取相关信息              baseUnit    warehouse    warehouseOnlineqty     qty
         不存在则查询商品基本资料       baseUnit    warehouse   warehouseOnlineqty=0   qty=0
         3、商品是否存在库存分配表   DCP_STOCK_CHANNEL  （ pluChannels ）

         一、 不存在规则且库存不自动分配
         1、新增 DCP_STOCK_ALLOCATION_RULE规则表
         二、 存在规则且库存不自动分配
         1、删除 DCP_STOCK_ALLOCATION_RULE规则表
         2、新增 DCP_STOCK_ALLOCATION_RULE规则表
         3、判断 DCP_STOCK_CHANNEL是否有资料，没有就创建
         三、 存在规则且库存自动分配
         1、删除DCP_STOCK_ALLOCATION_RULE规则表
         2、循环channelList 删除 DCP_STOCK_CHANNEL
         3、循环channelList {
         3.1、 新增 DCP_STOCK_ALLOCATION_RULE规则表
         3.2、 if pluChannels有值 {
         3.2.1、 循环pluChannels {
         判断 pluChannels  == channelList一致
         String onlineqty = pluChannel.get("ONLINEQTY").toString();
         String bonlineqty = pluChannel.get("BONLINEQTY").toString();
         String lockqty = pluChannel.get("LOCKQTY").toString();
         String blockqty = pluChannel.get("BLOCKQTY").toString();
         int onlineqtyw = Integer.parseInt(onlineqty);
         分配规则按照数量
         if  (ruleType.equals("0")){
         i1 =   前端传入的 iallocationValue 减去   pluChannels. onlineqty（渠道表里面的预留数） ;
         if(i1>0){direction ="1" }else if(i1<0){direction ="-1" }
         bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));;  转成baseUnit对应的分配数量
         unitConvert = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, i1+"");    转成baseUnit对应的分配数量减去线上库存数量
         if(warehouseOnlineQty==0){
         warehouseOnlineQty = getStockMap.get("ONLINEQTY").toString();  //仓库预留数==0
         yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));
         yQTY = 前端传入的分配数量
         }
         if(yQTY<0){yQTY =  yQTY*-1;}
         }

         分配规则按照比例
         if(ruleType.equals("1")){
         double v = warehouseQty * (iallocationValue * 0.01);   //  warehouseQty =  qty = getStockMap.get("QTY").toString();
         String strBaseUnit = new DecimalFormat(baseUnitRetain).format(v); （基准单位小数位数保留）
         String unitConvert2 = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, sUnit, v + "" );    转换成销售单位对应的数量
         String  strsUnit = new DecimalFormat(sUnitRetain).format(Double.parseDouble(unitConvert2));   销售单位小数位数
         bQty = Double.parseDouble(strBaseUnit);  基准单位对应的分配数量

         iallocationValue = Double.parseDouble(strsUnit) ;      //销售单位对应的分配数量
         baseUnitQty = Double.parseDouble(strBaseUnit);       //基准单位对应的分配数量
         yQTY = baseUnitQty - Double.parseDouble(bonlineqty);     bonlineqty = pluChannel.get("BONLINEQTY").toString();

         if(yQTY>0){ direction ="1";}else if(yQTY<0){ direction ="-1";yQTY = yQTY * -1; }
         if(warehouseOnlineQty==0){
         yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit,onlineqty+"" ));
         注  onlineqty = pluChannel.get("ONLINEQTY").toString();    基准单位对应的分配数量
         }
         }
         }---循环结束
         } --if pluChannels有值判断结束



         3.3、 if pluChannels没有值 {
         循环channelList
         if channelList.getChannelId().equals(channelId) {  channelId = channelList.getChannelId();
         String onlineqty = "0";
         String bonlineqty = "0";
         int onlineqtyw = Integer.parseInt(onlineqty);

         //分配规则按照数量
         if(ruleType.equals("0")) {
         i1 =   前端传入的 iallocationValue 减去   onlineqtyw   ;    onlineqtyw=0
         bQty =Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));;  转成baseUnit对应的分配数量
         unitConvert = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, i1+"");    转成baseUnit对应的分配数量
         yQTY = Double.parseDouble(unitConvert);
         if(warehouseOnlineQty==0){
         yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, iallocationValue+""));
         yQTY = 前端传入的基准单位对应的分配数量
         }
         if(yQTY>0){ direction ="1";}else if(yQTY<0){direction ="-1"; yQTY = i1*-1;}
         }

         //分配规则按照比例
         if(ruleType.equals("1")) {
         double v = warehouseQty * (iallocationValue * 0.01);       warehouseQty = getStockMap.get("QTY").toString();  //基准单位对应的分配数量
         strBaseUnit = new DecimalFormat(baseUnitRetain).format(v);   保留基准单位对应的小数位数
         unitConvert2 = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, sUnit, v + "");   转换成销售单位对应的分配数量
         strsUnit = new DecimalFormat(sUnitRetain).format(Double.parseDouble(unitConvert2));   //保留销售单位对应的小数位数
         bQty = Double.parseDouble(strBaseUnit);   //保留基准单位对应的小数位数

         iallocationValue = Double.parseDouble(strsUnit) ;    //销售单位对应的分配数量
         baseUnitQty = Double.parseDouble(strBaseUnit);    // 基准单位对应的小数位数 //基准单位对应的分配数量
         yQTY = baseUnitQty - Double.parseDouble(bonlineqty); // 多少         bonlineqty==0
         if  （warehouseQty==0）  {                // warehouseQty = Double.parseDouble(qty);    qty = getStockMap.get("QTY").toString();
         yQTY = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit,onlineqty+"" ));   --onlineqty==0
         }

         if(yQTY>0){direction ="1"; }else if(yQTY<0){ direction ="-1"; yQTY = yQTY * -1; }
         }
         }
         }


         4、 if(stockChannel.getExistStock()==false）
         插入 DCP_STOCK   // 库存表中不存在的时候 插入一条数据
         参考原服务DCP_StockAllocationRuleUpdate服务  代码 631行
         }else {
         修改 DCP_STOCK
         if(direction.equals("1")){ // 1：增加 ，  -1：减少
         ub2.addUpdateValue("ONLINEQTY", new DataValue(yQTY, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));
         }
         if(direction.equals("-1")){
         ub2.addUpdateValue("ONLINEQTY", new DataValue(yQTY, Types.VARCHAR, DataValue.DataExpression.SubSelf));
         }
         }

         5、 新增 DCP_STOCK_CHANNEL
         参考原服务DCP_StockAllocationRuleUpdate服务 代码695行

         6、循环前端传入的渠道数组
         6.1、 写入DCP_STOCK_CHANNEL_BILLGOODS
         6.2、 写入DCP_STOCK_CHANNEL_BILL

         7、//【ID1034458】【罗森尼娜3.3.0.5】库存分配逻辑bug  by jinzma 20230815
         参考原服务DCP_StockAllocationRuleUpdate服务 代码817行

         **/

        /**
         * 新版服务逻辑说明
         *   前端传入的如果是多规格商品，需要查询对应的子商品  sa王辉要求
         *   商品单位，直接取商品基本资料表里面的销售单位     sa王辉要求
         *   1、库存不自动分配
         *      删除 DCP_STOCK_ALLOCATION_RULE规则表
         *      新增 DCP_STOCK_ALLOCATION_RULE规则表
         *      判断 DCP_STOCK_CHANNEL是否有资料，没有就创建
         *        if DCP_STOCK_CHANNEL新增 then 新增 DCP_STOCK_CHANNEL_BILL 和 DCP_STOCK_CHANNEL_BILLGOODS

         *   2、库存自动分配
         *       删除 DCP_STOCK_ALLOCATION_RULE规则表
         *       删除 DCP_STOCK_CHANNEL
         *       按照分配规则，按数量或者按比例计算待分配的数量
         *       如果渠道表有资料，按比例计算的时候，需要加减原渠道表中的数量，按比例基准数取DCP_STOCK表中的QTY数量
         *       渠道a  比例10%
         *                                 dcp_stock.onlineqty  渠道a onlineqty
         *       执行前                         10个                    5个
         *       第一次执行 10*10% = 1           6个                     1个
         *       第二次执行 10*10% = 1           6个                     1个
         *
         *
         *       判断 DCP_STOCK是否有值，没有插入一笔 ，有值更新ONLINEQTY
         *       新增 DCP_STOCK_CHANNEL
         *       新增 DCP_STOCK_CHANNEL_BILL 和 DCP_STOCK_CHANNEL_BILLGOODS
         *       BUG修复 ID1034458】【罗森尼娜3.3.0.5】库存分配逻辑bug （删除未传入渠道的DCP_STOCK_CHANNEL）
         */


        try{
            String eId = req.geteId();
            String dealType = req.getRequest().getDealType();  //操作类型（0库存不自动分配，1库存自动分配）
            List<Plu> pluList = req.getRequest().getPluList();

            for (Plu plu:pluList){
                String pluNo = plu.getPluNo();
                //String pluName = plu.getPluName();
                String pluType = plu.getPluType();
                List<Shop> shopList = plu.getShopList();

                for (Shop shop:shopList){
                    String organizationNo = shop.getOrganizationNo();
                    String ruleType = shop.getRuleType();   //分配规则（0按数量1按比例）
                    List<Map<String, Object>> getMultiSpecPlus = new ArrayList<>();

                    //删除 DCP_STOCK_ALLOCATION_RULE      参阅旧版服务DCP_StockAllocationRuleUpdate //【ID1034458】【罗森尼娜3.3.0.5】库存分配逻辑bug  by jinzma 20230815
                    if (pluType.equals("MULTISPEC")){
                        String sql = " select distinct pluno from dcp_mspecgoods_subgoods where eid='"+eId+"' and masterpluno='"+pluNo+"'";
                        getMultiSpecPlus = this.doQueryData(sql, null);
                        if (CollectionUtils.isNotEmpty(getMultiSpecPlus)){
                            for (Map<String, Object> plus: getMultiSpecPlus){
                                DelBean db = new DelBean("DCP_STOCK_ALLOCATION_RULE");
                                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                                db.addCondition("PLUNO", new DataValue(plus.get("PLUNO").toString(), Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(db));
                            }
                        }else {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"多规格商品:"+pluNo+" 找不到对应的子商品");
                        }
                    } else if (pluType.equals("NORMAL")){
                        DelBean db = new DelBean("DCP_STOCK_ALLOCATION_RULE");
                        db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                        db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db));
                    }else {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"仅支持普通商品或者多规格商品调整库存分配! "); //SA王辉规划的，旧版的功能在新版里面没有了
                    }

                    this.doExecuteDataToDB();   //删除以后就去提交，这样后面的新增不会报错且分析旧版服务，这个表DCP_STOCK_ALLOCATION_RULE 每次都要删除


                    List<Channel> channelList = shop.getChannelList();
                    for (Channel channel:channelList){
                        String channelId = channel.getChannelId();
                        String allocationValue = channel.getAllocationValue();   //分配比例/数量值

                        //多规格商品实际针对子商品进行库存分配
                        if (pluType.equals("MULTISPEC")){
                            for (Map<String, Object> plus: getMultiSpecPlus){
                                // -1 代表删除这个渠道下面的 DCP_STOCK_CHANNEL
                                if ("-1".equals(allocationValue)){
                                    //删除dcp_stock_channel
                                    DelBean db = new DelBean("DCP_STOCK_CHANNEL");
                                    db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db.addCondition("PLUNO", new DataValue(plus.get("PLUNO").toString(), Types.VARCHAR));
                                    db.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));

                                    this.addProcessData(new DataProcessBean(db));
                                }else {
                                    if ("0".equals(dealType)) {
                                        ChannelStock(req, plus.get("PLUNO").toString(), organizationNo, ruleType, channelId, allocationValue);
                                    } else {
                                        ChannelDealStock(req, plus.get("PLUNO").toString(), organizationNo, ruleType, channelId, allocationValue);
                                    }
                                }

                                this.doExecuteDataToDB();   //一笔就提交,这样比较简单，便于内部产生异动单单据
                            }

                        }
                        if (pluType.equals("NORMAL")){
                            // -1 代表删除这个渠道下面的 DCP_STOCK_CHANNEL
                            if ("-1".equals(allocationValue)){
                                //删除dcp_stock_channel
                                DelBean db = new DelBean("DCP_STOCK_CHANNEL");
                                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                                db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                                db.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(db));

                            }else {
                                if ("0".equals(dealType)) {
                                    ChannelStock(req, pluNo, organizationNo, ruleType, channelId, allocationValue);
                                } else {
                                    ChannelDealStock(req, pluNo, organizationNo, ruleType, channelId, allocationValue);
                                }
                            }

                            this.doExecuteDataToDB();     //一笔就提交,这样比较简单，便于内部产生异动单单据
                        }

                    }


                    //【ID1034458】【罗森尼娜3.3.0.5】库存分配逻辑bug  by jinzma 20230815
                    String channelSql = " select channelid from dcp_stock_channel"
                            + " where eid='"+eId+"' and organizationno='"+organizationNo+"' "
                            + " and pluno='"+pluNo+"' ";
                    List<Map<String,Object>> channels = this.doQueryData(channelSql,null);
                    if (!CollectionUtils.isEmpty(channels)){
                        for (Map<String,Object>channel:channels){
                            if (channelList.stream().noneMatch(a->a.getChannelId().equals(channel.get("CHANNELID").toString()))){
                                //删除dcp_stock_channel
                                DelBean db = new DelBean("DCP_STOCK_CHANNEL");
                                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                                db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                                db.addCondition("CHANNELID", new DataValue(channel.get("CHANNELID").toString(), Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(db));
                            }
                        }
                    }

                    this.doExecuteDataToDB();

                }

            }



            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_StockAllocationRuleUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_StockAllocationRuleUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_StockAllocationRuleUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_StockAllocationRuleUpdateReq req) throws Exception {
        boolean isFail = false;

        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getDealType())) {
                errMsg.append("dealType 不可为空值, ");
                isFail = true;
            }

            List<Plu> pluList = req.getRequest().getPluList();
            if (CollectionUtils.isEmpty(pluList)) {
                errMsg.append("pluList 不可为空值, ");
                isFail = true;
            }else {
                for (Plu plu:pluList){
                    if (Check.Null(plu.getPluNo())){
                        errMsg.append("pluNo 不可为空值, ");
                        isFail = true;
                    }
                    if (Check.Null(plu.getPluType())){
                        errMsg.append("pluType 不可为空值, ");
                        isFail = true;
                    }
                    List<Shop> shopList = plu.getShopList();
                    if (CollectionUtils.isEmpty(shopList)) {
                        errMsg.append("shopList 不可为空值, ");
                        isFail = true;
                    }else {
                        for (Shop shop:shopList){
                            if (Check.Null(shop.getOrganizationNo())){
                                errMsg.append("organizationNo 不可为空值, ");
                                isFail = true;
                            }
                            if (Check.Null(shop.getRuleType())){
                                errMsg.append("ruleType 不可为空值, ");
                                isFail = true;
                            }
                            List<Channel> channelList = shop.getChannelList();
                            if (CollectionUtils.isEmpty(channelList)) {
                                errMsg.append("channelList 不可为空值, ");
                                isFail = true;
                            }else {
                                for (Channel channel:channelList){
                                    if (Check.Null(channel.getChannelId())){
                                        errMsg.append("channelId 不可为空值, ");
                                        isFail = true;
                                    }
                                    if (Check.Null(channel.getAllocationValue())){
                                        errMsg.append("allocationValue 不可为空值, ");
                                        isFail = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_N_StockAllocationRuleUpdateReq> getRequestType() {
        return new TypeToken<DCP_N_StockAllocationRuleUpdateReq>(){};
    }

    @Override
    protected DCP_N_StockAllocationRuleUpdateRes getResponseType() {
        return new DCP_N_StockAllocationRuleUpdateRes();
    }

    //渠道库存不自动分配
    private void ChannelStock(DCP_N_StockAllocationRuleUpdateReq req,String pluNo,String organizationNo,String ruleType,String channelId,String allocationValue) throws Exception {
        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql = " select sunit,baseunit from dcp_goods where eid='"+eId+"' and pluno='"+pluNo+"' ";
        List<Map<String, Object>> getPlus = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getPlus)){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+pluNo+" 在dcp_goods表中不存在 ");
        }
        String sUnit = getPlus.get(0).get("SUNIT").toString();
        String baseUnit = getPlus.get(0).get("BASEUNIT").toString();

        //获取仓库
        sql = "select out_cost_warehouse from dcp_org a where a.eid='" + eId + "' and a.organizationno='" + organizationNo + "' ";
        List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getWarehouse)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "组织:" + organizationNo + " 在dcp_org表中不存在 ");
        }
        String warehouse = getWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
        if (Check.Null(warehouse)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "组织:" + organizationNo + " 对应的出货仓库OUT_COST_WAREHOUSE为空 ");
        }

        //新增 DCP_STOCK_ALLOCATION_RULE
        {
            String[] columns = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","SUNIT","CHANNELID","RULETYPE",
                    "ALLOCATIONVALUE","CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(organizationNo, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(" ", Types.VARCHAR),      //这个服务只处理普通商品或多规格商品,所以特征码都是空
                    new DataValue(sUnit, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(ruleType, Types.VARCHAR),
                    new DataValue(allocationValue, Types.VARCHAR),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE)
            };
            InsBean ib = new InsBean("DCP_STOCK_ALLOCATION_RULE", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }

        //判断 DCP_STOCK_CHANNEL是否有资料，没有才创建
        {
            sql = " select channelid from dcp_stock_channel "
                    + "where eid='" + eId + "' and channelid='" + channelId + "' and organizationno='" + organizationNo + "' and pluno='" + pluNo + "' ";
            List<Map<String, Object>> getStockChannel = this.doQueryData(sql, null);
            if (CollectionUtils.isEmpty(getStockChannel)) {
                //插入 DCP_STOCK_CHANNEL
                {
                    String[] columns = {"EID", "CHANNELID", "ORGANIZATIONNO", "PLUNO", "FEATURENO", "WAREHOUSE", "SUNIT", "ONLINEQTY",
                            "LOCKQTY", "BASEUNIT", "BONLINEQTY", "BLOCKQTY", "CREATEOPID", "CREATEOPNAME", "CREATETIME"};
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),
                            new DataValue(organizationNo, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(" ", Types.VARCHAR),              //这个服务只处理普通商品或多规格商品,所以特征码都是空
                            new DataValue(warehouse, Types.VARCHAR),
                            new DataValue(sUnit, Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),              //ONLINEQTY
                            new DataValue("0", Types.VARCHAR),              //LOCKQTY
                            new DataValue(baseUnit, Types.VARCHAR),                //baseUnit
                            new DataValue("0", Types.VARCHAR),              //BONLINEQTY
                            new DataValue("0", Types.VARCHAR),              //BLOCKQTY
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(sDate, Types.DATE)
                    };
                    InsBean ib = new InsBean("DCP_STOCK_CHANNEL", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }

                //原本是同一个渠道下所有商品一张单子，现在是不管渠道了，一个商品就一个单子
                //插入 DCP_STOCK_CHANNEL_BILLGOODS
                String billNo = GetBillNo(req);
                {
                    String[] columns = {"EID","BILLNO","BILLTYPE","CHANNELID","ITEM","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO",
                            "WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue("QDFH001", Types.VARCHAR),       //BILLTYPE ，单据类型，此处根据旧服务里面的SA要求，固定为QDFH001
                            new DataValue(channelId, Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue(organizationNo, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(sUnit, Types.VARCHAR),
                            new DataValue(" ", Types.VARCHAR),            //这个服务只处理普通商品或多规格商品,所以特征码都是空
                            new DataValue(warehouse, Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue(baseUnit, Types.VARCHAR),
                            new DataValue(0, Types.VARCHAR),
                    };
                    InsBean ib = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }


                //插入 DCP_STOCK_CHANNEL_BILL
                {
                    String[] columns = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
                            "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","DEALTYPE"};
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue("QDFH001", Types.VARCHAR),   //BILLTYPE ，单据类型，此处根据旧服务里面的SA要求，固定为QDFH001
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(sDate, Types.DATE),
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(sDate, Types.DATE),
                            new DataValue("1", Types.VARCHAR),           //dealType  0:新增   1：调整   2：删除  照搬之前旧服务
                    };
                    InsBean ib = new InsBean("DCP_STOCK_CHANNEL_BILL", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

        }

        //判断 DCP_STOCK是否有资料，没有才创建
        {
            sql = " select pluno from dcp_stock where eid='"+eId+"' and organizationno='"+organizationNo+"' and pluno='"+pluNo+"'  ";
            List<Map<String, Object>> getStock = this.doQueryData(sql, null);
            if (CollectionUtils.isEmpty(getStock)) {
                String[] columns = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","WAREHOUSE","BASEUNIT","QTY","LOCKQTY","ONLINEQTY",
                        "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(organizationNo, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(" ", Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),          // QTY
                        new DataValue("0", Types.VARCHAR),          // LOCKQTY
                        new DataValue("0", Types.VARCHAR),          // ONLINEQTY
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE)
                };
                InsBean ib = new InsBean("DCP_STOCK", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
            }

        }


    }

    //渠道库存自动分配
    private void ChannelDealStock(DCP_N_StockAllocationRuleUpdateReq req,String pluNo,String organizationNo,String ruleType,String channelId,String allocationValue) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql = " select sunit,baseunit from dcp_goods where eid='"+eId+"' and pluno='"+pluNo+"' ";
        List<Map<String, Object>> getPlus = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getPlus)){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+pluNo+" 在dcp_goods表中不存在 ");
        }
        String sUnit = getPlus.get(0).get("SUNIT").toString();
        String baseUnit = getPlus.get(0).get("BASEUNIT").toString();

        //获取单位转换率
        sql = " select unitratio from dcp_goods_unit where eid='"+eId+"' and pluno='"+pluNo+"' and ounit='"+sUnit+"' and unit='"+baseUnit+"'  ";
        List<Map<String, Object>> getUnitRatio = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getUnitRatio)){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+pluNo+" 在dcp_goods_unit表中找不到对应的单位转换率 ");
        }
        String unitRatio = getUnitRatio.get(0).get("UNITRATIO").toString();
        if (!PosPub.isNumericType(unitRatio)){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+pluNo+" 在dcp_goods_unit表中的单位转换率异常 ");
        }
        BigDecimal unitRatio_b = new BigDecimal(unitRatio);

        //获取仓库
        sql = "select out_cost_warehouse from dcp_org a where a.eid='" + eId + "' and a.organizationno='" + organizationNo + "' ";
        List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getWarehouse)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "组织:" + organizationNo + " 在dcp_org表中不存在 ");
        }
        String warehouse = getWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
        if (Check.Null(warehouse)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "组织:" + organizationNo + " 对应的出货仓库OUT_COST_WAREHOUSE为空 ");
        }

        //获取库存数
        BigDecimal stockQty_b = new BigDecimal("0");
        sql = " select sum(qty) as qty from dcp_stock where eid='"+eId+"' and organizationno='"+organizationNo+"' and pluno='"+pluNo+"' ";
        List<Map<String, Object>> getStock = this.doQueryData(sql, null);
        String qty = getStock.get(0).get("QTY").toString();
        if (PosPub.isNumericType(qty)){
            stockQty_b = new BigDecimal(qty);  //如果库存是负数，也别分配了，负数分配有什么意义，修正原服务的一个BUG，此处只有当正数才赋值
        }

        //获取单位小数位数
        int udLength ;
        sql = "select udlength from dcp_unit where eid='"+eId+"' and unit='"+sUnit+"' ";
        List<Map<String, Object>> getUdLength = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getUdLength)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "单位:" + sUnit + " 在dcp_unit表中不存在 ");
        }
        udLength = Integer.parseInt(getUdLength.get(0).get("UDLENGTH").toString());

        //新增 DCP_STOCK_ALLOCATION_RULE
        {
            String[] columns = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","SUNIT","CHANNELID","RULETYPE",
                    "ALLOCATIONVALUE","CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(organizationNo, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(" ", Types.VARCHAR),      //这个服务只处理普通商品或多规格商品,所以特征码都是空
                    new DataValue(sUnit, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(ruleType, Types.VARCHAR),
                    new DataValue(allocationValue, Types.VARCHAR),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE)
            };
            InsBean ib = new InsBean("DCP_STOCK_ALLOCATION_RULE", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }

        //获取原渠道表里面的预留数
        String onlineQty = "0";     //预留数
        String bonlineQty = "0";    //基准单位预留数
        String lockQty = "0";       //锁定数
        String blockQty = "0";      //基准单位锁定数

        sql =" select onlineqty,bonlineqty,lockqty,blockqty from dcp_stock_channel "
                + "where eid='"+eId+"' and channelid='"+channelId+"' and organizationno='"+organizationNo+"' and pluno='"+pluNo+"' ";
        List<Map<String, Object>> getStockChannel = this.doQueryData(sql, null);
        if (CollectionUtils.isNotEmpty(getStockChannel)) {
            onlineQty = getStockChannel.get(0).get("ONLINEQTY").toString();
            bonlineQty = getStockChannel.get(0).get("BONLINEQTY").toString();
            lockQty = getStockChannel.get(0).get("LOCKQTY").toString();
            blockQty = getStockChannel.get(0).get("BLOCKQTY").toString();
        }

        //库存表DCP_STOCK调整数计算
        BigDecimal stockOnlineQty_b = new BigDecimal("0");     //准备回写 dcp_stock调整数 销售单位
        BigDecimal stockBOnlineQty_b = new BigDecimal("0");    //准备回写 dcp_stock调整数 基准单位
        BigDecimal channelOnlineQty_b = new BigDecimal("0");   //准备回写 dcp_stock_channel 预留数 销售单位
        BigDecimal channelBOnlineQty_b = new BigDecimal("0");  //准备回写 dcp_stock_channel 预留数 基准单位

        {
            //ruleType 0按数量
            if(ruleType.equals("0")){
                channelOnlineQty_b = new BigDecimal(allocationValue);
                channelBOnlineQty_b = channelOnlineQty_b.multiply(unitRatio_b).setScale(udLength, RoundingMode.HALF_UP);
                stockOnlineQty_b = channelOnlineQty_b.subtract(new BigDecimal(onlineQty));    //渠道表原本预留了10个，这次调整成20个， 用20-10 得出差异数10个，更新DCP_STOCK增加10个
                stockBOnlineQty_b = channelBOnlineQty_b.subtract(new BigDecimal(bonlineQty)); //渠道表原本预留了10个，这次调整成20个， 用20-10 得出差异数10个，更新DCP_STOCK增加10个

            }
            //ruleType 1按比例
            if(ruleType.equals("1")){
                channelOnlineQty_b = stockQty_b.multiply(BigDecimal.valueOf(Double.parseDouble(allocationValue) * 0.01));
                channelBOnlineQty_b = channelOnlineQty_b.multiply(unitRatio_b).setScale(udLength, RoundingMode.HALF_UP);
                stockOnlineQty_b = channelOnlineQty_b.subtract(new BigDecimal(onlineQty));    //渠道表原本预留了10个，这次调整成20个， 用20-10 得出差异数10个，更新DCP_STOCK增加10个
                stockBOnlineQty_b = channelBOnlineQty_b.subtract(new BigDecimal(bonlineQty)); //渠道表原本预留了10个，这次调整成20个， 用20-10 得出差异数10个，更新DCP_STOCK增加10个
            }

        }


        //删除dcp_stock_channel
        {
            DelBean db = new DelBean("DCP_STOCK_CHANNEL");
            db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
            db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
            db.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db));
        }

        //插入 DCP_STOCK_CHANNEL
        {
            String[] columns = {"EID", "CHANNELID", "ORGANIZATIONNO", "PLUNO", "FEATURENO", "WAREHOUSE", "SUNIT", "ONLINEQTY",
                    "LOCKQTY", "BASEUNIT", "BONLINEQTY", "BLOCKQTY", "CREATEOPID", "CREATEOPNAME", "CREATETIME"};
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(organizationNo, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(" ", Types.VARCHAR),              //这个服务只处理普通商品或多规格商品,所以特征码都是空
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(sUnit, Types.VARCHAR),
                    new DataValue(channelOnlineQty_b.toPlainString(), Types.VARCHAR),              //ONLINEQTY
                    new DataValue(lockQty, Types.VARCHAR),                                         //LOCKQTY
                    new DataValue(baseUnit, Types.VARCHAR),                                        //baseUnit
                    new DataValue(channelBOnlineQty_b.toPlainString(), Types.VARCHAR),             //BONLINEQTY
                    new DataValue(blockQty, Types.VARCHAR),                                        //BLOCKQTY
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE)
            };
            InsBean ib = new InsBean("DCP_STOCK_CHANNEL", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }

        //原本是同一个渠道下所有商品一张单子，现在是不管渠道了，一个商品就一个单子
        String billNo = GetBillNo(req);

        //插入 DCP_STOCK_CHANNEL_BILLGOODS
        {
            String[] columns = {"EID","BILLNO","BILLTYPE","CHANNELID","ITEM","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO",
                    "WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(billNo, Types.VARCHAR),
                    new DataValue("QDFH001", Types.VARCHAR),       //BILLTYPE ，单据类型，此处根据旧服务里面的SA要求，固定为QDFH001
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue(organizationNo, Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(sUnit, Types.VARCHAR),
                    new DataValue(" ", Types.VARCHAR),            //这个服务只处理普通商品或多规格商品,所以特征码都是空
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),                                   //DIRECTION
                    new DataValue(stockOnlineQty_b.toPlainString(), Types.VARCHAR),            //SQTY
                    new DataValue(baseUnit, Types.VARCHAR),
                    new DataValue(stockBOnlineQty_b.toPlainString(), Types.VARCHAR),           //BQTY
            };
            InsBean ib = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }

        //插入 DCP_STOCK_CHANNEL_BILL
        {
            String[] columns = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
                    "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","DEALTYPE"};
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(billNo, Types.VARCHAR),
                    new DataValue("QDFH001", Types.VARCHAR),   //BILLTYPE ，单据类型，此处根据旧服务里面的SA要求，固定为QDFH001
                    new DataValue(stockOnlineQty_b.toPlainString(), Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
                    new DataValue("1", Types.VARCHAR),           //dealType  0:新增   1：调整   2：删除  照搬之前旧服务
            };
            InsBean ib = new InsBean("DCP_STOCK_CHANNEL_BILL", columns);
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));
        }

        //判断 DCP_STOCK是否有资料，没有创建,存在修改
        {
            if (Check.Null(qty)) {
                String[] columns = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","WAREHOUSE","BASEUNIT","QTY","LOCKQTY","ONLINEQTY",
                        "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(organizationNo, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(" ", Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),                             // QTY
                        new DataValue("0", Types.VARCHAR),                             // LOCKQTY
                        new DataValue(channelBOnlineQty_b.toPlainString(), Types.VARCHAR),   // ONLINEQTY  DCP_STOCK不存在时，预留数=本次调整的数量，不是差异数
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE)
                };
                InsBean ib = new InsBean("DCP_STOCK", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
            }else {

                UptBean ub = new UptBean("DCP_STOCK");
                ub.addUpdateValue("ONLINEQTY", new DataValue(stockOnlineQty_b.toPlainString(), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));  // ONLINEQTY  DCP_STOCK存在时，预留数=差异数
                ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));
                ub.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));

                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                ub.addCondition("FEATURENO", new DataValue(" ", Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));

            }

        }



    }



    private String GetBillNo(DCP_N_StockAllocationRuleUpdateReq req) throws Exception{

        String eId = req.geteId();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String sql = " select max(billno) as billno from dcp_stock_channel_bill where eid = '"+eId+"' and billno like 'QDFH"+sDate+"%%' " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        String billNo = getQData.get(0).get("BILLNO").toString();
        if (Check.Null(billNo)){
            billNo = "QDFH" + sDate + "00001";
        }else {
            billNo = billNo.substring(4);
            long i = Long.parseLong(billNo) + 1;
            billNo = "QDFH" + i;
        }
        return billNo;
    }



}
