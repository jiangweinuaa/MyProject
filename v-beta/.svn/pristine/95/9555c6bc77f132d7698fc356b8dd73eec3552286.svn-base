package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsOnlinePreSaleInfoUpdateReq extends JsonBasicReq {
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Plu> pluList;
	  private level1PreSaleInfo preSaleInfo;
	  
		public List<level1Plu> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Plu> pluList) {
			this.pluList = pluList;
		}
		public level1PreSaleInfo getPreSaleInfo() {
			return preSaleInfo;
		}
		public void setPreSaleInfo(level1PreSaleInfo preSaleInfo) {
			this.preSaleInfo = preSaleInfo;
		}	  	  
	}
	
	
	public class level1Plu
	{
		private String pluNo;
		public String getPluNo() {
			return pluNo;
		}
	
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		
	}
	
	public class level1PreSaleInfo
	{
		private String preSale;//是否预订，需提前预订0-否1-是
		private String deliveryDateType;//发货时机类型1：付款成功后发货2：指定日期发货
		private String deliveryDateType2;//发货时间类型1：小时 2：天
		private String deliveryDateValue;//付款后%S天后发货
		private String deliveryDate;//预计发货日期
		public String getPreSale() {
			return preSale;
		}
		public void setPreSale(String preSale) {
			this.preSale = preSale;
		}
		public String getDeliveryDateType() {
			return deliveryDateType;
		}
		public void setDeliveryDateType(String deliveryDateType) {
			this.deliveryDateType = deliveryDateType;
		}
		public String getDeliveryDateType2() {
			return deliveryDateType2;
		}
		public void setDeliveryDateType2(String deliveryDateType2) {
			this.deliveryDateType2 = deliveryDateType2;
		}
		public String getDeliveryDateValue() {
			return deliveryDateValue;
		}
		public void setDeliveryDateValue(String deliveryDateValue) {
			this.deliveryDateValue = deliveryDateValue;
		}
		public String getDeliveryDate() {
			return deliveryDate;
		}
		public void setDeliveryDate(String deliveryDate) {
			this.deliveryDate = deliveryDate;
		}
			
			
		
	}
	
	
}
