package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ParaSetQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public class level1Elm {
		private String item;
		private String itemName;
		private String paraType;
		private String conType;
		private String def;
		private String defined;
		private String classNO;
		private String status;
		private String remark;

		private List<level2Elm> para;
		private List<level2ElmLang> itemlang;

		public String getParaType() {
			return paraType;
		}

		public void setParaType(String paraType) {
			this.paraType = paraType;
		}

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public List<level2ElmLang> getItemlang() {
			return itemlang;
		}

		public void setItemlang(List<level2ElmLang> itemlang) {
			this.itemlang = itemlang;
		}

		public List<level2Elm> getPara() {
			return para;
		}

		public void setPara(List<level2Elm> para) {
			this.para = para;
		}

		public String getConType() {
			return conType;
		}

		public void setConType(String conType) {
			this.conType = conType;
		}

		public String getDef() {
			return def;
		}

		public void setDef(String def) {
			this.def = def;
		}

		public String getDefined() {
			return defined;
		}

		public void setDefined(String defined) {
			this.defined = defined;
		}

		public String getClassNO() {
			return classNO;
		}

		public void setClassNO(String classNO) {
			this.classNO = classNO;
		}
	
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getRemark() {
			return remark;
		}


		public void setRemark(String remark) {
			this.remark = remark;
		}
	}

	public class level2Elm {
		private String itemValue;
		private String valueName;

		private String status;
		private String onSale;
		private List<level3ElmLang> valuelang;

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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getOnSale() {
			return onSale;
		}

		public List<level3ElmLang> getValuelang() {
			return valuelang;
		}

	
		public void setOnSale(String onSale) {
			this.onSale = onSale;
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

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
