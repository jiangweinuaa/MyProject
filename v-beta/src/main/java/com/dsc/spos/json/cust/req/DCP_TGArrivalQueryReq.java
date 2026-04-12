package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：TGArrivalGetDCP
 * 服务说明： 预计到团查询
 * @author jinzma
 * @since  2019-02-13
 */
public class DCP_TGArrivalQueryReq extends JsonBasicReq  {

	private String oShopId;
	private String orderNO;
	private String beginDate;
	private String endDate;
	private String travelNO;
	private String guideNO;
	private String status;

	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getOrderNO() {
		return orderNO;
	}
	public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTravelNO() {
		return travelNO;
	}
	public void setTravelNO(String travelNO) {
		this.travelNO = travelNO;
	}
	public String getGuideNO() {
		return guideNO;
	}
	public void setGuideNO(String guideNO) {
		this.guideNO = guideNO;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}




}
