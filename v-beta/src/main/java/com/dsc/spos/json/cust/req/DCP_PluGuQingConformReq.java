package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品实售同步
 * @author yuanyy 2020-03-01
 * 
 */
public class DCP_PluGuQingConformReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String rDate;
		private String guQingNo;
		private String saleNo;
		private String oEId;
		private String oShopId;
		private String modifyBy;
		private String modifyByName;
		private String platformType; // pos：POS,app：APP云POS,saoMa：扫码点餐
		
		private List<level2Elm> pluList;
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
		public String getSaleNo() {
			return saleNo;
		}
		public void setSaleNo(String saleNo) {
			this.saleNo = saleNo;
		}
	
		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		public String getModifyByName() {
			return modifyByName;
		}
		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}
		
		public String getPlatformType() {
			return platformType;
		}
		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}
		public List<level2Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level2Elm> pluList) {
			this.pluList = pluList;
		}
		
	}

	public class level2Elm{
		private String pluNo;
		private String pUnit;
		private String barCode;
		private String dtNo;
		private String dtName;
		private String restQty; //剩余可售数量
		private String adjustQty;
		private String adjustDirection; // 枚举: subtract：扣减，,addBack：加回
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getpUnit() {
			return pUnit;
		}
		public String getBarCode() {
			return barCode;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}
		public String getDtNo() {
			return dtNo;
		}
		public void setDtNo(String dtNo) {
			this.dtNo = dtNo;
		}
		public String getDtName() {
			return dtName;
		}
		public void setDtName(String dtName) {
			this.dtName = dtName;
		}
		public String getRestQty() {
			return restQty;
		}
		public void setRestQty(String restQty) {
			this.restQty = restQty;
		}
		public String getAdjustQty() {
			return adjustQty;
		}
		public void setAdjustQty(String adjustQty) {
			this.adjustQty = adjustQty;
		}
		public String getAdjustDirection() {
			return adjustDirection;
		}
		public void setAdjustDirection(String adjustDirection) {
			this.adjustDirection = adjustDirection;
		}
		
	}

}
