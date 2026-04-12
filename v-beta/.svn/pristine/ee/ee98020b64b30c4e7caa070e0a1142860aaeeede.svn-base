package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_DinnerAreaUpdateReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dinnerGroup;	
		private String dinnerGroupName;	
		private String status;
		//2019-07-15 新增一下三个字段， 用于优先级排序
		private String priority;
		private String toPriority;

		// 2021/3/10 增加两个字段 BY wangzyc
		private String restrictGroup; // 启用区域点单Y/N
		private String groupType; // 点单类型 0.用餐后结账 1.预先结账


		public String getDinnerGroup() {
			return dinnerGroup;
		}
		public void setDinnerGroup(String dinnerGroup) {
			this.dinnerGroup = dinnerGroup;
		}
		public String getDinnerGroupName() {
			return dinnerGroupName;
		}
		public void setDinnerGroupName(String dinnerGroupName) {
			this.dinnerGroupName = dinnerGroupName;
		}
		public String getPriority() {
			return priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getToPriority() {
			return toPriority;
		}
		public void setToPriority(String toPriority) {
			this.toPriority = toPriority;
		}

		public String getRestrictGroup() {
			return restrictGroup;
		}

		public void setRestrictGroup(String restrictGroup) {
			this.restrictGroup = restrictGroup;
		}

		public String getGroupType() {
			return groupType;
		}

		public void setGroupType(String groupType) {
			this.groupType = groupType;
		}
	}

}
