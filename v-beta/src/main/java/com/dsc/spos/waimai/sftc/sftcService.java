package com.dsc.spos.waimai.sftc;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;

public class sftcService
{
    //订单创建
    private static final String createorder = "createorder";
    //订单取消
    private static final String cancelorder = "cancelorder";

    private static final String logFileName = "sftcLog";

    private  String HOST = "https://openic.sf-express.com/open/api/external/";

    public String sftcOrderCreate(Map<String, Object> setMap, Map<String, Object> orderMap, Map<String, Object> SendShopInfoMap, List<Map<String, Object>> detailList) throws Exception
    {
        String method = "createorder";
        String orderNo = orderMap.get("ORDERNO").toString();
        try
        {
            String secret = setMap.get("APPSECRET").toString();//"u2Z1F7Fh";//客户秘钥
            String key = setMap.get("APPSIGNKEY").toString();//dev_id 	同城开发者ID
            String product_type = setMap.getOrDefault("PRODUCTTYPE","").toString();
            long dev_id = Long.parseLong(key);
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

            /************店铺ID，直接用接入方店铺ID********************/
            String senderShop = orderMap.getOrDefault("SHIPPINGSHOP", "").toString();//配送门店

            /***********************收件人信息********************************/
            String recipientName = orderMap.getOrDefault("GETMAN", "").toString();
            if (recipientName.isEmpty())
            {
                recipientName = orderMap.getOrDefault("CONTMAN", "").toString();
            }
            String recipientProvinceName = orderMap.getOrDefault("PROVINCE", "").toString();
            String recipientCityName = orderMap.getOrDefault("CITY", "").toString();
            String recipientCountyName = orderMap.getOrDefault("COUNTY", "").toString();
            String recipientAddress = orderMap.getOrDefault("ADDRESS", "").toString();
            String loadDocType = orderMap.getOrDefault("LOADDOCTYPE", "").toString();//客户业务类型；可以对客户订单进行业务或渠道区分
            if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
            {
                //pos地址只传了一个明细
                recipientAddress = recipientProvinceName + recipientCityName + recipientCountyName + recipientAddress;
            }
            String eId = orderMap.getOrDefault("EID", "").toString();
            String recipientMobile = orderMap.getOrDefault("GETMANTEL", "").toString();
            if (recipientMobile.isEmpty())
            {
                recipientMobile = orderMap.getOrDefault("CONTTEL", "").toString();
            }
            String remark = orderMap.getOrDefault("DELMEMO", "").toString();//配送备注
            String memo = orderMap.getOrDefault("MEMO", "").toString();//订单备注
            String lng = orderMap.getOrDefault("LONGITUDE", "").toString();
            String lat = orderMap.getOrDefault("LATITUDE", "").toString();
            double lng_d = 0;
            double lat_d = 0;
            try
            {
                lng_d = Double.parseDouble(lng);
                lat_d = Double.parseDouble(lat);
            }
            catch (Exception e)
            {

            }

            //POS订单不一定有经纬度
            if (lng_d<1||lat_d<1)
            {
                String location = this.getGeocode(eId,recipientAddress,recipientCityName);
                if (!location.isEmpty())
                {
                    String[] listjinwei=location.split(",");
                    lng = listjinwei[0];
                    lat = listjinwei[1];
                    //double[] receiverDoubles = OrderUtil.gaoDeToBaidu(Double.parseDouble(lng), Double.parseDouble(lat));
                }
            }


            String cstOrderNo = orderNo;//客户的订单号
            String sn = orderMap.getOrDefault("ORDER_SN", "0").toString();
            String tot_amt = orderMap.getOrDefault("TOT_AMT", "0").toString();
            double tot_amt_d = Double.parseDouble(tot_amt)*100;
            String tot_qty = orderMap.getOrDefault("TOT_QTY", "1").toString();

            sftcOrderCreateEntity sftcOrder = new sftcOrderCreateEntity();
            sftcOrder.setDev_id(dev_id);
            sftcOrder.setShop_type(2);//店铺ID类型 1：顺丰店铺ID ；2：接入方店铺ID
            sftcOrder.setShop_id(senderShop);//店铺ID
            sftcOrder.setShop_order_id(orderNo);//商家订单号
            String order_source = getOrderSouce(loadDocType);//1：美团；2：饿了么；3：百度；4：口碑；其他请直接填写中文字符串值
            sftcOrder.setOrder_source(order_source);//	订单接入来源
            sftcOrder.setOrder_sequence(sn);//取货序号 与order_source配合使用 如：饿了么10号单
            sftcOrder.setLbs_type(2);//1：百度坐标，2：高德坐标
            sftcOrder.setPay_type(1);//1：已付款 0：货到付款
            sftcOrder.setIs_appoint(0);//0：非预约单；1：预约单
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

            long longcur = System.currentTimeMillis();
            Date dateSta = new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
            long longsta = dateSta.getTime();
            long expect_time = longsta/1000;//用户期望送达时间
            long order_time = longcur/1000;//用户下单时间
            sftcOrder.setExpect_time(expect_time);//appoint_type=1时需必传,秒级时间戳
            sftcOrder.setOrder_time(order_time);
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
                    resJson.put("error_code","fffff");
                    resJson.put("error_msg","当前设置延时发物流，无需发单");
                    return resJson.toJSONString();
                }

            }
            else
            {
                if (diff >= 60)
                {
                    sftcOrder.setIs_appoint(1);
                    sftcOrder.setAppoint_type(1);//预约单的时候传入,1：预约单送达单；2：预约单上门单

                }
            }


            sftcOrder.setIs_insured(0);//是否保价，0：非保价；1：保价
            sftcOrder.setIs_person_direct(0);//是否是专人直送订单，0：否；1：是
            sftcOrder.setRemark(memo);
            sftcOrder.setReturn_flag(511);//1:商品总价格，2:配送距离，4:物品重量，8:起送时间，16:期望送达时间，32:支付费用，64:实际支持金额，128:优惠券总金额，256:结算方式 例如全部返回为填入511
            sftcOrder.setPush_time(order_time);//	推单时间
            sftcOrder.setVersion(17);//版本号 参照文档主版本号填写 如：文档版本号1.7,version=17

            receive receiveInfo = new receive();
            receiveInfo.setUser_name(recipientName);
            receiveInfo.setUser_phone(recipientMobile);
            receiveInfo.setUser_address(recipientAddress);
            receiveInfo.setUser_lng(lng);
            receiveInfo.setUser_lat(lat);
            sftcOrder.setReceive(receiveInfo);

            order_detail orderDetail = new order_detail();
            int total_price = (int) tot_amt_d;
            orderDetail.setTotal_price(total_price);
            orderDetail.setProduct_type(3);//3:百货
            if (product_type!=null&&!product_type.trim().isEmpty())
            {
                try {
                    orderDetail.setProduct_type(Integer.parseInt(product_type));
                }
                catch (Exception e)
                {

                }

            }

            orderDetail.setWeight_gram(1000);//	物品重量（单位：克）
            orderDetail.setProduct_num(Integer.parseInt(tot_qty));//物品个数
            orderDetail.setProduct_type_num(detailList.size());//	物品种类个数
            orderDetail.setProduct_detail(new ArrayList<product_detail>());
            if(detailList!=null&&detailList.isEmpty()==false)
            {
                for (Map<String, Object> mapGoods : detailList)
                {
                    String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
                    String pluNo = mapGoods.getOrDefault("PLUNO", "").toString();
                    String pluBarcode = mapGoods.getOrDefault("PLUBARCODE", "").toString();

                    if(pluNo.isEmpty())
                    {
                        pluNo = pluBarcode;
                    }

                    if(pluName.isEmpty())
                    {
                        pluName = pluNo;
                    }
                    String featureName = mapGoods.getOrDefault("FEATURENAME", "").toString();
                    if (!featureName.isEmpty())
                    {
                        pluName += " "+featureName;
                    }
                    String unitName = mapGoods.getOrDefault("SUNITNAME", "").toString();

                    if(unitName.isEmpty())
                    {
                        unitName = "件";
                    }
                    String qty = mapGoods.getOrDefault("QTY", "1").toString();

                    product_detail productDetail = new product_detail();
                    productDetail.setProduct_name(pluName);
                    productDetail.setProduct_num(Integer.parseInt(qty));
                    productDetail.setProduct_unit(unitName);

                    orderDetail.getProduct_detail().add(productDetail);
                }
            }

            sftcOrder.setOrder_detail(orderDetail);

            JSONObject param = (JSONObject) JSONObject.toJSON(sftcOrder);
            String reqJson = param.toJSONString();
            //将POST发送的字符串内容进行JSON序列化
            //使用&连接JSON的数据及dev_id、dev_key
            //对上步得到的字符串用MD5加密然后Base64计算得到签名密钥
            String sign = reqJson+"&"+dev_id+"&"+secret;
            sign = PosPub.encodeMD5(sign);
            sign = PosPub.encodeBASE64(sign);

            String url = HOST+method+"?sign="+sign;
            String res = sftcHttpClientUtil.postRequest(url,reqJson);

            return res;
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("【调用物流顺丰同城】"+method+"异常:"+e.getMessage()+",订单号orderNo="+orderNo,logFileName);
            return "";
        }

    }

    public String sftcOrderCancel(Map<String, Object> setMap, Map<String, Object> orderMap) throws Exception
    {
        String method = "cancelorder";
        String orderNo = orderMap.get("ORDERNO").toString();
        try
        {
            String secret = setMap.get("APPSECRET").toString();//"u2Z1F7Fh";//客户秘钥
            String key = setMap.get("APPSIGNKEY").toString();//dev_id 	同城开发者ID
            long dev_id = Long.parseLong(key);

            /************店铺ID，直接用接入方店铺ID********************/
            String senderShop = orderMap.getOrDefault("SHIPPINGSHOP", "").toString();//配送门店


            JSONObject param = new JSONObject();
            param.put("dev_id", dev_id);
            param.put("order_id", orderNo);//
            param.put("order_type", 2);//	1、顺丰订单号 2、商家订单号
            param.put("shop_type", 2);//1、顺丰店铺ID 2、接入方店铺ID
            param.put("shop_id", senderShop);//order_type=2时必传shop_id与shop_type
            long longcur = System.currentTimeMillis()/1000;
            param.put("push_time", longcur);

            String reqJson = param.toJSONString();
            //将POST发送的字符串内容进行JSON序列化
            //使用&连接JSON的数据及dev_id、dev_key
            //对上步得到的字符串用MD5加密然后Base64计算得到签名密钥
            String sign = reqJson+"&"+dev_id+"&"+secret;
            sign = PosPub.encodeMD5(sign);
            sign = PosPub.encodeBASE64(sign);

            String url = HOST+method+"?sign="+sign;
            String res = sftcHttpClientUtil.postRequest(url,reqJson);

            return res;

        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("【调用物流顺丰同城】"+method+"异常:"+e.getMessage()+",订单号orderNo="+orderNo,logFileName);
            return "";
        }

    }


    private  String getOrderSouce(String loadDocType) throws Exception
    {
        ////1：美团；2：饿了么；3：百度；4：口碑；其他请直接填写中文字符串值
        String order_source = "手机商城";
        try
        {
            if (loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.MTSG))
            {
                order_source="1";
            }
            else if (loadDocType.equals(orderLoadDocType.ELEME))
            {
                order_source="2";
            }
            else if (loadDocType.equals(orderLoadDocType.POS))
            {
                order_source="线下门店";
            }
            else if (loadDocType.equals(orderLoadDocType.POSANDROID))
            {
                order_source="线下门店";
            }
            else
            {
                order_source = "手机商城";
            }


        }
        catch (Exception e)
        {

        }
        return  order_source;

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
