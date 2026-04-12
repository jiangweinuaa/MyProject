package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

public class DCP_CustomerQueryRes extends JsonRes 
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String customerNo; //编号	
		private String customerName; //名称
		private String abbr; //简称	
		private String district; //地区
		private String legalPerson; //法人
		private String telephone;//联系电话
		private String address;//联系地址customerNo
		private String linkPerson;//联系人
		private String taxNO;//税号
		private String memo;//备注	
		private String status;
		private String restrictShop;
        private String deliveryAddress;
        private String copyWriting;
		private List<range> rangeList;
		private List<ServiceStaff> serviceStaffList;

		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getAbbr() {
			return abbr;
		}
		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getLegalPerson() {
			return legalPerson;
		}
		public void setLegalPerson(String legalPerson) {
			this.legalPerson = legalPerson;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getLinkPerson() {
			return linkPerson;
		}
		public void setLinkPerson(String linkPerson) {
			this.linkPerson = linkPerson;
		}
		public String getTaxNO() {
			return taxNO;
		}
		public void setTaxNO(String taxNO) {
			this.taxNO = taxNO;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public List<range> getRangeList()
		{
			return rangeList;
		}
		public void setRangeList(List<range> rangeList)
		{
			this.rangeList = rangeList;
		}
		public String getCustomerNo()
		{
			return customerNo;
		}
		public void setCustomerNo(String customerNo)
		{
			this.customerNo = customerNo;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public String getDeliveryAddress() {
			return deliveryAddress;
		}
		public void setDeliveryAddress(String deliveryAddress) {
			this.deliveryAddress = deliveryAddress;
		}
		public String getCopyWriting() {
			return copyWriting;
		}
		public void setCopyWriting(String copyWriting) {
			this.copyWriting = copyWriting;
		}
		public List<ServiceStaff> getServiceStaffList() {
			return serviceStaffList;
		}
		public void setServiceStaffList(List<ServiceStaff> serviceStaffList) {
			this.serviceStaffList = serviceStaffList;
		}
	}

	public class range {
		private String shopId; //门店编码	
		private String shopName; //门店名称

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
	}

	@Data
	public class ServiceStaff {
		private String opNo;
		private String opName;
	}

}
