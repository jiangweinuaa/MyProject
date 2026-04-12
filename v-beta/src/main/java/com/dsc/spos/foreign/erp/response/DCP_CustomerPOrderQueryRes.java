package com.dsc.spos.foreign.erp.response;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_CustomerPOrderQueryRes extends JsonRes{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
    	private String pOrderNo;
    	private String customerNo;
    	private String customerName;
    	private String telephone;
    	private String address;
    	private String templateNo;
    	private String shopNo;
    	private String eId;
    	private String tot_amt;
    	private String tot_qty;
    	private String tot_cqty;
    	private String memo;
    	private String rDate;
    	private String bDate;
    	private String salesManNo;
    	private String salesManName;
    	private String createOpId;
    	private String createOpName;
    	private String createTime;
    	private String lastModiOpId;
    	private String lastModiOpName;
    	private String lastModiTime;
    	private String confirmOpId;
    	private String confirmOpName;
    	private String confirmTime;
    	private String status;
    	private String processStatus;
    	
    	
		public String getProcessStatus() {
			return processStatus;
		}
		public void setProcessStatus(String processStatus) {
			this.processStatus = processStatus;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getpOrderNo() {
			return pOrderNo;
		}
		public void setpOrderNo(String pOrderNo) {
			this.pOrderNo = pOrderNo;
		}
		public String getCustomerNo() {
			return customerNo;
		}
		public void setCustomerNo(String customerNo) {
			this.customerNo = customerNo;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
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
		public String getTemplateNo() {
			return templateNo;
		}
		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
		public String getShopNo() {
			return shopNo;
		}
		public void setShopNo(String shopNo) {
			this.shopNo = shopNo;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getTot_amt() {
			return tot_amt;
		}
		public void setTot_amt(String tot_amt) {
			this.tot_amt = tot_amt;
		}
		public String getTot_qty() {
			return tot_qty;
		}
		public void setTot_qty(String tot_qty) {
			this.tot_qty = tot_qty;
		}
		public String getTot_cqty() {
			return tot_cqty;
		}
		public void setTot_cqty(String tot_cqty) {
			this.tot_cqty = tot_cqty;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
		public String getSalesManNo() {
			return salesManNo;
		}
		public void setSalesManNo(String salesManNo) {
			this.salesManNo = salesManNo;
		}
		public String getSalesManName() {
			return salesManName;
		}
		public void setSalesManName(String salesManName) {
			this.salesManName = salesManName;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
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
		public String getLastModiOpName() {
			return lastModiOpName;
		}
		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}
		public String getLastModiTime() {
			return lastModiTime;
		}
		public void setLastModiTime(String lastModiTime) {
			this.lastModiTime = lastModiTime;
		}
		public String getConfirmOpId() {
			return confirmOpId;
		}
		public void setConfirmOpId(String confirmOpId) {
			this.confirmOpId = confirmOpId;
		}

		public String getConfirmOpName() {
			return confirmOpName;
		}
		public void setConfirmOpName(String confirmOpName) {
			this.confirmOpName = confirmOpName;
		}
		public String getConfirmTime() {
			return confirmTime;
		}
		public void setConfirmTime(String confirmTime) {
			this.confirmTime = confirmTime;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		
	}

	
}
