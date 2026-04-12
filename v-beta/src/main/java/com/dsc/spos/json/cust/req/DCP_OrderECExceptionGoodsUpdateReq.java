package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.cust.JsonReq;

/**
 * 电商异常商品处理 （将电商平台品号 更新为 ERP的品号）
 * @author yuanyy 2019-06-14 
 *
 */
public class DCP_OrderECExceptionGoodsUpdateReq extends JsonReq {
	
	private String ecOrderNo;
	private String exceptionMemo;
	
	private List<level1Elm> datas;
	
	public String getEcOrderNo() {
		return ecOrderNo;
	}

	public void setEcOrderNo(String ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}

	public String getExceptionMemo() {
		return exceptionMemo;
	}

	public void setExceptionMemo(String exceptionMemo) {
		this.exceptionMemo = exceptionMemo;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public  class level1Elm{
		/**
		 *	 "ecPluNO": "1990120200002",
             "ecPluName": "檸檬桉油OLE",
             "erpPluNO": "200002",
             "erpPluName": "桉油",

		 */
		private String ecPluNO;
		private String ecPluName;
		private String erpPluNO;
		private String erpPluName;
		public String getEcPluNO() {
			return ecPluNO;
		}
		public void setEcPluNO(String ecPluNO) {
			this.ecPluNO = ecPluNO;
		}
		public String getEcPluName() {
			return ecPluName;
		}
		public void setEcPluName(String ecPluName) {
			this.ecPluName = ecPluName;
		}
		public String getErpPluNO() {
			return erpPluNO;
		}
		public void setErpPluNO(String erpPluNO) {
			this.erpPluNO = erpPluNO;
		}
		public String getErpPluName() {
			return erpPluName;
		}
		public void setErpPluName(String erpPluName) {
			this.erpPluName = erpPluName;
		}
		
		
	}
	
	
	
}
