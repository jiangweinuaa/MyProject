package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AttributionValueUpdateReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String attrValueId;			
		private String status;
		private String sortId;
		private String attrId;	
		private List<level1Elm> attrValueName_lang;
		
		public String getAttrValueId() {
			return attrValueId;
		}
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}
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
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public List<level1Elm> getAttrValueName_lang() {
			return attrValueName_lang;
		}
		public void setAttrValueName_lang(List<level1Elm> attrValueName_lang) {
			this.attrValueName_lang = attrValueName_lang;
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
