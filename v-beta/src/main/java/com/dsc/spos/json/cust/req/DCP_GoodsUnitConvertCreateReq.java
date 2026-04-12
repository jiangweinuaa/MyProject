package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 新增商品单位换算信息
 * 2018-09-20
 * @author yuanyy
 *
 */
public class DCP_GoodsUnitConvertCreateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pluNo ;
		private String oUnit ;
		private String unit ;
		private String oqty;
		private String qty;
		private String status ;

		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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
		public String getOqty() {
			return oqty;
		}
		public void setOqty(String oqty) {
			this.oqty = oqty;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
	}
}
