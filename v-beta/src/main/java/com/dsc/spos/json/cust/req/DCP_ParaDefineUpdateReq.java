package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level3Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level4Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.levelRequest;

/**
 * 服務函數：ParaDefineUpdate
 *    說明：参数定义修改
 * 服务说明：参数定义修改
 * @author jzma 
 * @since  2017-03-03
 */
public class DCP_ParaDefineUpdateReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
	
		private String item;
		private String paraType;
		private String conType;
		private String defValue;
		private String initdefValue;
		private String classNO;
		private String status;
		private String priority;
		private String toPriority;
		private String onSale;
		private String remark;
	//	private List<level1Elm> datas;
		private List<level1Elm> itemlang;
		
		private String itemName;
		private List<level2Elm> modulars;
		private List<level3Elm> para;
		public String getItem() {
			return item;
		}
		public String getParaType() {
			return paraType;
		}
		public String getConType() {
			return conType;
		}
		public String getDefValue() {
			return defValue;
		}
		public String getInitdefValue() {
			return initdefValue;
		}
		public String getClassNO() {
			return classNO;
		}
		public String getPriority() {
			return priority;
		}
		public String getToPriority() {
			return toPriority;
		}
		public String getOnSale() {
			return onSale;
		}
		public List<level1Elm> getItemlang() {
			return itemlang;
		}
		public String getItemName() {
			return itemName;
		}
		public List<level2Elm> getModulars() {
			return modulars;
		}
		public List<level3Elm> getPara() {
			return para;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public void setParaType(String paraType) {
			this.paraType = paraType;
		}
		public void setConType(String conType) {
			this.conType = conType;
		}
		public void setDefValue(String defValue) {
			this.defValue = defValue;
		}
		public void setInitdefValue(String initdefValue) {
			this.initdefValue = initdefValue;
		}
		public void setClassNO(String classNO) {
			this.classNO = classNO;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setToPriority(String toPriority) {
			this.toPriority = toPriority;
		}
		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}
		public void setItemlang(List<level1Elm> itemlang) {
			this.itemlang = itemlang;
		}
		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
		public void setModulars(List<level2Elm> modulars) {
			this.modulars = modulars;
		}
		public void setPara(List<level3Elm> para) {
			this.para = para;
		}
	
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}

	}
	
	public class level1Elm{
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
	
	public class level2Elm{
		
		private String modularId;
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
	
	public class level3Elm{
		private String itemValue;
		private String valueName;
		private String status;
		private String onSale;
		
		private List<level4Elm> valuelang;

		public String getItemValue() {
			return itemValue;
		}

		public String getValueName() {
			return valueName;
		}


		public String getOnSale() {
			return onSale;
		}
		
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
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

		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}

		public List<level4Elm> getValuelang() {
			return valuelang;
		}

		public void setValuelang(List<level4Elm> valuelang) {
			this.valuelang = valuelang;
		}
		
	}
	
	public class level4Elm{
		
		private String langType;
		private String valueName;
		
		public String getLangType() {
			return langType;
		}
		public String getValueName() {
			return valueName;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public void setValueName(String valueName) {
			this.valueName = valueName;
		}
		
	}
	
}
