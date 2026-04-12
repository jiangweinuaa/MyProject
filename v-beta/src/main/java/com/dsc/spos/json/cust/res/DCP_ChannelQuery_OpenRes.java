package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ChannelQuery_OpenRes extends JsonRes
{
	private List<level1Elm> datas;
	
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}



	public class level1Elm
	{
		private String channelId;//渠道id
		private String channelName;//渠道名称
		private String status;//状态		
		private String appNo;//
		private String appName;//
		private String cardTypeId;//
		private String cardTypeName;//
		private String createOpId;//
		private String createOpName;//
		private String createTime;//
		private String lastModiOpId;//
		private String lastModiOpName;//
		private String lastModiTime;//
		private String apiUserCode;//
		private String isRegByMachine;//
		
		
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getAppNo()
		{
			return appNo;
		}
		public void setAppNo(String appNo)
		{
			this.appNo = appNo;
		}
		public String getAppName()
		{
			return appName;
		}
		public void setAppName(String appName)
		{
			this.appName = appName;
		}
		public String getCardTypeId()
		{
			return cardTypeId;
		}
		public void setCardTypeId(String cardTypeId)
		{
			this.cardTypeId = cardTypeId;
		}
		public String getCardTypeName()
		{
			return cardTypeName;
		}
		public void setCardTypeName(String cardTypeName)
		{
			this.cardTypeName = cardTypeName;
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
		public String getApiUserCode()
		{
			return apiUserCode;
		}
		public void setApiUserCode(String apiUserCode)
		{
			this.apiUserCode = apiUserCode;
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
