package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_BookingTimeCreateReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String bookingTimeNo;//时段编码
		private String bookingTime;//开始时间HH:MM
		private String memo;
		private String status;
		
		private String restrictShop;//适用门店：0-所有门店1-指定门店	
		private List<range> rangeList;
			
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}		
		public List<range> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<range> rangeList) {
			this.rangeList = rangeList;
		}
		public String getBookingTimeNo() {
			return bookingTimeNo;
		}
		public void setBookingTimeNo(String bookingTimeNo) {
			this.bookingTimeNo = bookingTimeNo;
		}
		public String getBookingTime() {
			return bookingTime;
		}
		public void setBookingTime(String bookingTime) {
			this.bookingTime = bookingTime;
		}
											
	}
	
		
	public class range 
	{
		private String shopId;
		public String getShopId() {
			return shopId;
		}
	
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}		
	}
	
	

}
