package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.req.DCP_OrderPay_OpenReq;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定时调用 CRM 的支付查询接口（接口名 Query），
 * 轮询缓存信息，主动查CRM 的支付查询 Query接口， 返回成功后， 判断是否付清，写定金补录 然后删除缓存信息
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PayAddProcess extends InitJob {
    //******兼容即时服务的,只查询指定的那张单据******
    String pEID = "";
    String pShop = "";
    String pOrganizationNO = "";
    String pBillNo = "";

    public PayAddProcess() {

    }

    public PayAddProcess(String eId, String shop, String organizationNO, String billNo) {
        pEID = eId;
        pShop = shop;
        pOrganizationNO = organizationNO;
        pBillNo = billNo;
    }

    Logger logger = LogManager.getLogger(PayAddProcess.class.getName());

    static boolean bRun = false;//标记此服务是否正在执行中
    String logFileName = "PayAddProcess";
    //0-代表成功  其他返回失败信息
    public String doExe() {
        String sReturnInfo = null;
        try {
            //返回信息
            sReturnInfo = "";

            //此服务是否正在执行中
            if (bRun && pEID.equals("")) {
                logger.info("\r\n*********移动支付 订金补录处理 PayAddProcess 正在执行中,本次调用取消:************\r\n");

                sReturnInfo = "定时传输任务-移动支付 订金补录处理 PayAddProcess正在执行中！";
                return sReturnInfo;
            }

            bRun = true;//
            logger.info("\r\n*********移动支付 订金补录处理 PayAddProcess定时调用Start:************\r\n");
            HelpTools.writelog_fileName("*********移动支付 订金补录处理 PayAddProcess定时调用Start:************",logFileName);

            Calendar cal2 = Calendar.getInstance();// 获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dfss = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String createDate = df.format(cal2.getTime());
            Date dateTime = df.parse(createDate);
            RedisPosPub rpp = new RedisPosPub();

            MyCommon MC = new MyCommon();
            String redis_key = "ScanPayOrder";
            Map<String, String> allMap = rpp.getALLHashMap(redis_key);
            Set<String> keys = allMap.keySet();

            HelpTools.writelog_fileName(
                    "***********移动支付 订金补录处理 PayAddProcess  redis主键redis_key="+redis_key+",内容可能太多了，不记录  " + "*************",
                    logFileName);

            for (String str : keys) {

                JSONArray invList = new JSONArray();
                String hash_key = str;//eId + ":" + shopId + ":" + orderNo;
                //System.err.println("Redis 信息: " + hash_key + " " + allMap.get(hash_key));


                String allStr = allMap.get(hash_key);
                HelpTools.writelog_fileName(
                        "***********移动支付 订金补录处理 PayAddProcess  hash_key:" + hash_key + ",hash_value:"+allStr+"" + "*************",
                        logFileName);
                int indexofSpec = hash_key.indexOf(":");//eId + ":" + shopId + ":" + orderNo;
                String eId = hash_key.substring(0, indexofSpec);//企业编码

                JSONObject allJson = new JSONObject(new TreeMap<String, Object>());
                allJson = JSONObject.parseObject(allStr);

                String expireTime = allJson.get("expireTime").toString();
                String orderNo = allJson.get("orderNo").toString();
                String prepayId = allJson.get("prepayId").toString();
                String opNo = allJson.getOrDefault("opNo","").toString();
                String opName = "";
                try {
                    opName = allJson.getOrDefault("opName","").toString();
                } catch (Exception e) {
                    opName = "";
                }
                String loadDocType = allJson.getOrDefault("loadDocType","").toString();
                String openId = allJson.getOrDefault("openId","").toString();
                String orderAmount = allJson.getOrDefault("orderAmount","0").toString();
                String pointAmount = allJson.getOrDefault("pointAmount","0").toString();
                String sendMsg = allJson.getOrDefault("sendMsg","").toString();
                String workNo = allJson.getOrDefault("workNo","").toString();
                String squadNo = allJson.getOrDefault("squadNo","").toString();
                String machineNo = allJson.getOrDefault("machineNo","").toString();
                String sign = allJson.getOrDefault("sign","").toString();
                String strTimeout = allJson.getOrDefault("uploadTime","").toString();
                Date timeout = df.parse(strTimeout);
                HelpTools.writelog_fileName(
                        "***********移动支付 订金补录处理 PayAddProcess  dateTime:"+dateTime + "timout:"+timeout+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                        logFileName);

                Date createDateExpireTime = null;
                if (!Check.Null(expireTime)) {
                    createDateExpireTime = df.parse(expireTime);
                }

                // createPay
                JSONObject createPayReqJson = new JSONObject(new TreeMap<String, Object>());
                String createPayStr = allJson.get("createPay").toString();
                createPayReqJson = JSONObject.parseObject(createPayStr);

                // pay
                JSONArray payJson = allJson.getJSONArray("pay");

                String pay_type = createPayReqJson.get("pay_type").toString();
                String shop_code = createPayReqJson.get("shop_code").toString();
                String shopId = shop_code;
                String pos_code = createPayReqJson.get("pos_code").toString();

                String operation_id = "";
                try {
                    operation_id = createPayReqJson.get("operation_id").toString();
                } catch (Exception e) {
                    operation_id = "";
                }

                String ip = createPayReqJson.get("ip").toString();

                String dcpUrl = PosPub.getDCP_INNER_URL(eId);
                String crmPayUrl = PosPub.getPAY_INNER_URL(eId);

                String sql = "";
                // ********************************* 先检查订单下单 是否超时 超时进行退单 Begin *********************************
                if (createDateExpireTime != null) {
                    if (dateTime.compareTo(createDateExpireTime) == 0 || dateTime.compareTo(createDateExpireTime) == 1) {
                        HelpTools.writelog_fileName(
                                "***********移动支付 订金补录处理 PayAddProcess  下单超时" +",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                                logFileName);
                        sql = "select * from DCP_ORDER where EID = '" + eId + "' and ORDERNO = '" + orderNo + "'";
                        List<Map<String, Object>> getOrderInfo = this.doQueryData(sql, null);
                        if (!CollectionUtils.isEmpty(getOrderInfo)) {
                            String status = getOrderInfo.get(0).get("STATUS").toString();
                            String channelId = getOrderInfo.get(0).get("CHANNELID").toString();
                            String tot_amt = getOrderInfo.get(0).get("TOT_AMT").toString();
                            String payStatus = getOrderInfo.get(0).get("PAYSTATUS").toString();
                            if (status.equals("3")) {
                                HelpTools.writelog_fileName(
                                        "***********移动支付 订金补录处理 PayAddProcess  单号:" + orderNo + " 订单状态为已取消 " +",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                                        logFileName);
                                rpp.DeleteHkey(redis_key, hash_key);
                                break;
                            }
                            if (status.equals("12")) {
                                HelpTools.writelog_fileName(
                                        "***********移动支付 订金补录处理 PayAddProcess  单号:" + orderNo + " 订单状态为已退单 " +",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                                        logFileName);
                                rpp.DeleteHkey(redis_key, hash_key);
                                break;
                            }
                            if (payStatus.equals("3")) {
                                HelpTools.writelog_fileName(
                                        "***********移动支付 订金补录处理 PayAddProcess  单号:" + orderNo + " 订单付款状态为已支付 " + ",waimai单号="+orderNo+",支付单号order_id="+prepayId,
                                        logFileName);
                                rpp.DeleteHkey(redis_key, hash_key);
                                break;
                            }

                            // 订单下单时间已支付超时 进行退单  调用DCP_OrderRefund_ Open 接口.
                            // DCP_OrderRefund_ Open
                            //营业日期
                            String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopId);

                            // 获取当前时间 比较 预计结束时间
                            Date time = new Date();
                            String refundDatetime = dfss.format(time);

                            JSONObject orderRefundReq = new JSONObject();
                            orderRefundReq.put("serviceId", "DCP_OrderRefund_Open");

                            JSONObject requestReq = new JSONObject();
                            requestReq.put("eId", eId);
                            requestReq.put("shopId", shopId);
                            requestReq.put("opNo", opNo);
                            requestReq.put("opName", opName);
                            requestReq.put("refundType", "0");
                            requestReq.put("orderNo", orderNo);
                            requestReq.put("loadDocType", loadDocType);
                            requestReq.put("channelId", channelId);
                            requestReq.put("pickGoodsRefundType", "0");
                            requestReq.put("refundReasonNo", "");
                            requestReq.put("refundReasonName", "");
                            requestReq.put("refundReason", "");
                            requestReq.put("refundBdate", accountDate);
                            requestReq.put("refundDatetime", refundDatetime);
                            requestReq.put("tot_amt", tot_amt);

                            orderRefundReq.put("request", requestReq);

                            String orderRefundReqStr = requestReq.toString();
                            String orderRefundSign = PosPub.encodeMD5(orderRefundReqStr + sign);

                            JSONObject orderRefundSignJson = new JSONObject();
                            orderRefundSignJson.put("sign", orderRefundSign);
                            orderRefundSignJson.put("key", sign);

                            orderRefundReq.put("sign", orderRefundSignJson);

                            //********** 已经准备好 DCP_OrderRefund_Open 的json，开始调用 *************
                            String orderRefundResStr = HttpSend.Sendcom(orderRefundReq.toString(), dcpUrl).trim();
                            HelpTools.writelog_fileName("*********** 移动支付 订金补录处理 PayAddProcess 调用DCP_OrderRefund_Open接口信息：地址（" + dcpUrl + "）  请求Json：" + orderRefundReq+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                            HelpTools.writelog_fileName("*********** 移动支付 订金补录处理 PayAddProcess 调用DCP_OrderRefund_Open接口返回信息：" + orderRefundResStr+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);

                            JSONObject orderRefundResJson = new JSONObject();
                            orderRefundResJson = JSON.parseObject(orderRefundResStr);//String转json

                            String orderRefundSuccess = orderRefundResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                            String orderRefundStatus = orderRefundResJson.getString("serviceStatus").toUpperCase();
                            String orderRefundServiceDescription = orderRefundResJson.getString("serviceDescription").toUpperCase();
                            if (orderRefundSuccess.toUpperCase().equals("TRUE")) {
                                // 退单成功
                                HelpTools.writelog_fileName(
                                        "***********移动支付 订金补录处理 PayAddProcess  单号:" + orderNo + " 退单成功 " +",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                                        logFileName);
                                rpp.DeleteHkey(redis_key, hash_key);
                                continue;
                            }

                        }
                        continue;
                    }
                }

                // ********************************* 先检查订单下单 是否超时 超时进行退单 End *********************************


                // 检查缓存超时时间 有无超时 如果超出超时时间则 终止线程
                if (dateTime.compareTo(timeout) == 0 || dateTime.compareTo(timeout) == 1) {
                    HelpTools.writelog_fileName(
                            "***********移动支付 订金补录处理 PayAddProcess  支付缓存已超时  单号:"+orderNo+"" +",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId,
                            logFileName);
                   // rpp.DeleteHkey(redis_key, hash_key);
                    //continue;
                }

                // ************* 先走一下Query 查询， 看有没有支付成功 *****************
                JSONObject QueryReq = new JSONObject();
                QueryReq.put("serviceId", "Query");

                JSONObject payReq = new JSONObject();
                payReq.put("pay_type", pay_type);
                payReq.put("shop_code", shop_code);
                payReq.put("pos_code", pos_code);
                payReq.put("order_id", prepayId);
                payReq.put("trade_no", "");
                payReq.put("operation_id", operation_id);
                payReq.put("ip", ip);

                QueryReq.put("request", payReq);

                String queryReqStr = payReq.toString();
                String querySign = PosPub.encodeMD5(queryReqStr + sign);

                JSONObject querySignJson = new JSONObject();
                querySignJson.put("sign", querySign);
                querySignJson.put("key", sign);

                QueryReq.put("sign", querySignJson);

                //********** 已经准备好Query的json，开始调用 *************

                String queryResStr = HttpSend.Sendcom(QueryReq.toString(), crmPayUrl).trim();

                HelpTools.writelog_fileName("*********** PayAddProcess 调用Query接口信息：地址（" + crmPayUrl + "）  请求Json：" + QueryReq+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                HelpTools.writelog_fileName("*********** PayAddProcess 调用Query返回信息：" + queryResStr+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);

                JSONObject queryResJson = new JSONObject();
                queryResJson = JSON.parseObject(queryResStr);//String转json

                String querySuccess = queryResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                String queryStatus = queryResJson.getString("serviceStatus").toUpperCase();
                String queryServiceDescription = queryResJson.getString("serviceDescription").toUpperCase();

                if (queryStatus.equals("008") || queryServiceDescription.equals("已关闭")) { //008,已关闭。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                    rpp.DeleteHkey(redis_key, hash_key);
                    HelpTools.writelog_fileName("*********** 支付已关闭" + orderNo+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                    continue;
                }
                if (queryStatus.equals("NOTPAY") || queryServiceDescription.equals("未支付")) { //NOTPAY,未支付。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                    HelpTools.writelog_fileName("*********** 订单未支付" + orderNo+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                    continue;
                }
                if (querySuccess.toUpperCase().equals("TRUE")) {
                    // 查询到支付成功
                    List<DCP_OrderPay_OpenReq.level3Elm> payList = null;
                    try {
                        JSONObject queryResJson2 = new JSONObject(new TreeMap<String, Object>());
                        String queryResStr2 = queryResJson.get("datas").toString();
                        queryResJson2 = JSONObject.parseObject(queryResStr2);
                        String trade_no_query = queryResJson2.get("trade_no").toString();
//                        payList.forEach(p -> p.setRefNo(trade_no_query));
                        payList = JSONObject.parseArray(payJson.toString(), DCP_OrderPay_OpenReq.level3Elm.class);
                        for (DCP_OrderPay_OpenReq.level3Elm lv3 : payList) {
                            lv3.setRefNo(trade_no_query);
                        }
                    } catch (Exception e) {
                        payList = JSONObject.parseArray(payJson.toString(), DCP_OrderPay_OpenReq.level3Elm.class);
                        for (DCP_OrderPay_OpenReq.level3Elm lv3 : payList) {
                            lv3.setRefNo("");
                        }
                    }
                    // 判断订单支付状态 payStatus 是否等于 3 (已付清)。

                    sql = "select * from DCP_ORDER where EID = '" + eId + "' and ORDERNO = '" + orderNo + "'";
                    List<Map<String, Object>> getOrderInfo = this.doQueryData(sql, null);
                    String payStatus = "";
                    if (!CollectionUtils.isEmpty(getOrderInfo)) {
                        payStatus = getOrderInfo.get(0).get("PAYSTATUS").toString();
                    if (payStatus.equals("3")) {
                        // 已付清 则删除该缓存
                        rpp.DeleteHkey(redis_key, hash_key);
                        HelpTools.writelog_fileName("***********  PayAddProcess 订单已付清" + orderNo+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                        break;
                    } else {
                        // 未付清 调用 订金补录，成功后 删除该缓存
                        JSONObject modify_PayRequest = new JSONObject();
                        modify_PayRequest.put("serviceId", "DCP_OrderModify_PayAdd_Open");
                        modify_PayRequest.put("version", "3.0");
                        modify_PayRequest.put("langType", "zh_CN");

                        JSONObject modify_PayAddReq = new JSONObject();
                        modify_PayAddReq.put("orderNo", orderNo);
                        modify_PayAddReq.put("workNo", workNo);
                        modify_PayAddReq.put("squadNo", squadNo);
                        modify_PayAddReq.put("shopId", shopId);
                        modify_PayAddReq.put("machineNo", machineNo);
                        modify_PayAddReq.put("opNo", opNo);
                        modify_PayAddReq.put("opName", opName);
                        modify_PayAddReq.put("loadDocType", loadDocType);
                        modify_PayAddReq.put("pay", payList);
                        
                        modify_PayRequest.put("request", modify_PayAddReq);

                        String payRequestStr = modify_PayAddReq.toString();
                        String modify_PaySign = PosPub.encodeMD5(payRequestStr + sign);

                        JSONObject modify_PaySignJson = new JSONObject();
                        modify_PaySignJson.put("sign", modify_PaySign);
                        modify_PaySignJson.put("key", sign);

                        modify_PayRequest.put("sign", modify_PaySignJson);

                        //********** 已经准备好 DCP_OrderModify_PayAdd 的json，开始调用 *************
                        String modify_PayResStr = HttpSend.Sendcom(modify_PayRequest.toString(), dcpUrl).trim();
                        HelpTools.writelog_fileName("*********** PayAddProcess 调用 DCP_OrderModify_PayAdd_Open 接口信息：地址（" + dcpUrl + "）  请求Json：" + modify_PayRequest+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                        HelpTools.writelog_fileName("*********** PayAddProcess 调用 DCP_OrderModify_PayAdd_Open 返回信息：" + modify_PayResStr+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);

                        JSONObject modify_PayResJson = new JSONObject();
                        modify_PayResJson = JSON.parseObject(modify_PayResStr);//String转json

                        String modify_PaySuccess = modify_PayResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                        String modify_PayStatus = modify_PayResJson.getString("serviceStatus").toUpperCase();
                        String modify_PayServiceDescription = modify_PayResJson.getString("serviceDescription").toUpperCase();

                        if (modify_PaySuccess.toUpperCase().equals("TRUE")) {
                            // 定金补录成功
                            rpp.DeleteHkey(redis_key, hash_key);
                            HelpTools.writelog_fileName("*********** 定金补录成功，删除该缓存" + orderNo+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                            continue;
                        } else {
                            HelpTools.writelog_fileName("*********** 定金补录失败" + orderNo+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                            continue;
                        }

                    }
                }else {
                    HelpTools.writelog_fileName("*********** 定金补录失败" + orderNo+"订单不存在"+",waimi订单号hash_key="+hash_key+",支付单号order_id="+prepayId, logFileName);
                    continue;
                }
                } else {
                    continue;
                }

            }
        } catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\n******移动支付 订金补录处理 PayAddProcess报错信息" + e.getMessage() + "\r\n" + errors.toString() + "******\r\n");
                HelpTools.writelog_fileName("*********** 移动支付 订金补录处理 PayAddProcess报错信息" + e.getMessage()+","+errors.toString(), logFileName);
                pw = null;
                errors = null;
            } catch (IOException e1) {
                logger.error("\r\n******移动支付 订金补录处理 PayAddProcess报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo = "错误信息:" + e.getMessage();

        } finally {
            bRun = false;//
            logger.info("\r\n*********移动支付 订金补录处理 PayAddProcess定时调用End:************\r\n");
            try {
                HelpTools.writelog_fileName("*********移动支付 订金补录处理 PayAddProcess定时调用End:************","PayAddProcess");
            }
            catch (Exception e)
            {
            }
        }

        //
        return sReturnInfo;
    }

}
