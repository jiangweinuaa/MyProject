package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DingUserSetGetDCP
 * 服务说明： 钉钉用户设置查询
 * @author jinzma
 * @since  2019-10-28
 */
public class DCP_DingUserSetQueryRes  extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String opNo; 
		private String opName;
		private String opGName;
		private String staffsShopName;
		private String userId;
		private String userName;
		private String deptId;
		private String status;

		public String getOpNo() {
			return opNo;
		}
		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}
		public String getOpName() {
			return opName;
		}
		public void setOpName(String opName) {
			this.opName = opName;
		}
		public String getOpGName() {
			return opGName;
		}
		public void setOpGName(String opGName) {
			this.opGName = opGName;
		}
		public String getStaffsShopName() {
			return staffsShopName;
		}
		public void setStaffsShopName(String staffsShopName) {
			this.staffsShopName = staffsShopName;
		}
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}





	}

}
