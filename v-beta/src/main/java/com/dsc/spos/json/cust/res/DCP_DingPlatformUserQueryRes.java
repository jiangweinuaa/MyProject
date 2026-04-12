package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DingPlatformUserGetDCP
 * 服务说明： 钉钉平台用户查询
 * @author jinzma
 * @since  2019-10-28
 */
public class DCP_DingPlatformUserQueryRes extends JsonRes {

	private String userId ;
	private String userName;
	private String deptId;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

}
