package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 服務函數：DCP_GoodsSaleQuery
 *    說明：商品销售数据查询
 * 服务说明：商品销售数据查询
 * @author wangzyc 
 * @since  2020-11-17
 */
public class DCP_GoodsSaleQueryRes extends JsonBasicRes{

	private List<Level1Elm> datas;

	public List<Level1Elm> getDatas() {
		return datas;
	}
								
	public void setDatas(List<Level1Elm> datas) {
		this.datas = datas;
	}


	public class Level1Elm{
		private String pluNo; // 商品编码
		private String saleQty; // 销售数量
		private String refUAQty; // 未到货量
		private String lastSaleQty; // 最近一次订货量
		private String saleDate; //	最近一次订货日期
		
		// 增加特征码编码 销售数量
		private List<Level2Elm> feature; 
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getRefUAQty() {
			return refUAQty;
		}
		public void setRefUAQty(String refUAQty) {
			this.refUAQty = refUAQty;
		}
		public String getSaleQty() {
			return saleQty;
		}
		public void setSaleQty(String saleQty) {
			this.saleQty = saleQty;
		}
		public String getLastSaleQty() {
			return lastSaleQty;
		}
		public void setLastSaleQty(String lastSaleQty) {
			this.lastSaleQty = lastSaleQty;
		}
		public String getSaleDate() {
			return saleDate;
		}
		public void setSaleDate(String saleDate) {
			this.saleDate = saleDate;
		}
		public List<Level2Elm> getFeature() {
			return feature;
		}
		public void setFeature(List<Level2Elm> feature) {
			this.feature = feature;
		}
		
	}
	
	public class Level2Elm{
		private String featureNo;// 特征码编码
		private String saleQty; // 销售数量
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getSaleQty() {
			return saleQty;
		}
		public void setSaleQty(String saleQty) {
			this.saleQty = saleQty;
		}
	}
}
