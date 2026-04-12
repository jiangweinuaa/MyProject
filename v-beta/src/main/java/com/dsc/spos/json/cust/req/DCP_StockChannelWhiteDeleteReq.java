package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道商品分货白名单删除
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelWhiteDeleteReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}

	public class levelReq {
		private List<PluList> pluList;

		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class PluList{
		
		private String channelId;
		private String pluNo;
//		private String organizationNo;
//		private String featureNo;
//		private String sUnit;
//		private String warehouse;
//		private String baseUnit;
		
		public String getChannelId() {
			return channelId;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		
	}
	
}
