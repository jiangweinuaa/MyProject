package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelOrderQueryReq.ChannelList;

/**
 * 库存销售锁定查询
 * @author 2020-06-08
 *
 */
public class DCP_StockOrderLockDetail_OpenReq extends JsonBasicReq {
	
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
		private String pluNo;
		private String billNo;
		private List<ChannelList> channelList;
		private List<OrganizationList> organizationList;
		public String getPluNo() {
			return pluNo;
		}
		public List<OrganizationList> getOrganizationList() {
			return organizationList;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setOrganizationList(List<OrganizationList> organizationList) {
			this.organizationList = organizationList;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public String getBillNo() {
			return billNo;
		}
		public List<ChannelList> getChannelList() {
			return channelList;
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
		public void setChannelList(List<ChannelList> channelList) {
			this.channelList = channelList;
		}
		
		
	}
	
	public class OrganizationList{
		
		private String organizationNo;

		public String getOrganizationNo() {
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		
	}
	
	public class channelList{
		private String channelId;

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		
	}
}
