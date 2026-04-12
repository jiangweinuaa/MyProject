package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AttributionUpdateReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String attrId;
		private String productParam;//是否用于产品参数0-否1-是
		private String multiSpec;//是否用于多规格0-否1-是
		private List<attrGroup> attrGroup;//属性分组列表
		private String status;
		private String sortId;
		private String memo;
		private List<level1Elm> attrName_lang;
		
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
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		
		public String getProductParam() {
			return productParam;
		}
		public void setProductParam(String productParam) {
			this.productParam = productParam;
		}
		public String getMultiSpec() {
			return multiSpec;
		}
		public void setMultiSpec(String multiSpec) {
			this.multiSpec = multiSpec;
		}
		public List<attrGroup> getAttrGroup() {
			return attrGroup;
		}
		public void setAttrGroup(List<attrGroup> attrGroup) {
			this.attrGroup = attrGroup;
		}
		public List<level1Elm> getAttrName_lang() {
			return attrName_lang;
		}
		public void setAttrName_lang(List<level1Elm> attrName_lang) {
			this.attrName_lang = attrName_lang;
		}
		
			
		
	}
	
	public class attrGroup 
	{
		private String attrGroupId;		
		private String sortId;
		public String getAttrGroupId() {
			return attrGroupId;
		}
		public void setAttrGroupId(String attrGroupId) {
			this.attrGroupId = attrGroupId;
		}		
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
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
