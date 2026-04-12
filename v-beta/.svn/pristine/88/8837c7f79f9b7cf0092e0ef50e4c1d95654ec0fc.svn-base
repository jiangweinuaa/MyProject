package com.dsc.spos.json.cust.req;
import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：LStockOutErpUpdate
 * 服务说明：ERP报损单审核处理
 * @author jinzma
 * @since  2019-01-17
 */
public class DCP_LStockOutErpUpdateReq extends JsonBasicReq  {
	private String lstockOutNO;
	private String opType;
	private List<level1Elm> datas;
	
	public String getLstockOutNO() {
		return lstockOutNO;
	}
	public void setLstockOutNO(String lstockOutNO) {
		this.lstockOutNO = lstockOutNO;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public  class level1Elm {
		private String item;
		private String pluNO;
		private String pqty;
		private String baseQty;
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
	}
	
}
