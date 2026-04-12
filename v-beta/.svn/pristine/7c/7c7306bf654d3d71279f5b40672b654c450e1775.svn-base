package com.dsc.spos.waimai.sftc;



public class sftcOrderCreateEntity
{
    private long dev_id;
    /**
     * 店铺ID
     */
    private String shop_id;
    /**
     * 店铺ID类型 1：顺丰店铺ID ；2：接入方店铺ID
     */
    private int shop_type;
    /**
     * 商家订单号 string(64)
     */
    private String shop_order_id;

    //private int shop_preparation_time;//商家预计备餐时长,分钟级时间 比如: 10 分钟 则传入 10

    /**
     * 订单接入来源 1：美团；2：饿了么；3：百度；4：口碑；
     * 其他请直接填写中文字符串值
     */
    private String order_source;
    /**
     * 取货序号
     */
    private String order_sequence;
    /**
     * 坐标类型 1：百度坐标，2：高德坐标
     */
    private int lbs_type =2 ;
    /**
     * 	用户支付方式 1：已付款 0：货到付款
     */
    private int pay_type = 1;
    /**
     * 	用户下单时间 秒级时间戳 int（11）
     */
    private long order_time;
    /**
     * 	是否是预约单 0：非预约单；1：预约单
     */
    private int is_appoint;
    /**
     * 预约单的时候传入,1：预约单送达单；2：预约单上门单
     */
    private int appoint_type;
    /**
     * 	用户期望送达时间
     * 	若传入自此段且时间大于配送时效，则按照预约送达单处理，时间小于配送时效按照立即单处理；appoint_type=1时需必传,秒级时间戳；
     */
    private long expect_time;
    /**
     * 用户期望上门时间
     * appoint_type=2时需必传,秒级时间戳
     */
    private long expect_pickup_time;
    /**
     * 商家期望送达时间
     * 只展示给骑士，不参与时效考核；秒级时间戳
     */
    private long shop_expect_time;
    /**
     * 是否保价，0：非保价；1：保价
     */
    private int is_insured = 0;
    /**
     * 是否是专人直送订单，0：否；1：是
     */
    private int is_person_direct = 0;

    /**
     * 订单备注 string（512）
     */
    private String remark;

    /**
     * 	返回字段控制标志位（二进制）
     * 	1:商品总价格，2:配送距离，4:物品重量，8:起送时间，16:期望送达时间，32:支付费用，64:实际支持金额，128:优惠券总金额，256:结算方式
     * 例如全部返回为填入511
     */
    private int return_flag = 511;
    /**
     * 	推单时间 秒级时间戳
     */
    private long push_time;
    /**
     * 版本号 参照文档主版本号填写
     * 如：文档版本号1.7,version=17
     */
    private int version=17;

    private shop shop;
    private receive receive;
    private order_detail order_detail;


   /* private int product_type;
    private String user_address;
    private String user_lng;
    private String user_lat;
    private String city_name;
    private int weight;
    */

    public long getDev_id() {
        return dev_id;
    }

    public void setDev_id(long dev_id) {
        this.dev_id = dev_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public int getShop_type() {
        return shop_type;
    }

    public void setShop_type(int shop_type) {
        this.shop_type = shop_type;
    }

    public String getShop_order_id() {
        return shop_order_id;
    }

    public void setShop_order_id(String shop_order_id) {
        this.shop_order_id = shop_order_id;
    }

    public String getOrder_source() {
        return order_source;
    }

    public void setOrder_source(String order_source) {
        this.order_source = order_source;
    }

    public String getOrder_sequence() {
        return order_sequence;
    }

    public void setOrder_sequence(String order_sequence) {
        this.order_sequence = order_sequence;
    }

    public int getLbs_type() {
        return lbs_type;
    }

    public void setLbs_type(int lbs_type) {
        this.lbs_type = lbs_type;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public long getOrder_time() {
        return order_time;
    }

    public void setOrder_time(long order_time) {
        this.order_time = order_time;
    }

    public int getIs_appoint() {
        return is_appoint;
    }

    public void setIs_appoint(int is_appoint) {
        this.is_appoint = is_appoint;
    }

    public int getAppoint_type() {
        return appoint_type;
    }

    public void setAppoint_type(int appoint_type) {
        this.appoint_type = appoint_type;
    }

    public long getExpect_time() {
        return expect_time;
    }

    public void setExpect_time(long expect_time) {
        this.expect_time = expect_time;
    }

    public long getExpect_pickup_time() {
        return expect_pickup_time;
    }

    public void setExpect_pickup_time(long expect_pickup_time) {
        this.expect_pickup_time = expect_pickup_time;
    }

    public long getShop_expect_time() {
        return shop_expect_time;
    }

    public void setShop_expect_time(long shop_expect_time) {
        this.shop_expect_time = shop_expect_time;
    }

    public int getIs_insured() {
        return is_insured;
    }

    public void setIs_insured(int is_insured) {
        this.is_insured = is_insured;
    }

    public int getIs_person_direct() {
        return is_person_direct;
    }

    public void setIs_person_direct(int is_person_direct) {
        this.is_person_direct = is_person_direct;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getReturn_flag() {
        return return_flag;
    }

    public void setReturn_flag(int return_flag) {
        this.return_flag = return_flag;
    }

    public long getPush_time() {
        return push_time;
    }

    public void setPush_time(long push_time) {
        this.push_time = push_time;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public com.dsc.spos.waimai.sftc.shop getShop() {
        return shop;
    }

    public void setShop(com.dsc.spos.waimai.sftc.shop shop) {
        this.shop = shop;
    }

    public com.dsc.spos.waimai.sftc.receive getReceive() {
        return receive;
    }

    public void setReceive(com.dsc.spos.waimai.sftc.receive receive) {
        this.receive = receive;
    }

    public com.dsc.spos.waimai.sftc.order_detail getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(com.dsc.spos.waimai.sftc.order_detail order_detail) {
        this.order_detail = order_detail;
    }
}
