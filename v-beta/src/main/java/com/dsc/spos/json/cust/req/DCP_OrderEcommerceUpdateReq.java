package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_OrderEcommerceUpdateReq extends JsonBasicReq
{

	private levelRequest request;
	
	
	
	public levelRequest getRequest()
	{
		return request;
	}



	public void setRequest(levelRequest request)
	{
		this.request = request;
	}



	public class levelRequest
    {
        private String channelId;//渠道代号
        private String loadDocType;
        private String ecPlatformUrl;//应用网址
        private String ecPlatformHotline;//客服热线
        private String apiUrl;//API地址
        private String apiKey;//APIKEY
        private String apiSecret;//APISECRET
        private String apiSign;//APISIGN
        private String token;//TOKEN
        private String brandId;//品牌ID
        private String storeId;//店铺编号
        private String shippingShopNo;//默认配送机构编码
        private String warehouse;//默认出货仓
        private String currencyNo;//币别
        private String customerNo;//电商客户编码
        private String isReview;//是否开启订单审核
        private String isProDispatch;//是否开启生产调度
        private String isOrderLockStock;//是否订单中心锁库存
        private String isTest;//是否测试环境
        private String isJbp;//是否聚宝盆
        private String status;//状态（0：禁用；100启用）
        private String orderGoodsDeliver;//
        private String supportInvoice;//
        private String expireTime;//
        private String publicKey;//有赞对接需要的秘钥

        // 20220606 企迈对接 新增字段
        private String partnerMember; // digiwin  鼎捷    qimai企迈
        private String partnerApiUrl; // 服务API
        private String partnerApiKey; // 服务KEY
        private String partnerApiSecret; // 服务SECRET
        private String partnerApiSign; // 服务SIGN

        private String cardSite;    //1 企迈 2 鼎捷 3混合  嘉华配置成3
        private String reviewType;//1-支持审核配送和自提订单 2-仅支持审核配送订单(罗森妮娜需求，自提订单不需要审核)
        private String isReviewToShop;//订单审核是否到门店,Y：是；N：否
        private List<levelReviewShop> reviewShopList;
        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getLoadDocType() {
            return loadDocType;
        }

        public void setLoadDocType(String loadDocType) {
            this.loadDocType = loadDocType;
        }

        public String getEcPlatformUrl() {
            return ecPlatformUrl;
        }

        public void setEcPlatformUrl(String ecPlatformUrl) {
            this.ecPlatformUrl = ecPlatformUrl;
        }

        public String getEcPlatformHotline() {
            return ecPlatformHotline;
        }

        public void setEcPlatformHotline(String ecPlatformHotline) {
            this.ecPlatformHotline = ecPlatformHotline;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiSecret() {
            return apiSecret;
        }

        public void setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
        }

        public String getApiSign() {
            return apiSign;
        }

        public void setApiSign(String apiSign) {
            this.apiSign = apiSign;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getBrandId() {
            return brandId;
        }

        public void setBrandId(String brandId) {
            this.brandId = brandId;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getShippingShopNo() {
            return shippingShopNo;
        }

        public void setShippingShopNo(String shippingShopNo) {
            this.shippingShopNo = shippingShopNo;
        }

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }

        public String getCurrencyNo() {
            return currencyNo;
        }

        public void setCurrencyNo(String currencyNo) {
            this.currencyNo = currencyNo;
        }

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }

        public String getIsReview() {
            return isReview;
        }

        public void setIsReview(String isReview) {
            this.isReview = isReview;
        }

        public String getIsProDispatch() {
            return isProDispatch;
        }

        public void setIsProDispatch(String isProDispatch) {
            this.isProDispatch = isProDispatch;
        }

        public String getIsOrderLockStock() {
            return isOrderLockStock;
        }

        public void setIsOrderLockStock(String isOrderLockStock) {
            this.isOrderLockStock = isOrderLockStock;
        }

        public String getIsTest() {
            return isTest;
        }

        public void setIsTest(String isTest) {
            this.isTest = isTest;
        }

        public String getIsJbp() {
            return isJbp;
        }

        public void setIsJbp(String isJbp) {
            this.isJbp = isJbp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrderGoodsDeliver() {
            return orderGoodsDeliver;
        }

        public void setOrderGoodsDeliver(String orderGoodsDeliver) {
            this.orderGoodsDeliver = orderGoodsDeliver;
        }

        public String getSupportInvoice() {
            return supportInvoice;
        }

        public void setSupportInvoice(String supportInvoice) {
            this.supportInvoice = supportInvoice;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPartnerMember() {
            return partnerMember;
        }

        public void setPartnerMember(String partnerMember) {
            this.partnerMember = partnerMember;
        }

        public String getPartnerApiUrl() {
            return partnerApiUrl;
        }

        public void setPartnerApiUrl(String partnerApiUrl) {
            this.partnerApiUrl = partnerApiUrl;
        }

        public String getPartnerApiKey() {
            return partnerApiKey;
        }

        public void setPartnerApiKey(String partnerApiKey) {
            this.partnerApiKey = partnerApiKey;
        }

        public String getPartnerApiSecret() {
            return partnerApiSecret;
        }

        public void setPartnerApiSecret(String partnerApiSecret) {
            this.partnerApiSecret = partnerApiSecret;
        }

        public String getPartnerApiSign() {
            return partnerApiSign;
        }

        public void setPartnerApiSign(String partnerApiSign) {
            this.partnerApiSign = partnerApiSign;
        }

        public String getCardSite() {
            return cardSite;
        }

        public void setCardSite(String cardSite) {
            this.cardSite = cardSite;
        }

        public String getReviewType() {
            return reviewType;
        }

        public void setReviewType(String reviewType) {
            this.reviewType = reviewType;
        }

        public String getIsReviewToShop() {
            return isReviewToShop;
        }

        public void setIsReviewToShop(String isReviewToShop) {
            this.isReviewToShop = isReviewToShop;
        }

        public List<levelReviewShop> getReviewShopList() {
            return reviewShopList;
        }

        public void setReviewShopList(List<levelReviewShop> reviewShopList) {
            this.reviewShopList = reviewShopList;
        }
    }

    public class levelReviewShop
    {
        private String shopNo;
        private String shopName;

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }
}
