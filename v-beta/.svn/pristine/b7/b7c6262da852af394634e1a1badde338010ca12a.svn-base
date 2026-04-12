package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：DCP_GoodsStockQuery
 * 服务说明：商品库存查询
 * @author jinzma 
 * @since  2020-04-21
 */
public class DCP_GoodsStockQuery_OpenRes extends JsonRes {

	private levelElm datas;
	private String version;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public levelElm getDatas() {
		return datas;
	}
	public void setDatas(levelElm datas) {
		this.datas = datas;
	}

	public class levelElm{
		private List<level1Elm> pluList;
		public List<level1Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Elm> pluList) {
			this.pluList = pluList;
		}
	}

	public class level1Elm{
		private String pluNo;
		private String baseUnit;
		private String baseQty;
		private List<level2ElmFeature> featureList;
		private List<level2ElmBatch> batchList;

		public List<level2ElmFeature> getFeatureList() {
			return featureList;
		}
		public void setFeatureList(List<level2ElmFeature> featureList) {
			this.featureList = featureList;
		}
		public List<level2ElmBatch> getBatchList() {
			return batchList;
		}
		public void setBatchList(List<level2ElmBatch> batchList) {
			this.batchList = batchList;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
	}

	public class level2ElmFeature{
		private String featureNo;
		private String baseQty;
		private List<level3ElmBatch> batchList;

		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}

		public List<level3ElmBatch> getBatchList() {
			return batchList;
		}
		public void setBatchList(List<level3ElmBatch> batchList) {
			this.batchList = batchList;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
	}

	public class level2ElmBatch{
		private String batchNo;
		private String prodDate;
		private String baseQty;
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getProdDate() {
			return prodDate;
		}
		public void setProdDate(String prodDate) {
			this.prodDate = prodDate;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
	}

	public class level3ElmBatch{
		private String batchNo;
		private String prodDate;
		private String baseQty;
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getProdDate() {
			return prodDate;
		}
		public void setProdDate(String prodDate) {
			this.prodDate = prodDate;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
	}
}
