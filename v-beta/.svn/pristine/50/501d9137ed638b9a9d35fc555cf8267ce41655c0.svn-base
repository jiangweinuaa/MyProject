package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息修改 2018-09-26
 * @author yuanyy
 */
public class DCP_StaffUpdateReq extends JsonBasicReq {
	
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
		private String status;
		private String lang;
		private String staffName;
		private String departNo;
		private String en_cash;
		private String viewAbleDay;

		private String telephone;
		private String discPowerType;
		private String disc;
		private String maxFreeAmt;
        private String employeeNo;

        private String orgRange;

        private String distributor;
        private String defaultOrg;
		private List<levelWarehouseElm> warehouseList;

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

		private String[] opGroup;
		//private String[] org;

        private List<DCP_StaffUpdateReq.Org> org;
		
		private List<level1Elm> datas ;
		
		
		public String getStaffName() {
			return staffName;
		}

		public void setStaffName(String staffName) {
			this.staffName = staffName;
		}

		public String getDepartNo() {
			return departNo;
		}

		public void setDepartNo(String departNo) {
			this.departNo = departNo;
		}

		


		public String getStaffNo()
		{
			return staffNo;
		}

		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public String[] getOpGroup() {
			return opGroup;
		}

		public void setOpGroup(String[] opGroup) {
			this.opGroup = opGroup;
		}

		//public String[] getOrg() {
		//	return org;
		//}

		//public void setOrg(String[] org) {
		//	this.org = org;
		//}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}


        public List<levelWarehouseElm> getWarehouseList()
        {
            return warehouseList;
        }

        public void setWarehouseList(List<levelWarehouseElm> warehouseList)
        {
            this.warehouseList = warehouseList;
        }

        public String getEmployeeNo() {
            return employeeNo;
        }

        public void setEmployeeNo(String employeeNo) {
            this.employeeNo = employeeNo;
        }

        public String getOrgRange() {
            return orgRange;
        }

        public void setOrgRange(String orgRange) {
            this.orgRange = orgRange;
        }

        public List<DCP_StaffUpdateReq.Org> getOrg() {
            return org;
        }

        public void setOrg(List<DCP_StaffUpdateReq.Org> org) {
            this.org = org;
        }

        public String getDistributor() {
            return distributor;
        }

        public void setDistributor(String distributor) {
            this.distributor = distributor;
        }

        public String getDefaultOrg() {
            return defaultOrg;
        }

        public void setDefaultOrg(String defaultOrg) {
            this.defaultOrg = defaultOrg;
        }
    }
	
	
	public  class level1Elm {
		private String langType ;
		private String opName;
		private String status ;
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
    public  class levelWarehouseElm
    {
        private String organizationNo ;
        private String warehouseNo ;
		private String  isDefault ;

        public String getOrganizationNo()
        {
            return organizationNo;
        }

        public void setOrganizationNo(String organizationNo)
        {
            this.organizationNo = organizationNo;
        }

        public String getWarehouseNo()
        {
            return warehouseNo;
        }

        public void setWarehouseNo(String warehouseNo)
        {
            this.warehouseNo = warehouseNo;
        }

		public String getIsDefault()
		{
			return isDefault;
		}

		public void setIsDefault(String isDefault)
		{
			this.isDefault = isDefault;
		}
	}

    @Getter
    @Setter
    public class Org{
        private String organizationNo;
        private String isExpand;

    }
	
}
