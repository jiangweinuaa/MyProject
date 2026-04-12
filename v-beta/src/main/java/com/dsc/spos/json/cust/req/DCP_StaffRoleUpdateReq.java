package com.dsc.spos.json.cust.req;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StaffRoleUpdate
 *   說明：用户所属角色更新
 * 服务说明：用户所属角色更新
 * @author luoln 
 * @since  2017-02-23
 */
public class DCP_StaffRoleUpdateReq extends JsonBasicReq{
	/**JSON Request
	 * 
	  {
		"serviceId": "StaffRoleUpdate",	必傳且非空，服務名
		"token": "f14ee75ff5b220177ac0dc538bdea08c",	必傳且非空，訪問令牌
		"staffNO": "10001",	必傳且非空，用户编码
		selectedRoles: [	
		   {		         	
		      "opGroup": "111",	必傳且非空，角色编码
		   }
		]
	  }
	* 
	**/
	
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
		private String staffNo;
		private List<level1Elm> selectedRoles;
		
		
		
		public String getStaffNo()
		{
			return staffNo;
		}
		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}
		public List<level1Elm> getSelectedRoles() {
			return selectedRoles;
		}
		public void setSelectedRoles(List<level1Elm> selectedRoles) {
			this.selectedRoles = selectedRoles;
		}

	}
	
	
	public  class level1Elm
	{
		private String opGroup;

		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
	}
}
