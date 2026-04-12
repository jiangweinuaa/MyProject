package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_BookingTimeDeleteReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> bookingTimeList;

		public List<level1Elm> getBookingTimeList() {
			return bookingTimeList;
		}
	
		public void setBookingTimeList(List<level1Elm> bookingTimeList) {
			this.bookingTimeList = bookingTimeList;
		}
	    
		
		
	}
	
	public class level1Elm
	{		
		private String bookingTimeNo;

		public String getBookingTimeNo() {
			return bookingTimeNo;
		}
	
		public void setBookingTimeNo(String bookingTimeNo) {
			this.bookingTimeNo = bookingTimeNo;
		}

	
			
		
	}
	
	

}
