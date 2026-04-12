package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_AttributionValueQueryRes extends JsonRes {
	
private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String attrValueId;
		private String attrValueName;
		private String status;
		private String sortId;		
		private List<level2Elm> attrValueName_lang;
		private String attrId;	
		private String attrName;
		
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
		
		public List<level2Elm> getAttrValueName_lang() {
			return attrValueName_lang;
		}
		public void setAttrValueName_lang(List<level2Elm> attrValueName_lang) {
			this.attrValueName_lang = attrValueName_lang;
		}
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		
		public String getAttrValueName() {
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName) {
			this.attrValueName = attrValueName;
		}		
	}
	
	public class level2Elm{
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
