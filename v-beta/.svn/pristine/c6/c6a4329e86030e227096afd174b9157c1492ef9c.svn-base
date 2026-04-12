package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道商品分货白名单查询
 * @author 2020-06-03
 *
 */
public class DCP_StockChannelWhiteDetailReq extends JsonBasicReq {
	
	private ServerReq request;

	public ServerReq getRequest() {
		return request;
	}

	public void setRequest(ServerReq request) {
		this.request = request;
	}
	
	public class ServerReq{
		
		private String channelId;
		private String pluNo;
		private String keyTxt;
		private List<OrganizationList> organizationList;
		public String getChannelId() {
			return channelId;
		}
		public String getPluNo() {
			return pluNo;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public List<OrganizationList> getOrganizationList() {
			return organizationList;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public void setOrganizationList(List<OrganizationList> organizationList) {
			this.organizationList = organizationList;
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
	
}
