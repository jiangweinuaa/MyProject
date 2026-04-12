package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：OrderSyncResultGet
 * 服务说明：同步结果查询
 * @author jinzma 
 * @since  2019-04-11
 */
public class DCP_OrderSyncResultQueryRes extends JsonRes {

	private List<level1Elm> datas;
	public class level1Elm{

		private String erpShopNO ;
		private String erpShopName ;
		private String transID ;
		private String loadDocType ;
		private String transType ;
		private String createByName ;
		private String transDate ;
		private String transTime ;
		private String transFlg ;
		private String descripition ;

		public String getErpShopNO() {
			return erpShopNO;
		}
		public void setErpShopNO(String erpShopNO) {
			this.erpShopNO = erpShopNO;
		}
		public String getErpShopName() {
			return erpShopName;
		}
		public void setErpShopName(String erpShopName) {
			this.erpShopName = erpShopName;
		}
		public String getTransID() {
			return transID;
		}
		public void setTransID(String transID) {
			this.transID = transID;
		}
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
		public String getTransType() {
			return transType;
		}
		public void setTransType(String transType) {
			this.transType = transType;
		}
		public String getCreateByName() {
			return createByName;
		}
		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}
		public String getTransDate() {
			return transDate;
		}
		public void setTransDate(String transDate) {
			this.transDate = transDate;
		}
		public String getTransTime() {
			return transTime;
		}
		public void setTransTime(String transTime) {
			this.transTime = transTime;
		}
		public String getTransFlg() {
			return transFlg;
		}
		public void setTransFlg(String transFlg) {
			this.transFlg = transFlg;
		}
		public String getDescripition() {
			return descripition;
		}
		public void setDescripition(String descripition) {
			this.descripition = descripition;
		}

	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


}
