package com.dsc.spos.waimai.candao;


import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.candao.createOrderReq.product;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

public class candaoService {
    private static String test_ApiUrl = "http://candao-api-gateway.paas-beta.can-dao.com/api";//测试地址

    private static String product_ApiUrl = "https://openapi.can-dao.com/api";//正式地址

    private static String logFileName = "candaoLog";

    private String req_ApiUrl = "https://openapi.can-dao.com/api";
    public candaoService()
    {
        req_ApiUrl = product_ApiUrl;
    }

    public candaoService(boolean isTest)
    {
        req_ApiUrl = product_ApiUrl;
        if (isTest)
        {
            req_ApiUrl = test_ApiUrl;
        }
    }

    public String candaoOrderCreate(String orderCode, Map<String, Object> setMap, Map<String, Object> orderMap, Map<String, Object> SendShopInfoMap, List<Map<String, Object>> detailList, StringBuffer error) throws Exception
    {
        String res = "";
        String actionName = "candao.order.pushOrder";//4.3下单接口
        String serviceType = "pos";//默认
        String methodName = "【下单接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString();
            methodName = reqId + methodName;
            //String isTest = setMap.getOrDefault("ISTEST","").toString();
            String accessKey = setMap.get("APPSIGNKEY").toString();//用户ID
            String secret  = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String fromType = setMap.get("SHOPCODE").toString();//订单来源，参见fromType枚举，建议传此字段便于区分订单渠道
            if (fromType==null||fromType.isEmpty())
            {
                fromType = "dgw";
            }
            createOrderReq cdOrderReq = new createOrderReq();
            cdOrderReq.setFtType(new createOrderReq.ftTypeObj());
            cdOrderReq.setFromType(fromType);
            /**********************收件人地址信息******************************/
            orderNo = orderMap.get("ORDERNO").toString();
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            cdOrderReq.setExtOrderId(orderCode);
            cdOrderReq.setExtOrderNo(orderNo);

            String loadDocType = orderMap.get("LOADDOCTYPE").toString();
            String remark = orderMap.getOrDefault("MEMO", "").toString().trim();//订单备注
            String orderAddress = orderMap.getOrDefault("ADDRESS","").toString();

            String order_lng = orderMap.getOrDefault("LONGITUDE", "").toString();
            String order_lat = orderMap.getOrDefault("LATITUDE", "").toString();
            cdOrderReq.setLatitude(order_lat);
            cdOrderReq.setLongitude(order_lng);

            String thirdSn = orderMap.getOrDefault("ORDER_SN","").toString();
            String subStoreId = orderMap.getOrDefault("SHIPPINGSHOP","").toString();
            String extStoreName = orderMap.getOrDefault("SHIPPINGSHOPNAME","").toString();
            cdOrderReq.setThirdSn(thirdSn);
            cdOrderReq.setSubStoreId(subStoreId);
            cdOrderReq.setExtStoreName(extStoreName);

            String recipientName = orderMap.getOrDefault("GETMAN", "").toString();
            if (recipientName.isEmpty())
            {
                recipientName = orderMap.getOrDefault("CONTMAN", "").toString();
            }
            String recipientMobile = orderMap.getOrDefault("GETMANTEL", "").toString().trim();
            if (recipientMobile.isEmpty())
            {
                recipientMobile = orderMap.getOrDefault("CONTTEL", "").toString().trim();
            }
            cdOrderReq.setName(recipientName);
            cdOrderReq.setPhone(recipientMobile);
            cdOrderReq.setAddress(orderAddress);
            cdOrderReq.setUserNote(remark);//用户备注
            cdOrderReq.setOrderType(4);//参见枚举类orderType(我们传4：外卖预约)
            cdOrderReq.setBook(2);//是否预约单（1：即时，2：预约，0：不区分，默认）(我们传2：预约)

            String create_datetime = orderMap.getOrDefault("CREATE_DATETIME", "").toString().trim();//yyyymmddHHmmssSSS
            String stime = orderMap.getOrDefault("STIME", "").toString().trim();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
            String orderTime = df1.format(new Date());//默认当前时间 yyyy-MM-dd HH:mm:ss
            try
            {
                stime = stime.substring(0,14);
                Date stime_date = df2.parse(stime);
                orderTime =  df1.format(stime_date);//默认数据库存库时间
            }
            catch (Exception e)
            {

            }
            if (create_datetime!=null&&create_datetime.length()>=14)
            {
                create_datetime = create_datetime.substring(0,14);
                try
                {
                    Date create_datetime_date = df2.parse(create_datetime);
                    orderTime =  df1.format(create_datetime_date);//默认数据库存库时间
                }
                catch (Exception e)
                {

                }
            }
            cdOrderReq.setOrderTime(orderTime);

            /********************配送时间**************************/
            String pickupTime = "";
            String shipDate = orderMap.get("SHIPDATE").toString();
            String sdtime = orderMap.get("SHIPENDTIME").toString();//餐道取配送结束时间吧，SHIPSTARTTIME-SHIPENDTIME
            sdtime = sdtime.replace("-", "");
            if (sdtime.isEmpty())
            {
                sdtime = new SimpleDateFormat("HHmmss").format(new Date());
            }
            //做个兼容把，HHmm格式
            if (sdtime.length()==4)
            {
                sdtime +="00";
            }

            pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss
            Date dateShipping = df2.parse(pickupTime);
            String sendTime = df1.format(dateShipping);
            cdOrderReq.setSendTime(sendTime);//预送达时间 yyyy-MM-dd HH:mm:ss
            cdOrderReq.setOrderStatus(10);//1：待支付；7：商家待接单；10：商家已接单；12：备餐中；14：配送中；16：就餐中；18：待取餐；20：取餐超时；100：订单完成；-1：订单取消；21：备餐完成

            /********************金额**************************/
            String tot_amt = orderMap.getOrDefault("TOT_AMT", "0").toString();
            String shipfee = orderMap.getOrDefault("SHIPFEE", "0").toString();

            double tot_amt_d = 0;
            try
            {
                tot_amt_d = new BigDecimal(tot_amt).setScale(2, RoundingMode.HALF_UP).doubleValue();
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            double shipfee_d = 0;
            try
            {
                shipfee_d = new BigDecimal(shipfee).setScale(2, RoundingMode.HALF_UP).doubleValue();
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            cdOrderReq.setPrice(tot_amt_d);
            cdOrderReq.setMerchantPrice(tot_amt_d);
            cdOrderReq.setDeliveryFee(shipfee_d);

            cdOrderReq.setProducts(new ArrayList<>());
            /**********************商品信息******************************/
            if(detailList!=null&&detailList.size()>0)
            {
                //单个商品名称不可大于50个字符
                for (Map<String, Object> mapGoods : detailList)
                {
                    createOrderReq.product product = new product();
                    String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
                    String unitName = mapGoods.getOrDefault("SUNITNAME", "").toString();
                    String qty = mapGoods.getOrDefault("QTY", "1").toString();
                    double GoodsQuantity = 1;
                    try
                    {
                        GoodsQuantity = new BigDecimal(qty).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                    String price = mapGoods.getOrDefault("PRICE", "0").toString();
                    double GoodsPrice = 0;
                    try {

                        GoodsPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    }
                    catch (Exception e)
                    {

                    }
                    product.setName(pluName);
                    product.setNum(GoodsQuantity);
                    product.setPrice(GoodsPrice);

                    cdOrderReq.getProducts().add(product);
                }

            }

            String dataJson = JSONObject.toJSONString(cdOrderReq);
            long timestamp = System.currentTimeMillis();
            String sign = accessKey+actionName+secret+timestamp+dataJson;
            sign = PosPub.encodeMD5(sign);
            candaoRequest<createOrderReq> request = new candaoRequest<createOrderReq>();
            request.setAccessKey(accessKey);
            request.setActionName(actionName);
            request.setTicket(reqId);//guid
            request.setTimestamp(timestamp);
            request.setServiceType(serviceType);
            request.setSign(sign);
            request.setData(cdOrderReq);

            String req = JSONObject.toJSONString(request);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】发送url:"+req_ApiUrl+",请求req:"+req+logEndWithStr,logFileName);
            res = candaoHttpClientUtil.postRequest(req_ApiUrl, req);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】返回res:"+res+logEndWithStr,logFileName);

        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用餐道API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }


    public String candaoOrderCancel(String orderCode, Map<String, Object> setMap, Map<String, Object> orderMap, StringBuffer error) throws Exception
    {
        String res = "";
        String actionName = "candao.order.updateOrderStatus";//同步订单状态
        String serviceType = "pos";//默认
        String methodName = "【取消接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString();
            methodName = reqId + methodName;
            //String isTest = setMap.getOrDefault("ISTEST","").toString();
            String accessKey = setMap.get("APPSIGNKEY").toString();//用户ID
            String secret  = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String fromType = setMap.get("SHOPCODE").toString();//订单来源，参见fromType枚举，建议传此字段便于区分订单渠道
            if (fromType==null||fromType.isEmpty())
            {
                fromType = "dgw";
            }
            updateOrderStatusReq cdOrderReq = new updateOrderStatusReq();
            cdOrderReq.setStatus(-1);
            cdOrderReq.setFromType(fromType);
            /**********************收件人地址信息******************************/
            orderNo = orderMap.get("ORDERNO").toString();
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            cdOrderReq.setExtOrderId(orderCode);
            cdOrderReq.setExtOrderNo(orderNo);
            String out_deliveryNo = orderMap.get("OUTDOCORDERNO").toString();//餐道下单接口返回的order_id
            cdOrderReq.setOrderId(out_deliveryNo);

            String loadDocType = orderMap.get("LOADDOCTYPE").toString();

            String subStoreId = orderMap.getOrDefault("SHIPPINGSHOP","").toString();
            cdOrderReq.setSubStoreId(subStoreId);
            cdOrderReq.setCancelReason(201);
            cdOrderReq.setCancelReasonName("顾客不想要了");

            String dataJson = JSONObject.toJSONString(cdOrderReq);
            long timestamp = System.currentTimeMillis();
            String sign = accessKey+actionName+secret+timestamp+dataJson;
            sign = PosPub.encodeMD5(sign);
            candaoRequest<updateOrderStatusReq> request = new candaoRequest<updateOrderStatusReq>();
            request.setAccessKey(accessKey);
            request.setActionName(actionName);
            request.setTicket(reqId);//guid
            request.setTimestamp(timestamp);
            request.setServiceType(serviceType);
            request.setSign(sign);
            request.setData(cdOrderReq);

            String req = JSONObject.toJSONString(request);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】发送url:"+req_ApiUrl+",请求req:"+req+logEndWithStr,logFileName);
            res = candaoHttpClientUtil.postRequest(req_ApiUrl, req);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】返回res:"+res+logEndWithStr,logFileName);

        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用餐道API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }


    public String candaoGetRiderPosition(String orderCode, Map<String, Object> setMap, Map<String, Object> orderMap, StringBuffer error) throws Exception
    {
        String res = "";
        String actionName = "candao.rider.getRiderPosition";//查询骑手位置
        String serviceType = "pos";//默认
        String methodName = "【查询骑手位置接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString();
            methodName = reqId + methodName;
            //String isTest = setMap.getOrDefault("ISTEST","").toString();
            String accessKey = setMap.get("APPSIGNKEY").toString();//用户ID
            String secret  = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String fromType = setMap.get("SHOPCODE").toString();//订单来源，参见fromType枚举，建议传此字段便于区分订单渠道
            if (fromType==null||fromType.isEmpty())
            {
                fromType = "dgw";
            }
            getRiderPositionReq cdOrderReq = new getRiderPositionReq();
            cdOrderReq.setFromType(fromType);
            /**********************收件人地址信息******************************/
            orderNo = orderMap.get("ORDERNO").toString();
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            cdOrderReq.setExtOrderId(orderCode);
            String out_deliveryNo = orderMap.get("OUTDOCORDERNO").toString();//餐道下单接口返回的order_id
            cdOrderReq.setOrderId(out_deliveryNo);

            String loadDocType = orderMap.get("LOADDOCTYPE").toString();

            String subStoreId = orderMap.getOrDefault("SHIPPINGSHOP","").toString();
            cdOrderReq.setSubStoreId(subStoreId);
            String deliverySysType = orderMap.get("SUBDELIVERYCOMPANYNO").toString();//真正配送的物流平台
            cdOrderReq.setDeliverySysType(deliverySysType);

            String dataJson = JSONObject.toJSONString(cdOrderReq);
            long timestamp = System.currentTimeMillis();
            String sign = accessKey+actionName+secret+timestamp+dataJson;
            sign = PosPub.encodeMD5(sign);
            candaoRequest<getRiderPositionReq> request = new candaoRequest<getRiderPositionReq>();
            request.setAccessKey(accessKey);
            request.setActionName(actionName);
            request.setTicket(reqId);//guid
            request.setTimestamp(timestamp);
            request.setServiceType(serviceType);
            request.setSign(sign);
            request.setData(cdOrderReq);

            String req = JSONObject.toJSONString(request);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】发送url:"+req_ApiUrl+",请求req:"+req+logEndWithStr,logFileName);
            res = candaoHttpClientUtil.postRequest(req_ApiUrl, req);
            HelpTools.writelog_fileName(methodName+"【调用餐道API】返回res:"+res+logEndWithStr,logFileName);

        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用餐道API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }
}
