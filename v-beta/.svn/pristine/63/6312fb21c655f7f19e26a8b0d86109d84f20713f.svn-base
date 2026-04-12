package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：OrderGoodsSync
 * 服务说明：外卖商品同步
 * @author jinzma	 
 * @since  2019-02-11
 */
public class DCP_OrderGoodsSyncReq extends JsonBasicReq {
	private String[] loadDocType ;
	private String operType ;
	private List<level1goodsElm> goodsdatas;
	private List<level1shopsElm> shopsdatas;

	public String[] getLoadDocType() {
		return loadDocType;
	}

	public void setLoadDocType(String[] loadDocType) {
		this.loadDocType = loadDocType;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public List<level1goodsElm> getGoodsdatas() {
		return goodsdatas;
	}

	public void setGoodsdatas(List<level1goodsElm> goodsdatas) {
		this.goodsdatas = goodsdatas;
	}

	public List<level1shopsElm> getShopsdatas() {
		return shopsdatas;
	}

	public void setShopsdatas(List<level1shopsElm> shopsdatas) {
		this.shopsdatas = shopsdatas;
	}

	public  class level1goodsElm
	{
		private String pluNO ;

		private String status;

		public String getPluNO() {
			return pluNO;
		}

		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}

	public  class level1shopsElm
	{
		private String erpShopNO ;
		private String orderShopNO;
		private String orderShopName;

		public String getErpShopNO() {
			return erpShopNO;
		}
		public void setErpShopNO(String erpShopNO) {
			this.erpShopNO = erpShopNO;
		}
		public String getOrderShopNO() {
			return orderShopNO;
		}
		public void setOrderShopNO(String orderShopNO) {
			this.orderShopNO = orderShopNO;
		}
		public String getOrderShopName() {
			return orderShopName;
		}
		public void setOrderShopName(String orderShopName) {
			this.orderShopName = orderShopName;
		}

	}


}
