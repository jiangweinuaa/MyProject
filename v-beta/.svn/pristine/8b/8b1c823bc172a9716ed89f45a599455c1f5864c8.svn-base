package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CdsOrderStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderMealOut_OpenReq;
import com.dsc.spos.json.cust.res.DCP_CdsOrderStatusUpdate_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderMealOut_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: CDS叫号/取餐
 * @author: wangzyc
 * @create: 2021-10-11
 */
public class DCP_CdsOrderStatusUpdate_Open extends SPosAdvanceService<DCP_CdsOrderStatusUpdate_OpenReq, DCP_CdsOrderStatusUpdate_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_CdsOrderStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsOrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空");
            isFail = true;
        }

//        if (Check.Null(request.getHandleType())) {
//            errMsg.append("操作类型不能为空");
//            isFail = true;
//        }

        if (Check.Null(request.getBusinessType())) {
            errMsg.append("点单类型不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsOrderStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_CdsOrderStatusUpdate_OpenReq>() {
        };
    }

    @Override
    protected DCP_CdsOrderStatusUpdate_OpenRes getResponseType() {
        return new DCP_CdsOrderStatusUpdate_OpenRes();
    }


    @Override
    protected void processDUID(DCP_CdsOrderStatusUpdate_OpenReq req, DCP_CdsOrderStatusUpdate_OpenRes res) throws Exception {
        /**
         * 叫号的业务逻辑（handleType==0）
         * 1、触发取餐屏：变更DCP_PRODUCT_SALE.productStatus==2，刷新callTime；
         * 2、取餐提醒1：根据单据中的channelId，关联CRM_CHANNEL.APPNO，若APPNO==SCAN或WAIMAI，且入参businessType==0时，则走下面的逻辑：
         * 关联CRM_CHANNEL.appType，判断若appType==2或4，则异步调用CRM_MiniSendMsg_Open，调用成功回写DCP_PRODUCT_SALE.isSendMsg==Y；
         * 3、取餐提醒2：判断若isSendMsg==Y或入参isOrder==Y，则不再执行调用CRM_MiniSendMsg_Open；
         *
         * 取餐逻辑
         * 1、整单取餐：handleType==1.取餐，判断billNo是否有值，若有值&goodsList为空则直接执行变更DCP_PRODUCT_SALE.productStatus==3以及对应明细goodsStatus==3；
         * 2、单品取餐：若billNo为空&goodsList有值，则变更DCP_PRODUCT_DETAIL.goodsStatus==3，若当前订单goodsStatus全部为3，则同步变更单头productStatus==3；
         * 3、全部取餐：handleType==2&billNo和goodsList都为空，则将对应门店下所有productStatus!=3的数据变更为3；
         * 4、判断若isSendMsg==Y.已发送，则不执行调用CRM_MiniSendMsg_Open，否则按照上述规则调用，调用成功回写DCP_PRODUCT_SALE.isSendMsg==Y；
         *
         * 发送消息类型
         * appType == 2 为 #025：支付宝取餐提醒 4 为 #024：微信取餐提醒
         *
         * 历史单据重新叫号
         * handleType==3，判断billno，执行变更productStatus==2待取餐，以及对应明细goodsStatus==2待取餐
         *
         * handleType，如入参有值，根据入参执行。如为空，则判断改单CALLTIME是否为空，为空执行叫号逻辑；
         * 非空，判断商品状态，若已取餐，不执行操作，直接返回成功；若未取餐，执行取餐逻辑。
         */
        DCP_CdsOrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sds = new SimpleDateFormat("yyyyMMddHHmmss");
        String eId = req.geteId();
        String handleType = request.getHandleType();
        String machineId = request.getMachineId();
        String businessType = request.getBusinessType();
        String shopId = request.getShopId();
        String billNo = request.getBillNo();
        String isOrder = request.getIsOrder();
        String appid = req.getApiUserCode();
        res.setDatas(new ArrayList<>());
        List<DCP_CdsOrderStatusUpdate_OpenReq.level2Elm> goodsList = request.getGoodsList();
        //this.orderMealOut(req,billNo);

        try
        {
            if(Check.Null(handleType))
            {
                // 1.判断改单CALLTIME是否为空，为空执行叫号逻辑；
                String sql = "SELECT a.BILLNO, a.CALLTIME, b.OITEM, b.PLUNO, b.PLUBARCODE , b.goodsstatus " +
                        " FROM DCP_PRODUCT_SALE a  " +
                        " LEFT JOIN DCP_PRODUCT_DETAIL b ON a.eid = b.eid AND a.SHOPID = b.SHOPID AND a.BILLNO = b.BILLNO AND a.BILLTYPE = b.BILLTYPE " +
                        " WHERE a.eid = '"+eId+"' AND a.BILLNO = '"+billNo+"' AND a.shopid = '"+shopId+"'";
                List<Map<String, Object>> datas = this.doQueryData(sql, new String[]{req.geteId(), req.getRequest().getShopId(), req.getRequest().getBillNo()});
                if (!CollectionUtils.isEmpty(datas)) {
                    String calltime = datas.get(0).get("CALLTIME").toString();
                    if(Check.Null(calltime)){
                        // 叫号
                        handleType = "0";
                    }else{
                        if(CollectionUtils.isEmpty(goodsList)){
                            goodsList = new ArrayList<>();
                        }
                        for (Map<String, Object> data : datas) {
                            String goodsstatus = data.get("GOODSSTATUS").toString();
                            String oitem = data.get("OITEM").toString();
                            String pluno = data.get("PLUNO").toString();
                            String plubarcode = data.get("PLUBARCODE").toString();
                            if(!"3".equals(goodsstatus)){
                                DCP_CdsOrderStatusUpdate_OpenReq.level2Elm level2Elm = req.new level2Elm();
                                level2Elm.setBillNo(billNo);
                                level2Elm.setPluNo(pluno);
                                level2Elm.setOItem(oitem);
                                level2Elm.setPluBarCode(plubarcode);
                                goodsList.add(level2Elm);
                            }
                        }
                        if(!CollectionUtils.isEmpty(goodsList)){
                            // 取餐
                            handleType = "1";
                            billNo = "";
                        }


                    }
                }
            }


            if ("0".equals(handleType)||"3".equals(handleType))
            {
                // 叫号
                String sql = checkBillNo(req);
                List<Map<String, Object>> data = this.doQueryData(sql, new String[]{req.geteId(), req.getRequest().getShopId(), req.getRequest().getBillNo()});
                if (!CollectionUtils.isEmpty(data)) {

                    String callTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    UptBean ub = new UptBean("DCP_PRODUCT_SALE");
                    ub.addUpdateValue("PRODUCTSTATUS", new DataValue("2", Types.VARCHAR));
                    ub.addUpdateValue("CALLTIME", new DataValue(callTime, Types.VARCHAR));
                    ub.addUpdateValue("MAKETIME", new DataValue(callTime, Types.VARCHAR));
                    // condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));

                    UptBean ub2 = new UptBean("DCP_PRODUCT_DETAIL");
                    ub2.addUpdateValue("GOODSSTATUS", new DataValue("2", Types.VARCHAR));
                    ub2.addUpdateValue("MAKETIME", new DataValue(callTime, Types.VARCHAR));
                    // condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));

                    Map<String, Object> map = data.get(0);

                    if("0".equals(handleType)){
                        // 获取商品详情
                        String title = getTitle(req, goodsList);

                        // 发送消息通知
                        sendMsg(req,map,eId,shopId,businessType,isOrder,title,sdf,sds,billNo,appid);
                    }

                    // 写入缓存
                    setCdsCallTaskRedis(map,eId,shopId,callTime);
                } else {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单号不存在，请检查后重新操作！");
                    return;
                }
            }
            else if ("1".equals(handleType))
            {
                // 取餐/传菜
                // 1、整单取餐：handleType==1.取餐，判断billNo是否有值，若有值&goodsList为空则直接执行变更DCP_PRODUCT_SALE.productStatus==3以及对应明细goodsStatus==3；
                // 2、单品取餐：若billNo为空&goodsList有值，则变更DCP_PRODUCT_DETAIL.goodsStatus==3，若当前订单goodsStatus全部为3，则同步变更单头productStatus==3；
                if (!Check.Null(billNo) && CollectionUtils.isEmpty(goodsList))
                {
                    String PICKUPTIME = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    // 整单取餐
                    UptBean ub = new UptBean("DCP_PRODUCT_SALE");
                    ub.addUpdateValue("PRODUCTSTATUS", new DataValue("3", Types.VARCHAR));
                    ub.addUpdateValue("PICKUPTIME", new DataValue(PICKUPTIME, Types.VARCHAR));
                    // condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));

                    UptBean ub2 = new UptBean("DCP_PRODUCT_DETAIL");
                    ub2.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                    ub2.addUpdateValue("COMPLETETIME", new DataValue(PICKUPTIME, Types.VARCHAR));
                    // condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));
                    String sql = checkBillNo(req);
                    List<Map<String, Object>> data = this.doQueryData(sql, new String[]{req.geteId(), req.getRequest().getShopId(), req.getRequest().getBillNo()});
                    if (!CollectionUtils.isEmpty(data)) {
                        Map<String, Object> map = data.get(0);
                        String billtype = map.get("BILLTYPE").toString();

                        // 获取商品详情
                        String title = getTitle(req, goodsList);

                        // 发送消息通知
                        sendMsg(req,map,eId,shopId,businessType,isOrder,title,sdf,sds,billNo,appid);

                        // 整单取餐成功后 外卖类型订单 需返回订单信息 打印小票
                        if("Y".equals(isOrder)&&"ORDER".equals(billtype)){
                            String billDetail_sql = getBillDetail();
                            List<Map<String, Object>> getBillDetail = this.doQueryData(billDetail_sql, new String[]{eId, shopId, billNo});
                            if(!CollectionUtils.isEmpty(getBillDetail)){
                                // 过滤
                                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                                condition.put("BILLNO", true);
                                // 调用过滤函数
                                List<Map<String, Object>> getHeader = MapDistinct.getMap(getBillDetail, condition);
                                if(!CollectionUtils.isEmpty(getHeader)){
                                    for (Map<String, Object> oneData : getHeader) {
                                        DCP_CdsOrderStatusUpdate_OpenRes.level1Elm lv1 = res.new level1Elm();
                                        String channelname = oneData.get("CHANNELNAME").toString();
                                        String apptype = oneData.get("APPTYPE").toString();
                                        String appName = "";
                                        if ("POS".equals(apptype) || "POSANDROID".equals(apptype)) {
                                            appName = "POS";
                                        } else if ("SCAN".equals(apptype)) {
                                            appName = "扫码点单";
                                        } else if ("WAIMAI".equals(apptype)) {
                                            appName = "自营外卖";
                                        } else if ("ELEME".equals(apptype)) {
                                            appName = "饿了么";
                                        } else if ("MEITUAN".equals(apptype)) {
                                            appName = "美团";
                                        }

                                        String billno = oneData.get("BILLNO").toString();
                                        String trNo = oneData.get("TRNO").toString();
                                        String memo = oneData.get("MEMO").toString();
                                        String tot_qty = oneData.get("TOT_QTY").toString();
                                        String tot_amt = oneData.get("TOT_AMT").toString();
                                        String tot_disc = oneData.get("TOT_DISC").toString();
                                        String getman = oneData.get("GETMAN").toString();
                                        String getmantel = oneData.get("GETMANTEL").toString();
                                        String address = oneData.get("ADDRESS").toString();
                                        String shipendtime = oneData.get("SHIPENDTIME").toString();
                                        Long min = null;
                                        if(!Check.Null(shipendtime)){
                                            min  = getMin(shipendtime);
                                            shipendtime = sdf.format(sds.parse(shipendtime));
                                        }
                                        String maketime = oneData.get("MAKETIME").toString();
                                        if(!Check.Null(maketime)){
                                            maketime = sdf.format(sds.parse(maketime));
                                        }

                                        lv1.setOChannelName(channelname);
                                        lv1.setBillNo(billno);
                                        lv1.setTrNo(trNo);
                                        lv1.setMemo(memo);
                                        lv1.setAppName(appName);
                                        lv1.setTotQty(tot_qty);
                                        lv1.setTotAmt(tot_amt);
                                        lv1.setTotDisc(tot_disc);

                                        DCP_CdsOrderStatusUpdate_OpenRes.level2Elm lv2 = res.new level2Elm();
                                        lv2.setGetMan(getman);
                                        lv2.setGetManTel(getmantel);
                                        lv2.setAddress(address);
                                        lv1.setShipInfo(lv2);

                                        DCP_CdsOrderStatusUpdate_OpenRes.level3Elm lv3 = res.new level3Elm();
                                        lv3.setOrderTime(shipendtime);
                                        lv3.setMadeTime(maketime);
                                        lv3.setSomeTime(min+"");
                                        lv1.setTimeInfo(lv3);

                                        lv1.setGoodsList(new ArrayList<>());
                                        for (Map<String, Object> twoData : getBillDetail) {
                                            String billno2 = twoData.get("BILLNO").toString();
                                            // 过滤此单号的商品详情
                                            if(!billno2.equals(billno)){
                                                continue;
                                            }

                                            String oitem = twoData.get("OITEM").toString();
                                            String pluno = twoData.get("PLUNO").toString();
                                            String pluname = twoData.get("PLUNAME").toString();
                                            String plubarcode = twoData.get("PLUBARCODE").toString();
                                            String price = twoData.get("PRICE").toString();
                                            String qty = twoData.get("QTY").toString();
                                            String disc = twoData.get("DISC").toString();
                                            String amt = twoData.get("AMT").toString();
                                            String unitid = twoData.get("UNITID").toString();
                                            String unitname = twoData.get("UNITNAME").toString();
                                            String flavorstuffdetail = twoData.get("FLAVORSTUFFDETAIL").toString();
                                            String ispackage = twoData.get("ISPACKAGE").toString();
                                            String pgoodsdetail = twoData.get("PGOODSDETAIL").toString();
                                            String specattrdetail = twoData.get("SPECATTRDETAIL").toString();
                                            String assortedtime = twoData.get("ASSORTEDTIME").toString();
                                            if(!Check.Null(assortedtime)){
                                                assortedtime = sdf.format(sds.parse(assortedtime));
                                            }
                                            String plunomaketime = twoData.get("PLUNOMAKETIME").toString();
                                            if(!Check.Null(plunomaketime)){
                                                plunomaketime = sdf.format(sds.parse(plunomaketime));
                                            }
                                            String completetime = twoData.get("COMPLETETIME").toString();
                                            if(!Check.Null(completetime)){
                                                completetime = sdf.format(sds.parse(completetime));
                                            }

                                            DCP_CdsOrderStatusUpdate_OpenRes.level4Elm lv4 = res.new level4Elm();
                                            lv4.setOItem(oitem);
                                            lv4.setPluNo(pluno);
                                            lv4.setPluName(pluname);
                                            lv4.setPluBarcode(plubarcode);
                                            lv4.setPrice(price);
                                            lv4.setQty(qty);
                                            lv4.setDisc(disc);
                                            lv4.setAmt(amt);
                                            lv4.setSpecAttrDetail(specattrdetail);
                                            lv4.setUnitId(unitid);
                                            lv4.setUnitName(unitname);
                                            lv4.setFlavorstuffDetail(flavorstuffdetail);
                                            lv4.setIsPackage(ispackage);
                                            lv4.setPGoodsDetail(pgoodsdetail);

                                            DCP_CdsOrderStatusUpdate_OpenRes.level5Elm lv5 = res.new level5Elm();
                                            lv5.setOrderTime(shipendtime);
                                            lv5.setAssortedTime(assortedtime);
                                            lv5.setMadeTime(plunomaketime);
                                            lv5.setPickupTime(completetime);
                                            lv4.setTimeInfo(lv5);

                                            lv1.getGoodsList().add(lv4);
                                        }
                                        res.getDatas().add(lv1);

                                        /*if (orderLoadDocType.ELEME.equals(apptype)||orderLoadDocType.MEITUAN.equals(apptype))
                                        {
                                            this.orderMealOut(req,billNo);
                                        }*/
                                        this.orderMealOut(req,billNo);
                                    }
                                }
                            }
                        }
                    } else {
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        res.setServiceDescription("当前单号不存在，请检查后重新操作！");
                        return;
                    }


                }
                else if (!Check.Null(billNo) && !CollectionUtils.isEmpty(goodsList))
                {
                    // 单品取餐
                    for (DCP_CdsOrderStatusUpdate_OpenReq.level2Elm lv2 : goodsList)
                    {
                        String sql = checkBillNo(req);
                        List<Map<String, Object>> data = this.doQueryData(sql, new String[]{req.geteId(), req.getRequest().getShopId(), lv2.getBillNo()});
                        if(!CollectionUtils.isEmpty(data))
                        {
                            billNo = lv2.getBillNo();
                            UptBean ub = new UptBean("DCP_PRODUCT_DETAIL");
                            ub.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                            ub.addUpdateValue("COMPLETETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                            // condition
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                            ub.addCondition("OITEM", new DataValue(lv2.getOItem(), Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub));

                            //如果有加料商品也一起取餐
                            ub = new UptBean("DCP_PRODUCT_DETAIL");
                            ub.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                            ub.addUpdateValue("COMPLETETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                            // condition
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                            ub.addCondition("ISSTUFF", new DataValue("Y", Types.VARCHAR));
                            ub.addCondition("DETAILITEM", new DataValue(lv2.getOItem(), Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub));


                            this.doExecuteDataToDB();


                            // 查询下单号下所有商品是否全部取餐
                            String billDetali_sql = getBillDetail(req,lv2.getOItem());
                            List<Map<String, Object>> bliiDetail = this.doQueryData(billDetali_sql, null);
                            if(CollectionUtils.isEmpty(bliiDetail)){
                                Map<String, Object> map = data.get(0);
                                // 获取商品详情
                                String title = getTitle(req, goodsList);
                                // 发送消息通知
                                sendMsg(req,map,eId,shopId,businessType,isOrder,title,sdf,sds,billNo,appid);

                                UptBean ub2 = new UptBean("DCP_PRODUCT_SALE");
                                ub2.addUpdateValue("PRODUCTSTATUS", new DataValue("3", Types.VARCHAR));
                                ub2.addUpdateValue("PICKUPTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                // condition
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub2));


                                String billtype = map.get("BILLTYPE").toString();
                                // 整单取餐成功后 外卖类型订单 需返回订单信息 打印小票
                                if("Y".equals(isOrder)&&"ORDER".equals(billtype)){
                                    String billDetail_sql = getBillDetail();
                                    List<Map<String, Object>> getBillDetail = this.doQueryData(billDetail_sql, new String[]{eId, shopId, billNo});
                                    if(!CollectionUtils.isEmpty(getBillDetail)){
                                        // 过滤
                                        Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                                        condition.put("BILLNO", true);
                                        // 调用过滤函数
                                        List<Map<String, Object>> getHeader = MapDistinct.getMap(getBillDetail, condition);
                                        if(!CollectionUtils.isEmpty(getHeader)){
                                            for (Map<String, Object> oneData : getHeader) {
                                                DCP_CdsOrderStatusUpdate_OpenRes.level1Elm lv1 = res.new level1Elm();
                                                String channelname = oneData.get("CHANNELNAME").toString();
                                                String apptype = oneData.get("APPTYPE").toString();
                                                String appName = "";
                                                if ("POS".equals(apptype) || "POSANDROID".equals(apptype)) {
                                                    appName = "POS";
                                                } else if ("SCAN".equals(apptype)) {
                                                    appName = "扫码点单";
                                                } else if ("WAIMAI".equals(apptype)) {
                                                    appName = "自营外卖";
                                                } else if ("ELEME".equals(apptype)) {
                                                    appName = "饿了么";
                                                } else if ("MEITUAN".equals(apptype)) {
                                                    appName = "美团";
                                                }

                                                String billno = oneData.get("BILLNO").toString();
                                                String trNo = oneData.get("TRNO").toString();
                                                String memo = oneData.get("MEMO").toString();
                                                String tot_qty = oneData.get("TOT_QTY").toString();
                                                String tot_amt = oneData.get("TOT_AMT").toString();
                                                String tot_disc = oneData.get("TOT_DISC").toString();
                                                String getman = oneData.get("GETMAN").toString();
                                                String getmantel = oneData.get("GETMANTEL").toString();
                                                String address = oneData.get("ADDRESS").toString();
                                                String shipendtime = oneData.get("SHIPENDTIME").toString();
                                                Long min = null;
                                                if(!Check.Null(shipendtime)){
                                                    min  = getMin(shipendtime);
                                                    shipendtime = sdf.format(sds.parse(shipendtime));
                                                }
                                                String maketime = oneData.get("MAKETIME").toString();
                                                if(!Check.Null(maketime)){
                                                    maketime = sdf.format(sds.parse(maketime));
                                                }

                                                lv1.setOChannelName(channelname);
                                                lv1.setBillNo(billno);
                                                lv1.setTrNo(trNo);
                                                lv1.setMemo(memo);
                                                lv1.setAppName(appName);
                                                lv1.setTotQty(tot_qty);
                                                lv1.setTotAmt(tot_amt);
                                                lv1.setTotDisc(tot_disc);

                                                DCP_CdsOrderStatusUpdate_OpenRes.level2Elm level2Elm = res.new level2Elm();
                                                level2Elm.setGetMan(getman);
                                                level2Elm.setGetManTel(getmantel);
                                                level2Elm.setAddress(address);
                                                lv1.setShipInfo(level2Elm);

                                                DCP_CdsOrderStatusUpdate_OpenRes.level3Elm lv3 = res.new level3Elm();
                                                lv3.setOrderTime(shipendtime);
                                                lv3.setMadeTime(maketime);
                                                lv3.setSomeTime(min+"");
                                                lv1.setTimeInfo(lv3);

                                                lv1.setGoodsList(new ArrayList<>());
                                                for (Map<String, Object> twoData : getBillDetail) {
                                                    String billno2 = twoData.get("BILLNO").toString();
                                                    // 过滤此单号的商品详情
                                                    if(!billno2.equals(billno)){
                                                        continue;
                                                    }

                                                    String oitem = twoData.get("OITEM").toString();
                                                    String pluno = twoData.get("PLUNO").toString();
                                                    String pluname = twoData.get("PLUNAME").toString();
                                                    String plubarcode = twoData.get("PLUBARCODE").toString();
                                                    String price = twoData.get("PRICE").toString();
                                                    String qty = twoData.get("QTY").toString();
                                                    String disc = twoData.get("DISC").toString();
                                                    String amt = twoData.get("AMT").toString();
                                                    String unitid = twoData.get("UNITID").toString();
                                                    String unitname = twoData.get("UNITNAME").toString();
                                                    String flavorstuffdetail = twoData.get("FLAVORSTUFFDETAIL").toString();
                                                    String ispackage = twoData.get("ISPACKAGE").toString();
                                                    String pgoodsdetail = twoData.get("PGOODSDETAIL").toString();
                                                    String specattrdetail = twoData.get("SPECATTRDETAIL").toString();
                                                    String assortedtime = twoData.get("ASSORTEDTIME").toString();
                                                    if(!Check.Null(assortedtime)){
                                                        assortedtime = sdf.format(sds.parse(assortedtime));
                                                    }
                                                    String plunomaketime = twoData.get("PLUNOMAKETIME").toString();
                                                    if(!Check.Null(plunomaketime)){
                                                        plunomaketime = sdf.format(sds.parse(plunomaketime));
                                                    }
                                                    String completetime = twoData.get("COMPLETETIME").toString();
                                                    if(!Check.Null(completetime)){
                                                        completetime = sdf.format(sds.parse(completetime));
                                                    }

                                                    DCP_CdsOrderStatusUpdate_OpenRes.level4Elm lv4 = res.new level4Elm();
                                                    lv4.setOItem(oitem);
                                                    lv4.setPluNo(pluno);
                                                    lv4.setPluName(pluname);
                                                    lv4.setPluBarcode(plubarcode);
                                                    lv4.setPrice(price);
                                                    lv4.setQty(qty);
                                                    lv4.setDisc(disc);
                                                    lv4.setAmt(amt);
                                                    lv4.setSpecAttrDetail(specattrdetail);
                                                    lv4.setUnitId(unitid);
                                                    lv4.setUnitName(unitname);
                                                    lv4.setFlavorstuffDetail(flavorstuffdetail);
                                                    lv4.setIsPackage(ispackage);
                                                    lv4.setPGoodsDetail(pgoodsdetail);

                                                    DCP_CdsOrderStatusUpdate_OpenRes.level5Elm lv5 = res.new level5Elm();
                                                    lv5.setOrderTime(shipendtime);
                                                    lv5.setAssortedTime(assortedtime);
                                                    lv5.setMadeTime(plunomaketime);
                                                    lv5.setPickupTime(completetime);
                                                    lv4.setTimeInfo(lv5);

                                                    lv1.getGoodsList().add(lv4);
                                                }
                                                res.getDatas().add(lv1);
                                            }
                                        }
                                    }
                                }
                            }


                        }else {
                            res.setSuccess(false);
                            res.setServiceStatus("200");
                            res.setServiceDescription("当前单号不存在，请检查后重新操作！");
                            return;
                        }
                    }

                }
            } else if ("2".equals(handleType)) {
                // 调用 CRM_MiniSendMsg_Open 调用成功回写DCP_PRODUCT_SALE.isSendMsg==Y；
                String memberUrl = PosPub.getCRM_INNER_URL(eId);
                if (memberUrl.trim().equals("")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                }
                // 全部取餐
                // 先查询出门店下所有的未取菜品详情
                String bliiDetails_sql = getBillDetails(req);
                List<Map<String, Object>> billDetails = this.doQueryData(bliiDetails_sql, null);

                // 过滤
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                condition.put("BILLNO", true);
                // 调用过滤函数
                List<Map<String, Object>> getHeader = MapDistinct.getMap(billDetails, condition);

                if(!CollectionUtils.isEmpty(getHeader)){
                    for (Map<String, Object> map : getHeader) {
                        String billNo1 = map.get("BILLNO").toString();
                        String billtype = map.get("BILLTYPE").toString();
                        List<String> titles = new ArrayList<>();
                        for (Map<String, Object> billDetail : billDetails) {
                            String billNo2 =  billDetail.get("BILLNO").toString();
                            String pluname =  billDetail.get("PLUNAME").toString();
                            // 过滤不属于此单号的商品
                            if(!billNo1.equals(billNo2)){
                                continue;
                            }
                            titles.add(pluname);
                        }

                        UptBean ub = new UptBean("DCP_PRODUCT_SALE");
                        ub.addUpdateValue("PRODUCTSTATUS", new DataValue("3", Types.VARCHAR));
                        // condition
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("BILLNO", new DataValue(billNo1, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));

                        UptBean ub2 = new UptBean("DCP_PRODUCT_DETAIL");
                        ub2.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                        // condition
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("BILLNO", new DataValue(billNo1, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));

                        String title = PosPub.getArrayStrSQLIn(titles.toArray(new String[titles.size()]));
                        // 发送消息通知
                        sendMsg(req,map,eId,shopId,businessType,isOrder,title,sdf,sds,billNo1,appid);

                        if("ORDER".equals(billtype)){
                            String billDetail_sql = getBillDetail();
                            List<Map<String, Object>> getBillDetail = this.doQueryData(billDetail_sql, new String[]{eId, shopId, billNo});
                            if(!CollectionUtils.isEmpty(getBillDetail)){
                                // 过滤
                                Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
                                condition2.put("BILLNO", true);
                                // 调用过滤函数
                                List<Map<String, Object>> getHeader2 = MapDistinct.getMap(getBillDetail, condition2);
                                condition2.clear();
                                if(!CollectionUtils.isEmpty(getHeader2)){
                                    for (Map<String, Object> oneData : getHeader2) {
                                        DCP_CdsOrderStatusUpdate_OpenRes.level1Elm lv1 = res.new level1Elm();
                                        String channelname = oneData.get("CHANNELNAME").toString();
                                        String apptype = oneData.get("APPTYPE").toString();
                                        String appName = "";
                                        if ("POS".equals(apptype) || "POSANDROID".equals(apptype)) {
                                            appName = "POS";
                                        } else if ("SCAN".equals(apptype)) {
                                            appName = "扫码点单";
                                        } else if ("WAIMAI".equals(apptype)) {
                                            appName = "自营外卖";
                                        } else if ("ELEME".equals(apptype)) {
                                            appName = "饿了么";
                                        } else if ("MEITUAN".equals(apptype)) {
                                            appName = "美团";
                                        }

                                        String billno = oneData.get("BILLNO").toString();
                                        String trNo = oneData.get("TRNO").toString();
                                        String memo = oneData.get("MEMO").toString();
                                        String tot_qty = oneData.get("TOT_QTY").toString();
                                        String tot_amt = oneData.get("TOT_AMT").toString();
                                        String tot_disc = oneData.get("TOT_DISC").toString();
                                        String getman = oneData.get("GETMAN").toString();
                                        String getmantel = oneData.get("GETMANTEL").toString();
                                        String address = oneData.get("ADDRESS").toString();
                                        String shipendtime = oneData.get("SHIPENDTIME").toString();
                                        Long min = null;
                                        if(!Check.Null(shipendtime)){
                                            min  = getMin(shipendtime);
                                            shipendtime = sdf.format(sds.parse(shipendtime));
                                        }
                                        String maketime = oneData.get("MAKETIME").toString();
                                        if(!Check.Null(maketime)){
                                            maketime = sdf.format(sds.parse(maketime));
                                        }

                                        lv1.setOChannelName(channelname);
                                        lv1.setBillNo(billno);
                                        lv1.setTrNo(trNo);
                                        lv1.setMemo(memo);
                                        lv1.setAppName(appName);
                                        lv1.setTotQty(tot_qty);
                                        lv1.setTotAmt(tot_amt);
                                        lv1.setTotDisc(tot_disc);

                                        DCP_CdsOrderStatusUpdate_OpenRes.level2Elm lv2 = res.new level2Elm();
                                        lv2.setGetMan(getman);
                                        lv2.setGetManTel(getmantel);
                                        lv2.setAddress(address);
                                        lv1.setShipInfo(lv2);

                                        DCP_CdsOrderStatusUpdate_OpenRes.level3Elm lv3 = res.new level3Elm();
                                        lv3.setOrderTime(shipendtime);
                                        lv3.setMadeTime(maketime);
                                        lv3.setSomeTime(min+"");
                                        lv1.setTimeInfo(lv3);

                                        lv1.setGoodsList(new ArrayList<>());
                                        for (Map<String, Object> twoData : getBillDetail) {
                                            String billno2 = twoData.get("BILLNO").toString();
                                            // 过滤此单号的商品详情
                                            if(!billno2.equals(billno)){
                                                continue;
                                            }

                                            String oitem = twoData.get("OITEM").toString();
                                            String pluno = twoData.get("PLUNO").toString();
                                            String pluname = twoData.get("PLUNAME").toString();
                                            String plubarcode = twoData.get("PLUBARCODE").toString();
                                            String price = twoData.get("PRICE").toString();
                                            String qty = twoData.get("QTY").toString();
                                            String disc = twoData.get("DISC").toString();
                                            String amt = twoData.get("AMT").toString();
                                            String unitid = twoData.get("UNITID").toString();
                                            String unitname = twoData.get("UNITNAME").toString();
                                            String flavorstuffdetail = twoData.get("FLAVORSTUFFDETAIL").toString();
                                            String ispackage = twoData.get("ISPACKAGE").toString();
                                            String pgoodsdetail = twoData.get("PGOODSDETAIL").toString();
                                            String specattrdetail = twoData.get("SPECATTRDETAIL").toString();
                                            String assortedtime = twoData.get("ASSORTEDTIME").toString();
                                            if(!Check.Null(assortedtime)){
                                                assortedtime = sdf.format(sds.parse(assortedtime));
                                            }
                                            String plunomaketime = twoData.get("PLUNOMAKETIME").toString();
                                            if(!Check.Null(plunomaketime)){
                                                plunomaketime = sdf.format(sds.parse(plunomaketime));
                                            }
                                            String completetime = twoData.get("COMPLETETIME").toString();
                                            if(!Check.Null(completetime)){
                                                completetime = sdf.format(sds.parse(completetime));
                                            }

                                            DCP_CdsOrderStatusUpdate_OpenRes.level4Elm lv4 = res.new level4Elm();
                                            lv4.setOItem(oitem);
                                            lv4.setPluNo(pluno);
                                            lv4.setPluName(pluname);
                                            lv4.setPluBarcode(plubarcode);
                                            lv4.setPrice(price);
                                            lv4.setQty(qty);
                                            lv4.setDisc(disc);
                                            lv4.setAmt(amt);
                                            lv4.setSpecAttrDetail(specattrdetail);
                                            lv4.setUnitId(unitid);
                                            lv4.setUnitName(unitname);
                                            lv4.setFlavorstuffDetail(flavorstuffdetail);
                                            lv4.setIsPackage(ispackage);
                                            lv4.setPGoodsDetail(pgoodsdetail);

                                            DCP_CdsOrderStatusUpdate_OpenRes.level5Elm lv5 = res.new level5Elm();
                                            lv5.setOrderTime(shipendtime);
                                            lv5.setAssortedTime(assortedtime);
                                            lv5.setMadeTime(plunomaketime);
                                            lv5.setPickupTime(completetime);
                                            lv4.setTimeInfo(lv5);

                                            lv1.getGoodsList().add(lv4);
                                        }
                                        res.getDatas().add(lv1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CdsOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CdsOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CdsOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 效验该单号是否存在
     *
     * @param req
     * @return
     */
    private String checkBillNo(DCP_CdsOrderStatusUpdate_OpenReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT a.BILLNO , b.APPID ,a.CHANNELID,b.APPNO ,b.APPTYPE,a.ISSENDMSG ,c.ORG_NAME ,a.ORDERTIME,a.WXOPENID,a.BILLTYPE,a.trno,a.APPTYPE AAPPTYPE FROM DCP_PRODUCT_SALE a  " +
                              " LEFT JOIN CRM_CHANNEL b ON a.EID  = b.EID  AND a.CHANNELID  = b.CHANNELID  " +
                              " LEFT JOIN DCP_ORG_LANG c ON a.EID  = c.EID  AND a.SHOPID  = c.ORGANIZATIONNO  AND c.LANG_TYPE  = '" + req.getLangType() + "'" +
                              " WHERE a.eid = ? AND a.SHOPID  = ? AND a.BILLNO = ? ");
        return sqlbuf.toString();
    }

    /**
     * 查询商品详情
     *
     * @param req
     * @param itemStr
     * @return
     */
    private String getPlunoDetails(DCP_CdsOrderStatusUpdate_OpenReq req, String itemStr) {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT PLUNAME FROM DCP_PRODUCT_DETAIL WHERE eid = '" + req.geteId() + "' AND shopid = '" + req.getRequest().getShopId() + "' AND BILLNO = '" + req.getRequest().getBillNo() + "'  ");
        if (!Check.Null(itemStr)) {
            sqlbuf.append("AND oitem in (" + itemStr + ") ");
        }
        return sqlbuf.toString();
    }

    /**
     * 查询下 该单号下所有商品是否都取餐完成
     * @param req
     * @param oItem
     * @return
     */
    private String getBillDetail(DCP_CdsOrderStatusUpdate_OpenReq req,String oItem){
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_PRODUCT_DETAIL WHERE eid = '"+req.geteId()+"' AND SHOPID  = '"+req.getRequest().getShopId()+"' AND BILLNO = '"+req.getRequest().getBillNo()+"'  AND oitem <> '"+oItem+"' and goodsstatus = '2'");
        return sqlbuf.toString();
    }

    /**
     * 查询该门店下所有未取餐的菜品信息
     * @param req
     * @return
     */
    private String getBillDetails(DCP_CdsOrderStatusUpdate_OpenReq req){
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT b.BILLNO ,c.APPID,b.CHANNELID,c.APPNO ,c.APPTYPE,b.ISSENDMSG ,d.ORG_NAME ,b.ORDERTIME,b.WXOPENID,a.PLUNAME,a.OITEM,b.TRNO,b.BILLTYPE " +
                              " FROM DCP_PRODUCT_DETAIL a  " +
                              " LEFT JOIN DCP_PRODUCT_SALE b ON a.EID  = b.EID  AND a.BILLNO  = b.BILLNO  AND a.SHOPID  = b.SHOPID  " +
                              " LEFT JOIN CRM_CHANNEL c ON a.EID  = c.EID  AND b.CHANNELID  = c.CHANNELID  " +
                              " LEFT JOIN DCP_ORG_LANG d ON a.EID  = d.EID  AND a.SHOPID  = d.ORGANIZATIONNO  AND d.LANG_TYPE  = '"+req.getLangType()+"' " +
                              " WHERE a.EID  = '"+req.geteId()+"' AND a.SHOPID  = '"+req.getRequest().getShopId()+"' and a.GOODSSTATUS<>'3' and b.PRODUCTSTATUS<>'3' and b.BILLTYPE in('ORDER','SALE')");
        return sqlbuf.toString();
    }

    /**
     * 整单取餐 外卖类型订单 取餐完成后打印小票需查询返回订单详情
     * @param
     * @return
     */
    private String getBillDetail(){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT c.CHANNELNAME,a.APPTYPE,a.BILLNO ,a.TRNO ,a.MEMO ,d.TOT_QTY,d.TOT_AMT,d.TOT_DISC,d.GETMAN ,d.GETMANTEL ," +
                              "  CASE WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME  || ')'  " +
                              "  WHEN b.SPECNAME IS  NULL AND b.ATTRNAME IS NOT NULL  THEN '('  || b.ATTRNAME || ')'  " +
                              "  WHEN b.SPECNAME IS not NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END  AS specAttrDetail ," +
                              " d.ADDRESS ,a.SHIPENDTIME ,a.MAKETIME ,b.OITEM, b.PLUNO ,b.PLUNAME ,b.PLUBARCODE ,e.PRICE ,b.QTY ,e.DISC ,e.AMT ," +
                              " b.UNITID ,b.UNITNAME ,b.FLAVORSTUFFDETAIL ,b.ISPACKAGE , b.PGOODSDETAIL ,b.ASSORTEDTIME ,b.MAKETIME PLUNOMAKETIME ,b.COMPLETETIME " +
                              " FROM DCP_PRODUCT_SALE a  " +
                              " LEFT JOIN DCP_PRODUCT_DETAIL b ON a.eid = b.eid  AND  a.BILLNO = b.BILLNO  AND a.SHOPID = b.SHOPID " +
                              " LEFT JOIN CRM_CHANNEL c ON a.CHANNELID = c.CHANNELID AND a.EID = c.eid  " +
                              " LEFT JOIN DCP_ORDER d ON a.EID  = d.EID  AND a.BILLNO  = d.ORDERNO  " +
                              " LEFT JOIN DCP_ORDER_DETAIL e ON a.EID  = e.EID  AND d.ORDERNO  = e.ORDERNO AND b.PLUNO  = e.PLUNO  AND b.PLUBARCODE  = e.PLUBARCODE " +
                              " WHERE a.EID  = ? AND a.BILLTYPE  = 'ORDER' and  a.SHOPID  = ? and a.BILLNO = ?");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 计算下单时间和当前时间差 分钟
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public Long getMin(String sdate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date start = simpleDateFormat.parse(sdate);
        Date end = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        long between = (end.getTime() - start.getTime()) / 1000;
        long min = between / 60;
        return min;
    }
    private void sendMsg(DCP_CdsOrderStatusUpdate_OpenReq req,Map<String, Object> map,String eId,String shopId,String businessType,String isOrder,String title,SimpleDateFormat sdf,SimpleDateFormat sds,String billNo,String appid) throws Exception {
        String appNo = map.get("APPNO").toString();
        String appType = map.get("APPTYPE").toString();
        String isSendMsg = map.get("ISSENDMSG").toString();
        String trno = map.get("TRNO").toString();
        String org_name = map.get("ORG_NAME").toString();
        String ordertime = map.get("ORDERTIME").toString();
        String openId = map.get("WXOPENID").toString();
        String channelid = map.get("CHANNELID").toString();
        String APPID = map.get("APPID").toString();

        String occation ="";
        if("2".equals(appType)){
            occation = "#025";
        }else if("4".equals(appType)){
            occation = "#024";
        }
        if ("SCAN".equals(appNo) || "WAIMAI".equals(appNo)) {
            if ("0".equals(businessType)) {
                if ("Y".equals(isSendMsg) || "Y".equals(isOrder)) {
                    // 判断若isSendMsg==Y或入参isOrder==Y，则不再执行调用CRM_MiniSendMsg_Open；
                } else {
                    // 调用 CRM_MiniSendMsg_Open 调用成功回写DCP_PRODUCT_SALE.isSendMsg==Y；
                    String memberUrl = PosPub.getCRM_INNER_URL(eId);
                    if (memberUrl.trim().equals("")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                    }

                    String sql = "SELECT * FROM CRM_MSGCONTROLKEY_DEF WHERE OCCATION = '"+occation+"'";
                    List<Map<String, Object>> getMsgKeys = this.doQueryData(sql, null);
                    if(!CollectionUtils.isEmpty(getMsgKeys))
                    {
                        Map<String, Object> getMsgKey = getMsgKeys.get(0);

                        JSONObject sendMsReq = new JSONObject();
                        sendMsReq.put("serviceId", "CRM_MiniSendMsg_Open");

                        com.alibaba.fastjson.JSONArray datalistArray = new com.alibaba.fastjson.JSONArray();
                        // 取餐编号
                        JSONObject dataMsg1 = new JSONObject(new TreeMap<String, Object>());
                        dataMsg1.put("key","取餐编号");//标题  character_string12  getMsgKey.get("KEYWORD1").toString()
                        dataMsg1.put("value", trno);//内容
                        datalistArray.add(dataMsg1);

                        // 餐品详情

                        //不能超过40个字符，20个汉字
                        int iLen=PosPub.getByteCount(title);
                        if (iLen>40)
                        {
                            title=title.subSequence(0,20).toString();
                        }

                        JSONObject dataMsg2 = new JSONObject(new TreeMap<String, Object>());
                        dataMsg2.put("key", "餐品详情");//标题 thing11  getMsgKey.get("KEYWORD2").toString()
                        dataMsg2.put("value", title);//内容
                        datalistArray.add(dataMsg2);

                        // 门店名称

                        //不能超过40个字符，20个汉字
                        iLen=PosPub.getByteCount(org_name);
                        if (iLen>40)
                        {
                            org_name=org_name.subSequence(0,20).toString();
                        }

                        JSONObject dataMsg3 = new JSONObject(new TreeMap<String, Object>());
                        dataMsg3.put("key", "门店名称");//标题 thing23  getMsgKey.get("KEYWORD3").toString()
                        dataMsg3.put("value", org_name);//内容
                        datalistArray.add(dataMsg3);

                        // 点餐时间
                        JSONObject dataMsg4 = new JSONObject(new TreeMap<String, Object>());
                        dataMsg4.put("key","点餐时间");//标题 date3  getMsgKey.get("KEYWORD4").toString()
                        String time = "";
                        if (!Check.Null(ordertime)) {
                            time = sdf.format(sds.parse(ordertime));
                        }
                        dataMsg4.put("value", time);//内容
                        datalistArray.add(dataMsg4);

                        // 温馨提醒
                        JSONObject dataMsg5 = new JSONObject(new TreeMap<String, Object>());
                        dataMsg5.put("key", "温馨提醒");//标题 thing7 getMsgKey.get("KEYWORD5").toString()
                        dataMsg5.put("value", "您的商品已制作完成，请尽快来取哟");//内容
                        datalistArray.add(dataMsg5);

                        JSONObject message = new JSONObject();
                        message.put("first", "");
                        message.put("remark", "");
                        message.put("page", "");
                        message.put("data", datalistArray);

                        JSONObject cpReq = new JSONObject();
                        cpReq.put("occation", occation); // 消息发送场景  暂时只支持 微信取餐提醒
                        cpReq.put("appId", APPID);
                        cpReq.put("openId", openId);
                        cpReq.put("orderNo", billNo);
                        cpReq.put("message", message);

                        sendMsReq.put("request", cpReq);

                        String sendMsgReqStr = cpReq.toString();
                        String senMsgSign = PosPub.encodeMD5(sendMsgReqStr + req.getApiUser().getUserKey());

                        JSONObject sendMsgSignJson = new JSONObject();
                        sendMsgSignJson.put("sign", senMsgSign);
                        sendMsgSignJson.put("key", appid);

                        sendMsReq.put("sign", sendMsgSignJson);
                        //********** 已经准备好 CRM_MiniSendMsg_Open 的json，开始调用 *************

                        HelpTools.writelog_fileName("***********小程序服务通知 DCP_CdsOrderStatusUpdate_Open 调用 CRM_MiniSendMsg_Open 接口，请求json：" + sendMsReq.toString()
                                                            + "*************", "DCP_CdsOrderStatusUpdate_Open");

                        String sendMsgResStr = HttpSend.Sendcom(sendMsReq.toString(), memberUrl).trim();

                        HelpTools.writelog_fileName("***********小程序服务通知 DCP_CdsOrderStatusUpdate_Open 调用 CRM_MiniSendMsg_Open 接口返回信息：" + sendMsgResStr + "*************", "DCP_CdsOrderStatusUpdate_Open");

                        //简单判断一下json格式
                        if ((sendMsgResStr.startsWith("{") && sendMsgResStr.endsWith("}")) || (sendMsgResStr.startsWith("[") && sendMsgResStr.endsWith("]")))
                        {
                            JSONObject sendMsgResJson = new JSONObject();
                            sendMsgResJson = JSON.parseObject(sendMsgResStr);//String转json
                            String sendMsgSuccess = sendMsgResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                            String sendMsgServiceStatus = sendMsgResJson.getString("serviceStatus").toUpperCase();
                            String sendMsgServiceDescription = sendMsgResJson.getString("serviceDescription").toUpperCase();
                            if (!Check.Null(sendMsgSuccess) && sendMsgSuccess.equals("TRUE"))
                            {
                                UptBean ub2 = new UptBean("DCP_PRODUCT_SALE");
                                ub2.addUpdateValue("ISSENDMSG", new DataValue("Y", Types.VARCHAR));
                                // condition
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub2));
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 获取菜品详情
     * @param req
     * @param goodsList
     * @return
     * @throws Exception
     */
    private String getTitle(DCP_CdsOrderStatusUpdate_OpenReq req,List<DCP_CdsOrderStatusUpdate_OpenReq.level2Elm> goodsList) throws Exception {
        // 获取下菜品详情
        String title = "";
        String items_str = "";
        if (!CollectionUtils.isEmpty(goodsList)) {
            List<String> items = new ArrayList<>();
            for (DCP_CdsOrderStatusUpdate_OpenReq.level2Elm lv2 : goodsList) {
                items.add(lv2.getOItem());
            }
            items_str = PosPub.getArrayStrSQLIn(items.toArray(new String[items.size()]));
        }
        String plunoDetails_sql = getPlunoDetails(req, items_str);
        List<Map<String, Object>> getPlunoDetails = this.doQueryData(plunoDetails_sql, null);
        if (!CollectionUtils.isEmpty(getPlunoDetails)) {
            StringBuffer titleBuf = new StringBuffer("");
            for (Map<String, Object> getPlunoDetail : getPlunoDetails) {
                titleBuf.append(getPlunoDetail.get("PLUNAME").toString());
                titleBuf.append(",");
            }
            titleBuf.deleteCharAt(titleBuf.length()-1);
            title = titleBuf.toString();
        }
        return title;
    }

    /**
     * 叫号之后放入缓存
     * @param map
     * @param eId
     * @param shopId
     */
    private void setCdsCallTaskRedis(Map<String, Object> map,String eId,String shopId,String callTime){
        // 放入叫号缓存队列
        RedisPosPub Rpp = new RedisPosPub();
        //叫号任务缓存节点
        String key="cdsCallTask" + ":" +eId +":" + shopId;
        String billNo = map.get("BILLNO").toString();
        String aapptype = map.get("AAPPTYPE").toString();
        String appName = "";
        if("POS".equals(aapptype)||"POSANDROID".equals(aapptype)){
            appName = "POS";
        }else if("SCAN".equals(aapptype)){
            appName = "扫码点单";
        }else if ("WAIMAI".equals(aapptype)){
            appName = "自营外卖";
        } else if("ELEME".equals(aapptype)){
            appName = "饿了么";
        }else if("MEITUAN".equals(aapptype)){
            appName = "美团";
        }

        JSONObject cdsCallReq = new JSONObject();
        cdsCallReq.put("billNo", billNo); // 单号
        cdsCallReq.put("appName", appName); // 应用类型 POS/POSANDROID：统称"POS" SCAN：扫码点单 WAIMAI：自营外卖 ELEME：饿了么 MEITUAN：美团
        cdsCallReq.put("appType", aapptype);
        cdsCallReq.put("callTime", callTime); // 叫号时间
//                    cdsCallReq.put("taskID", workNo); // 叫号流水号
        cdsCallReq.put("trNo", map.get("TRNO").toString()); // 取餐号
        String cdsCallJsonStr = cdsCallReq.toString();
        Rpp.setHashMap(key, billNo, cdsCallJsonStr);
        Rpp.Close();
    }

    /**
     * 订单调用出餐接口
     * @param req
     * @param orderNo
     * @throws Exception
     */
    private void orderMealOut(DCP_CdsOrderStatusUpdate_OpenReq req, String orderNo) throws Exception
    {
        try
        {
            //1.深拷贝一份源服务请求
            ParseJson pj = new ParseJson();
            //先处理基类字段
            String jsonReq=pj.beanToJson(req);
            DCP_OrderMealOut_OpenReq dcp_orderMealOut_openReq = pj.jsonToBean(jsonReq,new TypeToken<DCP_OrderMealOut_OpenReq>(){});
            dcp_orderMealOut_openReq.seteId(req.geteId());
            dcp_orderMealOut_openReq.setApiUser(req.getApiUser());
            dcp_orderMealOut_openReq.setServiceId("DCP_OrderMealOut_Open");
            DCP_OrderMealOut_OpenReq.level1Elm request = dcp_orderMealOut_openReq.new level1Elm();
            request.seteId(req.geteId());
            request.setOrderNo(orderNo);
            request.setOpNo("");
            request.setOpName("");
            request.setShopId(req.getRequest().getShopId());
            dcp_orderMealOut_openReq.setRequest(request);

            String json_req = pj.beanToJson(dcp_orderMealOut_openReq);
            HelpTools.writelog_fileName("饿了么美团订单cds调用出餐接口，请求req:"+json_req,"DCP_CdsOrderMealOut");
            DCP_OrderMealOut_Open dcp_orderMealOut_open = new DCP_OrderMealOut_Open();
            DCP_OrderMealOut_OpenRes dcp_orderMealOut_openRes = new DCP_OrderMealOut_OpenRes();
            dcp_orderMealOut_open.setDao(this.dao);
            dcp_orderMealOut_open.processDUID(dcp_orderMealOut_openReq,dcp_orderMealOut_openRes);
            String json_res = pj.beanToJson(dcp_orderMealOut_openRes);
            HelpTools.writelog_fileName("饿了么美团订单cds调用出餐接口，返回res:"+json_res,"DCP_CdsOrderMealOut");
            pj = null;

        }
        catch (Exception e)
        {

        }



    }


}
