package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * CRM与云中台支付方式映射关系查询
 * @author yuanyy 2019-08-27
 *
 */
public class DCP_PayMentNRCQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String platformPayCode;
		private String platformPayName;
		private String crmPayCode;
		private String crmPayName;
		private String priority; 
		private String status;
		private String createBy;
		private String createByName;
		private String createTime;
		private String updateBy;
		private String updateByName;
		private String updateTime;
		private String onSale;
		
		public String getPlatformPayCode() {
			return platformPayCode;
		}
		public void setPlatformPayCode(String platformPayCode) {
			this.platformPayCode = platformPayCode;
		}
		public String getPlatformPayName() {
			return platformPayName;
		}
		public void setPlatformPayName(String platformPayName) {
			this.platformPayName = platformPayName;
		}
		public String getCrmPayCode() {
			return crmPayCode;
		}
		public void setCrmPayCode(String crmPayCode) {
			this.crmPayCode = crmPayCode;
		}
		public String getCrmPayName() {
			return crmPayName;
		}
		public void setCrmPayName(String crmPayName) {
			this.crmPayName = crmPayName;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCreateBy() {
			return createBy;
		}
		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}
		public String getCreateByName() {
			return createByName;
		}
		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getUpdateBy() {
			return updateBy;
		}
		public void setUpdateBy(String updateBy) {
			this.updateBy = updateBy;
		}
		public String getUpdateByName() {
			return updateByName;
		}
		public void setUpdateByName(String updateByName) {
			this.updateByName = updateByName;
		}
		public String getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}
		public String getOnSale() {
			return onSale;
		}
		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}
	}
	
}
