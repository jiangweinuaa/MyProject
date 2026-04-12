package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsOnlineLogisticsInfoUpdateReq extends JsonBasicReq {
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
	  private level1LogisticsInfo logisticsInfo;
	  
		public List<level1Plu> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Plu> pluList) {
			this.pluList = pluList;
		}
		public level1LogisticsInfo getLogisticsInfo() {
			return logisticsInfo;
		}
		public void setLogisticsInfo(level1LogisticsInfo logisticsInfo) {
			this.logisticsInfo = logisticsInfo;
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
	
	public class level1LogisticsInfo
	{
		private String shopPickUp;//是否支持自提0-否1-是
		private String cityDeliver;//是否支持同城配送0-否1-是
		private String expressDeliver;//是否支持全国快递0-否1-是
		private String freightFree;//是否包邮0-否1-是
		private String freightTemplateId;//运费模板编码
	public String getShopPickUp() {
		return shopPickUp;
	}
	public void setShopPickUp(String shopPickUp) {
		this.shopPickUp = shopPickUp;
	}
	public String getCityDeliver() {
		return cityDeliver;
	}
	public void setCityDeliver(String cityDeliver) {
		this.cityDeliver = cityDeliver;
	}
	public String getExpressDeliver() {
		return expressDeliver;
	}
	public void setExpressDeliver(String expressDeliver) {
		this.expressDeliver = expressDeliver;
	}
	public String getFreightFree() {
		return freightFree;
	}
	public void setFreightFree(String freightFree) {
		this.freightFree = freightFree;
	}
	public String getFreightTemplateId() {
		return freightTemplateId;
	}
	public void setFreightTemplateId(String freightTemplateId) {
		this.freightTemplateId = freightTemplateId;
	}
		
			
			
		
	}
	
	
}
