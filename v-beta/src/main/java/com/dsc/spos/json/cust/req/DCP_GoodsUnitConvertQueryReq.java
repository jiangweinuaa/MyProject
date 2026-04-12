package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品单位换算信息查询
 * 2018-09-21
 * @author yuanyy
 *
 */
public class DCP_GoodsUnitConvertQueryReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pluNo ;
		private String pluName ;
		private String oUnit ;
		private String unit ;
		private String status ;
		private String keyTxt;

		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getoUnit() {
			return oUnit;
		}
		public void setoUnit(String oUnit) {
			this.oUnit = oUnit;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}


}
