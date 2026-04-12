package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 基础设置列表查询
 * @author yuanyy 2020-03-03
 * 
 */
public class DCP_BasicSetQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		
		private String templateNo;
		private String updateServiceAddress;
		private String posServiceAddress;
		private String memberServiceAddress;
		private String userCode;
		private String userName;
		private String userKey;
		private String spreadAppNo;
		private String spreadAppName;
		private String restrictShop;
		private String createopid;
		private String createTime;
		private String lastmodiopid;
		private String lastmoditime;
		private String pictureGetAddress;
		private String payServiceAddress;
		
		public String getTemplateNo() {
			return templateNo;
		}
		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
		public String getUpdateServiceAddress() {
			return updateServiceAddress;
		}
		public void setUpdateServiceAddress(String updateServiceAddress) {
			this.updateServiceAddress = updateServiceAddress;
		}
		public String getPosServiceAddress() {
			return posServiceAddress;
		}
		public void setPosServiceAddress(String posServiceAddress) {
			this.posServiceAddress = posServiceAddress;
		}
		public String getMemberServiceAddress() {
			return memberServiceAddress;
		}
		public void setMemberServiceAddress(String memberServiceAddress) {
			this.memberServiceAddress = memberServiceAddress;
		}
		public String getUserCode() {
			return userCode;
		}
		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getUserKey() {
			return userKey;
		}
		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}
		public String getSpreadAppNo() {
			return spreadAppNo;
		}
		public void setSpreadAppNo(String spreadAppNo) {
			this.spreadAppNo = spreadAppNo;
		}
		public String getSpreadAppName() {
			return spreadAppName;
		}
		public void setSpreadAppName(String spreadAppName) {
			this.spreadAppName = spreadAppName;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public String getCreateopid() {
			return createopid;
		}
		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getLastmodiopid() {
			return lastmodiopid;
		}
		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}
		public String getLastmoditime() {
			return lastmoditime;
		}
		public void setLastmoditime(String lastmoditime) {
			this.lastmoditime = lastmoditime;
		}
		public String getPictureGetAddress() {
			return pictureGetAddress;
		}
		public void setPictureGetAddress(String pictureGetAddress) {
			this.pictureGetAddress = pictureGetAddress;
		}
		public String getPayServiceAddress() {
			return payServiceAddress;
		}
		public void setPayServiceAddress(String payServiceAddress) {
			this.payServiceAddress = payServiceAddress;
		}
		
		
	}
	
	
}
