package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 货运包裹新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageCreateReq extends JsonBasicReq 
{

	private levelRequest request;		

	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String packageNo;
		private String packageName;
		private String measureNo;
		private String measureName;
		private String temperateNo;
		private String temperateName;
		private String packageFee;
		private String status;

		public String getPackageNo() {
			return packageNo;
		}
		public void setPackageNo(String packageNo) {
			this.packageNo = packageNo;
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getMeasureNo() {
			return measureNo;
		}
		public void setMeasureNo(String measureNo) {
			this.measureNo = measureNo;
		}
		public String getMeasureName() {
			return measureName;
		}
		public void setMeasureName(String measureName) {
			this.measureName = measureName;
		}
		public String getTemperateNo() {
			return temperateNo;
		}
		public void setTemperateNo(String temperateNo) {
			this.temperateNo = temperateNo;
		}
		public String getTemperateName() {
			return temperateName;
		}
		public void setTemperateName(String temperateName) {
			this.temperateName = temperateName;
		}
		public String getPackageFee() {
			return packageFee;
		}
		public void setPackageFee(String packageFee) {
			this.packageFee = packageFee;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}




}
