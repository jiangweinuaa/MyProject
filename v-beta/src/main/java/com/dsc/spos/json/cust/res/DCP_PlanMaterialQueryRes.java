package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 生产计划商品查询 
 * @author yuanyy 2019-11-11
 *	
 */
public class DCP_PlanMaterialQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String pluNo;
		private String pluName;
		private String unit;
		private String spec;
		private String avgQty;
		private String predictQty;
		private String residueQty;
		private String outQty;
		private String changeQty;
		private String actQty;
		private String price;
		private String distriPrice;
		private String totAmt;
		private String distriAmt;
		private String isOut;
		
		private String nowQty;
		private String udLength;
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getAvgQty() {
			return avgQty;
		}
		public void setAvgQty(String avgQty) {
			this.avgQty = avgQty;
		}
		public String getPredictQty() {
			return predictQty;
		}
		public void setPredictQty(String predictQty) {
			this.predictQty = predictQty;
		}
		public String getResidueQty() {
			return residueQty;
		}
		public void setResidueQty(String residueQty) {
			this.residueQty = residueQty;
		}
		public String getOutQty() {
			return outQty;
		}
		public void setOutQty(String outQty) {
			this.outQty = outQty;
		}
		public String getChangeQty() {
			return changeQty;
		}
		public void setChangeQty(String changeQty) {
			this.changeQty = changeQty;
		}
		public String getActQty() {
			return actQty;
		}
		public void setActQty(String actQty) {
			this.actQty = actQty;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getIsOut() {
			return isOut;
		}
		public void setIsOut(String isOut) {
			this.isOut = isOut;
		}
		public String getUdLength() {
			return udLength;
		}
		public void setUdLength(String udLength) {
			this.udLength = udLength;
		}
		public String getNowQty() {
			return nowQty;
		}
		public void setNowQty(String nowQty) {
			this.nowQty = nowQty;
		}
		
		
	}
 	
	
	
	
}	
