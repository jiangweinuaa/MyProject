package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ParaSetQueryNewRes extends JsonRes {
	
	private level1Elm datas;

	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private List<ClassList> classList;

		public List<ClassList> getClassList() {
			return classList;
		}

		public void setClassList(List<ClassList> classList) {
			this.classList = classList;
		}
		
	}
	
	public class ClassList{
		private String classNo;// classNo 规格中没有，方便后期维护和查问题，我先加上
		private String className;
		
		private List<ItemList> itemList;
		
		public String getClassNo() {
			return classNo;
		}

		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}

		public String getClassName() {
			return className;
		}

		public List<ItemList> getItemList() {
			return itemList;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public void setItemList(List<ItemList> itemList) {
			this.itemList = itemList;
		}
		
	}
	

	public class ItemList {
		private String item;
		private String itemName;
		private String itemValue;
		private String remark;
		private String conType;
		
		private List<level2Elm> para;
		private List<level2ElmLang> itemlang;
		
		public List<level2ElmLang> getItemlang() {
			return itemlang;
		}

		public void setItemlang(List<level2ElmLang> itemlang) {
			this.itemlang = itemlang;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getConType() {
			return conType;
		}

		public void setConType(String conType) {
			this.conType = conType;
		}

		public String getItem() {
			return item;
		}

		public String getItemValue() {
			return itemValue;
		}

		public List<level2Elm> getPara() {
			return para;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		public void setPara(List<level2Elm> para) {
			this.para = para;
		}

	}

	public class level2Elm {
		
		private String itemValue;
		private String valueName;
		private String status;
		private String onSale;
		private List<level3ElmLang> valuelang;
		

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		public String getValueName() {
			return valueName;
		}

		public void setValueName(String valueName) {
			this.valueName = valueName;
		}

		public String getOnSale() {
			return onSale;
		}

		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}

		public List<level3ElmLang> getValuelang() {
			return valuelang;
		}

		public void setValuelang(List<level3ElmLang> valuelang) {
			this.valuelang = valuelang;
		}

	}

	public class level2ElmLang {
		private String langType;
		private String itemName;

		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
	}

	public class level3ElmLang {
		private String langType;
		private String valueName;

		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getValueName() {
			return valueName;
		}

		public void setValueName(String valueName) {
			this.valueName = valueName;
		}

	}

	public class level3Elm {
		private String modularId;// 模块代号
		private String modularName;

		public String getModularId() {
			return modularId;
		}

		public String getModularName() {
			return modularName;
		}

		public void setModularId(String modularId) {
			this.modularId = modularId;
		}

		public void setModularName(String modularName) {
			this.modularName = modularName;
		}

	}

}
