package com.dsc.spos.waimai.candao;

import java.util.List;

public class createOrderReq
{
    private String extOrderId;
    private String extOrderNo;
    private String thirdSn;
    private String longitude;
    private String latitude;
    /**
     * 统一门店id
     */
    private String storeId;
    /**
     * 供应商门店id
     */
    private String subStoreId;
    /**
     * 平台门店名称
     */
    private String extStoreName;
    /**
     * 联系人
     */
    private String name;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 送餐地址或自取地址，业务类型非堂食单时，必传(orderType!=3)
     */
    private String address;
    /**
     * 用户备注
     */
    private String userNote;
    /**
     * 业务类型，参见枚举类orderType(我们传4：外卖预约)
     */
    private int orderType = 4 ;

    /**
     * 是否预约单（1：即时，2：预约，0：不区分，默认）(我们传2：预约)
     */
    private int book = 2 ;
    /**
     * 下单时间 yyyy-MM-dd HH:mm:ss
     */
    private String orderTime ;

    /**
     * 预送达时间 yyyy-MM-dd HH:mm:ss
     */
    private String sendTime ;

    /**
     * 订单来源，参见fromType枚举，建议传此字段便于区分订单渠道
     */
    private String fromType = "dgw" ;
    /**
     * 支付类型，参见枚举类payType(我们传2：在线支付)
     */
    private int payType = 2 ;
    /**
     * 订单状态，1：待支付；7：商家待接单；10：商家已接单；12：备餐中；14：配送中；16：就餐中；18：待取餐；20：取餐超时；100：订单完成；-1：订单取消；21：备餐完成
     */
    private int orderStatus ;

    /**
     * 是否已经支付
     */
    private boolean isPayed = true ;

    /**
     * 是否开发票
     */
    private boolean isInvoice = false ;
    /**
     * 订单支付总金额
     */
    private Double price ;
    /**
     * 配送费
     */
    private Double deliveryFee ;
    /**
     * 餐盒费
     */
    private Double mealFee ;

    /**
     * 商户实收金额（保留小数点后两位，正数）
     */
    private Double merchantPrice ;

    /**
     * 订单餐品重量，单位：克
     */
    private Double weight=1000.00 ;

    private List<product> products;

    private ftTypeObj ftType;

    public String getExtOrderId() {
        return extOrderId;
    }

    public void setExtOrderId(String extOrderId) {
        this.extOrderId = extOrderId;
    }

    public String getExtOrderNo() {
        return extOrderNo;
    }

    public void setExtOrderNo(String extOrderNo) {
        this.extOrderNo = extOrderNo;
    }

    public String getThirdSn() {
        return thirdSn;
    }

    public void setThirdSn(String thirdSn) {
        this.thirdSn = thirdSn;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSubStoreId() {
        return subStoreId;
    }

    public void setSubStoreId(String subStoreId) {
        this.subStoreId = subStoreId;
    }

    public String getExtStoreName() {
        return extStoreName;
    }

    public void setExtStoreName(String extStoreName) {
        this.extStoreName = extStoreName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    public boolean isInvoice() {
        return isInvoice;
    }

    public void setInvoice(boolean invoice) {
        isInvoice = invoice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getMealFee() {
        return mealFee;
    }

    public void setMealFee(Double mealFee) {
        this.mealFee = mealFee;
    }

    public Double getMerchantPrice() {
        return merchantPrice;
    }

    public void setMerchantPrice(Double merchantPrice) {
        this.merchantPrice = merchantPrice;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<product> getProducts() {
        return products;
    }

    public void setProducts(List<product> products) {
        this.products = products;
    }

    public ftTypeObj getFtType() {
        return ftType;
    }

    public void setFtType(ftTypeObj ftType) {
        this.ftType = ftType;
    }

    public static class product
    {
        private String pid;
        private String subPid;
        private String upc;
        private String name;
        private Double num;
        private Double price;
        private String remark;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getSubPid() {
            return subPid;
        }

        public void setSubPid(String subPid) {
            this.subPid = subPid;
        }

        public String getUpc() {
            return upc;
        }

        public void setUpc(String upc) {
            this.upc = upc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getNum() {
            return num;
        }

        public void setNum(Double num) {
            this.num = num;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    public static class ftTypeObj
    {
        /**
         * 支持餐品id为0
         */
        private boolean emptyProductId = true;
        /**
         * 支持餐品状态异常 餐品状态包括餐品售罄、库存不足、下架
         */
        private boolean productAberrant = true;
        /**
         * 支持门店状态异常 门店状态包括门店繁忙
         */
        private boolean storeAberrant = false;

        public boolean isEmptyProductId() {
            return emptyProductId;
        }

        public void setEmptyProductId(boolean emptyProductId) {
            this.emptyProductId = emptyProductId;
        }

        public boolean isProductAberrant() {
            return productAberrant;
        }

        public void setProductAberrant(boolean productAberrant) {
            this.productAberrant = productAberrant;
        }

        public boolean isStoreAberrant() {
            return storeAberrant;
        }

        public void setStoreAberrant(boolean storeAberrant) {
            this.storeAberrant = storeAberrant;
        }
    }
}
