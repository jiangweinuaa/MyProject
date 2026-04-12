package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_SaleTargetQueryRes extends JsonRes
{
	private List<level1Elm> datas;

	public class level1Elm
	{
		private String dateType;
		private String dateValue;
		private String saleType;
		private String saleAMT;
		private String shopId;
		private String shopName;

		public String getDateType() {
			return dateType;
		}
		public void setDateType(String dateType) {
			this.dateType = dateType;
		}
		public String getDateValue() {
			return dateValue;
		}
		public void setDateValue(String dateValue) {
			this.dateValue = dateValue;
		}
		public String getSaleType() {
			return saleType;
		}
		public void setSaleType(String saleType) {
			this.saleType = saleType;
		}
		public String getSaleAMT() {
			return saleAMT;
		}
		public void setSaleAMT(String saleAMT) {
			this.saleAMT = saleAMT;
		}
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
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
