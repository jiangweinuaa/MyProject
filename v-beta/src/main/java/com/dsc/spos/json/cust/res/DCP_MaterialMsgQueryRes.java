package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 物料报单查询
 * @author yuanyy
 *
 */
public class DCP_MaterialMsgQueryRes extends JsonRes {
	
	private String pfNo;
	private String bDate;
	private String pfOrderType;
	private String rDate; // 计划保单需求日期
	
	private List<level1Elm> datas;
	
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
	public String getPfOrderType() {
		return pfOrderType;
	}
	public void setPfOrderType(String pfOrderType) {
		this.pfOrderType = pfOrderType;
	}
	public String getrDate() {
		return rDate;
	}
	public void setrDate(String rDate) {
		this.rDate = rDate;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm {
		
		private String item;
		private String materialPluNo;
		private String materialPluName;
		private String materialUnit;
		private String minQty;
		private String maxQty;
		private String mulQty;
		
		private String materialKQty;
		private String refWQty;
		private String price;
		private String pQty;
		private String mainPlu;
		
		// 2019-10-28 增加以下字段，用于物料报单
		private String dbyQty; //前日实存
		private String yWQty; // 昨日实存
		private String rQty; // 实收数量
		private String uQty; // 修改（退货或报损数量）
		private String dQty; // 差异数量
		private String tQty; // 今日底货
		
		private String fileName;
		private String spec;
		private String materialUnitName;
		private String distriPrice; //进货价
		private String porderNO;
		private String status;
		private String punitUdLength;
		
		public String getDbyQty() {
			return dbyQty;
		}
		public void setDbyQty(String dbyQty) {
			this.dbyQty = dbyQty;
		}
		public String getyWQty() {
			return yWQty;
		}
		public void setyWQty(String yWQty) {
			this.yWQty = yWQty;
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
		public String getMaterialPluName() {
			return materialPluName;
		}
		public void setMaterialPluName(String materialPluName) {
			this.materialPluName = materialPluName;
		}
		public String getMaterialUnit() {
			return materialUnit;
		}
		public void setMaterialUnit(String materialUnit) {
			this.materialUnit = materialUnit;
		}
		public String getMinQty() {
			return minQty;
		}
		public void setMinQty(String minQty) {
			this.minQty = minQty;
		}
		public String getMaxQty() {
			return maxQty;
		}
		public String getMulQty() {
			return mulQty;
		}
		public void setMaxQty(String maxQty) {
			this.maxQty = maxQty;
		}
		public void setMulQty(String mulQty) {
			this.mulQty = mulQty;
		}
		public String getMaterialKQty() {
			return materialKQty;
		}
		public void setMaterialKQty(String materialKQty) {
			this.materialKQty = materialKQty;
		}
		public String getRefWQty() {
			return refWQty;
		}
		public void setRefWQty(String refWQty) {
			this.refWQty = refWQty;
		}
		public String getpQty() {
			return pQty;
		}
		public void setpQty(String pQty) {
			this.pQty = pQty;
		}
		public String getMainPlu() {
			return mainPlu;
		}
		public void setMainPlu(String mainPlu) {
			this.mainPlu = mainPlu;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getMaterialUnitName() {
			return materialUnitName;
		}
		public void setMaterialUnitName(String materialUnitName) {
			this.materialUnitName = materialUnitName;
		}
		
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
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
		public String getPunitUdLength() {
			return punitUdLength;
		}
		public void setPunitUdLength(String punitUdLength) {
			this.punitUdLength = punitUdLength;
		}
		
	}
	

}
