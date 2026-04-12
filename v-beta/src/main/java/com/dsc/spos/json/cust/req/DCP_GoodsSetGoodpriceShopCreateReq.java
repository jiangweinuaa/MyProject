package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetGoodpriceShopCreateReq extends JsonBasicReq {

	private String pluNO;
	private String pluName;
	private String unitNO;
	private String supplierNO;
	private String supplierName;

	private List<level1Elm> datas;

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

	public String getUnitNO() {
		return unitNO;
	}

	public void setUnitNO(String unitNO) {
		this.unitNO = unitNO;
	}

	public String getSupplierNO() {
		return supplierNO;
	}

	public void setSupplierNO(String supplierNO) {
		this.supplierNO = supplierNO;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String shopId;
		private String shopName;
		private String shopGoodPrice;

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

		public String getShopGoodPrice() {
			return shopGoodPrice;
		}

		public void setShopGoodPrice(String shopGoodPrice) {
			this.shopGoodPrice = shopGoodPrice;
		}

	}

}
