package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OTakeClassShopUpdateReq extends JsonBasicReq
{
	private String docType;
private List<level1Elm> datas;
	
	public  class level1Elm
	{
		private String erpShopNO;
		private String orderClassNO;
		private String orderClassName;
		private String classNO;
		private String className;
		private String priority;
	public String getErpShopNO() {
		return erpShopNO;
	}
	public void setErpShopNO(String erpShopNO) {
		this.erpShopNO = erpShopNO;
	}
	public String getOrderClassNO() {
		return orderClassNO;
	}
	public void setOrderClassNO(String orderClassNO) {
		this.orderClassNO = orderClassNO;
	}
	public String getOrderClassName() {
		return orderClassName;
	}
	public void setOrderClassName(String orderClassName) {
		this.orderClassName = orderClassName;
	}
	public String getClassNO() {
		return classNO;
	}
	public void setClassNO(String classNO) {
		this.classNO = classNO;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	}

	public List<level1Elm> getDatas() {
	return datas;
	}

	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}

	public String getDocType() {
	return docType;
	}

	public void setDocType(String docType) {
	this.docType = docType;
	}
	
}
