package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道库存分配单据列表查询
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelOrderQueryReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		
		private String beginDate;
		private String endDate;
		private String billNo;
		private String keyTxt;
		
		private List<ChannelList> channelList;

		public String getBeginDate() {
			return beginDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}

		public List<ChannelList> getChannelList() {
			return channelList;
		}

		public void setChannelList(List<ChannelList> channelList) {
			this.channelList = channelList;
		}
		
	}
	
	public class ChannelList{
		private String channelId;

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		
	}
	
	
	
}
