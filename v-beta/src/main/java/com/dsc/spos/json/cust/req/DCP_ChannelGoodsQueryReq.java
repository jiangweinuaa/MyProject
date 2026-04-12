package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道商品查询
 * @author 2020-06-05
 *
 */
public class DCP_ChannelGoodsQueryReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		private String channelId;
		private String keyTxt;
		
		//2020-11-04 根据红艳和玲霞的设计，  查询不再关联模板， 筛选条件组织信息删除
//		private List<OrganizationList> organizationList;

		public String getChannelId() {
			return channelId;
		}

		public String getKeyTxt() {
			return keyTxt;
		}

//		public List<OrganizationList> getOrganizationList() {
//			return organizationList;
//		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}

//		public void setOrganizationList(List<OrganizationList> organizationList) {
//			this.organizationList = organizationList;
//		}
		
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
