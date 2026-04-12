package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayClassDeleteReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> classList;
		public List<level1Elm> getClassList() {
			return classList;
		}
	
		public void setClassList(List<level1Elm> classList) {
			this.classList = classList;
		}
		
	}
	
	public class level1Elm
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
