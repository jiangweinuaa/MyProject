package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 组织结构树查询
 *
 */
public class DCP_OrgTreeQueryRes extends JsonRes {
	private List<OrgDatas> datas ;

	public List<OrgDatas> getDatas() {
		return datas;
	}

	public void setDatas(List<OrgDatas> datas) {
		this.datas = datas;
	}
	
	public class OrgDatas{
		private String organizationNo;
		private String orgName;
		private String orgType;
		private String orgForm;
		private String upOrg;
		private String belfirm;
		private String status;
		
		private List<WarehouseList> warehouseList;
		private List<OrgDatas> children;
		public String getOrganizationNo() {
			return organizationNo;
		}
		public String getOrgName() {
			return orgName;
		}
		public String getOrgType() {
			return orgType;
		}
		public String getOrgForm() {
			return orgForm;
		}
		public String getUpOrg() {
			return upOrg;
		}
		public String getBelfirm() {
			return belfirm;
		}
		public List<WarehouseList> getWarehouseList() {
			return warehouseList;
		}
		public List<OrgDatas> getChildren() {
			return children;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}
		public void setOrgForm(String orgForm) {
			this.orgForm = orgForm;
		}
		public void setUpOrg(String upOrg) {
			this.upOrg = upOrg;
		}
		public void setBelfirm(String belfirm) {
			this.belfirm = belfirm;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setWarehouseList(List<WarehouseList> warehouseList) {
			this.warehouseList = warehouseList;
		}
		public void setChildren(List<OrgDatas> children) {
			this.children = children;
		}
		
	} 
	
	public class WarehouseList{
		private String warehouse;
		private String warehouseName;
		public String getWarehouse() {
			return warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		
		
	}
	
	
}
