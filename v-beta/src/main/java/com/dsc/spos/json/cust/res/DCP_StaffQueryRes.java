package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 用户信息查询 2018-09-26	
 * @author yuanyy	
 *
 */
public class DCP_StaffQueryRes extends JsonRes {
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String staffNo;
		private String opName;
		private String organizationNo;
		private String organizationName;

		private String departNo;
		private String departName;

		private String langType;
		private String status ;
		private String en_cash;
		private String viewAbleDay;

        private String telephone;
        private String discPowerType;
        private String disc;
        private String maxFreeAmt;

        private String orgRange;

        private String distributor;

        private String employeeNo;

        private String defaultOrg;

        private String defaultOrgName;

        private String createOpId;
        private String createOpName;
        private String createDeptName;

        private String createDeptId;
        private String createTime;
        private String lastModiOpId;
        private String lastModiTime;

        private String lastModiOpName;

		private List<levelwarehouseElm> warehouseList;

        public String getTelephone()
        {
            return telephone;
        }

        public void setTelephone(String telephone)
        {
            this.telephone = telephone;
        }

        public String getDiscPowerType()
        {
            return discPowerType;
        }

        public void setDiscPowerType(String discPowerType)
        {
            this.discPowerType = discPowerType;
        }

        public String getDisc()
        {
            return disc;
        }

        public void setDisc(String disc)
        {
            this.disc = disc;
        }

        public String getMaxFreeAmt()
        {
            return maxFreeAmt;
        }

        public void setMaxFreeAmt(String maxFreeAmt)
        {
            this.maxFreeAmt = maxFreeAmt;
        }

        public String getViewAbleDay() {
			return viewAbleDay;
		}

		public void setViewAbleDay(String viewAbleDay) {
			this.viewAbleDay = viewAbleDay;
		}

		public String getEn_cash() {
			return en_cash;
		}

		public void setEn_cash(String en_cash) {
			this.en_cash = en_cash;
		}

		private List<level2Elm> roleList;
		private List<level3Elm> orgList;
		private List<level4Elm> datas;

		public String getDepartNo() {
			return departNo;
		}

		public void setDepartNo(String departNo) {
			this.departNo = departNo;
		}

		public String getDepartName() {
			return departName;
		}

		public void setDepartName(String departName) {
			this.departName = departName;
		}

		

		public String getOpName() {
			return opName;
		}

		public void setOpName(String opName) {
			this.opName = opName;
		}

		

		public String getStaffNo()
		{
			return staffNo;
		}

		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}

		public String getOrganizationNo()
		{
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo)
		{
			this.organizationNo = organizationNo;
		}

		public String getOrganizationName() {
			return organizationName;
		}

		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}

		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level2Elm> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<level2Elm> roleList) {
			this.roleList = roleList;
		}

		public List<level3Elm> getOrgList() {
			return orgList;
		}

		public void setOrgList(List<level3Elm> orgList) {
			this.orgList = orgList;
		}

		public List<level4Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level4Elm> datas) {
			this.datas = datas;
		}

        public List<levelwarehouseElm> getWarehouseList()
        {
            return warehouseList;
        }

        public void setWarehouseList(List<levelwarehouseElm> warehouseList)
        {
            this.warehouseList = warehouseList;
        }

        public String getOrgRange() {
            return orgRange;
        }

        public void setOrgRange(String orgRange) {
            this.orgRange = orgRange;
        }

        public String getEmployeeNo() {
            return employeeNo;
        }

        public void setEmployeeNo(String employeeNo) {
            this.employeeNo = employeeNo;
        }

        public String getDefaultOrg() {
            return defaultOrg;
        }

        public void setDefaultOrg(String defaultOrg) {
            this.defaultOrg = defaultOrg;
        }

        public String getCreateOpId() {
            return createOpId;
        }

        public void setCreateOpId(String createOpId) {
            this.createOpId = createOpId;
        }

        public String getCreateDeptId() {
            return createDeptId;
        }

        public void setCreateDeptId(String createDeptId) {
            this.createDeptId = createDeptId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getLastModiOpId() {
            return lastModiOpId;
        }

        public void setLastModiOpId(String lastModiOpId) {
            this.lastModiOpId = lastModiOpId;
        }

        public String getLastModiTime() {
            return lastModiTime;
        }

        public void setLastModiTime(String lastModiTime) {
            this.lastModiTime = lastModiTime;
        }

        public String getDistributor() {
            return distributor;
        }

        public void setDistributor(String distributor) {
            this.distributor = distributor;
        }

        public String getLastModiOpName() {
            return lastModiOpName;
        }

        public void setLastModiOpName(String lastModiOpName) {
            this.lastModiOpName = lastModiOpName;
        }

        public String getDefaultOrgName() {
            return defaultOrgName;
        }

        public void setDefaultOrgName(String defaultOrgName) {
            this.defaultOrgName = defaultOrgName;
        }

        public String getCreateOpName() {
            return createOpName;
        }

        public void setCreateOpName(String createOpName) {
            this.createOpName = createOpName;
        }

        public String getCreateDeptName() {
            return createDeptName;
        }

        public void setCreateDeptName(String createDeptName) {
            this.createDeptName = createDeptName;
        }
    }

	public class level2Elm{
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

	public class level3Elm{
		private String org;
		private String orgName;

        private String expand;

        private List<level3Elm> children;

		public String getOrg() {
			return org;
		}
		public void setOrg(String org) {
			this.org = org;
		}
		public String getOrgName() {
			return orgName;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}


		public String getExpand() {
			return expand;
		}

		public void setExpand(String expand) {
			this.expand = expand;
		}

		public List<level3Elm> getChildren() {
			return children;
		}

		public void setChildren(List<level3Elm> children) {
			this.children = children;
		}
	}

	public class level4Elm{
		private String langType;
		private String opName;
		private String status;
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getOpName() {
			return opName;
		}
		public void setOpName(String opName) {
			this.opName = opName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}

    public class levelwarehouseElm
    {
        private String warehouseNo;
        private String warehouseName;
        private String isDefault;
		private String organizationNo;
		private String organizationName;



		public String getWarehouseNo()
        {
            return warehouseNo;
        }

        public void setWarehouseNo(String warehouseNo)
        {
            this.warehouseNo = warehouseNo;
        }

        public String getWarehouseName()
        {
            return warehouseName;
        }

        public void setWarehouseName(String warehouseName)
        {
            this.warehouseName = warehouseName;
        }

        public String getIsDefault()
        {
            return isDefault;
        }

        public void setIsDefault(String isDefault)
        {
            this.isDefault = isDefault;
        }

		public String getOrganizationNo()
		{
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo)
		{
			this.organizationNo = organizationNo;
		}

		public String getOrganizationName()
		{
			return organizationName;
		}

		public void setOrganizationName(String organizationName)
		{
			this.organizationName = organizationName;
		}
	}
}
