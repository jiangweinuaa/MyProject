package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_AttributionCreateReq.attrGroup;
import com.dsc.spos.json.cust.req.DCP_AttributionCreateReq.level1Elm;

public class DCP_AttributionQueryRes extends JsonRes {
	
private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String attrId;
		private String attrName;
		private String productParam;//是否用于产品参数：0-否1-是
		private String multiSpec;//是否用于多规格
		private List<attrGroup> attrGroup;//属性分组
		private String status;
		private String sortId;
		private String memo;
		private List<level2Elm> attrName_lang;
		private List<attrValue> attrValue;//属性规格
		
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
		public List<attrGroup> getAttrGroup() {
			return attrGroup;
		}
		public void setAttrGroup(List<attrGroup> attrGroup) {
			this.attrGroup = attrGroup;
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
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public List<level2Elm> getAttrName_lang() {
			return attrName_lang;
		}
		public void setAttrName_lang(List<level2Elm> attrName_lang) {
			this.attrName_lang = attrName_lang;
		}
		public List<attrValue> getAttrValue() {
			return attrValue;
		}
		public void setAttrValue(List<attrValue> attrValue) {
			this.attrValue = attrValue;
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
	
	public class attrGroup 
	{
		private String attrGroupId;
		private String attrGroupName;
		private String sortId;
		public String getAttrGroupId() {
			return attrGroupId;
		}
		public void setAttrGroupId(String attrGroupId) {
			this.attrGroupId = attrGroupId;
		}
		public String getAttrGroupName() {
			return attrGroupName;
		}
		public void setAttrGroupName(String attrGroupName) {
			this.attrGroupName = attrGroupName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		
		
	}
	
	public class attrValue
	{
		private String attrValueId;
		private String attrValueName;
		//private String sortId;
		public String getAttrValueId() {
			return attrValueId;
		}
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}
		public String getAttrValueName() {
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName) {
			this.attrValueName = attrValueName;
		}
		/*public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}*/
		
		
	}

}
