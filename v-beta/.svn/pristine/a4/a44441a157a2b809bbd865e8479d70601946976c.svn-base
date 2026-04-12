package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsOnlineClassUpdateReq extends JsonBasicReq {
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Plu> pluList;
		private List<level1Class> classList;
		public List<level1Plu> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Plu> pluList) {
			this.pluList = pluList;
		}
		public List<level1Class> getClassList() {
			return classList;
		}
		public void setClassList(List<level1Class> classList) {
			this.classList = classList;
		}			  
	}
	
	
	public class level1Plu
	{
		private String pluNo;
		private String pluType;
		public String getPluNo() {
			return pluNo;
		}
	
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}

		public String getPluType() {
			return pluType;
		}
	
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		
	}
	
	public class level1Class
	{
		private String classNo;
		public String getClassNo() {
			return classNo;
		}	
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
							
	}
	
	
}
