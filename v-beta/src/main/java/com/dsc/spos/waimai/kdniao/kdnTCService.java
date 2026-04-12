package com.dsc.spos.waimai.kdniao;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.kdniao.query.RequestData;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class kdnTCService {

    private static String test_ApiUrl = "http://183.62.170.46:38092/tcpapi/orderdist";//测试地址

    private static String product_ApiUrl = "https://api.kdniao.com/tcpapi/orderdist";//正式地址

    private static String DataType = "2";//请求、返回数据类型：2表示json格式；

    private static String logFileName = "kdniaoLog";

    private String req_ApiUrl = "https://api.kdniao.com/tcpapi/orderdist";

    public kdnTCService()
    {
        req_ApiUrl = product_ApiUrl;
    }

    public kdnTCService(boolean isTest)
    {
        req_ApiUrl = product_ApiUrl;
        if (isTest)
        {
            req_ApiUrl = test_ApiUrl;
        }

    }

    /**
     * 快递鸟门店创建
     * @param setMap
     * @param requestData
     * @param error
     * @return
     * @throws Exception
     */
    public String kdnStoreCreate(Map<String, Object> setMap,kdnTCStoreReq requestData,StringBuffer error)throws Exception
    {
        String res = "";
        String interfaceType = "2211";//4.1.9创建门店接口
        String methodName = "【同城快递】【创建门店接口】";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//customerCode 客户编码
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            // pj = new ParseJson();
            //String requestDataJson = pj.beanToJson(requestData);
            requestData.setAddress(fiterStr(requestData.getAddress()));
            String requestDataJson = com.alibaba.fastjson.JSON.toJSONString(requestData);
            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            //params.put("requestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("requestData", requestDataJson);
            params.put("customerCode", EBusinessID);
            params.put("interfaceType", interfaceType);//快递接口指令
            //params.put("DataType", DataType);

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("dataSign", urlEncoder(dataSign, "UTF-8"));

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
     * 快递鸟门店创建
     * @param setMap
     * @param requestData
     * @param error
     * @return
     * @throws Exception
     */
    public String kdnStoreUpdate(Map<String, Object> setMap,kdnTCStoreReq requestData,StringBuffer error)throws Exception
    {
        String res = "";
        String interfaceType = "2212";//4.1.10修改门店信息接口
        String methodName = "【同城快递】【修改门店接口】";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//customerCode 客户编码
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            // pj = new ParseJson();
            //String requestDataJson = pj.beanToJson(requestData);
            requestData.setAddress(fiterStr(requestData.getAddress()));
            String requestDataJson = com.alibaba.fastjson.JSON.toJSONString(requestData);
            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            //params.put("requestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("requestData", requestDataJson);
            params.put("customerCode", EBusinessID);
            params.put("interfaceType", interfaceType);//快递接口指令
            //params.put("DataType", DataType);

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("dataSign", urlEncoder(dataSign, "UTF-8"));

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
     * 快递鸟即时配下单接口(同城配送)
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
        String interfaceType = "2202";//4.1.2创建订单接口
        String methodName = "【同城快递】【下单接口】";
        String orderNo = "";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;

            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String intraCode = setMap.get("INSTANTCONFIGTYPE").toString();//即时配编码
            String goodsType = setMap.get("PRODUCTTYPE").toString();//物品类型
            String categoryThree = setMap.get("LEVELCATEGORY").toString();//商品三级类目

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

            String order_lng = orderMap.getOrDefault("LONGITUDE", "").toString();
            String order_lat = orderMap.getOrDefault("LATITUDE", "").toString();

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

            JSONObject receiver = new JSONObject();
            receiver.put("provinceName",orderProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            receiver.put("cityName",orderCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            receiver.put("areaName",orderExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            receiver.put("address",fiterStr(orderAddress));//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中
            if (recipientMobile.startsWith("1")&&recipientMobile.length()==11)
            {
                receiver.put("mobile",recipientMobile);
            }
            else
            {
                receiver.put("phone",recipientMobile);
            }
            receiver.put("name",fiterStr(recipientName));
            receiver.put("longitude",order_lng);
            receiver.put("latitude",order_lat);

            /**********************发件人地址信息******************************/
            String sendProvinceName = SendShopInfoMap.getOrDefault("PROVINCE","").toString();
            String sendCityName = SendShopInfoMap.getOrDefault("CITY","").toString();
            String sendExpAreaName = SendShopInfoMap.getOrDefault("COUNTY","").toString();
            String sendAddress = SendShopInfoMap.getOrDefault("ADDRESS","").toString();
            String send_lng = SendShopInfoMap.getOrDefault("LONGITUDE", "").toString();//前面已经处理过了
            String send_lat = SendShopInfoMap.getOrDefault("LATITUDE", "").toString();
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

            JSONObject sender = new JSONObject();
            sender.put("provinceName",sendProvinceName);//收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)支持字符长度为20个以内，不支持数字与字母
            sender.put("cityName",sendCityName);//收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)支持字符长度为20个以内，不支持数字与字母
            sender.put("areaName",sendExpAreaName);//收件区/县(如福田区，不要缺少“区”或“县”)
            sender.put("address",fiterStr(sendAddress));//收件人详细地址(填写具体的街道、门牌号，不要将省市区加入其中
            if (senderMobile.startsWith("1")&&senderMobile.length()==11)
            {
                sender.put("mobile",senderMobile);
            }
            else
            {
                sender.put("phone",senderMobile);
            }
            sender.put("name",fiterStr(senderName));
            sender.put("longitude",send_lng);
            sender.put("latitude",send_lat);
            /**********************基本信息******************************/
            JSONObject RequestData = new JSONObject();
            RequestData.put("receiver",receiver);
            RequestData.put("sender",sender);

            RequestData.put("storeBusinessType",1);//门店业务类型 1：商家门店 2：个人即时配
            RequestData.put("storeCode",SendShopInfoMap.getOrDefault("ORGANIZATIONNO",""));
            RequestData.put("orderCode",orderCode);//商家订单编号(自定义且不可重复；同一个OrderCode订单号只能下单一次)
            RequestData.put("productTypeCode","B");//产品类型编码，默认:B
            RequestData.put("intraCode",intraCode);//即时配编码
            RequestData.put("travelWay",0);//交通方式，默认为 0：未指定
            RequestData.put("deliveryType",1);//下单类型 1：帮我送 2：帮我取  默认1
            RequestData.put("lbsType",2);//坐标类型 1:百度地图 、2:高德地图 、3:腾讯地图
            RequestData.put("goodsType",goodsType);//物品类型
            RequestData.put("totalWeight",1);//物品总重量单位kg，大于等于1
            RequestData.put("totalVolume",1);//物品总体积单位立方厘米
            RequestData.put("orderName",senderName);//下单人 ===发件人
            if (senderMobile.startsWith("1")&&senderMobile.length()==11)
            {
                RequestData.put("orderMobile",senderMobile);//下单人手机号
            }
            else
            {
                RequestData.put("orderPhone",senderMobile);//下单人座机号
            }
            String tot_amt = orderMap.getOrDefault("TOT_AMT", "0").toString();
            double declaredValue = new BigDecimal(tot_amt).setScale(2,RoundingMode.HALF_UP).doubleValue();
            if (declaredValue<1)
            {
                declaredValue =1;
            }
            RequestData.put("declaredValue",declaredValue);//物品声明价值单位：元。去尾法，保留两位小数。所有物品总计声明价值 必须大于等于1
            RequestData.put("insured",0);//暂不支持保价，默认传0
            RequestData.put("insuranceFee",0);//丢破损保障费用 单位：元。暂不支持，默认传0
            String remark = orderMap.getOrDefault("DELMEMO", "").toString().trim();//配送备注
            if (remark.isEmpty())
            {
                remark = orderMap.getOrDefault("MEMO", "").toString().trim();
            }
            if (!remark.isEmpty())
            {
                remark = fiterStr(remark);
                if (remark.length()>20)
                {
                    remark = remark.substring(0,20);
                }

                RequestData.put("remark",remark);
            }
            int goodsNum = 0;
            /**********************商品信息******************************/
            if(detailList!=null&&detailList.size()>0)
            {
                //单个商品名称不可大于50个字符
                //总的商品名称不可大于150个字符
                int aveNameLength = 20;
                JSONArray Commodity = new JSONArray();
                for (Map<String, Object> mapGoods : detailList)
                {
                    String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
                    pluName = fiterStr(pluName);
                    if (pluName.length()>aveNameLength)
                    {
                        pluName = pluName.substring(0,aveNameLength);
                    }
                    String unitName = mapGoods.getOrDefault("SUNITNAME", "").toString();
                    if (unitName.isEmpty())
                    {
                        unitName = "件";
                    }
                    if (unitName.length()>10)
                    {
                        unitName = unitName.substring(0,10);
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
                    itemObj.put("categoryThree",categoryThree);
                    itemObj.put("unit",unitName);
                    itemObj.put("goodsName",pluName);
                    itemObj.put("quantity",GoodsQuantity);
                    itemObj.put("goodsPrice",GoodsPrice);
                    goodsNum += GoodsQuantity;
                    Commodity.put(itemObj);
                }
                RequestData.put("goodsList",Commodity);

            }
            RequestData.put("goodsNum",goodsNum);//总数量
            int appointmentType = 0;//预约类型0：立即单 1：预约单
            RequestData.put("appointmentType",appointmentType);

            /********************配送时间**************************/
            String pickupTime = "";
            String shipDate = orderMap.get("SHIPDATE").toString();
            String sdtime = orderMap.get("SHIPSTARTTIME").toString();//SHIPENDTIME
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

            long longcur = System.currentTimeMillis();//当前时间
            Date dateSta = new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
            long longsta = dateSta.getTime();//用户期望送达时间
            // 差别到分钟
            long diff = (longsta - longcur) / (1000 * 60);
            if (diff>=120)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateSta);
                //设置秒为0
                cal.set(Calendar.SECOND,00);
                //计算分能否被10整除，向上取整,16:04-->16:10,16:58-->17:00
                int singleMin=cal.get(Calendar.MINUTE)%10;
                if (singleMin!=0)
                {
                    cal.add(Calendar.MINUTE,10-singleMin);
                }
                //预约单必填预约上门时间格式：
                //YYYY-MM-DD  hh:mm:ss
                //说明：当前时间+2小时，10分钟间隔向上取整。
                String appointmentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                appointmentType = 1;//预约类型0：立即单 1：预约单
                RequestData.put("appointmentType",appointmentType);
                RequestData.put("appointmentTime",appointmentTime);
            }


            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            //params.put("requestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("requestData", requestDataJson);
            params.put("customerCode", EBusinessID);
            params.put("interfaceType", interfaceType);//快递接口指令

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("dataSign", urlEncoder(dataSign, "UTF-8"));

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
     * 快递鸟即时配取消接口(同城配送)
     * @param orderCode 传入的物流单号
     * @param setMap 配置信息
     * @param orderNo 订单单号
     * @param cancelCode 取消原因
     * @param cancelReason 取消原因描述
     * @param error
     * @return
     * @throws Exception
     */
    public String kdnOrderCancel(String orderCode, Map<String, Object> setMap, String orderNo, String cancelCode,String cancelReason,StringBuffer error) throws Exception
    {
        //1. 用于取消客户创建的订单，并实时同步返回取消结果(主动取消无203取消状态回传)
        //2. 已取件(state:104)/已揽件(state:301)状态后的订单无法取消
        String res = "";
        String interfaceType = "2204";//4.4订单取消接口
        String methodName = "【同城快递】【取消接口】";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key

            String cancelType = "3";//取消发起人 1：下单人  2：平台  3：商家
            if (cancelCode==null|| cancelCode.trim().isEmpty())
            {
                cancelCode = "80";//80=其他(请传值具体取消原因)
                cancelReason ="顾客不想要了";
            }
            if (cancelReason==null|| cancelReason.trim().isEmpty())
            {
                cancelReason ="顾客不想要了";
            }

            JSONObject RequestData = new JSONObject();
            RequestData.put("kdnOrderCode",orderCode);
            RequestData.put("cancelType",cancelType);
            RequestData.put("cancelCode",cancelCode);
            RequestData.put("cancelReason",cancelReason);
            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            //params.put("requestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("requestData", requestDataJson);
            params.put("customerCode", EBusinessID);
            params.put("interfaceType", interfaceType);//快递接口指令

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("dataSign", urlEncoder(dataSign, "UTF-8"));

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
     * 业务员轨迹查询接口
     * @param orderCode
     * @param setMap
     * @param orderNo
     * @param error
     * @return
     * @throws Exception
     */
    public String kdnGetRiderPosition(String orderCode, Map<String, Object> setMap, String orderNo, StringBuffer error) throws Exception
    {
        String res = "";
        String interfaceType = "2206";//4.1.5业务员轨迹查询接口
        String methodName = "【同城快递】【轨迹查询接口】";
        String logEndWithStr = "";
        try
        {
            String reqId = UUID.randomUUID().toString().replace("-","");
            methodName = reqId + methodName;
            logEndWithStr = ",订单号:"+orderNo+",物流订单号:"+orderCode;
            String EBusinessID = setMap.get("APPSIGNKEY").toString();//用户ID
            String ApiKey = setMap.get("APPSECRET").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            JSONObject RequestData = new JSONObject();
            RequestData.put("kdnOrderCode",orderCode);

            String requestDataJson = RequestData.toString();

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            //params.put("requestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("requestData", requestDataJson);
            params.put("customerCode", EBusinessID);
            params.put("interfaceType", interfaceType);//快递接口指令

            String dataSign = encrypt(requestDataJson, ApiKey, "UTF-8");
            params.put("dataSign", urlEncoder(dataSign, "UTF-8"));

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
