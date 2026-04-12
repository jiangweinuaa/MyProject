package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ChannelQueryReq extends JsonBasicReq
{

	private levelRequest request;	
	
	
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}



	public class levelRequest
	{
		private String channelId;//
		private String status;//-1：未启用 100：已生效 0：已失效
		private String searchString;//根据编码、名称、简称模糊搜索
		
		private String appNo;//渠道类型编码
		private String isThird;//是否第三方   default 0,查非第3方渠道,不传仅查非第3方渠道 =1 查第3方渠道	=2,查全部渠道
		private String onlyOnline;//2=仅查询线下 1=仅查线上0=查询全部
		private String isRegByMachine;//是否按机台授权应用对应的渠道Y/N
		private String onlyThird ;//1=仅查询三方 0=查询自有和三方渠道

        // ADD 2021/6/11 应SA 王欢需求需求增加appId By wangzyc
        private String appId;

        public String getOnlyThird() {
			return onlyThird;
		}

		public void setOnlyThird(String onlyThird) {
			this.onlyThird = onlyThird;
		}

		public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSearchString() {
			return searchString;
		}
		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}
		public String getAppNo()
		{
			return appNo;
		}
		public void setAppNo(String appNo)
		{
			this.appNo = appNo;
		}
		public String getIsThird()
		{
			return isThird;
		}
		public void setIsThird(String isThird)
		{
			this.isThird = isThird;
		}
		public String getOnlyOnline()
		{
			return onlyOnline;
		}
		public void setOnlyOnline(String onlyOnline)
		{
			this.onlyOnline = onlyOnline;
		}
		public String getIsRegByMachine()
		{
			return isRegByMachine;
		}
		public void setIsRegByMachine(String isRegByMachine)
		{
			this.isRegByMachine = isRegByMachine;
		}		
		
	}	
}
