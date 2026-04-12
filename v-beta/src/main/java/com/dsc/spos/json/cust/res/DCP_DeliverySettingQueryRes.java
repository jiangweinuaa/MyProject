package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 货运厂商查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingQueryRes extends JsonRes 
{
	private level1Elm datas;	
	
	public level1Elm getDatas()
	{
		return datas;
	}

	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}

	public class level1Elm 
	{
		private List<delivery> deliveryList;

		public List<delivery> getDeliveryList()
		{
			return deliveryList;
		}

		public void setDeliveryList(List<delivery> deliveryList)
		{
			this.deliveryList = deliveryList;
		}		
	}

	public class delivery 
	{
		private String deliveryType;
		private String appId;
		private String appSecret;
		private String appSignKey;
		private String shopCode;
		private String apiUrl;
		private String apiUrlTwo;
		private String apiUrlThree;
		private String iv;
		private String v;
		private String deliveryMode;
		private String printMode;
		private String status;
		private String createOpId;
		private String createOpName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;
        /**
         * 1-全部门店2-指定门店
         */
        private String shopType;
		private String appName;
        /**
         * 圆通物流是否对接新的接口(N-否；Y-是)
         */
        private String ytoType;
        /**
         * 物品类型(顺丰同城需要，1快餐;3百货;13蛋糕；34面包糕点)
         */
        private String productType;
        /**
         * 快递鸟物流查询类型( 快递鸟需要,8001-在途监控接口指令; 8002-快递查询接口指令)
         */
        private String queryType;
        /**
         * 快递鸟物流产品类型(快递鸟需要，3=2小时收;4=半日收;5=当日收 )
         */
        private String shipperType;

        /**
         * 快递鸟物流类型(快递鸟需要， 0全国快递，1同城快递 )
         */
        private String shipMode;
        /**
         * 快递鸟同城-即时配类型
         */
        private String instantConfigType ;
        /**
         * 快递鸟同城-三级类目编码
         */
        private String levelCategory ;

		/**
		 * 是否测试环境
		 */
		private String isTest ;

		/**
		 * 是否延时发物流(顺丰同城物流)(如果设置Y，都是发即时单)
		 */
		private String isDelay ;

        /**
         * 延时发物流时长(分)(小于60不起作用)
         */
        private String delayTimeSpan ;


		public String getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public String getAppId()
		{
			return appId;
		}
		public void setAppId(String appId)
		{
			this.appId = appId;
		}
		public String getAppSecret()
		{
			return appSecret;
		}
		public void setAppSecret(String appSecret)
		{
			this.appSecret = appSecret;
		}
		public String getAppSignKey()
		{
			return appSignKey;
		}
		public void setAppSignKey(String appSignKey)
		{
			this.appSignKey = appSignKey;
		}
		public String getShopCode()
		{
			return shopCode;
		}
		public void setShopCode(String shopCode)
		{
			this.shopCode = shopCode;
		}
		public String getApiUrl()
		{
			return apiUrl;
		}
		public void setApiUrl(String apiUrl)
		{
			this.apiUrl = apiUrl;
		}
		public String getApiUrlTwo()
		{
			return apiUrlTwo;
		}
		public void setApiUrlTwo(String apiUrlTwo)
		{
			this.apiUrlTwo = apiUrlTwo;
		}
		public String getApiUrlThree()
		{
			return apiUrlThree;
		}
		public void setApiUrlThree(String apiUrlThree)
		{
			this.apiUrlThree = apiUrlThree;
		}
		public String getIv()
		{
			return iv;
		}
		public void setIv(String iv)
		{
			this.iv = iv;
		}
		public String getV()
		{
			return v;
		}
		public void setV(String v)
		{
			this.v = v;
		}
		public String getDeliveryMode()
		{
			return deliveryMode;
		}
		public void setDeliveryMode(String deliveryMode)
		{
			this.deliveryMode = deliveryMode;
		}
		
		
		public String getPrintMode()
		{
			return printMode;
		}
		public void setPrintMode(String printMode)
		{
			this.printMode = printMode;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getCreateOpId()
		{
			return createOpId;
		}
		public void setCreateOpId(String createOpId)
		{
			this.createOpId = createOpId;
		}
		public String getCreateOpName()
		{
			return createOpName;
		}
		public void setCreateOpName(String createOpName)
		{
			this.createOpName = createOpName;
		}
		public String getCreateTime()
		{
			return createTime;
		}
		public void setCreateTime(String createTime)
		{
			this.createTime = createTime;
		}
		public String getLastModiOpId()
		{
			return lastModiOpId;
		}
		public void setLastModiOpId(String lastModiOpId)
		{
			this.lastModiOpId = lastModiOpId;
		}
		public String getLastModiOpName()
		{
			return lastModiOpName;
		}
		public void setLastModiOpName(String lastModiOpName)
		{
			this.lastModiOpName = lastModiOpName;
		}
		public String getLastModiTime()
		{
			return lastModiTime;
		}
		public void setLastModiTime(String lastModiTime)
		{
			this.lastModiTime = lastModiTime;
		}

        public String getShopType() {
            return shopType;
        }

        public void setShopType(String shopType) {
            this.shopType = shopType;
        }

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

        public String getYtoType() {
            return ytoType;
        }

        public void setYtoType(String ytoType) {
            this.ytoType = ytoType;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getShipperType() {
            return shipperType;
        }

        public void setShipperType(String shipperType) {
            this.shipperType = shipperType;
        }

        public String getShipMode() {
            return shipMode;
        }

        public void setShipMode(String shipMode) {
            this.shipMode = shipMode;
        }

        public String getInstantConfigType() {
            return instantConfigType;
        }

        public void setInstantConfigType(String instantConfigType) {
            this.instantConfigType = instantConfigType;
        }

        public String getLevelCategory() {
            return levelCategory;
        }

        public void setLevelCategory(String levelCategory) {
            this.levelCategory = levelCategory;
        }

		public String getIsDelay() {
			return isDelay;
		}

		public void setIsDelay(String isDelay) {
			this.isDelay = isDelay;
		}

		public String getDelayTimeSpan() {
            return delayTimeSpan;
        }

        public void setDelayTimeSpan(String delayTimeSpan) {
            this.delayTimeSpan = delayTimeSpan;
        }

        public String getIsTest() {
            return isTest;
        }

        public void setIsTest(String isTest) {
            this.isTest = isTest;
        }
    }





}
