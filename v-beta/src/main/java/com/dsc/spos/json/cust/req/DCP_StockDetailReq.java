package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 库存结存查询
 * @author yuanyy
 *
 */
public class DCP_StockDetailReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		private String pluNo;
		private String keyTxt;
		private List<OrganizationList> organizationList;
		public String getPluNo() {
			return pluNo;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public List<OrganizationList> getOrganizationList() {
			return organizationList;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public void setOrganizationList(List<OrganizationList> organizationList) {
			this.organizationList = organizationList;
		}
		
		
	}
	
	public class OrganizationList{
		
		private String organizationNo;

		public String getOrganizationNo() {
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		
	}
	
	
}
