package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 物料报单修改
 * @author yuanyy 2019-10-28
 *	
 */
public class DCP_MaterialMsgUpdateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pfNo;
		private String bDate;
		private String porderNO;
		private String status;//要货单状态

		private List<level1Elm> datas;

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}


		public String getPfNo() {
			return pfNo;
		}

		public void setPfNo(String pfNo) {
			this.pfNo = pfNo;
		}

		public String getbDate() {
			return bDate;
		}

		public void setbDate(String bDate) {
			this.bDate = bDate;
		}

		public String getPorderNO() {
			return porderNO;
		}

		public void setPorderNO(String porderNO) {
			this.porderNO = porderNO;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public  class level1Elm{
		private String item;
		private String materialPluNo;
		private String pQty; //实报数量
		private String rQty; 
		private String uQty;
		private String dQty;
		private String tQty;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getMaterialPluNo() {
			return materialPluNo;
		}
		public void setMaterialPluNo(String materialPluNo) {
			this.materialPluNo = materialPluNo;
		}
		public String getrQty() {
			return rQty;
		}
		public void setrQty(String rQty) {
			this.rQty = rQty;
		}
		public String getuQty() {
			return uQty;
		}
		public void setuQty(String uQty) {
			this.uQty = uQty;
		}
		public String getdQty() {
			return dQty;
		}
		public void setdQty(String dQty) {
			this.dQty = dQty;
		}
		public String gettQty() {
			return tQty;
		}
		public void settQty(String tQty) {
			this.tQty = tQty;
		}
		public String getpQty() {
			return pQty;
		}
		public void setpQty(String pQty) {
			this.pQty = pQty;
		}



	}
}
