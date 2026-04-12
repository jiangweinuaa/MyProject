package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderProSchedQueryReq extends JsonBasicReq
{
	//1、单个门店 2、批量调度
	private String optype;
	private List<level1Elm> datas;
	public  class level1Elm
	{
		private String orderNO;
		private String docType;
		public String getOrderNO() {
		return orderNO;
		}

		public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
		}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
		
	}

	public List<level1Elm> getDatas() {
	return datas;
	}
	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}
	public String getOptype() {
	return optype;
	}
	public void setOptype(String optype) {
	this.optype = optype;
	}
	
}
