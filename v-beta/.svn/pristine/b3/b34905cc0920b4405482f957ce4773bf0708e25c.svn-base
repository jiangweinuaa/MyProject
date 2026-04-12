package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：SaleSumGet
 *   說明：销售汇总查询
 * 服务说明：销售汇总查询
 * @author luoln
 * @since  2017-06-21
 */
public class DCP_SaleSumQueryRes extends JsonRes{

	private String totQty;
	private String totAmt;

	public String getTotQty() {
		return totQty;
	}
	public void setTotQty(String totQty) {
		this.totQty = totQty;
	}

	public String getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String shopId;
		private String shopName;
		private List<level2Elm> datas;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}

	public class level2Elm
	{
		private String category;
		private String categoryName;
		private List<level3Elm> datas;

		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}

		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public List<level3Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level3Elm> datas) {
			this.datas = datas;
		}
	}

	public class level3Elm
	{
		private String pluNO;
		private String pluName;
		private String featureNO;
		private String featureName;
		private String wunitName;
		private String qty;
		private String amt;
		private float ratio;

		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}

		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getFeatureNO() {
			return featureNO;
		}
		public void setFeatureNO(String featureNO) {
			this.featureNO = featureNO;
		}

		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public String getWunitName() {
			return wunitName;
		}
		public void setWunitName(String wunitName) {
			this.wunitName = wunitName;
		}

		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}

		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}

		public float getRatio() {
			return ratio;
		}
		public void setRatio(float ratio) {
			this.ratio = ratio;
		}
	}
}
