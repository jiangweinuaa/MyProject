package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayClassCreateReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String classNo;
		private String status;
		private String sortId;
		private String memo;
		private List<level1Elm> className_lang;
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}	
		
		public String getMemo() {
				return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public List<level1Elm> getClassName_lang() {
			return className_lang;
		}
		public void setClassName_lang(List<level1Elm> className_lang) {
			this.className_lang = className_lang;
		}
			
			
		
	}
	
	public class level1Elm {
		private String langType;
		private String name;
		
		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}

		
		
	

	}

}
