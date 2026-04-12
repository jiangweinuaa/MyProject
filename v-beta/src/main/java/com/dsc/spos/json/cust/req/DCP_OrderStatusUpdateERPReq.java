package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_OrderStatusUpdate
 * 服务说明：订单状态修改
 * @author jinzma 
 * @since  2020-10-30
 */
public class DCP_OrderStatusUpdateERPReq extends JsonBasicReq {

	private String orderNo ;
	private String deliveryNo ;
	private String status ;
	private String deliveryStatus ;
	private String productStatus ;
	private String delName;
	private String delTelephone;
	private String deliveryType;

	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getDeliveryNo() {
		return deliveryNo;
	}
	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
	public String getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
	public String getDelName() {
		return delName;
	}
	public void setDelName(String delName) {
		this.delName = delName;
	}
	public String getDelTelephone() {
		return delTelephone;
	}
	public void setDelTelephone(String delTelephone) {
		this.delTelephone = delTelephone;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

}
