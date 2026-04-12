package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_ClassGoodsDisplayNameUpdateReq  extends JsonBasicReq 
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
		private String classType;
		private String classNo;
		private String pluNo;
		private List<displayName> displayName_lang;
		
		
		public List<displayName> getDisplayName_lang() {
			return displayName_lang;
		}
		public void setDisplayName_lang(List<displayName> displayName_lang) {
			this.displayName_lang = displayName_lang;
		}
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
	
	
	public class displayName
	{
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
