package com.dsc.spos.waimai.shansong;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 闪送3.0
 */
public class SHANSONGService {

    // 订单计费
    private static final String ORDERCALCULATE = "/orderCalculate";
    // 提交订单
    private static final String ORDERPLACE = "/orderPlace";
    // 订单取消
    private static final String ABORTORDER = "/abortOrder";

    // 测试环境域名
    private static final String QA_HOST = "http://open.s.bingex.com/openapi/merchants/v5";
//    // 线上环境域名
    private static final String ONLINE_HOST = "http://open.ishansong.com/openapi/merchants/v5";

    private static final String logFileName = "shansongLog";

//    private String HOST = "http://open.s.bingex.com/openapi/merchants/v5";

    /**
     * 下单接口 （计费和下单接口合并成一个）
     *
     * @param
     * @return
     */
    public String ssOrderCreate(Map<String, Object> setMap, Map<String, Object> orderMap, Map<String, Object> shippingShopInfo) throws Exception {

        String orderNo = orderMap.get("ORDERNO").toString();
        try {

            String secret = setMap.get("APPSECRET").toString();//App_secret（App-密钥）
            String key = setMap.get("APPSIGNKEY").toString();//Client ID（App-key）
            String shopcode = setMap.get("SHOPCODE").toString();//Shop ID（商户ID）
            /************店铺ID，使用闪送平台物流门店ID********************/
            String senderShop = setMap.getOrDefault("DELIVERYSHOPID", "").toString();//闪送物流门店ID

            String senderName = orderMap.getOrDefault("SHIPPINGSHOPNAME", "").toString();//寄件人姓名--》配送门店名称
            if (senderName.isEmpty()) {
                senderName = orderMap.getOrDefault("SHIPPINGSHOP", "").toString();
            }

            String senderProvinceName = shippingShopInfo.getOrDefault("PROVINCE", "").toString();//寄件人省名称
            String senderCityName = shippingShopInfo.getOrDefault("CITY", "").toString();//寄件人市名称

            String senderCountyName = shippingShopInfo.getOrDefault("COUNTY", "").toString();//寄件人区县名称
            String senderAddress = shippingShopInfo.getOrDefault("ADDRESS", "").toString();//寄件人详细地址
            String senderMobile = shippingShopInfo.getOrDefault("PHONE", "").toString();//寄件人联系电话
            String senderLatitude = shippingShopInfo.getOrDefault("LATITUDE", "").toString();//寄件人维度
            String senderLongitude = shippingShopInfo.getOrDefault("LONGITUDE", "").toString();//寄件人经度
            String cityName = shippingShopInfo.getOrDefault("CITY", "").toString();//取配送门店
            //【ID1038573】【红房子3.0】顺丰同城要求离配送时间大于等于12小时的订单先保留在鼎捷系统不自动发单，小于12小时的订单才自动发单
            String delaytime_span = setMap.getOrDefault("DELAYTIME_SPAN", "0").toString();//延时发物流时长(分)(顺丰同城)(小于60不起作用)
            int delaytime_span_i = 60;//最少60分钟
            try
            {
                delaytime_span_i = Integer.parseInt(delaytime_span);
            }
            catch (Exception e)
            {

            }

            if (delaytime_span_i<60)
            {
                delaytime_span_i = 60;
            }
            //【ID1038573】【红房子3.0】顺丰同城要求离配送时间大于等于12小时的订单先保留在鼎捷系统不自动发单，小于12小时的订单才自动发单
            String isDelay = setMap.getOrDefault("ISDELAY", "N").toString();//是否延时发物流(顺丰同城)(如果设置Y，都是发即时单)流)


            /***********************收件人信息********************************/
            String receiverName = orderMap.getOrDefault("GETMAN", "").toString();  // 收件人姓名
            if (receiverName.isEmpty())
            {
                receiverName = orderMap.getOrDefault("CONTMAN", "").toString();
            }

            String receiverAddress = orderMap.getOrDefault("ADDRESS", "").toString(); // 地址
            String receiverMobile = orderMap.getOrDefault("GETMANTEL", "").toString(); // 联系电话
            if (receiverMobile.isEmpty())
            {
                receiverMobile = orderMap.getOrDefault("CONTTEL", "").toString();
            }
            String remark = orderMap.getOrDefault("DELMEMO", "").toString();//配送备注
            String lng = orderMap.getOrDefault("LONGITUDE", "").toString();
            String lat = orderMap.getOrDefault("LATITUDE", "").toString();
//            String lat = "36.13344608939148";
//            String lng = "113.13949262451169";
            String PROVINCE = orderMap.getOrDefault("PROVINCE", "").toString();
            String CITY = orderMap.getOrDefault("CITY", "").toString();
            String COUNTY = orderMap.getOrDefault("COUNTY", "").toString();
            String eId = orderMap.getOrDefault("EID", "").toString();

            String loadDocType = orderMap.getOrDefault("LOADDOCTYPE", "").toString();//客户业务类型；可以对客户订单进行业务或渠道区分
            if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
            {
                //pos地址只传了一个明细
                receiverAddress = PROVINCE + CITY + COUNTY + receiverAddress;
            }

            SsOrderCreateEntity ssOrderCreateEntity = new SsOrderCreateEntity();
            ssOrderCreateEntity.setCityName(cityName); // 城市名称
            ssOrderCreateEntity.setAppointType(0); // 0立即单，1预约单

            String pickupTime = ""; // 配送时间
            String shipDate = orderMap.get("SHIPDATE").toString();
            String sdtime = orderMap.get("SHIPSTARTTIME").toString();//SHIPENDTIME
            sdtime = sdtime.replace("-", "");
            if (sdtime.isEmpty())
            {
                sdtime = new SimpleDateFormat("HHmmss").format(new Date());
            }
            pickupTime = shipDate + sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

            long longcur = System.currentTimeMillis();
            Date dateSta = new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
            long longsta = dateSta.getTime();
            // 差别到分钟
            long diff = (longsta - longcur) / (1000 * 60);
            if ("Y".equals(isDelay))
            {
                //如果设置的是延迟发单，都是及时单，不存在预约单
                if (diff>=delaytime_span_i)
                {
                    String shipdatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateSta);
                    HelpTools.writelog_fileName("【顺丰同城】当前物流配置参数isDelay="+isDelay+",延时发物流时长delaytime_span="+delaytime_span+",订单配送时间="+shipdatetime+",无需发物流，订单号orderNo="+orderNo,logFileName);
                    JSONObject resJson = new JSONObject();
                    resJson.put("status","fffff");
                    resJson.put("msg","当前设置延时发物流，无需发单");
                    return resJson.toJSONString();
                }

            }
            else
            {
                // 判断是否是预订单，预订单超过两个小时则为预订单 ,预约取件时间,只支持两个小时以后两天以内
                if (diff >= 120) {
                    ssOrderCreateEntity.setAppointType(1);
                    ssOrderCreateEntity.setAppointmentDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateSta)); // 预约时间
                }
            }


            ssOrderCreateEntity.setStoreId(senderShop); // 店铺ID
            /***********************寄件人信息********************************/
            SsOrderCreateEntity.Sender sender = ssOrderCreateEntity.new Sender();
            sender.setFromAddress(senderAddress); // 寄件地址
            sender.setFromAddressDetail(senderProvinceName + senderCityName + senderCountyName + senderAddress); // 寄件详细地址
            sender.setFromSenderName(senderName); // 寄件人姓名
            sender.setFromMobile(senderMobile); // 寄件人联系电话
            double[] senderDoubles = OrderUtil.gaoDeToBaidu(Double.parseDouble(senderLongitude), Double.parseDouble(senderLatitude));
//            double[] senderDoubles = OrderUtil.gaoDeToBaidu(Double.parseDouble("113.12796955586767"), Double.parseDouble("36.1975030803593"));
            sender.setFromLongitude(senderDoubles[0] + "");// 寄件经度
            sender.setFromLatitude(senderDoubles[1] + ""); // 寄件纬度
            ssOrderCreateEntity.setSender(sender);

            /***********************收件人信息********************************/
            SsOrderCreateEntity.receiver receiver = ssOrderCreateEntity.new receiver();
            receiver.setOrderNo(orderNo); // 第三方平台流水号
            receiver.setToAddress(receiverAddress); // 收件人地址
            if (Double.parseDouble(lng)<1||Double.parseDouble(lat)<1)
            {
                //这里，需要反查经纬度了。。。
                receiver.setToLongitude("");// 收件经度
                receiver.setToLatitude(""); // 收件纬度
                String location = this.getGeocode(eId,receiverAddress,CITY);
                if (!location.isEmpty())
                {
                    String[] listjinwei=location.split(",");
                    lng = listjinwei[0];
                    lat = listjinwei[1];
                    double[] receiverDoubles = OrderUtil.gaoDeToBaidu(Double.parseDouble(lng), Double.parseDouble(lat));
                    receiver.setToLongitude(receiverDoubles[0] + "");// 收件经度
                    receiver.setToLatitude(receiverDoubles[1] + ""); // 收件纬度
                }
            }
            else
            {
                double[] receiverDoubles = OrderUtil.gaoDeToBaidu(Double.parseDouble(lng), Double.parseDouble(lat));
                receiver.setToLongitude(receiverDoubles[0] + "");// 收件经度
                receiver.setToLatitude(receiverDoubles[1] + ""); // 收件纬度
            }

            receiver.setToReceiverName(receiverName); // 收件人姓名
            receiver.setToMobile(receiverMobile); // 收件联系人
            receiver.setGoodType(10); // 物品类型
            receiver.setWeight(1); // 物品重量
            receiver.setRemarks(remark); // 备注
            ssOrderCreateEntity.setReceiverList(new ArrayList<>());
            ssOrderCreateEntity.getReceiverList().add(receiver);

            JSONObject param = (JSONObject) JSONObject.toJSON(ssOrderCreateEntity);
            String reqJsonData = param.toJSONString();

            ShanSongEntity shanSongEntity = new ShanSongEntity();
            shanSongEntity.setClientId(key).setData(reqJsonData).setShopId(shopcode).setTimestamp(longcur);
            //计算签名
            String sign = SHANSONGUtil.bytesToMD5((secret+shanSongEntity.toString()).getBytes("UTF-8"));
            shanSongEntity.setSign(sign);
            Map<String, Object> params = shanSongEntity.toMap();
            String url = ONLINE_HOST + ORDERCALCULATE;
            //请求接口获取的结果
            HelpTools.writelog_fileName("【调用闪送】订单计费接口" + ORDERCALCULATE + ",请求地址为" + url + ",请求参数" + params, logFileName);
            String res = SHANSONGUtil.sendPost(url, params);
            HelpTools.writelog_fileName("【调用闪送】订单计费接口" + ORDERCALCULATE + "结束,返回结果" + res, logFileName);

            JSONObject json1 = JSONObject.parseObject(res);
            JSONObject data_res = json1.getJSONObject("data");
            String status = json1.getString("status").toString();
//            String msg = json1.getString("msg").toString();

            if ("200".equals(status)) {
                // *************************** 订单下单 *****************************
                String orderNumber = "";
                if (data_res.containsKey("orderNumber")) {
                    orderNumber = data_res.get("orderNumber").toString();
                }
                String data = "{\"issOrderNo\":\"" + orderNumber + "\"}";
                shanSongEntity.setClientId(key).setData(data).setShopId(shopcode).setTimestamp(longcur);
                //计算签名
                sign = SHANSONGUtil.bytesToMD5((secret+shanSongEntity.toString()).getBytes("UTF-8"));
                shanSongEntity.setSign(sign);
                params = shanSongEntity.toMap();
                url = ONLINE_HOST + ORDERPLACE;
                //请求接口获取的结果
                HelpTools.writelog_fileName("【调用闪送】提交订单接口" + ORDERPLACE + ",请求地址为" + url + ",请求参数" + params, logFileName);
                res = SHANSONGUtil.sendPost(url, params);
                HelpTools.writelog_fileName("【调用闪送】提交订单接口" + ORDERPLACE + "结束,返回结果" + res, logFileName);
            }
            return res;
        } catch (Exception e) {
            // TODO: handle exception
            HelpTools.writelog_fileName("【调用闪送跑腿】下单异常:" + e.getMessage() + ",订单号orderNo=" + orderNo, logFileName);
            return "";
        }

    }

    /**
     * 订单退单
     *
     * @return
     */
    public String ssOrderCancel(Map<String, Object> setMap, Map<String, Object> orderMap) throws IOException {
        String orderNo = orderMap.get("ORDERNO").toString();
        try {
            String secret = setMap.get("APPSECRET").toString();//"u2Z1F7Fh";//客户秘钥
            String key = setMap.get("APPSIGNKEY").toString();//dev_id 	App-key
            String shopcode = setMap.get("SHOPCODE").toString();//"oDZKFb";//客户标识
            long longcur = System.currentTimeMillis();
            /************店铺ID，使用闪送平台物流门店ID********************/
//            String senderShop = setMap.getOrDefault("DELIVERYSHOPID", "").toString();//闪送物流门店ID

            String deliveryno = orderMap.getOrDefault("DELIVERYNO", "").toString();
            String data = "{\"issOrderNo\":\"" + deliveryno + "\"}";
            ShanSongEntity shanSongEntity = new ShanSongEntity();
            shanSongEntity.setClientId(key).setData(data).setShopId(shopcode).setTimestamp(longcur);
            //计算签名
            String sign = SHANSONGUtil.bytesToMD5((secret+shanSongEntity.toString()).getBytes("UTF-8"));
            shanSongEntity.setSign(sign);
            Map<String, Object> params = shanSongEntity.toMap();
            String url = ONLINE_HOST + ABORTORDER;
            //请求接口获取的结果
            HelpTools.writelog_fileName("【调用闪送】订单取消接口" + ABORTORDER + ",请求地址为" + url + ",请求参数" + params, logFileName);
            String res = SHANSONGUtil.sendPost(url, params);
            HelpTools.writelog_fileName("【调用闪送】订单取消接口" + ABORTORDER + "结束,返回结果" + res, logFileName);

            return res;
        } catch (Exception e) {
            HelpTools.writelog_fileName("【调用闪送跑腿】取消订单异常:" + e.getMessage() + ",订单号orderNo=" + orderNo, logFileName);
            return "";
        }

    }

    public static JSONObject queryAllStores(String clientId,String appSecret,String shopId,String isTest) throws Exception
    {
        JSONObject jobject= new JSONObject();
        jobject.put("status","-1");//状态码为200代表正常，其它代表不正常。
        jobject.put("msg","调用接口失败");
        jobject.put("data",new JSONObject());
        try
        {
            String url = ONLINE_HOST;
            if ("Y".equals(isTest))
            {
                url = QA_HOST;
            }
            String timestamp = System.currentTimeMillis()+"";
            //将参数按照（App_secret的值+"clientId”+App ID的值+"data”+data的值+shopId+"商户ID的值"+"timestamp”+timestamp的值）顺序拼接
            //参数对应的值不存在，参数和值都不进行拼接，参数使用utf-8编码。
            StringBuffer sb = new StringBuffer(appSecret);
            sb.append("clientId").append(clientId);
            sb.append("shopId").append(shopId);
            sb.append("timestamp").append(timestamp);

            String sign = SHANSONGUtil.bytesToMD5(sb.toString().getBytes("UTF-8"));
            Map<String,Object> map_params = new HashMap<String,Object>();
            map_params.put("clientId",clientId);
            map_params.put("shopId",shopId);
            map_params.put("timestamp",timestamp);
            map_params.put("sign",sign);

            String url_api = url+"/queryAllStores";
            HelpTools.writelog_fileName("【调用闪送】查询商户店铺接口,请求地址为" + url_api + ",请求参数" + map_params, logFileName);
            String res = SHANSONGUtil.sendPost(url_api, map_params);
            HelpTools.writelog_fileName("【调用闪送】查询商户店铺接口,返回结果" + res, logFileName);
            if (res==null||res.isEmpty())
            {
                jobject.put("msg","闪送查询商户店铺接口返回为空");
                return jobject;
            }
            JSONObject res_orderPlace = JSONObject.parseObject(res);
            return res_orderPlace;
        }
        catch (Exception e)
        {
            jobject.put("msg","调用闪送查询商户店铺接口异常:"+e.getMessage());
        }
        return jobject;
    }


    public static void main(String[] args) throws UnsupportedEncodingException, ParseException {
//        SHANSONGService shansong = new SHANSONGService(true);
//////        String orderNumber = shansong.OrderCalculate();
//////        String s = shansong.orderPlace(orderNumber);
////        String s = shansong.orderPlace(null, null);
//        String s = shansong.generateKOrderCreate(null, null, null);


//        String str = "{\"queryCondition\":[{\"groupId\":\"102\",\"goodsQty\":\"all\"},{\"groupId\":\"108\",\"goodsQty\":\"all\"},{\"groupId\":\"101\",\"goodsQty\":\"all\"},{\"groupId\":\"103\",\"goodsQty\":\"all\"},{\"groupId\":\"104\",\"goodsQty\":\"all\"},{\"groupId\":\"106\",\"goodsQty\":\"all\"},{\"groupId\":\"107\",\"goodsQty\":\"all\"},{\"groupId\":\"110\",\"goodsQty\":\"all\"}],\"shopId\":\"XHB004\",\"memberId\":\"6070513499\",\"orderType\":\"2\"}";
//        String str = "{\"memberId\":\"L062036\",\"shopId\":\"2076\",\"status\":2,\"couponCode\":\"\"}";
//        String s = PosPub.encodeMD5(str + "wx88fd21cf04527ff6");
//        System.out.println(s);、

//        long longcur = System.currentTimeMillis();
//        Date dateSta = new SimpleDateFormat("yyyyMMddHHmmss").parse("20220126113030");
//        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateSta);
//        System.out.println(format);
//        long longsta = dateSta.getTime();
//        long diff = (longsta - longcur) / (1000 * 60);
//        System.out.println(diff);
//        String clientId = "sssHhHPkzCNWG1sFs";
//        String appSecret = "qBfAr1fAAFRw2Jat4i4yUaqE3Nh3NSw0";
//        String shopId = "20000000000001715";

        String clientId = "ss2UTBmfsHDLBNmQ5";
        String appSecret = "yE0LFtu4jhbSEuxPXGQkoG0BzSlKsDQG";
        String shopId = "20000000000002589";

//        String shopId = "20000000000003581";
        Long timestamp = System.currentTimeMillis();
//        ShanSongEntity shanSongEntity = new ShanSongEntity();
//        String data = "{\"cityName\":\"上海市\",\"appointType\":\"1\",\"appointmentDate\":\"2022-01-15 20:00\",\"storeId\":\"\",\"travelWay\":\"\",\"deliveryType\":1,\"sender\":{\"fromAddress\":\"上海市静安区\",\"fromAddressDetail\":\"江场路1377弄7号\",\"fromSenderName\":\"王\",\"fromMobile\":\"17629566036\",\"fromLatitude\":\"31.311685211299622\",\"fromLongitude\":\"121.4661063042455\"},\"receiverList\":{\"orderNo\":\"123213221233\",\"toAddress\":\"上海市静安区\",\"toAddressDetail\":\"长临路380弄\",\"toLatitude\":\"31.33028261434527\",\"toLongitude\":\"121.449146938379\",\"toReceiverName\":\"王子瑜\",\"toMobile\":\"17629566036\",\"goodType\":10,\"weight\":1,\"remarks\":\"\",\"insurance\":\"\",\"insuranceProId\":\"\",\"additionFee\":\"\",\"orderingSourceType\":\"\",\"orderingSourceNo\":\"\"}}";
////        shanSongEntity.setClientId(clientId).setData(data).setShopId(shopId).setTimestamp(timestamp);
////        //计算签名
////        String sign = SHANSONGUtil.bytesToMD5(shanSongEntity.toString().getBytes());
//        shanSongEntity.setSign(sign);
//        Map<String, Object> params = shanSongEntity.toMap();

        StringBuffer sb = new StringBuffer();
        sb.append(appSecret).append("clientId").append(clientId)
//                .append("data").append("{\"cityName\":\"北京市\",\"sender\":{\"fromAddress\":\"东升科技国际园\",\"fromAddressDetail\":\"2层202\",\"fromSenderName\":\"小闪\",\"fromMobile\":\"13800000000\",\"fromLatitude\":\"40.047858\",\"fromLongitude\":\"116.378424\"},\"receiverList\":[{\"orderNo\":\"C1119A000013053981\",\"toAddress\":\"永泰庄地铁站\",\"toAddressDetail\":\"1楼\",\"toLatitude\":\"40.043612\",\"toLongitude\":\"116.361199\",\"toReceiverName\":\"小送\",\"toMobile\":\"13545880179\",\"goodType\":5,\"weight\":2,\"remarks\":\"不要加蒜 取餐号：P10222菜品\",\"additionFee\":500,\"insurance\":200,\"insuranceProId\":\"SS_baofei_001\",\"orderingSourceType\":4,\"orderingSourceNo\":\"MT001\"}],\"appointType\":0,\"appointmentDate\":\"\",\"storeId\":392907,\"travelWay\":0,\"deliveryType\":1}")
                .append("shopId").append(shopId)
                .append("timestamp").append(timestamp);
        String sign = SHANSONGUtil.bytesToMD5(sb.toString().getBytes("UTF-8"));
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        params.put("shopId", shopId);
//        params.put("data", "{\"cityName\":\"北京市\",\"sender\":{\"fromAddress\":\"东升科技国际园\",\"fromAddressDetail\":\"2层202\",\"fromSenderName\":\"小闪\",\"fromMobile\":\"13800000000\",\"fromLatitude\":\"40.047858\",\"fromLongitude\":\"116.378424\"},\"receiverList\":[{\"orderNo\":\"C1119A000013053981\",\"toAddress\":\"永泰庄地铁站\",\"toAddressDetail\":\"1楼\",\"toLatitude\":\"40.043612\",\"toLongitude\":\"116.361199\",\"toReceiverName\":\"小送\",\"toMobile\":\"13545880179\",\"goodType\":5,\"weight\":2,\"remarks\":\"不要加蒜 取餐号：P10222菜品\",\"additionFee\":500,\"insurance\":200,\"insuranceProId\":\"SS_baofei_001\",\"orderingSourceType\":4,\"orderingSourceNo\":\"MT001\"}],\"appointType\":0,\"appointmentDate\":\"\",\"storeId\":392907,\"travelWay\":0,\"deliveryType\":1}");
        params.put("sign", sign);
        params.put("timestamp", timestamp);

        String url = "http://open.s.bingex.com/openapi/merchants/v5/queryAllStores";
        String res = SHANSONGUtil.sendPost(url, params);
        System.out.println(res);
    }

    /**
     * 根据地址获取经纬度(经度，纬度)
     * @param address 详细地址
     * @param city 城市名称
     * @return 返回格式:经度，纬度
     * @throws Exception
     */
    public String getGeocode(String eId,String address, String city) throws Exception {
        String location = "";
        try {
            address = URLEncoder.encode(address, "utf-8" );
            String gaoDeKey = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AMAPWEBKEY");
            //调用高德地图查找经纬度
            String url="https://restapi.amap.com/v3/geocode/geo?address="+address+"&output=JSON&key="+gaoDeKey;
           /* if (city!=null&&!city.trim().isEmpty())
            {
                city = URLEncoder.encode(city, "utf-8" );
                url += "&city=" + city;
            }*/
            HelpTools.writelog_fileName("【调用高德】查询配送地址对应经纬度,请求req:" + url, logFileName);
            String responseStr= OrderUtil.Sendcom("",url,"GET");
            HelpTools.writelog_fileName("【调用高德】查询配送地址对应经纬度,返回res:" + responseStr, logFileName);
            //解析返回的内容
            try
            {
                ParseJson pj = new ParseJson();
                GaoDeGeoModel curreginfo=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
                pj=null;

                if(curreginfo.getStatus().equals("1"))
                {
                    //默认是第一条位置的经纬度
                    location = curreginfo.getGeocodes().get(0).getLocation();
                }
            }
            catch(Exception ex)
            {

            }

        } catch (Exception e) {

        }
        return location;
    }

}






