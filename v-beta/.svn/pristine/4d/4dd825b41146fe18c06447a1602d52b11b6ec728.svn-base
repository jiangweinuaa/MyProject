package com.dsc.spos.waimai.kdniao;

import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class kdnQGService {

    private static String test_ApiUrl = "http://183.62.170.46:8081/api/dist";//测试地址

    private static String product_ApiUrl = "https://api.kdniao.com/api/OOrderService";//正式地址

    private static String DataType = "2";//请求、返回数据类型：2表示json格式；

    private static String logFileName = "kdniaoLog";

    private String req_ApiUrl = "https://api.kdniao.com/api/OOrderService";

    public kdnQGService ()
    {
        req_ApiUrl = product_ApiUrl;
    }

    public kdnQGService ( boolean isTest)
    {
        req_ApiUrl = product_ApiUrl;
        if (isTest)
        {
            req_ApiUrl = test_ApiUrl;
        }

    }


    /**
     * 快递鸟超区校验接口(全国快递)
     * @param setMap 配置参数
     * @param orderMap 订单单头信息
     * @param SendShopInfoMap 发货门店信息
     * @param error 返回异常信息
     * @return
     * @throws Exception
     */
    public String checkDeliveryRange(Map<String, Object> setMap,Map<String, Object> orderMap,Map<String, Object> SendShopInfoMap,StringBuffer error) throws Exception
    {
        String res = "";
        String RequestType = "1814";//4.1超区校验接口
        String methodName = "【全国快递】【超区校验接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String ShipperType = setMap.get("SHIPPERTYPE").toString();//产品类型 3=2小时收;4=半日收;5=当日收
            /**********************收件人地址信息******************************/
            orderNo = orderMap.get("ORDERNO").toString();
            logEndWithStr = ",订单号:"+orderNo;
            String loadDocType = orderMap.get("LOADDOCTYPE").toString();
            String orderProvinceName = orderMap.getOrDefault("PROVINCE","").toString();
            String orderCityName = orderMap.getOrDefault("CITY","").toString();
            String orderExpAreaName = orderMap.getOrDefault("COUNTY","").toString();
            String orderAddress = orderMap.getOrDefault("ADDRESS","").toString();
            if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
            {
                //商城的地址，接过来的时候做了拼接，现在还原回去 address = province+city+county+address+address2+streetNo;
                String startWithStr = orderProvinceName+orderCityName+orderExpAreaName;
                if (startWithStr.length()>0&&orderAddress.startsWith(startWithStr))
                {
                    orderAddress = orderAddress.substring(startWithStr.length());
                }
            }
            if ("北京市".equals(orderProvinceName)||"上海市".equals(orderProvinceName)||"天津市".equals(orderProvinceName)||"重庆市".equals(orderProvinceName))
            {
                //收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
                orderProvinceName = orderProvinceName.substring(0,orderProvinceName.length()-1);
            }
            if (orderExpAreaName.isEmpty())
            {
                if (orderProvinceName.startsWith("广东"))
                {
                    if("东莞市".equals(orderCityName)||"中山市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
               else if (orderProvinceName.startsWith("海南"))
                {
                    if("詹州市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
                else if (orderProvinceName.startsWith("甘肃"))
                {
                    if("嘉峪关市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
            }
            JSONObject Receiver = new JSONObject();
            Receiver.put("ProvinceName",orderProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            Receiver.put("CityName",orderCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            Receiver.put("ExpAreaName",orderExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            Receiver.put("Address",orderAddress);//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中
            /**********************发件人地址信息******************************/
            String sendProvinceName = SendShopInfoMap.getOrDefault("PROVINCE","").toString();
            String sendCityName = SendShopInfoMap.getOrDefault("CITY","").toString();
            String sendExpAreaName = SendShopInfoMap.getOrDefault("COUNTY","").toString();
            String sendAddress = SendShopInfoMap.getOrDefault("ADDRESS","").toString();
            if ("北京市".equals(sendProvinceName)||"上海市".equals(sendProvinceName)||"天津市".equals(sendProvinceName)||"重庆市".equals(sendProvinceName))
            {
                //收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
                sendProvinceName = sendProvinceName.substring(0,sendProvinceName.length()-1);
            }

            //以下城市没有区县:广东省东莞市、中山市，海南省詹州市，甘肃省嘉峪关市
            if (sendExpAreaName.isEmpty())
            {
                if (sendProvinceName.startsWith("广东"))
                {
                    if("东莞市".equals(sendCityName)||"中山市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
                else if (sendProvinceName.startsWith("海南"))
                {
                    if("詹州市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
                else if (sendProvinceName.startsWith("甘肃"))
                {
                    if("嘉峪关市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
            }
            JSONObject Sender = new JSONObject();
            Sender.put("ProvinceName",sendProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            Sender.put("CityName",sendCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            Sender.put("ExpAreaName",sendExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            Sender.put("Address",sendAddress);//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中

            JSONObject RequestData = new JSONObject();
            RequestData.put("ShipperType",ShipperType);
            RequestData.put("Receiver",Receiver);
            RequestData.put("Sender",Sender);
            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            params.put("RequestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("EBusinessID", EBusinessID);
            params.put("RequestType", RequestType);//快递接口指令
            params.put("DataType", DataType);

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));

            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】发送url:"+req_ApiUrl+",请求RequestData转码前:"+requestDataJson+",请求req:"+params.toString()+logEndWithStr,logFileName);
            res = sendPost(req_ApiUrl, params,error);
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】返回res:"+res+logEndWithStr,logFileName);
        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }

    /**
     * 快递鸟下单接口(全国快递)
     * @param orderCode 传入的物流单号
     * @param setMap 配置信息
     * @param orderMap 订单单头信息
     * @param SendShopInfoMap 发货门店信息
     * @param detailList 订单商品明细
     * @param error 异常信息
     * @return
     * @throws Exception
     */
    public String kdnOrderCreate(String orderCode, Map<String, Object> setMap, Map<String, Object> orderMap, Map<String, Object> SendShopInfoMap, List<Map<String, Object>> detailList,StringBuffer error) throws Exception
    {
        String res = "";
        String RequestType = "1801";//4.3下单接口
        String methodName = "【全国快递】【下单接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;

            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String ShipperType = setMap.get("SHIPPERTYPE").toString();//产品类型 3=2小时收;4=半日收;5=当日收
            /**********************收件人地址信息******************************/
            orderNo = orderMap.get("ORDERNO").toString();
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            String loadDocType = orderMap.get("LOADDOCTYPE").toString();
            String orderProvinceName = orderMap.getOrDefault("PROVINCE","").toString();
            String orderCityName = orderMap.getOrDefault("CITY","").toString();
            String orderExpAreaName = orderMap.getOrDefault("COUNTY","").toString();
            String orderAddress = orderMap.getOrDefault("ADDRESS","").toString();
            if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
            {
                //商城的地址，接过来的时候做了拼接，现在还原回去 address = province+city+county+address+address2+streetNo;
                String startWithStr = orderProvinceName+orderCityName+orderExpAreaName;
                if (startWithStr.length()>0&&orderAddress.startsWith(startWithStr))
                {
                    orderAddress = orderAddress.substring(startWithStr.length());
                }
            }
            if ("北京市".equals(orderProvinceName)||"上海市".equals(orderProvinceName)||"天津市".equals(orderProvinceName)||"重庆市".equals(orderProvinceName))
            {
                //收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
                orderProvinceName = orderProvinceName.substring(0,orderProvinceName.length()-1);
            }

            //以下城市没有区县:广东省东莞市、中山市，海南省詹州市，甘肃省嘉峪关市
            if (orderExpAreaName.isEmpty())
            {
                if (orderProvinceName.startsWith("广东"))
                {
                    if("东莞市".equals(orderCityName)||"中山市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
                else if (orderProvinceName.startsWith("海南"))
                {
                    if("詹州市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
                else if (orderProvinceName.startsWith("甘肃"))
                {
                    if("嘉峪关市".equals(orderCityName))
                    {
                        orderExpAreaName = orderCityName;
                    }

                }
            }

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

            JSONObject Receiver = new JSONObject();
            Receiver.put("ProvinceName",orderProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            Receiver.put("CityName",orderCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            Receiver.put("ExpAreaName",orderExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            Receiver.put("Address",fiterStr(orderAddress));//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中
            if (recipientMobile.startsWith("1")&&recipientMobile.length()==11)
            {
                Receiver.put("Mobile",recipientMobile);
            }
            else
            {
                Receiver.put("Tel",recipientMobile);
            }
            Receiver.put("Name",fiterStr(recipientName));

            /**********************发件人地址信息******************************/
            String sendProvinceName = SendShopInfoMap.getOrDefault("PROVINCE","").toString();
            String sendCityName = SendShopInfoMap.getOrDefault("CITY","").toString();
            String sendExpAreaName = SendShopInfoMap.getOrDefault("COUNTY","").toString();
            String sendAddress = SendShopInfoMap.getOrDefault("ADDRESS","").toString();
            if ("北京市".equals(sendProvinceName)||"上海市".equals(sendProvinceName)||"天津市".equals(sendProvinceName)||"重庆市".equals(sendProvinceName))
            {
                //收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
                sendProvinceName = sendProvinceName.substring(0,sendProvinceName.length()-1);
            }
            //以下城市没有区县:广东省东莞市、中山市，海南省詹州市，甘肃省嘉峪关市
            if (sendExpAreaName.isEmpty())
            {
                if (sendProvinceName.startsWith("广东"))
                {
                    if("东莞市".equals(sendCityName)||"中山市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
                else if (sendProvinceName.startsWith("海南"))
                {
                    if("詹州市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
                else if (sendProvinceName.startsWith("甘肃"))
                {
                    if("嘉峪关市".equals(sendCityName))
                    {
                        sendExpAreaName = sendCityName;
                    }

                }
            }
            String senderName = orderMap.getOrDefault("SHIPPINGSHOPNAME", "").toString();//寄件人姓名--》配送门店名称
            if(senderName.isEmpty())
            {
                senderName = orderMap.getOrDefault("SHIPPINGSHOP", "").toString();
            }
            String senderMobile = SendShopInfoMap.getOrDefault("PHONE", "").toString();//寄件人联系电话

            JSONObject Sender = new JSONObject();
            Sender.put("ProvinceName",sendProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            Sender.put("CityName",sendCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            Sender.put("ExpAreaName",sendExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            Sender.put("Address",fiterStr(sendAddress));//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中

            if (senderMobile.startsWith("1")&&senderMobile.length()==11)
            {
                Sender.put("Mobile",senderMobile);
            }
            else
            {
                Sender.put("Tel",senderMobile);
            }
            Sender.put("Name",fiterStr(senderName));
            /**********************基本信息******************************/
            JSONObject RequestData = new JSONObject();
            RequestData.put("Receiver",Receiver);
            RequestData.put("Sender",Sender);

            RequestData.put("ShipperType",ShipperType);
            RequestData.put("OrderCode",orderCode);//商家订单编号(自定义且不可重复；同一个OrderCode订单号只能下单一次)
            RequestData.put("ExpType",1);//快递类型。默认为1,预留备用字段
            RequestData.put("PayType",3);//支付类型：3-月结
            //RequestData.put("Quantity",1);//包裹数(默认为1)

            String remark = orderMap.getOrDefault("DELMEMO", "").toString().trim();//配送备注
            if (remark.isEmpty())
            {
                remark = orderMap.getOrDefault("MEMO", "").toString().trim();
            }
            if (!remark.isEmpty())
            {
                remark = fiterStr(remark);
                if (remark.length()>60)
                {
                    remark = remark.substring(0,60);
                }

                RequestData.put("Remark",remark);
            }
            /**********************商品信息******************************/
            if(detailList!=null&&detailList.size()>0)
            {
                //单个商品名称不可大于50个字符
                //总的商品名称不可大于150个字符
                int aveNameLength = 50;
                try {
                    aveNameLength = 150/detailList.size();
                }
                catch (Exception e) {
                }
                if (aveNameLength>50)
                {
                    aveNameLength = 50;
                }
                JSONArray Commodity = new JSONArray();
                for (Map<String, Object> mapGoods : detailList)
                {
                    String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
                    pluName = fiterStr(pluName);
                    if (pluName.length()>aveNameLength)
                    {
                        pluName = pluName.substring(0,aveNameLength);
                    }
                    String pluNo = mapGoods.getOrDefault("PLUNO", "").toString();
                    if (pluNo.length()>20)
                    {
                        pluNo = pluNo.substring(0,20);
                    }
                    String qty = mapGoods.getOrDefault("QTY", "1").toString();
                    int GoodsQuantity = 1;
                    try
                    {
                        GoodsQuantity = Integer.parseInt(qty);
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
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("GoodsCode",pluNo);
                    itemObj.put("GoodsName",pluName);
                    itemObj.put("GoodsQuantity",GoodsQuantity);
                    itemObj.put("GoodsPrice",GoodsPrice);
                    Commodity.put(itemObj);
                }
                RequestData.put("Commodity",Commodity);

            }


            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            params.put("RequestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("EBusinessID", EBusinessID);
            params.put("RequestType", RequestType);//快递接口指令
            params.put("DataType", DataType);

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));

            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】发送url:"+req_ApiUrl+",请求RequestData转码前:"+requestDataJson+",请求req:"+params.toString()+logEndWithStr,logFileName);
            res = sendPost(req_ApiUrl, params,error);
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】返回res:"+res+logEndWithStr,logFileName);
        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }

    /**
     * 快递鸟取消接口(全国快递)
     * @param orderCode 传入的物流单号
     * @param setMap 配置信息
     * @param orderNo 订单单号
     * @param cancelType 取消类型
     * @param cancelMsg 取消原因描述
     * @param error
     * @return
     * @throws Exception
     */
    public String kdnOrderCancel(String orderCode, Map<String, Object> setMap, String orderNo, String cancelType,String cancelMsg,StringBuffer error) throws Exception
    {
        //1. 用于取消客户创建的订单，并实时同步返回取消结果(主动取消无203取消状态回传)
        //2. 已取件(state:104)/已揽件(state:301)状态后的订单无法取消
        String res = "";
        String RequestType = "1802";//4.4订单取消接口
        String methodName = "【全国快递】【取消接口】";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String ShipperType = setMap.get("SHIPPERTYPE").toString();//产品类型 3=2小时收;4=半日收;5=当日收
            if (cancelType==null|| cancelType.trim().isEmpty())
            {
                cancelType = "11";//11=其他(请传值具体取消原因)
                cancelMsg ="顾客不想要了";
            }
            if (cancelMsg==null|| cancelMsg.trim().isEmpty())
            {
                cancelMsg ="顾客不想要了";
            }

            JSONObject RequestData = new JSONObject();
            RequestData.put("OrderCode",orderCode);
            RequestData.put("CancelType",cancelType);
            RequestData.put("CancelMsg",cancelMsg);
            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            params.put("RequestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("EBusinessID", EBusinessID);
            params.put("RequestType", RequestType);//快递接口指令
            params.put("DataType", DataType);

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));

            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】发送url:"+req_ApiUrl+",请求RequestData转码前:"+requestDataJson+",请求req:"+params.toString()+logEndWithStr,logFileName);
            res = sendPost(req_ApiUrl, params,error);
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】返回res:"+res+logEndWithStr,logFileName);

        }
        catch (Exception e)
        {
            error.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用快递鸟API】异常:"+e.getMessage()+logEndWithStr,logFileName);
            return res;
        }
        return res;
    }

    /**
     * MD5加密
     * str 内容
     * charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String MD5(String str,String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * str 内容
     * charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str, String charset) throws UnsupportedEncodingException {
        String encoded = Base64.encode(str.getBytes(charset));
        return encoded;
    }

    @SuppressWarnings("unused")
    private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * content 内容
     * keyValue ApiKey
     * charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private  String encrypt (String content,String keyValue,String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * url 发送请求的 URL
     * params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private  String sendPost(String url, Map<String,String> params, StringBuffer error) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (param.length() > 0) {
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                    //System.out.println(entry.getKey()+":"+entry.getValue());
                }
                //System.out.println("param:"+param.toString());
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            error.append(e.getMessage());
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                error.append(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 去掉特殊字符
     * @param str
     * @return
     * @throws Exception
     */
    private String fiterStr (String str) throws Exception
    {
        try
        {
            String regEx = "['\"#&+<>%\\\\]";
            Pattern p =Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").toString();

        }
        catch (Exception e)
        {

        }
        return str;
    }
}
