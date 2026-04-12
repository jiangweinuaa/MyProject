package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderMappingShopCreateReq extends JsonBasicReq 
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
		private String loadDocType;
		private String account;
		private List<level1Elm> datas;
		
		
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
		}
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

	}
	
	
	public  class level1Elm
	{
		private String channelId;
		private String orderShopNo;
		private String orderShopName;
		private String erpShopNo;
		private String erpShopName;
		private String appAuthToken;
		private String appKey;
		private String appSecret;		
		private String appName;
		private String isTest;
		private String isJbp;
        private String userId;
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getOrderShopNo()
		{
			return orderShopNo;
		}
		public void setOrderShopNo(String orderShopNo)
		{
			this.orderShopNo = orderShopNo;
		}
		public String getOrderShopName()
		{
			return orderShopName;
		}
		public void setOrderShopName(String orderShopName)
		{
			this.orderShopName = orderShopName;
		}
		public String getErpShopNo()
		{
			return erpShopNo;
		}
		public void setErpShopNo(String erpShopNo)
		{
			this.erpShopNo = erpShopNo;
		}
		public String getErpShopName()
		{
			return erpShopName;
		}
		public void setErpShopName(String erpShopName)
		{
			this.erpShopName = erpShopName;
		}
		public String getAppAuthToken()
		{
			return appAuthToken;
		}
		public void setAppAuthToken(String appAuthToken)
		{
			this.appAuthToken = appAuthToken;
		}
		public String getAppKey()
		{
			return appKey;
		}
		public void setAppKey(String appKey)
		{
			this.appKey = appKey;
		}
		public String getAppSecret()
		{
			return appSecret;
		}
		public void setAppSecret(String appSecret)
		{
			this.appSecret = appSecret;
		}
		public String getAppName()
		{
			return appName;
		}
		public void setAppName(String appName)
		{
			this.appName = appName;
		}
		public String getIsTest()
		{
			return isTest;
		}
		public void setIsTest(String isTest)
		{
			this.isTest = isTest;
		}
		public String getIsJbp()
		{
			return isJbp;
		}
		public void setIsJbp(String isJbp)
		{
			this.isJbp = isJbp;
		}
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}
