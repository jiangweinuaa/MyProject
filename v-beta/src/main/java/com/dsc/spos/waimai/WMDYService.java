package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderGoodsItemAgio;
import com.dsc.spos.waimai.entity.orderGoodsItemMessage;
import com.dsc.spos.waimai.entity.orderInvoice;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMDYService extends SWaimaiBasicService {
    private String response = "";
    private String messageId ="";
    private String event = "";//消息类型
    public WMDYService()
    {
        //this.messageId = UUID.randomUUID().toString();
    }
    public WMDYService(String msgId)
    {
        if (msgId==null||msgId.isEmpty())
        {
            //this.messageId = UUID.randomUUID().toString();
        }
        else
        {
            this.messageId = msgId;
        }

    }
    @Override
    public String execute(String json) throws Exception {
        if (json == null || json.length() == 0) {
            return null;
        }
        //String token = WMDYUtilTools.GetTokenInRedis(false, "1112222", "2222222");
        Map<String, Object> res = new HashMap<String, Object>();
        this.processDUID(json, res);

        return response;
    }

    @Override
    protected void processDUID(String req, Map<String, Object> res) throws Exception {
        try
        {
            HelpTools.writelog_waimai("【收到抖音外卖消息内容】"+ req +",【消息ID】"+ messageId);
            JSONObject obj = new JSONObject(req);
            event = obj.optString("event");//消息类型，用于区分各类消息
            String client_key = obj.optString("client_key");
            String content = obj.optString("content");
            if ("verify_webhook".equalsIgnoreCase(event))
            {
                //第一次验证消息，直接返回即可
                response = content;
                return;
            }
            if ("life_takeout_order_pay_success".equalsIgnoreCase(event))
            {
                //"content": "{\"order\":{\"order_id\":\"1000001703409282301\",.....}"
                // 存缓存
                order dcpOrder = NewOrderProcess(content, "1", "1");
                // 存数据库
                SaveOrder(dcpOrder);

            }
            else
            {
                String status = "";// 门店管理对应的 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单  5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
                String refundstatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
                JSONObject contentObj = new JSONObject(content);
                String orderNo = contentObj.optString("order_id","");
                String poi_id = contentObj.optString("poi_id","");
                if ("life_takeout_order_merchant_receive".equalsIgnoreCase(event))
                {
                    //已接单
                    status = "2";
                }
                else if ("life_takeout_order_merchant_refuse".equalsIgnoreCase(event))
                {
                    //已拒单
                    status = "3";

                }
                else if ("life_trade_refund_complete".equalsIgnoreCase(event))
                {
                    //商家取消订单推送消息
                    status = "12";
                    refundstatus = "6";
                }
                else if ("life_trade_refund_audit".equalsIgnoreCase(event))
                {
                    //已同意/拒绝退款消息
                    //这里面还得区分，全退还是部分退
                    String pass = contentObj.optString("pass","");//是否同意退款
                    String after_sale_id = contentObj.optString("after_sale_id","");//退款单号
                    if ("true".equalsIgnoreCase(pass))
                    {
                        //同意退单
                        status = "12";
                        refundstatus = "6";
                    }
                    else
                    {
                        //拒绝退单
                        status = "11";
                        refundstatus = "3";
                    }


                }
                else if ("life_takeout_delivery_change".equalsIgnoreCase(event))
                {
                    //配送状态变更消息
                    UpdateOrderShippingStatus(content);
                    return;
                }
                else
                {
                    HelpTools.writelog_waimai("【收到抖音外卖的消息类型】:" + event + ",暂未对接,【消息ID】" + messageId);
                    return;
                }
//              //更新缓存
                order dcpOrder = UpdateOrderProcess(poi_id,orderNo,status,refundstatus);
                SaveOrder(dcpOrder);
            }


        }
        catch (Exception e)
        {
            HelpTools.writelog_waimai("【处理抖音外卖的消息内容】异常:" + e.getMessage()+",【消息ID】"+ messageId);
            return;
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(String req) throws Exception {
        return null;
    }

    private order NewOrderProcess(String content,String paraStatus, String paraRefundStatus) throws Exception
    {
        if (content==null||content.trim().isEmpty())
        {
            return null;
        }
        try
        {
            //用户下单消息，新订单
            JSONObject contentObj = new JSONObject(content);
            JSONObject orderObj = contentObj.optJSONObject("order");//订单基础信息
            if (orderObj==null)
            {
                HelpTools.writelog_waimai("【抖音外卖】【新订单的消息】解析异常:order节点为空,【消息ID】"+ messageId);
                return null;
            }
            JSONObject poiObj = contentObj.optJSONObject("poi");//门店信息
            if (poiObj==null)
            {
                HelpTools.writelog_waimai("【抖音外卖】【新订单的消息】解析异常:poi节点为空,【消息ID】"+ messageId);
                return null;
            }
            JSONArray productsJSONArray = contentObj.optJSONArray("products");//商品信息
            if (productsJSONArray==null||productsJSONArray.length()==0)
            {
                HelpTools.writelog_waimai("【抖音外卖】【新订单的消息】解析异常:products节点为空,【消息ID】"+ messageId);
                return null;
            }
            JSONObject receiver_info = contentObj.optJSONObject("receiver_info");//收货人信息
            JSONObject buyer_info = contentObj.optJSONObject("buyer_info");//下单人信息（仅鲜花、蛋糕品类）
            JSONObject amount_info = contentObj.optJSONObject("amount_info");//新金额信息amount_info中的各字段，主要表达的是订单中各类金额的总和
            order dcpOrder = new order();
            dcpOrder.setPay(new ArrayList<orderPay>());
            dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
            String eId = "99";
            String orderNo = "";
            String loadDocType = orderLoadDocType.DYWM;// 渠道类型
            String channelId = "";// 渠道编码 多个饿了么应用
            String shopno = "";// 主键不能为空，所以默认空格
            String erpShopName = "";

            Calendar cal = Calendar.getInstance();// 获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String sDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String sTime = df.format(cal.getTime());
            df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String sDateTime = df.format(cal.getTime());

            /*****************订单基础信息order节点下内容***********************/
            orderNo = orderObj.optString("order_id","");
            dcpOrder.setSn(orderObj.optString("shop_number",""));//流水号
            String is_book = orderObj.optString("is_book","");//是否预订单，1表示即时单、2表示预订单
            if ("1".equals(is_book))
            {
                dcpOrder.setIsBook("N");
            }
            else
            {
                dcpOrder.setIsBook("Y");
            }
            String is_self_deliver = orderObj.optString("is_self_deliver","");//是否商家自配送，0表示不是自配送、1表示自配送 is_self_deliver = deliver_mode==1
            String shipType = "1"; // 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
            String isMerPay = "N";//配送费是否商家结算
            if ("1".equals(is_self_deliver))
            {
                shipType = "6";//商家自配送
                isMerPay = "Y";
            }
            dcpOrder.setShipType(shipType);
            dcpOrder.setIsMerPay(isMerPay);
            String remark = orderObj.optString("remark","");//[贺卡](鲜花品类)/[祝福语](蛋糕品类)+用户填写的祝福内容+[其他备注]+用户填写的备注信息
            dcpOrder.setMemo(remark);
            dcpOrder.setsTime(sDateTime);//系统时间 yyyyMMddhhmmssSSS

            String ctime = orderObj.optString("create_time","");// 下单时间戳，单位秒
            String createDatetime = sDateTime;
            String createDate_order = sDate;
            String createTime_order = sTime;
            try {
                long lt = new Long(ctime);
                Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
                createDate_order = new SimpleDateFormat("yyyyMMdd").format(date);
                createTime_order = new SimpleDateFormat("HHmmss").format(date);
            } catch (Exception e) {

            }
            dcpOrder.setCreateDatetime(createDatetime);// String 创建时间

            String deliveryTime = orderObj.optString("sys_expect_time","");// 预计送达时间/用户期望送达时间(根据是否预订单)，格式：起始时间戳（单位秒）- 截止时间戳（单位秒），示例：1669370006-1669371206
            String shipDate = "";//配送日期默认下单日期
            String shipStartTime = "";//配送时间默认下单时间
            String shipEndTime = "";//配送时间默认下单时间
            if (deliveryTime!=null&&!deliveryTime.trim().isEmpty())
            {
                //1698131740-1698132940
                try
                {
                    String [] ss = deliveryTime.split("-");
                    String s1= ss[0];
                    String s2 = ss[1];

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                    long lt = new Long(s1);
                    Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    shipDate = dateFormat.format(date);
                    shipStartTime = timeFormat.format(date);


                    lt = new Long(s2);
                    date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    shipDate = dateFormat.format(date);
                    shipEndTime = timeFormat.format(date);
                }
                catch (Exception e)
                {

                }
            }
            dcpOrder.setShipDate(shipDate);
            dcpOrder.setShipStartTime(shipStartTime);//配送开始时间
            dcpOrder.setShipEndTime(shipEndTime);//配送结束时间
            String downgraded = "N";
            dcpOrder.setDowngraded(downgraded);

            /*****************订单门店信息poi节点下内容***********************/
            String poi_id = poiObj.optString("poi_id","");
            String poi_name = poiObj.optString("poi_name","");
            Map<String, String>	mappingShopMap = HelpTools.GetDYMappingShop(poi_id);//查询下门店对应缓存MT_MappingShop
            eId = mappingShopMap.get("eId");
            shopno = mappingShopMap.get("erpShopNo");
            channelId = mappingShopMap.get("channelId");
            erpShopName = mappingShopMap.getOrDefault("erpShopName", "");
            if (erpShopName==null||erpShopName.isEmpty())
            {
                erpShopName = poi_name;
            }
            dcpOrder.seteId(eId);
            dcpOrder.setLoadDocType(loadDocType);
            dcpOrder.setChannelId(channelId);
            dcpOrder.setOrderNo(orderNo);//dcp单号=来源单号
            dcpOrder.setLoadDocOrderNo(orderNo);//来源单号
            String orderCodeView = "";
            dcpOrder.setOrderCodeView(orderCodeView);

            dcpOrder.setOrderShop(poi_id);//第三方门店ID
            dcpOrder.setOrderShopName(poi_name);//第三方门店名称
            dcpOrder.setShopNo(shopno);
            dcpOrder.setShopName(erpShopName);
            dcpOrder.setShippingShopNo(shopno);
            dcpOrder.setShippingShopName(erpShopName);
            dcpOrder.setMachShopNo(shopno);
            dcpOrder.setMachShopName(erpShopName);
            /*****************订单收货人信息receiver_info节点下内容***********************/
            String getMan = "";
            String getManTel = "";
            String address = "";
            String lat = "0";
            String lng = "0";
            if (receiver_info!=null)
            {

                getMan = receiver_info.optString("receiver_name","");
                String receiver_phone = receiver_info.optString("receiver_phone","");//脱敏手机号187****2278
                String secret_number = receiver_info.optString("secret_number","");//隐私号17264842068_9936
                String receive_real_phone = receiver_info.optString("receive_real_phone","");//11位手机号，仅当用户在下单时取消“号码保护”时下发
                getManTel = secret_number;
                if (secret_number.isEmpty())
                {
                    getManTel = receiver_phone;
                }
                String province = receiver_info.optString("province","");//省
                String city = receiver_info.optString("city","");//市
                String district = receiver_info.optString("district","");//区
                String town = receiver_info.optString("town","");//乡镇
                String location_address = receiver_info.optString("location_address","");//详细地址，商家自配送单会展示，其他情况脱敏处理
                String location_name = receiver_info.optString("location_name","");//详细地址后半部分
                String door_plate_num = receiver_info.optString("door_plate_num","");//门牌号
                address = city+district+town+location_address+location_name+door_plate_num;//抖音用户侧显示的地址,可参考使用:city+district+town+location_name+door_plate_num
                lat = receiver_info.optString("lat","0");//收货地址纬度，商家自配送单会传，其他情况不传
                lng = receiver_info.optString("lng","0");//收货地址经度，商家自配送单会传，其他情况不传
                try
                {
                    BigDecimal lat_b = new BigDecimal(lat);//数据库里面是number
                }
                catch (Exception e)
                {
                    lat = "0";
                    lng = "0";
                }
                dcpOrder.setProvince(province);
                dcpOrder.setCity(city);
                dcpOrder.setCounty(district);
                dcpOrder.setStreet(town);

            }
            dcpOrder.setGetMan(getMan);
            dcpOrder.setGetManTel(getManTel);
            dcpOrder.setAddress(address);
            dcpOrder.setLatitude(lat);
            dcpOrder.setLongitude(lng);

            /*****************订单下单信息buyer_info节点下内容(仅鲜花、蛋糕品类)***********************/
            String contMan = getMan;
            String contManTel = "";
            if (buyer_info!=null)
            {
                String buyer_phone = buyer_info.optString("buyer_phone","");//下单人手机号（脱敏处理）187****2278
                String buyer_secret_number = buyer_info.optString("buyer_secret_number","");//下单人虚拟号17264842068_9936
                String buyer_real_phone = buyer_info.optString("buyer_real_phone","");//下单人真实手机号，11位手机号，仅当用户在下单时取消“号码保护”时下发
                if (!buyer_real_phone.isEmpty())
                {
                    contManTel = buyer_real_phone;
                }
                else
                {
                    contManTel = buyer_phone;
                }

                if (contManTel.isEmpty())
                {
                    contManTel = getManTel;
                }
            }
            dcpOrder.setContMan(contMan);
            dcpOrder.setContTel(contManTel);

            /*****************订单金额信息amount_info节点下内容***********************/
            //region 金额信息
            //视角1     origin_amount=用户实付金额pay_amount+订单折扣金额discount_amount
            //视角2 origin_amount=订单内商品售卖金额sale_price+配送费原价
            int origin_amount = 0;//订单原始金额，单位分;未计入任何折扣/补贴/优惠的订单金额
            try {
                //订单原始金额，单位分;未计入任何折扣/补贴/优惠的订单金额
                origin_amount = Integer.parseInt(amount_info.optString("origin_amount","0"));
            }
            catch (Exception e) {
            }

            int pay_amount = 0;//用户实付金额，单位分;用户实际支付了的包含了货款、配送费、以及各类折扣/补贴/优惠之后的最终金额
            try {
                //用户实付金额，单位分;用户实际支付了的包含了货款、配送费、以及各类折扣/补贴/优惠之后的最终金额
                pay_amount = Integer.parseInt(amount_info.optString("pay_amount","0"));
            }
            catch (Exception e) {
            }

            int discount_amount = 0;//订单折扣金额，单位分;包含商品、配送费、支付优惠等下列折扣列表中全部折扣的总和
            try {
                //订单折扣金额，单位分;包含商品、配送费、支付优惠等下列折扣列表中全部折扣的总和
                discount_amount = Integer.parseInt(amount_info.optString("origin_amount","0"));
            }
            catch (Exception e) {
            }

            int sale_price = 0;//商品售卖金额，单位分
            try {
                //商品售卖金额，单位分
                sale_price = Integer.parseInt(amount_info.optString("sale_price","0"));
            }
            catch (Exception e) {
            }


            //原价金额配送费；目前没有餐盒费，所以算到原价里面的配送费，用订单原价金额-商品金额合计
            int origin_deliver_fee = origin_amount - sale_price;

            /*int freight_pay_amount = 0;//用户支付配送费，单位分
            try {
                //用户支付配送费，单位分
                freight_pay_amount = Integer.parseInt(amount_info.optString("freight_pay_amount","0"));
            }
            catch (Exception e) {
            }*/

            int activities_fee_amount = 0;//商家活动补贴
            try {
                //商家活动补贴
                activities_fee_amount = Integer.parseInt(amount_info.optString("activities_fee_amount","0"));
            }
            catch (Exception e) {
            }

            //平台活动补贴
            int platform_fee_amount = discount_amount - activities_fee_amount;//单位分;平台活动补贴=总折扣-商家活动补贴

            int estimated_order_income = 0;//商家预计实收
            try {
                //商家预计实收
                estimated_order_income = Integer.parseInt(amount_info.optString("estimated_order_income","0"));
            }
            catch (Exception e) {
            }

            int commission_amount = 0;//佣金
            try {
                //佣金
                commission_amount = Integer.parseInt(amount_info.optString("commission_amount","0"));
            }
            catch (Exception e) {
            }

            double tot_oldAmt = new BigDecimal(origin_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();
            double tot_amt = new BigDecimal(pay_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();
            double tot_disc = new BigDecimal(discount_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();
            double sellerDisc = new BigDecimal(activities_fee_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();// 商家承担优惠
            double platformDisc = new BigDecimal(platform_fee_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();// 平台优惠总额(需要加上平台 hongbao+代理商承担的折扣)
            double packageFee = 0;//目前没有餐盒费
            double shipFee = new BigDecimal(origin_deliver_fee).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();//原价金额对应的配送费
            double incomeAmt = new BigDecimal(estimated_order_income).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();//商家预计实收
            double serviceCharge = new BigDecimal(commission_amount).divide(new BigDecimal(100),2, RoundingMode.HALF_UP).doubleValue();//佣金

            dcpOrder.setTot_oldAmt(tot_oldAmt);
            dcpOrder.setTot_Amt(tot_amt);
            dcpOrder.setPayAmt(tot_amt);
            dcpOrder.setTotDisc(tot_disc);
            dcpOrder.setSellerDisc(sellerDisc);
            dcpOrder.setPlatformDisc(platformDisc);
            dcpOrder.setShipFee(shipFee);
            dcpOrder.setPackageFee(packageFee);
            dcpOrder.setServiceCharge(serviceCharge);
            dcpOrder.setIncomeAmt(incomeAmt);
            String payStatus = "3";// 默认都是已支付
            dcpOrder.setPayStatus(payStatus);// 支付状态 1.未支付 2.部分支付 3.付清
            dcpOrder.setShopShareShipfee(0);// 商家替用户承担的配送费
            dcpOrder.setRefundAmt(0);// 部分退单 的退款金额
            dcpOrder.setRefundReason("");// 退单原因

            //endregion
            /***********************发票相关处理【抖音外卖目前没有】*******************************/
            orderInvoice dcpOrderInvoiceDetail = new orderInvoice();
            //String isInvoice = "N";// 是否开发票
            dcpOrderInvoiceDetail.setIsInvoice("N");
            dcpOrderInvoiceDetail.setInvoiceType("");
            dcpOrderInvoiceDetail.setInvoiceTitle("");
            dcpOrderInvoiceDetail.setTaxRegNumber("");
            dcpOrder.setInvoiceDetail(dcpOrderInvoiceDetail);

            /*****************订单商品信息products节点下内容***********************/
            //region 处理商品信息
            int item = 0;// 项次
            double tot_qty = 0;
            //抖音都是套餐商品的形式，所以不用展开套餐
            dcpOrder.setIsApportion("Y");//不需要展开套餐
            for (int i = 0; i < productsJSONArray.length(); i++)
            {
                try
                {
                    item++;
                    //int mItem = item++ //这种写法错误，先赋值，然后才++，改成下面写法
                    int mItem = item;//相当于套餐主商品项次
                    orderGoodsItem goodsItem_main = new orderGoodsItem();//外层套餐主商品
                    goodsItem_main.setVirtual("Y");//外层虚拟商品
                    goodsItem_main.setPackageType("2");////1、正常商品 2、套餐主商品  3、套餐子商品
                    goodsItem_main.setItem(mItem+"");//相当于套餐主商品项次
                    goodsItem_main.setMessages(new ArrayList<orderGoodsItemMessage>());
                    goodsItem_main.setAgioInfo(new ArrayList<orderGoodsItemAgio>());

                    JSONObject productObj = productsJSONArray.getJSONObject(i);
                    JSONArray commodities = productObj.optJSONArray("commodities");
                    //先搞外层套餐虚拟商品
                    String product_id = productObj.optString("product_id");// APP方菜品id
                    String product_name = productObj.optString("product_name");// 菜品名称
                    String sku_id = productObj.optString("sku_id");// sku编码
                    String out_id = productObj.optString("out_id");// 三方商品条码
                    String product_num = productObj.optString("num");// 商品数量
                    String product_origin_amount = productObj.optString("origin_amount");//金额
                    String product_unit_name = "份";//固定
                    double product_price = 0;
                    double product_amt = 0;
                    int num = 1;
                    try
                    {
                        num = Integer.parseInt(product_num);
                        BigDecimal product_origin_amount_b = new BigDecimal(product_origin_amount);
                        BigDecimal product_amt_b = product_origin_amount_b.divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
                        product_amt = product_amt_b.doubleValue();
                        product_price = product_amt_b.divide(new BigDecimal(num),2, RoundingMode.HALF_UP).doubleValue();

                    }
                    catch (Exception e)
                    {

                    }
                    goodsItem_main.setPluNo(out_id);
                    goodsItem_main.setPluBarcode(out_id);
                    goodsItem_main.setSkuId(sku_id);
                    goodsItem_main.setPluName(product_name);
                    goodsItem_main.setSpecName("");
                    goodsItem_main.setAttrName("");
                    goodsItem_main.setFeatureNo("");
                    goodsItem_main.setFeatureName("");
                    goodsItem_main.setsUnit("");
                    goodsItem_main.setsUnitName(product_unit_name);
                    goodsItem_main.setPrice(product_price);
                    goodsItem_main.setOldPrice(product_price);
                    goodsItem_main.setQty(num);
                    goodsItem_main.setAmt(product_amt);
                    goodsItem_main.setOldAmt(product_amt);
                    goodsItem_main.setDisc(0);
                    goodsItem_main.setBoxNum(0);
                    goodsItem_main.setBoxPrice(0);
                    goodsItem_main.setGoodsGroup("");
                    goodsItem_main.setIsMemo("N");

                    dcpOrder.getGoodsList().add(goodsItem_main);

                    //解析套餐分组下面的商品
                    if (commodities==null||commodities.length()==0)
                    {
                        continue;
                    }

                    for (int j = 0; j < commodities.length(); j++)
                    {
                        try
                        {
                            JSONObject commoditiesObj = commodities.getJSONObject(j);
                            JSONArray items = commoditiesObj.optJSONArray("items");
                            if (items==null||items.length()==0)
                            {
                                continue;
                            }
                            //相当于套餐分类，如 主食、小食
                            String group_name = commoditiesObj.optString("group_name","");
                            for (int k = 0; k < items.length(); k++)
                            {
                                try
                                {
                                    JSONObject itemsObj = items.getJSONObject(k);
                                    item++;
                                    orderGoodsItem goodsItem = new orderGoodsItem();
                                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                                    goodsItem.setPackageType("3");////1、正常商品 2、套餐主商品  3、套餐子商品
                                    goodsItem.setPackageMitem(mItem+"");//套餐主商品项次
                                    goodsItem.setItem(item+"");//商品项次

                                    String name = itemsObj.optString("name");//商品名称
                                    String count = itemsObj.optString("count","0");// 菜品名称
                                    String item_product_id = itemsObj.optString("product_id");// 单品IDproduct_id
                                    String item_out_id = itemsObj.optString("out_id");//单品ID
                                    String item_sku_id = itemsObj.optString("sku_id");// //规格id 不一定有
                                    String item_out_sku_id = itemsObj.optString("out_sku_id");// 三方规格id 不一定有

                                    if (item_sku_id==null||item_sku_id.trim().isEmpty()||"-1".equals(item_sku_id))
                                    {
                                        item_sku_id = item_product_id;
                                    }
                                    if (item_out_sku_id==null||item_out_sku_id.trim().isEmpty()||"-1".equals(item_out_sku_id))
                                    {
                                        item_out_sku_id = item_out_id;
                                    }
                                    String unitName = itemsObj.optString("unit");// 单位
                                    String price = itemsObj.optString("price","");//价格
                                    String spec = "";
                                    String attr = "";
                                    int count_i = 0;
                                    double item_price = 0;
                                    double item_amt = 0;

                                    try {
                                        count_i = Integer.parseInt(count);
                                        BigDecimal price_b = new BigDecimal(price).divide(new BigDecimal("100"),2, RoundingMode.HALF_UP);
                                        item_price = price_b.doubleValue();
                                        item_amt = price_b.multiply(new BigDecimal(count)).setScale(2,RoundingMode.HALF_UP).doubleValue();
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    tot_qty +=count_i;

                                    //region 属性名称比如 ，标准杯，去冰
                                    JSONArray sku_attr_list = itemsObj.optJSONArray("sku_attr_list");
                                    if (sku_attr_list!=null&&sku_attr_list.length()>0)
                                    {
                                        for (int h = 0;h <sku_attr_list.length();h++ )
                                        {
                                            try {
                                                String attr_name = sku_attr_list.getJSONObject(h).optString("attr_name","");
                                                if (attr_name.isEmpty())
                                                {
                                                    continue;
                                                }
                                                if (attr.isEmpty())
                                                {
                                                    attr = attr_name;
                                                }
                                                else
                                                {
                                                    attr = attr+","+attr_name;
                                                }
                                            }
                                            catch (Exception e) {
                                                HelpTools.writelog_waimai("解析抖音外卖商品信息sku_attr_list节点失败：" + e.getMessage()+",单号orderNo="+orderNo);
                                                continue;
                                            }


                                        }

                                    }
                                    //endregion

                                    goodsItem.setPluNo(item_out_sku_id);
                                    goodsItem.setPluBarcode(item_out_sku_id);
                                    goodsItem.setSkuId(item_sku_id);
                                    goodsItem.setPluName(name);
                                    goodsItem.setSpecName(spec);
                                    goodsItem.setAttrName(attr);
                                    goodsItem.setFeatureNo("");
                                    goodsItem.setFeatureName("");
                                    goodsItem.setsUnit("");
                                    goodsItem.setsUnitName(unitName);
                                    goodsItem.setPrice(item_price);
                                    goodsItem.setOldPrice(item_price);
                                    goodsItem.setQty(count_i);
                                    goodsItem.setAmt(item_amt);
                                    goodsItem.setOldAmt(item_amt);
                                    goodsItem.setDisc(0);
                                    goodsItem.setBoxNum(0);
                                    goodsItem.setBoxPrice(0);
                                    goodsItem.setGoodsGroup(group_name);
                                    goodsItem.setIsMemo("N");

                                    dcpOrder.getGoodsList().add(goodsItem);

                                }
                                catch (Exception e)
                                {
                                    HelpTools.writelog_waimai("解析抖音外卖商品信息items节点失败：" + e.getMessage()+",单号orderNo="+orderNo);
                                    continue;
                                }
                            }

                        }
                        catch (Exception e)
                        {
                            HelpTools.writelog_waimai("解析抖音外卖商品信息commodities节点失败：" + e.getMessage()+",单号orderNo="+orderNo);
                            continue;
                        }

                    }




                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("解析抖音外卖products节点失败：" + e.getMessage()+",单号orderNo="+orderNo);
                    continue;
                }

            }
            //endregion
            dcpOrder.setTot_qty(tot_qty);
            dcpOrder.setTotQty(dcpOrder.getTot_qty());
            dcpOrder.setLoadDocTypeName("抖音外卖");
            dcpOrder.setChannelIdName("抖音外卖");

            dcpOrder.setStatus("1");// 订单状态 订单开立
            dcpOrder.setRefundStatus("1");// 退单状态 1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
            // 传进来的 状态
            if (paraStatus != null && paraStatus.trim().length() > 0)
            {
                dcpOrder.setStatus(paraStatus);// 订单状态
                //HelpTools.writelog_waimai("【ELM开始写缓存】" + "orderNo=" + orderNo + " 传参paraStatus=" + paraStatus);
            }
            if (paraRefundStatus != null && paraRefundStatus.trim().length() > 0)
            {
                dcpOrder.setRefundStatus(paraRefundStatus);
                //HelpTools.writelog_waimai("【ELM开始写缓存】" + "orderNo=" + orderNo + " 传参paraRefundStatus=" + paraRefundStatus);
            }

            //调用支付方式
            StringBuffer errorPayMessage = new StringBuffer();
            HelpTools.updateOrderPayByMapping(dcpOrder, errorPayMessage);
            errorPayMessage = new StringBuffer();
            HelpTools.updateOrderDetailInfo(dcpOrder, errorPayMessage);
            //不用展开套餐
            //HelpTools.updateOrderWithPackage(dcpOrder, "", errorPayMessage);

            String status_json = dcpOrder.getStatus();// 获取下订单状态

            ParseJson pj = new ParseJson();
            String Response_json = pj.beanToJson(dcpOrder);

            String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopno;
            String hash_key = orderNo;
            String redis_key_dywm = orderRedisKeyInfo.redis_OrderTableName + ":"+"DYWM";// elm 写新订单写2次缓存不同主键，方便后续状态变更
            try
            {
                // 有做映射的门店才写缓存，数据库还是要存的
                //非降级订单才写缓存
                if (shopno != null && shopno.trim().length() > 0&&"N".equals(downgraded))
                {

                    boolean IsUpdateRedis = true;
                    RedisPosPub redis = new RedisPosPub();
                    if (!"1".equals(dcpOrder.getStatus()))
                    {
                        //已接单、已完成状态不在写缓存
                        IsUpdateRedis = false;
                    }
                    else
                    {
                        HelpTools.writelog_waimai(
                                "【抖音外卖开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            if (status_json != null && status_json.equals("1")) // 新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
                            {
                                IsUpdateRedis = false;
                                HelpTools.writelog_waimai("【抖音外卖订单开立状态】【抖音外卖已经存在hash_key的缓存】【说明缓存已经最新状态不用更新缓存】！"
                                        + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
						/*else
						{
							redis.DeleteHkey(redis_key, hash_key);//
							HelpTools.writelog_waimai(
									"【ELM删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						}*/
                        }
                        else
                        {
                            //防止重复推送新订单
                            if (status_json != null && status_json.equals("1")) // 新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
                            {
                                boolean isexistNewOrder = redis.IsExistHashKey(redis_key_dywm, hash_key);
                                if (isexistNewOrder)
                                {
                                    IsUpdateRedis = false;
                                    HelpTools.writelog_waimai("【抖音外卖订单开立状态】【重复推送】【之前新订单缓存已经处理被删了】！"
                                            + "redis_key:" + redis_key_dywm + " hash_key:" + hash_key);
                                }

                            }
                        }
                    }

                    if (IsUpdateRedis)
                    {
                        boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                        if (nret)
                        {
                            HelpTools.writelog_waimai(
                                    "【抖音外卖写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                        } else
                        {
                            HelpTools.writelog_waimai(
                                    "【抖音外卖写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                        }
                    }

                    boolean nret_elm = redis.setHashMap(redis_key_dywm, hash_key, Response_json);
                    if (nret_elm)
                    {
                        HelpTools.writelog_waimai(
                                "【抖音外卖写DYSM缓存】OK" + " redis_key:" + redis_key_dywm + " hash_key:" + hash_key);
                    } else
                    {
                        HelpTools.writelog_waimai(
                                "【抖音外卖写DYSM缓存】Error" + " redis_key:" + redis_key_dywm + " hash_key:" + hash_key);
                    }
                    // redis.Close();
                }
            } catch (Exception e)
            {
                HelpTools.writelog_waimai(
                        "【抖音外卖写缓存】异常:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }

            return dcpOrder;
        }
        catch (Exception e)
        {
            HelpTools.writelog_waimai("【抖音外卖】【新订单的消息】解析异常:" + e.getMessage()+",【消息ID】"+ messageId);
            return null;
        }

    }


    // 更新缓存
    private order UpdateOrderProcess(String poi, String orderno, String status, String refundStatus) throws Exception
    {
        String Response_json = "";
        try
        {
            RedisPosPub redis = new RedisPosPub();
            String redis_key = orderRedisKeyInfo.redis_OrderTableName+":DYWM";
            String hash_key = orderno;
            String ordermap = "";
            order orderDB = null;
            boolean needQueryDB = false;// 是否需要从数据库查询对应erp门店
            try
            {
                ordermap = redis.getHashMap(redis_key, hash_key);
            } catch (Exception e)
            {

            }
            ParseJson pj = new ParseJson();
            // 缓存中如果已经删除了
            if (ordermap == null || ordermap.isEmpty() || ordermap.length() == 0)
            {
                String eId = "";
                String erpshopno = "";
                String channelId = "";
                String erpShopName = "";
                Map<String, String>	mappingShopMap = HelpTools.GetDYMappingShop(poi);
                eId = mappingShopMap.get("eId");
                erpshopno = mappingShopMap.get("erpShopNo");
                channelId = mappingShopMap.get("channelId");
                erpShopName = mappingShopMap.getOrDefault("erpShopName", "");

                // 如果WMORDER_ELM缓存没有，那么不一定有单身明细，查询下数据库
                HelpTools.writelog_waimai("【抖音外卖】更新订单状态查询数据库开始：eid=" + eId + " shopNo=" + erpshopno
                        + " orderNO=" + orderno + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus
                );
                orderDB = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao,eId,  orderLoadDocType.DYWM, orderno);
                ordermap = pj.beanToJson(orderDB);
                HelpTools.writelog_waimai("【抖音外卖】更新订单状态查询数据库完成：eid=" + eId + " shopNo=" + erpshopno
                        + " orderNO=" + orderno + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus
                        + " 查询数据库返回:"+ordermap );

                //数据库没有，在线查询接口
                if(orderDB==null)
                {

                    // 如果WMORDER_ELM缓存没有，那么不一定有单身明细，在线查询下
                    try
                    {
                        // 如果WMORDER_ELM缓存没有，那么不一定有单身明细，在线查询下
                        HelpTools.writelog_waimai(
                                "【更新的单据抖音外卖缓存"+redis_key+"不存在】准备在线查询接口" + " 订单号orderNO:" + orderno + " 订单状态status=" + status);

                    } catch (Exception e)
                    {
                        // TODO: handle exception

                    }
                }



                if (orderDB == null)
                {
                    orderDB = new order();
                    orderDB.seteId(eId);
                    orderDB.setLoadDocType(orderLoadDocType.DYWM);
                    orderDB.setOrderNo(orderno);
                    orderDB.setLoadDocOrderNo(orderno);
                    orderDB.setStatus(status);
                    orderDB.setRefundStatus(refundStatus);
                    orderDB.setGoodsList(new ArrayList<orderGoodsItem>());
                }




            }
            else
            {
                orderDB = pj.jsonToBean(ordermap, new TypeToken<order>(){});
            }

            //部分退单成功
            if (refundStatus.equals("10")) // 部分退款
            {
            }

            orderDB.setStatus(status);
            orderDB.setRefundStatus(refundStatus);
            //orderDB.setRefundReason("");


            if (status.equals("11") || status.equals("3") || status.equals("12"))
            {
                redis.DeleteHkey(redis_key, hash_key);//
                String sss = "订单已完成";
                boolean isUpdateRQTY = false;//是否更新
                if (status.equals("3"))
                {
                    sss = "订单已取消";
                    isUpdateRQTY = true;
                } else if (status.equals("12"))
                {
                    sss = "订单已退款";
                    isUpdateRQTY = true;
                }

                if (isUpdateRQTY)
                {
                    if (orderDB.getGoodsList()!=null&&orderDB.getGoodsList().isEmpty()==false)
                    {
                        for (orderGoodsItem goodsItem : orderDB.getGoodsList())
                        {
                            goodsItem.setrQty(goodsItem.getQty());
                        }
                        HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
                    }

                }

                HelpTools.writelog_waimai(redis_key + "(" + hash_key + sss + ")【DYWM删除缓存】成功！");
            }

            Response_json = pj.beanToJson(orderDB);

            order dcpOrder = orderDB;

            String eId = dcpOrder.geteId();
            String shopNo = dcpOrder.getShopNo();

            redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" +eId + ":" + shopNo;
            if (shopNo != null && shopNo.trim().length() > 0)
            {
                if (status.equals("3") || status.equals("12"))
                {
                    HelpTools.setWaiMaiOrderToSaleOrRefundRedisLock("1",eId,orderno);
                }
				/*boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
				if (isexistHashkey)
				{
					redis.DeleteHkey(redis_key, hash_key);//
					HelpTools.writelog_waimai(
							"【ELM删除存在hash_key的缓存ELM】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				}*/

                if ("life_takeout_order_merchant_receive".equals(event))
                {
                    //已接单、已完成状态不在写缓存
                }
                else
                {
                    HelpTools.writelog_waimai("【抖音外卖订单状态变更开始更新缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key
                            + " hash_value:" + Response_json);
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret)
                    {
                        HelpTools.writelog_waimai("【抖音外卖订单状态变更更新缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    } else
                    {
                        HelpTools.writelog_waimai("【抖音外卖订单状态变更更新缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                }


            }

            return dcpOrder;
        } catch (Exception e)
        {
            HelpTools.writelog_waimai("抖音外卖订单状态变更,更新缓存中抖音外卖内容异常！" + e.getMessage() + " OrderNO:" + orderno);
            return null;
        }

    }

    // 存数据库
    private void SaveOrder(order dcpOrder) throws Exception
    {
        if(dcpOrder==null)
        {
            return;
        }

        String orderNo = dcpOrder.getOrderNo();
        try
        {
            String orderstatus = dcpOrder.getStatus();// 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 9.待配送 10.已发货 11.已完成 12.已退单

            String refundStatus = dcpOrder.getRefundStatus();

            String eId = dcpOrder.geteId();
            String shopNo = dcpOrder.getShopNo();
            String loadDocType = dcpOrder.getLoadDocType();

            if (orderstatus != null)
            {
                if (orderstatus.equals("1")) // 插入
                {
                    StringBuffer errorMessage = new StringBuffer();
                    List<order> orderList = new ArrayList<order>();
                    orderList.add(dcpOrder);
                    ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, errorMessage,null);
                    if (DPB != null && DPB.size() > 0)
                    {
                        for (DataProcessBean dataProcessBean : DPB)
                        {
                            this.addProcessData(dataProcessBean);
                        }
                        try
                        {
                            this.doExecuteDataToDB();
                            HelpTools.writelog_waimai("【抖音外卖保存数据库成功】" + " 订单号orderNo:" + orderNo);

                            //商品资料异常
                            HelpTools.waimaiOrderAbnormalSave(dcpOrder, errorMessage);

                        }
                        catch (Exception e)
                        {
                            this.pData.clear();
                            // 如果保存异常了，判断数据库有没有，如果有了，就相当于重复推送了
                        }

                        // 写订单日志
                        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                        orderStatusLog onelv1 = new orderStatusLog();
                        onelv1.setLoadDocType(loadDocType);
                        onelv1.setChannelId(dcpOrder.getChannelId());
                        onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                        onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                        onelv1.seteId(eId);
                        String opNO = "";
                        String o_opName = "抖音外卖用户";



                        onelv1.setOpName(o_opName);
                        onelv1.setOpNo(opNO);
                        onelv1.setShopNo(shopNo);
                        onelv1.setOrderNo(orderNo);
                        onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                        onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                        String statusType = "";
                        String updateStaus = orderstatus;
                        statusType = "1";// 订单状态
                        onelv1.setStatusType(statusType);
                        onelv1.setStatus(updateStaus);
                        StringBuilder statusTypeNameObj = new StringBuilder();
                        String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                        String statusTypeName = statusTypeNameObj.toString();
                        onelv1.setStatusTypeName(statusTypeName);
                        onelv1.setStatusName(statusName);

                        String memo = "";
                        memo += statusName;
                        if ("Y".equals(dcpOrder.getDowngraded()))
                        {
                            memo +="<br>降级订单";
                        }
                        onelv1.setMemo(memo);
                        onelv1.setDisplay("1");

                        String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        onelv1.setUpdate_time(updateDatetime);

                        orderStatusLogList.add(onelv1);

                        StringBuilder errorStatusLogMessage = new StringBuilder();
                        boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                        if (nRet) {
                            HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
                        }
                        this.pData.clear();


                    }

                } else// 更新 数据库状态
                {
                    HelpTools.writelog_waimai("【抖音外卖开始更新数据库】" + " 订单号orderNo:" + orderNo + " 订单状态status=" + orderstatus);
                    UpdateOrderStatus(dcpOrder);
                }

            }

        } catch (SQLException e)
        {
            HelpTools.writelog_waimai("【抖音外卖执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
        } catch (Exception e)
        {
            HelpTools.writelog_waimai("【抖音外卖执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
        }
    }

    private void UpdateOrderStatus(order dcpOrder) throws Exception
    {
        if(dcpOrder==null)
        {
            return;
        }
        String orderNo = dcpOrder.getOrderNo();
        try
        {
            //JSONObject obj = new JSONObject(req);
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            ParseJson pj = new ParseJson();

            String status = dcpOrder.getStatus();
            String refundStatus = dcpOrder.getRefundStatus();
            String eId = dcpOrder.geteId();
            String shopNo = dcpOrder.getShopNo();
            String loadDocType = dcpOrder.getLoadDocType();

            String reason = dcpOrder.getRefundReason();// 退单原因

            if (reason != null && reason.length() > 255)
            {
                reason = reason.substring(0, 255);
            }

            double partRefundAmt = 0;
            partRefundAmt = dcpOrder.getRefundAmt();

            Map<String, Object> orderHead = new HashMap<String, Object>();
            boolean IsExistOrder = IsExistOrder(eId, orderNo, orderHead);

            String status_DB = "";
            String refundStatus_DB = "";
            try
            {
                status_DB = orderHead.get("STATUS").toString();
                refundStatus_DB = orderHead.get("REFUNDSTATUS").toString();
                if (shopNo == null || shopNo.trim().isEmpty() || shopNo.trim().length() == 0)
                {
                    shopNo = orderHead.get("SHOP").toString();
                }
            }
            catch (Exception e)
            {

            }

            if (IsExistOrder) // 存在就Update
            {
                if (refundStatus.equals("2") || refundStatus.equals("7")) // 申请了退款，或者部分退款
                {
                    try
                    {
                        /*
                         * String status_DB =orderHead.get("STATUS").toString();
                         * String refundStatus_DB
                         * =orderHead.get("REFUNDSTATUS").toString();
                         */
                        if (refundStatus.equals("2"))
                        {
                            if (refundStatus_DB.equals("6") || refundStatus_DB.equals("3") || refundStatus_DB.equals("5"))
                            {
                                HelpTools.writelog_waimai("【抖音外卖退单重复推送退单申请】" + " 订单号orderNo:" + orderNo + " 订单状态status="
                                        + status + " 退单状态refundStatus=" + refundStatus);
                                // 已经处理了的退款，删除缓存
                                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                                String hash_key = orderNo;

                                if (refundStatus_DB.equals("6")) // 不能直接删缓存，否则pos收不到退单消息																	//
                                {
                                    //obj.put("status", "12");
                                    //obj.put("refundStatus", refundStatus_DB);

                                    dcpOrder.setStatus("12");
                                    dcpOrder.setRefundStatus(refundStatus_DB);
                                    if (dcpOrder.getGoodsList()!=null&&dcpOrder.getGoodsList().isEmpty()==false)
                                    {
                                        for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
                                        {
                                            goodsItem.setrQty(goodsItem.getQty());
                                        }
                                        HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
                                    }
                                    String hash_value = pj.beanToJson(dcpOrder);
                                    WriteRedis(redis_key, hash_key, hash_value);

                                }
                                else
                                {
                                    DeleteRedis(redis_key, hash_key);
                                }
                                return;
                            }

                        } else
                        {
                            if (refundStatus_DB.equals("10") || refundStatus_DB.equals("8") || refundStatus_DB.equals("9"))
                            {
                                // 已经处理了的部分退款，删除缓存
                                HelpTools.writelog_waimai("【抖音外卖部分退单重复推送部分退单申请】" + " 订单号orderNO:" + orderNo
                                        + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus);
                                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                                String hash_key = orderNo;
                                // DeleteRedis(redis_key,hash_key);
                                if (refundStatus_DB.equals("10")) // 这时候
                                // 不能直接删缓存，否则pos收不到退单消息
                                {
                                    //obj.put("refundStatus", refundStatus_DB);
                                    dcpOrder.setRefundStatus(refundStatus_DB);

                                    String hash_value = pj.beanToJson(dcpOrder);
                                    WriteRedis(redis_key, hash_key, hash_value);

                                } else
                                {
                                    DeleteRedis(redis_key, hash_key);
                                }
                                return;
                            }

                        }

                    } catch (Exception e)
                    {

                    }

                }

                UptBean ub1 = null;
                ub1 = new UptBean("DCP_ORDER");
                ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

                // 部分退单，最后还推送已完成订单refundStatus=1，如果数据库已经是退单成功状态,refundStatus就不更新了
                if (refundStatus_DB != null && refundStatus_DB.equals("10")) //
                {

                } else
                {
                    ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus, Types.VARCHAR));
                }

                if (refundStatus.equals("2") || refundStatus.equals("7"))
                {
                    HelpTools.writelog_fileName("【抖音外卖】申请退款更新数据，单号orderNO=" + orderNo + " 退货原因refundReason=" + reason,
                            "refunReasonLog");
                    HelpTools.writelog_waimai("【抖音外卖】申请退款更新数据，单号orderNO=" + orderNo + " 退货原因refundReason=" + reason);
                    ub1.addUpdateValue("REFUNDREASON", new DataValue(reason, Types.VARCHAR));
                }

                if (refundStatus.equals("10"))
                {
                    ub1.addUpdateValue("REFUNDAMT", new DataValue(partRefundAmt, Types.VARCHAR));// 这样存就不会出现float小数点问题
                    ub1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(partRefundAmt,Types.VARCHAR));
                    ub1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(partRefundAmt,Types.VARCHAR));
                    //ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
                    ub1.addUpdateValue("LASTREFUNDTIME",
                            new DataValue(
                                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()),
                                    Types.VARCHAR));
                }

                ub1.addUpdateValue("UPDATE_TIME",
                        new DataValue(
                                new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()),
                                Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(
                        new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                if(status.equals("11")||status.equals("3")||status.equals("12"))
                {
                    ub1.addUpdateValue("COMPLETE_DATETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    if(status.equals("3")||status.equals("12"))
                    {
                        ub1.addUpdateValue("LASTREFUNDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    }
                }
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
                HelpTools.writelog_waimai("【更新STATUS成功】 " + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
                // 同意部分退款后，再插入退款的明细商品 不用放在同一个事务里面
                if (refundStatus.equals("10"))
                {
                    HelpTools.writelog_waimai("【抖音外卖部分退单新增单身开始】" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
                    boolean isExist = false;
                    String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    try
                    {

                        StringBuffer error_partRefundGoods = new StringBuffer();
                        List<orderGoodsItem> goodsList_partRefund = HelpTools.waimaiPartRefundGoodsProcess(dcpOrder, error_partRefundGoods);//真正的转化出来的部分退单单身(包含套餐商品处理)

                        //更新原单
                        for (orderGoodsItem goodsItem : goodsList_partRefund)
                        {
                            try
                            {

                                String oItem = goodsItem.getoItem();//原单对应的商品项次

                                double qty = goodsItem.getQty();

                                //先简单处理
                                String execsql = "update DCP_ORDER_detail set rqty="+qty+""
                                        + " where rownum =1 and eid='"+eId+"' and orderno='"+orderNo+"' and item="+oItem+"  and qty>="+qty;

                                HelpTools.writelog_waimai("【部分退单成功后】更新原单单身已退数量sql="+execsql+",订单号orderNo="+orderNo+",原单项次item="+oItem);
                                ExecBean exBean = new ExecBean(execsql);
                                this.addProcessData(new DataProcessBean(exBean));
                                isExist = true;

                            }
                            catch (Exception e)
                            {
                                HelpTools.writelog_waimai("【开始保存数据库】添加部分退单单身异常："+e.getMessage()+"(eid="+eId+" shopno="+shopNo+" orderNo="+orderNo+")");
                                continue;
                            }
                        }


                        if (isExist)
                        {
                            this.doExecuteDataToDB();
                            HelpTools.writelog_waimai("【抖音外卖部分退单单身添加执行语句成功】" + " 订单号orderNO:" + orderNo + " 订单状态status="
                                    + status + " 退单状态refundStatus=" + refundStatus);
                            StringBuffer error_partRefund = new StringBuffer();
                            HelpTools.waimaiOrderPartRefundProcess(dcpOrder, goodsList_partRefund, "", error_partRefund);
                        }
                        else
                        {
                            HelpTools.writelog_waimai("【抖音外卖部分退单单身为空！】" + " 订单号orderNO:" + orderNo + " 订单状态status="
                                    + status + " 退单状态refundStatus=" + refundStatus);
                        }

                    }
                    catch (SQLException e)
                    {
                        HelpTools.writelog_waimai("【抖音外卖部分退单新增单身执行语句】异常：" + e.getMessage() + " 订单号orderNO:" + orderNo );
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                        HelpTools.writelog_waimai("【抖音外卖部分退单新增单身执行语句】异常：" + e.getMessage() + " 订单号orderNO:" + orderNo );
                    }
                }



                //推送取消或者退单消息时，执行下。
                if(status.equals("3")||status.equals("12"))
                {
                    StringBuffer RefundOrCancelError = new StringBuffer();
                    HelpTools.OrderRefundOrCancelProcess(dcpOrder, sdate, RefundOrCancelError);
                }

            }
            else
            {
                HelpTools.writelog_waimai("【更新的单据不存在】开始插入到数据库" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);

            }

            // region写订单日志
            try
            {

                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(dcpOrder.getChannelId());
                onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                onelv1.seteId(eId);
                String opNO = "";
                String o_opName = "抖音用户";

                onelv1.setOpName(o_opName);
                onelv1.setOpNo(opNO);
                onelv1.setShopNo(shopNo);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                String statusType = "";
                String updateStaus = status;
                statusType = "1";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);

                String memo = "";
                memo += statusName;
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");

                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);

                //orderStatusLogList.add(onelv1); //这里需要注意（先不添加，没有申请退单在添加，因为申请退单之前的订单状态已经存了)，

                // 如果有申请退单
                if (refundStatus.equals("2") || refundStatus.equals("7"))
                {
                    orderStatusLog onelv2 = new orderStatusLog();
                    onelv2.setLoadDocType(loadDocType);
                    onelv2.setChannelId(dcpOrder.getChannelId());
                    onelv2.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                    onelv2.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                    onelv2.seteId(eId);
                    onelv2.setOpName(o_opName);
                    onelv2.setOpNo(opNO);
                    onelv2.setShopNo(shopNo);
                    onelv2.setOrderNo(orderNo);
                    onelv2.setMachShopNo(dcpOrder.getMachShopNo());
                    onelv2.setShippingShopNo(dcpOrder.getShippingShopNo());

                    statusType = "3";// 退单状态
                    updateStaus = refundStatus;
                    onelv2.setStatusType(statusType);
                    onelv2.setStatus(updateStaus);
                    statusTypeNameObj = new StringBuilder();
                    statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    statusTypeName = statusTypeNameObj.toString();
                    onelv2.setStatusTypeName(statusTypeName);
                    onelv2.setStatusName(statusName);

                    memo = "";
                    memo += statusName;
                    onelv2.setMemo(memo);
                    onelv2.setDisplay("1");

                    updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv2.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv2);


                }
                else
                {
                    orderStatusLogList.add(onelv1);
                }

                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
                }
            } catch (Exception e)
            {

            }
            // endregion

        }
        catch (SQLException e)
        {
            this.pData.clear();
            HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
        }
        catch (Exception e)
        {
            this.pData.clear();
            HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
        }

    }

    /**
     * 查询下订单是否存在，返回订单状态信息
     * @param eid
     * @param orderNO
     * @param orderHead
     * @return
     * @throws Exception
     */
    private boolean IsExistOrder(String eid,  String orderNO,Map<String, Object> orderHead) throws Exception
    {
        boolean nRet = false;

        String sql = "select * from DCP_order ";
        sql += " where ORDERNO='" + orderNO + "' and EID='" + eid + "'";

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
        {
            nRet = true;
            String status = getQDataDetail.get(0).get("STATUS").toString();
            String refundStatus = getQDataDetail.get(0).get("REFUNDSTATUS").toString();
            String shop = getQDataDetail.get(0).get("SHOP").toString();
            String channelId = getQDataDetail.get(0).get("CHANNELID").toString();
            orderHead.put("STATUS", status);
            orderHead.put("REFUNDSTATUS", refundStatus);
            orderHead.put("SHOP", shop);
            orderHead.put("CHANNELID", channelId);

        }
        return nRet;

    }

    private void UpdateOrderShippingStatus(String content)
    {
        try
        {
            JSONObject contentObj = new JSONObject(content);
            String orderNo = contentObj.optString("order_id","");
            String poi_id = contentObj.optString("poi_id","");
            String delivery_status = contentObj.optString("delivery_status","");
            String rider_name = contentObj.optString("delivery_status","");
            String rider_phone = contentObj.optString("delivery_status","");
            String deliveryStatus = "";// -1预下单 0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常,5=手动撤销 6 到店 7重下单
            if ("102".equals(delivery_status))
            {
                //102：骑手已接单
                deliveryStatus = "1";
            }
            else if ("103".equals(delivery_status))
            {
                //103：骑手已到店
                deliveryStatus = "6";
            }
            else if ("104".equals(delivery_status))
            {
                //104：骑手配送中
                deliveryStatus = "2";
            }
            else if ("200".equals(delivery_status))
            {
                //200：骑手已送达
                deliveryStatus = "3";
            }
            else if ("300".equals(delivery_status))
            {
                //300：运单已撤销
                deliveryStatus = "4";
            }
            else
            {
                deliveryStatus = "";
                HelpTools.writelog_waimai("【抖音外卖更新配送状态DeliveryStutas异常】平台配送状态节点delivery_status=" + delivery_status + "，配送状态未知，订单号orderNO:" + orderNo );
                return;
            }

            String loadDocType = orderLoadDocType.DYWM;

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_ORDER");
            ub1.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus, Types.VARCHAR));
            ub1.addUpdateValue("DELNAME", new DataValue(rider_name, Types.VARCHAR));
            if (rider_phone != null && rider_phone.trim().length() > 0)
            {
                ub1.addUpdateValue("DELTELEPHONE", new DataValue(rider_phone, Types.VARCHAR));
            }

            ub1.addUpdateValue("UPDATE_TIME", new DataValue(
                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(
                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();
            HelpTools.writelog_waimai(
                    "【抖音外卖更新配送状态DeliveryStutas成功】" + " 订单号orderNO:" + orderNo + " 配送状态DeliveryStutas=" + deliveryStatus);

            String eId = "99";
            String shopNo = "";
            String channelId = "";
            Map<String, String>	mappingShopMap = HelpTools.GetDYMappingShop(poi_id);
            eId = mappingShopMap.get("eId");
            shopNo = mappingShopMap.get("erpShopNo");
            channelId = mappingShopMap.get("channelId");

            if (eId == null || eId.isEmpty())
            {
                eId = "99";
            }

            // region 写日志
            try
            {

                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo(orderNo);
                onelv1.seteId(eId);
                String opNO = "";
                String o_opName = "骑士：" + rider_name;


                onelv1.setOpName(o_opName);
                onelv1.setOpNo(opNO);
                onelv1.setShopNo(shopNo);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(shopNo);
                onelv1.setShippingShopNo(shopNo);
                String statusType = "2";//配送状态
                String updateStaus = deliveryStatus;

                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);

                String memo = "";
                memo += statusName;

                if (rider_phone != null && rider_phone.isEmpty() == false)
                {
                    memo += " 配送电话-->" + rider_phone;
                }
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");

                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);

                orderStatusLogList.add(onelv1);

                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
                }
                this.pData.clear();

            }
            catch (Exception e)
            {

            }
            // endregion

        }
        catch (Exception e)
        {

        }
    }

    private void WriteRedis(String redis_key, String hash_key, String hash_value) throws Exception
    {
        try
        {
            RedisPosPub redis = new RedisPosPub();
            HelpTools.writelog_waimai(
                    "【开始写缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + hash_value);
            //redis.DeleteHkey(redis_key, hash_key);//
            boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
            if (nret)
            {
                HelpTools.writelog_waimai("【抖音外卖写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            } else
            {
                HelpTools.writelog_waimai("【抖音外卖写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            // redis.Close();

        } catch (Exception e)
        {
            HelpTools.writelog_waimai(
                    "【开始写缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
        }
    }

    private void DeleteRedis(String redis_key, String hash_key) throws Exception
    {
        try
        {
            RedisPosPub redis = new RedisPosPub();
            HelpTools.writelog_waimai("【开始删除缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            redis.DeleteHkey(redis_key, hash_key);//
            HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            // redis.Close();

        } catch (Exception e)
        {
            HelpTools.writelog_waimai(
                    "【删除存在hash_key的缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
        }
    }

}
