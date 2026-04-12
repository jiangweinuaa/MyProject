package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：OrgGet
 *    說明：选取组织查询
 * 服务说明：选取组织查询
 * @author luoln 
 * @since  2017-03-06
 */
public class DCP_OrgQueryRes extends JsonRes{
	/**JSON Response
	 * 	
	  {	
		"success": true,	成功否
		"serviceStatus": "000",	服務狀態代碼
		"serviceDescription": "服務執行成功",	服務狀態說明
		datas: [	
		   {
		      "organizationNO": "111",	组织编码
		      "orgName": "张小小",	组织名称
		      "upOrg": "张小小",	上级组织编号
      		  "upOrgName": "",	上级组织
      		  "orgForm": "",	组织形态

		   }	
		]	
	  }
	 * 
	**/
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{		
		private String organizationNo;
		private String orgName;
		private String upOrg;
		private String upOrgName;
		private String orgForm;

		public String getOrganizationNo() {
			return organizationNo;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public String getOrgName() {
			return orgName;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		public String getUpOrg() {
			return upOrg;
		}
		public void setUpOrg(String upOrg) {
			this.upOrg = upOrg;
		}
		
		public String getUpOrgName() {
			return upOrgName;
		}
		public void setUpOrgName(String upOrgName) {
			this.upOrgName = upOrgName;
		}
		
		public String getOrgForm() {
			return orgForm;
		}
		public void setOrgForm(String orgForm) {
			this.orgForm = orgForm;
		}
	}
}
