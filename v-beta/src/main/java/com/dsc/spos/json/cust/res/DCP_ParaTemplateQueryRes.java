package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 模板参数设置查询
 * @author 2020-05-29
 *
 */
public class DCP_ParaTemplateQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String templateId;
		private String templateName;
		private String templateType;
		private String restrictShop;
		private String restrictMachine;
		
		private List<ItemList> itemList;
		public String getTemplateId() {
			return templateId;
		}
		public String getTemplateName() {
			return templateName;
		}
		public String getTemplateType() {
			return templateType;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public String getRestrictMachine() {
			return restrictMachine;
		}
		public List<ItemList> getItemList() {
			return itemList;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public void setRestrictMachine(String restrictMachine) {
			this.restrictMachine = restrictMachine;
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
		private String cnfflg;
		private String onSale;
		private List<level3ElmLang> valuelang;


		public String getCnfflg() {
			return cnfflg;
		}

		public void setCnfflg(String cnfflg) {
			this.cnfflg = cnfflg;
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
