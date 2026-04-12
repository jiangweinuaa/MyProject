package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 物料报单修改
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_MaterialMsgCreateReq extends JsonBasicReq {
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
		private String maxItem; //这个字段的来源： 查询返回原料总数totCqty（种数），原则上，种数就是原料个数

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

		public String getMaxItem() {
			return maxItem;
		}

		public void setMaxItem(String maxItem) {
			this.maxItem = maxItem;
		}

		public String getbDate() {
			return bDate;
		}

		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
	}

	public  class level1Elm{

		private String materialPluNo;
		private String pUnit;
		private String wUnit;
		private String price;
		private String preSaleQty; //昨日销量

		public String getMaterialPluNo() {
			return materialPluNo;
		}
		public void setMaterialPluNo(String materialPluNo) {
			this.materialPluNo = materialPluNo;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getwUnit() {
			return wUnit;
		}
		public void setwUnit(String wUnit) {
			this.wUnit = wUnit;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getPreSaleQty() {
			return preSaleQty;
		}
		public void setPreSaleQty(String preSaleQty) {
			this.preSaleQty = preSaleQty;
		}

	}
}
