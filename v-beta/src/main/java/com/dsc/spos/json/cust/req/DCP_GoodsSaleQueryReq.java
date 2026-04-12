package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_GoodsSaleQuery
 *    說明：商品销售数据查询
 * 服务说明：商品销售数据查询
 * @author wangzyc
 * @since  2020-11-17
 */
public class DCP_GoodsSaleQueryReq extends JsonBasicReq{
	
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String shopId; // 门店编码
		private String [] pluNo; // 编码列表
		private String beginDate; // 开始日期
		private String endDate; // 截止日期
		private String saleType; // 单据类型数组，0:要货单
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String[] getPluNo() {
			return pluNo;
		}
		public void setPluNo(String[] pluNo) {
			this.pluNo = pluNo;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getSaleType() {
			return saleType;
		}
		public void setSaleType(String saleType) {
			this.saleType = saleType;
		}
	}
	
	

}
