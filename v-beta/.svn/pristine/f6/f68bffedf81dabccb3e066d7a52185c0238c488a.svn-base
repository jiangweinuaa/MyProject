package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsImageDeleteReq extends JsonBasicReq
{
	
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	
	public class level1Elm
	{
		private String appType;//应用类型：ALL-所有应用 POS_SELF-自助收银 MALL-商城 ORDER_SCAN扫码点餐 ORDER_WAIMAI-外卖点餐 POS-POS，不可修改
		
		private List<levelGoods> pluList;

		public String getAppType() {
			return appType;
		}

		public void setAppType(String appType) {
			this.appType = appType;
		}

		public List<levelGoods> getPluList() {
			return pluList;
		}

		public void setPluList(List<levelGoods> pluList) {
			this.pluList = pluList;
		}
		
		
	}
	
	public class levelGoods
	{
		private String pluNo;//

		public String getPluNo() {
			return pluNo;
		}

		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}		
	}
	
}
