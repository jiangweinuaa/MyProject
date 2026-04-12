package com.dsc.spos.utils.ec;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.model.ShangyouOrder;
import com.dsc.spos.model.ShangyouReturnDish;
import com.dsc.spos.utils.HttpSend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * 商有云管家：
 * https://www.showdoc.cc/shangyou?page_id=3083920262205250
 * 密码：sy666666
 *
 * 测试域名地址：http://steward-qa.syoo.cn/open/out
 * 线上域名地址：http://steward.syoo.cn/open/out
 */
public class shangyou
{
    private static Logger logger = LogManager.getLogger(shangyou.class.getName());


    /**
     * 获取签名
     * @param authToken
     * @param signKey
     * @return
     */
    private String getSign(String authToken,String signKey,long timestamp)
    {
        //测试店铺
        //authToken = "bf63d86b-439d-46d4-993d-62bf32be82a1";
        //signKey = "0c71becfd51747ae8d678f6c01eca52a";
        //Long timestamp = new Date().getTime();

        String sign;

        String str = signKey + authToken + timestamp;

        sign = encryption(str);

        System.out.println(sign);
        return sign;

    }

    /**
     * MD5加密
     * @param plainText
     * @return
     */
    private String encryption(String plainText)
    {
        String re_md5 = new String();
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++)
            {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("\r\nencryption error:{}",e);
            e.printStackTrace();
        }
        return re_md5;
    }


    /**
     * 门店绑定，商有切换店铺到云管家可以新建店铺，我们负责映射绑定就行，
     * 新建门店是需要经纬度，myCommon里面有个根据地址经纬度获取
     * @return
     */
    public  String storeBinding(String apiUrl,String authToken,String signKey,long storeId,long thirdShopId) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/store/storeBinding";
            }
            else
            {
                apiUrl+="/open/out/store/storeBinding";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));

            JSONObject detail = new JSONObject();
            detail.put("storeId",storeId);//门店id
            detail.put("thirdShopId",thirdShopId);//第三方门店id

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("storeBinding",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 查询门店信息，商家主动退单部分参数需要取pid
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param storeId
     * @return
     * @throws JSONException
     */
    public  String getOpenStoreInfo(String apiUrl,String authToken,String signKey,long storeId) throws JSONException
    {
        //       tradeEnum：
//        中餐	19
//        快餐	21
//        烧烤	24
//        西餐	23
//        披萨	22
//        日韩料理	20
//        咖啡	15
//        奶茶/果汁	16
//        面包/糕点	17
//        蛋糕	14
//        零食小吃	1
//        便利店	2
//        水站/奶站	3
//        五金日用	4
//        粮油调味	5
//        文具店	6
//        酒水/茶行	7
//        超市	8
//        书店	9
//        传统百货	11
//        医药	12
//        鲜花	13
//        快递配送	18
//        服装	28
//        汽修零配	30
//        票务文件	31
//        日化美妆	32
//        线上商城	33
//        其他	34
//        果蔬	25
//        海鲜水产	26
//        冷冻速食	27
//        电子数码/手机	29

        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/store/getOpenStoreInfo";
            }
            else
            {
                apiUrl+="/open/out/store/getOpenStoreInfo";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));

            JSONObject detail = new JSONObject();
            detail.put("storeId",storeId);//门店id

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("getOpenStoreInfo",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 门店配送方式和物流状态是否可用
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param storeId
     * @return
     * @throws JSONException
     */
    public  String getStoreExpressAndStatus(String apiUrl,String authToken,String signKey,long storeId) throws JSONException
    {
        //ExpressEnum
//        name	id
//        0	自配送
//        2	聚合物流
//        3	未绑定
//        5	专送物流-顺丰
//        6	顺丰众包（授权）
//        7	专送物流-美团
//        8	专送物流-达达
//        9	专送物流-蜂鸟
//        10	闪急送
//        11	达达众包（授权）
//        16	抢单物流
//        17	顺丰授权

        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/store/getStoreExpressAndStatus";
            }
            else
            {
                apiUrl+="/open/out/store/getStoreExpressAndStatus";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));

            JSONObject detail = new JSONObject();
            detail.put("storeId",storeId);//门店id

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("getOpenStoreInfo",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 线上商城订单+已接单状态，新建订单
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param order
     * @return
     * @throws JSONException
     */
    public  String saveNewOrder(String apiUrl,String authToken,String signKey, long thirdShopId, ShangyouOrder order) throws JSONException
    {
        //ExpressEnum
//        name	id
//        0	自配送
//        2	聚合物流
//        3	未绑定
//        5	专送物流-顺丰
//        6	顺丰众包（授权）
//        7	专送物流-美团
//        8	专送物流-达达
//        9	专送物流-蜂鸟
//        10	闪急送
//        11	达达众包（授权）
//        16	抢单物流
//        17	顺丰授权

        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/saveNewOrder";
            }
            else
            {
                apiUrl+="/open/out/order/saveNewOrder";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);

            JSONObject detail = new JSONObject();
            detail.put("orderNo",order.getOrderNo());
            detail.put("storeId",order.getStoreId());
            detail.put("memberId",order.getMemberId());
            detail.put("openChannelType",order.getOpenChannelType());
            detail.put("storeName",order.getStoreName());
            detail.put("salesType",order.getSalesType());
            detail.put("deliveryType",order.getDeliveryType());
            detail.put("selfType",order.getSelfType());
            detail.put("payType",order.getPayType());
            detail.put("payStatus",order.getPayStatus());
            detail.put("recipientPhone",order.getRecipientPhone());
            detail.put("customerName",order.getCustomerName());
            detail.put("orderStartTime",order.getOrderStartTime());
            detail.put("deliveryTime",order.getDeliveryTime());
            detail.put("pickupAddress",order.getPickupAddress());
            detail.put("hasInvoiced",order.getHasInvoiced());
            detail.put("invoiceTitle",order.getInvoiceTitle());
            detail.put("status",order.getStatus());
            detail.put("description",order.getDescription());
            detail.put("address",order.getAddress());
            detail.put("deliverFee",order.getDeliverFee());
            detail.put("merchantDeliverySubsidy",order.getMerchantDeliverySubsidy());
            detail.put("vipDeliveryFeeDiscount",order.getVipDeliveryFeeDiscount());
            detail.put("totalPrice",order.getTotalPrice());
            detail.put("originalPrice",order.getOriginalPrice());
            detail.put("foodNum",order.getFoodNum());
            detail.put("dinnersNumber",order.getDinnersNumber());
            detail.put("orderReceiveTime",order.getOrderReceiveTime());
            detail.put("orderConfirmTime",order.getOrderConfirmTime());
            detail.put("pickupTime",order.getPickupTime());
            detail.put("boxNum",order.getBoxNum());
            detail.put("packageFee",order.getPackageFee());
            detail.put("daySeq",order.getDaySeq());
            detail.put("merchantPhone",order.getMerchantPhone());
            detail.put("channel",order.getChannel());
            detail.put("isThirdShipping",order.getIsThirdShipping());
            detail.put("logisticsType",order.getLogisticsType());
            detail.put("distance",order.getDistance());
            detail.put("latitude",order.getLatitude());
            detail.put("longitude",order.getLongitude());
            detail.put("lalType",order.getLalType());
            detail.put("income",order.getIncome());
            detail.put("tableNo",order.getTableNo());
            detail.put("orderType",order.getOrderType());
            detail.put("payChannel",order.getPayChannel());
            detail.put("orderFlag",order.getOrderFlag());
            detail.put("mealFee",order.getMealFee());
            detail.put("payUserName",order.getPayUserName());
            detail.put("payUserTel",order.getPayUserTel());
            detail.put("productType",order.getProductType());
            detail.put("activityTotal",order.getActivityTotal());
            //优惠活动
            if (order.getOrderActivities() != null)
            {
                JSONArray orderActivities = new JSONArray();
                for (ShangyouOrder.Activities orderActivity : order.getOrderActivities())
                {
                    JSONObject activity = new JSONObject();
                    activity.put("remark",orderActivity.getRemark());
                    activity.put("price",orderActivity.getPrice());
                    orderActivities.put(activity);
                }
                detail.put("orderActivities",orderActivities);
            }

            //商品明细
            if (order.getFoodDtoList() != null)
            {
                JSONArray foodDtoList = new JSONArray();
                for (ShangyouOrder.Food food : order.getFoodDtoList())
                {
                    JSONObject eatinList = new JSONObject();
                    eatinList.put("foodId",food.getFoodId());
                    eatinList.put("goodType",food.getGoodType());
                    eatinList.put("batchNo",food.getBatchNo());
                    eatinList.put("userImage",food.getUserImage());
                    eatinList.put("userName",food.getUserName());
                    eatinList.put("skuId",food.getSkuId());
                    eatinList.put("storeId",food.getStoreId());
                    eatinList.put("quantity",food.getQuantity());
                    eatinList.put("price",food.getPrice());
                    eatinList.put("name",food.getName());
                    eatinList.put("extendCode",food.getExtendCode());
                    eatinList.put("barCode",food.getBarCode());
                    eatinList.put("weight",food.getWeight());
                    eatinList.put("userPrice",food.getUserPrice());
                    eatinList.put("shopPrice",food.getShopPrice());
                    eatinList.put("boxNum",food.getBoxNum());
                    eatinList.put("boxPrice",food.getBoxPrice());
                    eatinList.put("unit",food.getUnit());
                    eatinList.put("gifTitle",food.getGifTitle());
                    eatinList.put("gifUrl",food.getGifUrl());
                    //子商品，内容与foodDtoList相同（套餐商品时必填）
                    if (food.getChildFoodList() != null)
                    {
                        JSONArray childFoodList = new JSONArray();
                        for (ShangyouOrder.Food food1 : food.getChildFoodList())
                        {
                            JSONObject child = new JSONObject();
                            child.put("foodId",food1.getFoodId());
                            child.put("goodType",food1.getGoodType());
                            child.put("batchNo",food1.getBatchNo());
                            child.put("userImage",food1.getUserImage());
                            child.put("userName",food1.getUserName());
                            child.put("skuId",food1.getSkuId());
                            child.put("storeId",food1.getStoreId());
                            child.put("quantity",food1.getQuantity());
                            child.put("price",food1.getPrice());
                            child.put("name",food1.getName());
                            child.put("extendCode",food1.getExtendCode());
                            child.put("barCode",food1.getBarCode());
                            child.put("weight",food1.getWeight());
                            child.put("userPrice",food1.getUserPrice());
                            child.put("shopPrice",food1.getShopPrice());
                            child.put("boxNum",food1.getBoxNum());
                            child.put("boxPrice",food1.getBoxPrice());
                            child.put("unit",food1.getUnit());
                            child.put("gifTitle",food1.getGifTitle());
                            child.put("gifUrl",food1.getGifUrl());
                            childFoodList.put(child);
                        }
                        eatinList.put("childFoodList",childFoodList);
                    }
                    //配料
                    if (food.getIngredients() != null)
                    {
                        JSONArray ingredients = new JSONArray();
                        for (ShangyouOrder.Ingredient ingredient : food.getIngredients())
                        {
                            JSONObject ing = new JSONObject();
                            ing.put("optionName",ingredient.getOptionName());
                            ing.put("amount",ingredient.getAmount());
                            ing.put("price",ingredient.getPrice());
                            ing.put("thirdCode",ingredient.getThirdCode());
                            ingredients.put(ing);
                        }
                        eatinList.put("ingredients",ingredients);
                    }
                    //多规格
                    if (food.getNewSpecs() != null)
                    {
                        JSONArray newSpecs = new JSONArray();
                        for (ShangyouOrder.Spec newSpec : food.getNewSpecs())
                        {
                            JSONObject spec = new JSONObject();
                            spec.put("specId",newSpec.getSpecId());
                            spec.put("name",newSpec.getName());
                            spec.put("value",newSpec.getValue());
                            spec.put("price",newSpec.getPrice());
                            spec.put("stock",newSpec.getStock());
                            spec.put("thirdCode",newSpec.getThirdCode());
                            spec.put("packingFee",newSpec.getPackingFee());
                            spec.put("onShelf",newSpec.getOnShelf());
                            spec.put("extendCode",newSpec.getExtendCode());
                            spec.put("barCode",newSpec.getBarCode());
                            spec.put("weight",newSpec.getWeight());
                            newSpecs.put(spec);
                        }
                        eatinList.put("newSpecs",newSpecs);
                    }
                    //多属性
                    if (food.getAttributes() != null)
                    {
                        JSONArray attributes = new JSONArray();
                        for (ShangyouOrder.Attribute attribute : food.getAttributes())
                        {
                            JSONObject attr = new JSONObject();
                            attr.put("name",attribute.getName());
                            attr.put("value",attribute.getValue());
                            attr.put("thirdCode",attribute.getThirdCode());
                            attributes.put(attr);
                        }
                        eatinList.put("attributes",attributes);
                    }
                    foodDtoList.put(eatinList);

                }
                detail.put("foodDtoList",foodDtoList);
            }

            //堂食加菜列表，内容与foodDtoList相同（加菜时必填）
            if (order.getChildEatinList() != null)
            {
                JSONArray childEatinList = new JSONArray();
                for (ShangyouOrder.Food food : order.getChildEatinList())
                {
                    JSONObject eatinList = new JSONObject();
                    eatinList.put("foodId",food.getFoodId());
                    eatinList.put("goodType",food.getGoodType());
                    eatinList.put("batchNo",food.getBatchNo());
                    eatinList.put("userImage",food.getUserImage());
                    eatinList.put("userName",food.getUserName());
                    eatinList.put("skuId",food.getSkuId());
                    eatinList.put("storeId",food.getStoreId());
                    eatinList.put("quantity",food.getQuantity());
                    eatinList.put("price",food.getPrice());
                    eatinList.put("name",food.getName());
                    eatinList.put("extendCode",food.getExtendCode());
                    eatinList.put("barCode",food.getBarCode());
                    eatinList.put("weight",food.getWeight());
                    eatinList.put("userPrice",food.getUserPrice());
                    eatinList.put("shopPrice",food.getShopPrice());
                    eatinList.put("boxNum",food.getBoxNum());
                    eatinList.put("boxPrice",food.getBoxPrice());
                    eatinList.put("unit",food.getUnit());
                    eatinList.put("gifTitle",food.getGifTitle());
                    eatinList.put("gifUrl",food.getGifUrl());
                    //子商品，内容与foodDtoList相同（套餐商品时必填）
                    if (food.getChildFoodList() != null)
                    {
                        JSONArray childFoodList = new JSONArray();
                        for (ShangyouOrder.Food food1 : food.getChildFoodList())
                        {
                            JSONObject child = new JSONObject();
                            child.put("foodId",food1.getFoodId());
                            child.put("goodType",food1.getGoodType());
                            child.put("batchNo",food1.getBatchNo());
                            child.put("userImage",food1.getUserImage());
                            child.put("userName",food1.getUserName());
                            child.put("skuId",food1.getSkuId());
                            child.put("storeId",food1.getStoreId());
                            child.put("quantity",food1.getQuantity());
                            child.put("price",food1.getPrice());
                            child.put("name",food1.getName());
                            child.put("extendCode",food1.getExtendCode());
                            child.put("barCode",food1.getBarCode());
                            child.put("weight",food1.getWeight());
                            child.put("userPrice",food1.getUserPrice());
                            child.put("shopPrice",food1.getShopPrice());
                            child.put("boxNum",food1.getBoxNum());
                            child.put("boxPrice",food1.getBoxPrice());
                            child.put("unit",food1.getUnit());
                            child.put("gifTitle",food1.getGifTitle());
                            child.put("gifUrl",food1.getGifUrl());
                            childFoodList.put(child);
                        }
                        eatinList.put("childFoodList",childFoodList);
                    }
                    //配料
                    if (food.getIngredients() != null)
                    {
                        JSONArray ingredients = new JSONArray();
                        for (ShangyouOrder.Ingredient ingredient : food.getIngredients())
                        {
                            JSONObject ing = new JSONObject();
                            ing.put("optionName",ingredient.getOptionName());
                            ing.put("amount",ingredient.getAmount());
                            ing.put("price",ingredient.getPrice());
                            ing.put("thirdCode",ingredient.getThirdCode());
                            ingredients.put(ing);
                        }
                        eatinList.put("ingredients",ingredients);
                    }
                    //多规格
                    if (food.getNewSpecs() != null)
                    {
                        JSONArray newSpecs = new JSONArray();
                        for (ShangyouOrder.Spec newSpec : food.getNewSpecs())
                        {
                            JSONObject spec = new JSONObject();
                            spec.put("specId",newSpec.getSpecId());
                            spec.put("name",newSpec.getName());
                            spec.put("value",newSpec.getValue());
                            spec.put("price",newSpec.getPrice());
                            spec.put("stock",newSpec.getStock());
                            spec.put("thirdCode",newSpec.getThirdCode());
                            spec.put("packingFee",newSpec.getPackingFee());
                            spec.put("onShelf",newSpec.getOnShelf());
                            spec.put("extendCode",newSpec.getExtendCode());
                            spec.put("barCode",newSpec.getBarCode());
                            spec.put("weight",newSpec.getWeight());
                            newSpecs.put(spec);
                        }
                        eatinList.put("newSpecs",newSpecs);
                    }
                    //多属性
                    if (food.getAttributes() != null)
                    {
                        JSONArray attributes = new JSONArray();
                        for (ShangyouOrder.Attribute attribute : food.getAttributes())
                        {
                            JSONObject attr = new JSONObject();
                            attr.put("name",attribute.getName());
                            attr.put("value",attribute.getValue());
                            attr.put("thirdCode",attribute.getThirdCode());
                            attributes.put(attr);
                        }
                        eatinList.put("attributes",attributes);
                    }
                    childEatinList.put(eatinList);
                }
                detail.put("childEatinList",childEatinList);
            }

            header.put("param",detail);
            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("saveNewOrder",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }


    /**
     * 商家主动取消订单，取消后状态变成6、无效订单
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @param cancelOrderDescription 取消原因
     * @return
     * @throws JSONException
     */
    public  String orderValid(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo,String cancelOrderDescription) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/valid";
            }
            else
            {
                apiUrl+="/open/out/order/valid";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号
            detail.put("cancelOrderDescription",cancelOrderDescription);//取消原因

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderValid",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }


    /**
     * 用户申请退单后，订单状态是7、退单中
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @param refundDescription 退单原因
     * @param isRefundAll 是否是全部退款 0否 1是
     * @param refundFood 退菜商品列表
     * @return
     * @throws JSONException
     */
    public  String orderChargeBack(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo,String refundDescription,int isRefundAll, List<ShangyouReturnDish> refundFood) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/chargeBack";
            }
            else
            {
                apiUrl+="/open/out/order/chargeBack";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号
            detail.put("refundDescription",refundDescription);//退单原因
            detail.put("isRefundAll",isRefundAll);//是否是全部退款 0否 1是
            //部分退商品处理
            if (refundFood != null && refundFood.size()>0)
            {
                JSONArray refundFoods = new JSONArray();
                for (ShangyouReturnDish Dish : refundFood)
                {
                    JSONObject food = new JSONObject();
                    food.put("name",Dish.getName());
                    food.put("price",Dish.getPrice());
                    food.put("num",Dish.getNum());
                    food.put("totalPrice",Dish.getTotalPrice());
                    refundFoods.put(food);
                }
                detail.put("refundFood",refundFoods);
            }
            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderChargeBack",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 同意退款后，订单状态是8、退单成功
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @return
     * @throws JSONException
     */
    public  String orderChargeBackFinish(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/chargeBackFinish";
            }
            else
            {
                apiUrl+="/open/out/order/chargeBackFinish";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderChargeBackFinish",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 拒绝退款后，订单状态是9、退单失败
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @return
     * @throws JSONException
     */
    public  String orderChargeBackFail(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/chargeBackFail";
            }
            else
            {
                apiUrl+="/open/out/order/chargeBackFail";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderChargeBackFail",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }



    /**
     * 订单完成后，订单状态是5、已完成
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @return
     * @throws JSONException
     */
    public  String orderFinishOrder(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/finishOrder";
            }
            else
            {
                apiUrl+="/open/out/order/finishOrder";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderFinishOrder",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 查询外卖订单详情，商家主动退款接口部分参数，需要从这里取mengYouNo
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @return
     * @throws JSONException
     */
    public  String orderFindOrder(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/order/findOrder";
            }
            else
            {
                apiUrl+="/open/out/order/findOrder";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("orderFindOrder",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }


    /**
     *商家主动退款
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo
     * @param refundDescription
     * @param refundTotal
     * @return
     * @throws JSONException
     */
    public  String orderWorkRefundAll(String apiUrl,String authToken,String signKey,long storeId,long thirdShopId,String orderNo,String refundDescription, BigDecimal refundTotal) throws JSONException
    {
        String resJson="";
        try
        {
            long pid=0;//店铺id，需要调用查询门店信息接口获取
            String mengYouNo="";//商有云管家单号，需要调用订单详情接口获取

            //查询商有云管家门店信息
            String json=getOpenStoreInfo(apiUrl,authToken,signKey,storeId);
            JSONObject pJson= new JSONObject(json);
            if (pJson.get("errorCode").toString().equals("000"))
            {
                JSONObject v_data=pJson.getJSONObject("data");
                pid=v_data.getLong("pid");

                //查询外卖订单详情
                json=orderFindOrder(apiUrl,authToken,signKey,thirdShopId,orderNo);
                pJson= new JSONObject(json);
                if (pJson.get("errorCode").toString().equals("000"))
                {
                    v_data=pJson.getJSONObject("data");
                    mengYouNo=v_data.get("mengYouNo").toString();

                    if(apiUrl.endsWith("/"))
                    {
                        apiUrl+="open/out/order/work/refundAll";
                    }
                    else
                    {
                        apiUrl+="/open/out/order/work/refundAll";
                    }

                    long timestamp=new Date().getTime();
                    JSONObject header = new JSONObject();

                    header.put("timestamp",timestamp);
                    header.put("authToken",authToken);
                    header.put("sign",getSign(authToken,signKey,timestamp));
                    header.put("thirdShopId",thirdShopId);//第三方门店id

                    JSONObject detail = new JSONObject();
                    detail.put("orderNo",orderNo);//第三方订单号
                    detail.put("mengYouNo",mengYouNo);//盟友单号
                    detail.put("pid",pid);//店铺id
                    detail.put("storeId",storeId);//门店id
                    detail.put("refundDescription",refundDescription);//退款原因
                    detail.put("refundTotal",refundTotal);//退款金额

                    header.put("param",detail);

                    //
                    String request=header.toString();

                    StringBuffer sbErr=new StringBuffer();
                    String  resbody= HttpSend.SendShangYou("orderWorkRefundAll",request,apiUrl,sbErr);

                    try
                    {
                        JSON.parse(resbody);

                        resJson= resbody;
                    }
                    catch (Exception e)
                    {
                        //返回自定义JSON对象
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("errorCode","101");
                        errorJson.put("errorMsg",sbErr.toString());
                        resJson=errorJson.toString();
                    }
                }
                else
                {
                    //返回自定义JSON对象
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("errorCode","101");
                    errorJson.put("errorMsg","盟友订单号无法获取，调用商有云管家外卖订单查询接口");
                    resJson=errorJson.toString();
                    return resJson;
                }
            }
            else
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg","店铺id无法获取，调用商有云管家门店查询接口");
                resJson=errorJson.toString();
                return resJson;
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }


    /**
     * 查询物流详情，根据订单号，这个是查最新配送状态、配送费、配送商、配送距离、配送员及电话
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo 这个字段要注意：orderType传1代表此字段是物流单号，要通过回调接口取物流单号；orderType传2代表此字段是商有云管家单号
     * @param orderType 订单编号类型：1、物流单号； 2、管家单号。默认:1
     * @return
     * @throws JSONException
     */
    public  String logisticsOrderdetail(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo,int orderType) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/logistics/orderdetail";
            }
            else
            {
                apiUrl+="/open/out/logistics/orderdetail";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号
            detail.put("orderType",orderType);//orderNo代表字段类型

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("logisticsOrderdetail",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }


    /**
     * 查询物流状态列表，根据订单号，将此单所有配送历程全部返回，配送时间、配送状态等,没配送之前data对象是null要注意解析
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo 这个字段要注意：orderType传1代表此字段是物流单号，要通过回调接口取物流单号；orderType传2代表此字段是商有云管家单号
     * @param orderType 订单编号类型：1、物流单号； 2、管家单号。默认:1
     * @return
     * @throws JSONException
     */
    public  String logisticsStatuslist(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo,int orderType) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/logistics/statuslist";
            }
            else
            {
                apiUrl+="/open/out/logistics/statuslist";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号
            detail.put("orderType",orderType);//orderNo代表字段类型

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("logisticsStatuslist",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 取消物流
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @param thirdShopId
     * @param orderNo 这个字段要注意：orderType传1代表此字段是物流单号，要通过回调接口取物流单号；orderType传2代表此字段是商有云管家单号
     * @param orderType 订单编号类型：1、物流单号； 2、管家单号。默认:1
     * @return
     * @throws JSONException
     */
    public  String logisticsCancelorder(String apiUrl,String authToken,String signKey,long thirdShopId,String orderNo,int orderType) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/logistics/cancelorder";
            }
            else
            {
                apiUrl+="/open/out/logistics/cancelorder";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));
            header.put("thirdShopId",thirdShopId);//第三方门店id

            JSONObject detail = new JSONObject();
            detail.put("orderNo",orderNo);//第三方订单号
            detail.put("orderType",orderType);//orderNo代表字段类型

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("logisticsCancelorder",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }

    /**
     * 获取所有商有云管家门店，用于门店映射
     * @param apiUrl
     * @param authToken
     * @param signKey
     * @return
     * @throws JSONException
     */
    public  String getOpenStoreInfoList(String apiUrl,String authToken,String signKey) throws JSONException
    {
        String resJson="";
        try
        {
            if(apiUrl.endsWith("/"))
            {
                apiUrl+="open/out/store/getOpenStoreInfoList";
            }
            else
            {
                apiUrl+="/open/out/store/getOpenStoreInfoList";
            }

            long timestamp=new Date().getTime();
            JSONObject header = new JSONObject();

            header.put("timestamp",timestamp);
            header.put("authToken",authToken);
            header.put("sign",getSign(authToken,signKey,timestamp));

            JSONObject detail = new JSONObject();
            detail.put("pageNum",1);
            detail.put("pageSize",1000);

            header.put("param",detail);

            //
            String request=header.toString();

            StringBuffer sbErr=new StringBuffer();
            String  resbody= HttpSend.SendShangYou("getOpenStoreInfoList",request,apiUrl,sbErr);

            try
            {
                JSON.parse(resbody);

                resJson= resbody;
            }
            catch (Exception e)
            {
                //返回自定义JSON对象
                JSONObject errorJson = new JSONObject();
                errorJson.put("errorCode","101");
                errorJson.put("errorMsg",sbErr.toString());
                resJson=errorJson.toString();
            }
        }
        catch (Exception ex)
        {
            //返回自定义JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("errorCode","101");
            errorJson.put("errorMsg",ex.toString());
            resJson=errorJson.toString();
        }

        return  resJson;
    }




}
