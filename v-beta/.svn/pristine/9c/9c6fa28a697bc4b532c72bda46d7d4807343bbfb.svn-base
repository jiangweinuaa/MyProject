package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ClassGoodsQueryReq extends JsonBasicReq 
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
		private String keyTxt;
		private level1Elm classList;
		private String channelId;   // add 适用渠道编码 2021/5/21 By wangzyc
		private String appNo;   // add 入参新增appNo，根据CRM_CHANNEL.APPNO下属所有channelId匹配销售分组适用渠道（包括全部） 2021/6/1 By wangzyc

		public level1Elm getClassList() {
			return classList;
		}
	
		public void setClassList(level1Elm classList) {
			this.classList = classList;
		}

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getAppNo() {
            return appNo;
        }

        public void setAppNo(String appNo) {
            this.appNo = appNo;
        }
    }
	
	public  class level1Elm
	{
		private String classType;
		private String classNo;
		
		public String getClassType() {
			return classType;
		}
		public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
				
	}

}
