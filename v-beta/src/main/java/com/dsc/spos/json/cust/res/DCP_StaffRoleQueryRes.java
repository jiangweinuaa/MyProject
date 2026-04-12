package com.dsc.spos.json.cust.res;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：StaffRoleGet
 *    說明：用户所属角色信息查询
 * 服务说明：用户所属角色信息查询
 * @author luoln 
 * @since  2017-02-28
 */
public class DCP_StaffRoleQueryRes extends JsonRes{
	/**JSON Response
	 * 	
		{	
			"success": true,	成功否
			"serviceStatus": "000",	服務狀態代碼
			"serviceDescription": "服務執行成功",	服務狀態說明
			unselectRoles: [	
			   {
			      "opGroup": "111",	角色编码
			      "opgName": "店长",	角色名称
			   }	
			]	
			selectedRoles: [	
			   {
			      "opGroup": "111",	角色编码
			      "opgName": "店长",	角色名称
			   }
			 ]
		 }
	 * 
	 */
	
	private List<level1Elm> unselectRoles;
	private List<level1Elm> selectedRoles;
	
	public List<level1Elm> getUnselectRoles() {
		return unselectRoles;
	}
	public void setUnselectRoles(List<level1Elm> unselectRoles) {
		this.unselectRoles = unselectRoles;
	}
	
	public List<level1Elm> getSelectedRoles() {
		return selectedRoles;
	}
	public void setSelectedRoles(List<level1Elm> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
	
	public class level1Elm
	{
		private String opGroup;
		private String opgName;
		
		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
		
		public String getOpgName() {
			return opgName;
		}
		public void setOpgName(String opgName) {
			this.opgName = opgName;
		}
	}
}
