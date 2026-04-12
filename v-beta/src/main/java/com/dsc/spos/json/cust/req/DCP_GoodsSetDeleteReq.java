package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetDeleteReq extends JsonBasicReq
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
		private	List<Plu> pluNoList;

		public List<Plu> getPluNoList() {
			return pluNoList;
		}	
		public void setPluNoList(List<Plu> pluNoList) {
			this.pluNoList = pluNoList;
		}		
	}
	
	public class Plu
	{
		private String pluNo;

		public String getPluNo() {
			return pluNo;
		}
	
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		
		
	}
	
}
