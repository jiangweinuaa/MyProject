package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 修改密码 2018-09-26
 * @author yuanyy
 *
 */
public class DCP_PasswordUpdateReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String oldPassword;
		private String newPassword;
		private String updateType;
		private String o_opNo;
		public String getOldPassword() {
			return oldPassword;
		}
		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}
		public String getNewPassword() {
			return newPassword;
		}
		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
		public String getUpdateType() {
			return updateType;
		}
		public void setUpdateType(String updateType) {
			this.updateType = updateType;
		}
		public String getO_opNo() {
			return o_opNo;
		}
		public void setO_opNo(String o_opNo) {
			this.o_opNo = o_opNo;
		}
	}
}
