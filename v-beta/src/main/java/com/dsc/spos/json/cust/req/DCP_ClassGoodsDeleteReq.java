package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ClassGoodsDeleteReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> goodsList;
		public List<level1Elm> getGoodsList() {
			return goodsList;
		}
	
		public void setGoodsList(List<level1Elm> goodsList) {
			this.goodsList = goodsList;
		}
		
		
	}
	
	public class level1Elm
	{
		private String classType;
		private String classNo;
		private String pluNo;
		public String getClassType() {
			return classType;
		}
		public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}

		
			
	}
	
	

}
