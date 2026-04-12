package com.dsc.spos.json.cust.req;
import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_ParaDefineQueryReq.levelRequest;
/**
 * 服務函數：ParaDefineCreate
 *    說明：参数定义新增
 * 服务说明：参数定义新增
 * @author jzma 
 * @since  2017-02-27
 */
public class DCP_ParaDefineCreateReq extends JsonBasicReq {
	/**
"serviceId": "ParaDefineCreateDCP",	必传且非空，服务名	
"token": "f14ee75ff5b220177ac0dc538bdea08c",	必传且非空，访问令牌	
"item": "IS_DATEEND",	必传且非空，参数编码	
"itemName": "是否日结",	必传且非空，参数名称	
"paraType": "1"	必传且非空，参数类型	1.中台参数 2.门店参数 3.POS参数 4.机台参数
"conType":"6"	必传且非空，控件类型	1.文本格式 2.数字格式 3.日期格式 4.时间格式 5.下拉框 6.状态格式
"defValue","Y"	可选，默认值	
"classNO","1"	可选，参数分类	1.通用 2.要货 3.收货 4.调拨 5.盘点 6.完工			

	 * 
	 */	
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
		private String paraType ;
		private String conType ;
		private String defValue ;
		private String initdefValue ;
		private String classNO ;
		private String priority;
		private String onSale;
		private String remark;
		private List<level1Elm> itemlang;
	
		private String itemName;
		private List<level2Elm> modulars;
		private List<level3Elm> para;
	
	
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}	
		public String getParaType() {
			return paraType;
		}
		public void setParaType(String paraType) {
			this.paraType = paraType;
		}
		public String getConType() {
			return conType;
		}
		public void setConType(String conType) {
			this.conType = conType;
		}
		public String getDefValue() {
			return defValue;
		}
		public void setDefValue(String defValue) {
			this.defValue = defValue;
		}
		public String getClassNO() {
			return classNO;
		}
		public void setClassNO(String classNO) {
			this.classNO = classNO;
		}
		public String getInitdefValue() {
			return initdefValue;
		}
		public void setInitdefValue(String initdefValue) {
			this.initdefValue = initdefValue;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getOnSale() {
			return onSale;
		}
		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public List<level1Elm> getItemlang() {
			return itemlang;
		}
		public void setItemlang(List<level1Elm> itemlang) {
			this.itemlang = itemlang;
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
		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
		public void setModulars(List<level2Elm> modulars) {
			this.modulars = modulars;
		}
		public void setPara(List<level3Elm> para) {
			this.para = para;
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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
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
