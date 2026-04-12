package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 低消信息修改
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String miniChargeNo;

		//1:依设定金额计算低消
		//2:依限定品项计算低消
		//3:依限定品项+设定金额计算低消
		//4:依限定品项+清洁费计算低消
		private String mcType;

		private String adultQty;
		private String childQty;
		private String priceClean; //清洁费
		private String amtMini; //低消金额（共锅）
		private String status;
		private List<level1Elm> datas ;


		public String getMiniChargeNo() {
			return miniChargeNo;
		}
		public void setMiniChargeNo(String miniChargeNo) {
			this.miniChargeNo = miniChargeNo;
		}
		public String getMcType() {
			return mcType;
		}
		public void setMcType(String mcType) {
			this.mcType = mcType;
		}
		public String getAdultQty() {
			return adultQty;
		}
		public void setAdultQty(String adultQty) {
			this.adultQty = adultQty;
		}
		public String getChildQty() {
			return childQty;
		}
		public void setChildQty(String childQty) {
			this.childQty = childQty;
		}
		public String getPriceClean() {
			return priceClean;
		}
		public void setPriceClean(String priceClean) {
			this.priceClean = priceClean;
		}
		public String getAmtMini() {
			return amtMini;
		}
		public void setAmtMini(String amtMini) {
			this.amtMini = amtMini;
		}

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public  class level1Elm {
		private String pluNo;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
	}
}
