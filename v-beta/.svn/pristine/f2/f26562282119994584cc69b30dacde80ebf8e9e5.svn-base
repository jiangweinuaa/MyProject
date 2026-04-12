package com.dsc.spos.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商有云管家新建请求订单结构
 */
@Data
public class ShangyouOrder
{
    /**
     * 第三方订单id，必传
     */
    public  String orderNo;

    /**
     * 商有云管家 店铺id,传thirdShopId则storeId非必传
     */
    public  Long storeId;

    /**
     *会员id
     */
    public  String memberId;

    /**
     *订单来源 默认是1 自有订单,2 饿了么 3 美团 4 饿百，必传
     */
    public  String openChannelType;
    /**
     *店铺名称
     */
    public  String storeName;
    /**
     *销售类型 1。预约单 2。现售单  必传
     */
    public  int salesType;
    /**
     *提货方式 1.外送 2.自提  必传
     */
    public  int deliveryType;
    /**
     *自提类型 1 店内就餐 2 打包带走
     */
    public  Integer selfType;
    /**
     *付款类型 1.在线 2.货到付款 必传
     */
    public  int payType;
    /**
     *支付状态 1.未支付 2.已支付  必传
     */
    public  int payStatus;
    /**
     *收货人电话 必传
     */
    public  String recipientPhone;
    /**
     *收货人名称 必传
     */
    public  String customerName;
    /**
     *下单时间yyyy-MM-dd HH:mm:ss 必传
     */
    public  String orderStartTime;
    /**
     *预计送达时间yyyy-MM-dd HH:mm:ss（预约单必传）
     */
    public String deliveryTime;
    /**
     *自提点地址
     */
    public  String pickupAddress;
    /**
     *是否开发票：1，是 0 否
     */
    public  Integer hasInvoiced;
    /**
     *活动优惠总价(订单维度 元) 必传
     */
    public BigDecimal activityTotal;
    /**
     *活动列表
     */
    public List<Activities> orderActivities;


    /**
     *发票抬头
     */
    public  String invoiceTitle;

    /**
     * 必传
     *
     *订单状态：1、未生效 2、未处理 3、已接单 4、已出餐 5、已完成 6、无效订单 7、退单中 8、退单成功 9、退单失败 11、拒单（餐后付款），16、取消（餐后付款）
     */
    public  long status;

    /**
     *备注
     */
    public  String description;

    /**
     *送餐地址  必传
     */
    public  String address;

    /**
     *配送费 元
     */
    public  BigDecimal deliverFee;

    /**
     *商家承担配送费 元
     */
    public  BigDecimal merchantDeliverySubsidy;

    /**
     *会员减免运费 元
     */
    public  BigDecimal vipDeliveryFeeDiscount;

    /**
     *订单总价(实付 元)  必传
     */
    public  BigDecimal totalPrice;

    /**
     *订单原价 元
     */
    public  BigDecimal originalPrice;

    /**
     *sku数量 必传
     */
    public  int foodNum;

    /**
     *用餐人数
     */
    public  Integer dinnersNumber;

    /**
     *商户收到时间 yyyy-MM-dd HH:mm:ss
     */
    public  String orderReceiveTime;

    /**
     *商户接单时间 yyyy-MM-dd HH:mm:ss
     */
    public  String orderConfirmTime;

    /**
     *用户自提时间yyyy-MM-dd HH:mm:ss(自提单必填)
     */
    public  String pickupTime;
    /**
     *餐盒数量
     */
    public  Integer boxNum;
    /**
     *餐盒费(总价 元)
     */
    public  BigDecimal packageFee;
    /**
     *店铺当日订单流水(限制6位）  必传
     */
    public  String daySeq;
    /**
     *商家电话
     */
    public  String merchantPhone;
    /**
     *渠道 0：饿了么 1：美团 2：盟有
     */
    public  Integer channel;
    /**
     *是否是平台专送 0 是 1否
     */
    public  Integer isThirdShipping;
    /**
     *0：自配送 1：平台专送 2：外卖管家配送 3:未绑定
     */
    public  Integer  logisticsType;
    /**
     *距离
     */
    public  Double distance;
    /**
     *收货地址纬度 必传
     */
    public  String latitude;
    /**
     *收货地址经度 必传
     */
    public  String longitude;
    /**
     *经纬度类型(默认) 1为高德(GCJ02) 2 百度(BD09) 3 WGS84
     */
    public  int lalType;
    /**
     *预计收入 元
     */
    public  BigDecimal income;
    /**
     *桌台号
     */
    public  String tableNo;

    /**
     *100 外卖 200 堂食 201 餐后付款 300 优惠买单 400 券包核销订单 500 储值订单 （订单类型对应的功能，暂时只对云店有效）
     */
    public  Integer orderType;

    /**
     *支付渠道 1 支付宝 2 微信
     */
    public  Integer payChannel;
    /**
     *1 正常订单 2 加菜订单（包含加菜信息的订单）
     */
    public  Integer orderFlag;
    /**
     *餐位费 元
     */
    public  BigDecimal mealFee;
    /**
     *堂食加菜列表，内容与foodDtoList相同（加菜时必填）
     */
    public  List<Food> childEatinList;
    /**
     *付款人
     */
    public  String payUserName;
    /**
     *付款人电话
     */
    public  String payUserTel;
    /**
     *商品类型
     */
    public  Integer productType;
    /**
     *商品明细
     */
    public  List<Food> foodDtoList;


    /**
     * 活动列表
     */
    @Data
    public  static  class  Activities
    {
        /**
         *优惠说明
         */
        public  String remark;
        /**
         *优惠金额
         */
        public  String price;

    }

    /**
     * 商品列表
     */
    @Data
    public static class Food
    {
        /**
         *第三方商品id  必传
         */
        public  long foodId;

        /**
         *1.单品 2套餐 3 加菜  必传
         */
        public  int goodType;
        /**
         *批次号（加菜批次唯一标示）
         */
        public  String batchNo;
        /**
         *用户头像
         */
        public  String userImage;
        /**
         *用户名称
         */
        public  String userName;
        /**
         *skuId
         */
        public  Integer skuId;
        /**
         *店铺Id
         */
        public  Long storeId;
        /**
         *商品数量  必传
         */
        public  int quantity;
        /**
         *商品价格（单价 元） 必传
         */
        public  BigDecimal price;
        /**
         *商品名称 必传
         */
        public  String name;

        /**
         *多规格
         */
        public  List<Spec> newSpecs;

        /**
         *多属性
         */
        public  List<Attribute> attributes;

        /**
         *商品扩展码
         */
        public  String extendCode;
        /**
         *商品条形码
         */
        public  String barCode;
        /**
         *商品重量
         */
        public  Double weight;
        /**
         *用户侧价格（商品总价,去掉商品活动之后的总价 元） 必传
         */
        public  BigDecimal userPrice;
        /**
         *商户侧价格（商品总价 元）
         */
        public  BigDecimal shopPrice;
        /**
         *配料
         */
        public  List<Ingredient> ingredients;
        /**
         *餐盒数量
         */
        public  Integer boxNum;

        /**
         *餐盒价格 元
         */
        public  BigDecimal boxPrice;
        /**
         *单位
         */
        public  String unit;
        /**
         *视频文件标题
         */
        public  String gifTitle;
        /**
         *视频文件路径
         */
        public  String gifUrl;
        /**
         *子商品，内容与foodDtoList相同（套餐商品时必填）
         */
        public  List<Food> childFoodList;

    }

    /**
     *多规格
     */
    @Data
    public static class Spec
    {

        /**
         *规格id  必传
         */
        public  long specId;
        /**
         *名称   必传
         */
        public  String name;
        /**
         *规格值 必传
         */
        public  String value;
        /**
         *商品价格 必传
         */
        public  double price;
        /**
         *库存  必传
         */
        public  int stock;
        /**
         *第三方编码
         */
        public  String thirdCode;
        /**
         *包装费 必传
         */
        public  double packingFee;
        /**
         *是否上架 0 未上架 1已上架  必传
         */
        public  int onShelf;
        /**
         *商品扩展码
         */
        public  String extendCode;
        /**
         *商品条形码
         */
        public  String barCode;
        /**
         *重量（克）
         */
        public  Integer weight;

    }

    /**
     * 多属性
     */
    @Data
    public static class Attribute
    {
        /**
         *属性名称
         */
        public  String name;
        /**
         *值
         */
        public  String value;
        /**
         *第三方编码
         */
        public  String thirdCode;

    }

    /**
     * 配料
     */
    @Data
    public static class Ingredient
    {

        /**
         *名称  必传
         */
        public  String optionName;
        /**
         *数量  必传
         */
        public  int amount;
        /**
         *价格（分） 必传
         */
        public  BigDecimal price;
        /**
         *第三方编码
         */
        public  String thirdCode;
    }


}
