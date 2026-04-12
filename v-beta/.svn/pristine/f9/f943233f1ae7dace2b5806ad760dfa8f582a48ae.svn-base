package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsOnAndOffShelfReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String billType;//单据类型：1线上商品设置，2商品上下架
		private String oprType;//操作类型1-渠道上架2-渠道下架3-门店上架4-门店下架
		private String channelId;//渠道编码，按渠道上下架时必传
		private String orgType;//操作机构类型1-公司2-门店
		private String orgId;//操作机构编码
		
		private List<level1Plu> pluList;
		private List<level1Range> shelfRangeList;// 按门店上下架时必传
		
		public String getBillType() {
			return billType;
		}
		public void setBillType(String billType) {
			this.billType = billType;
		}
		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getOrgType() {
			return orgType;
		}
		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}
		public String getOrgId() {
			return orgId;
		}
		public void setOrgId(String orgId) {
			this.orgId = orgId;
		}
		public List<level1Plu> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Plu> pluList) {
			this.pluList = pluList;
		}
		public List<level1Range> getShelfRangeList()
		{
			return shelfRangeList;
		}
		public void setShelfRangeList(List<level1Range> shelfRangeList)
		{
			this.shelfRangeList = shelfRangeList;
		}
		
		
									
	}
	
	public class level1Plu
	{		
		private String pluNo;
		private String pluType;
		private String pluName;
		public String getPluNo() {
			return pluNo;
		}	
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluType()
		{
			return pluType;
		}
		public void setPluType(String pluType)
		{
			this.pluType = pluType;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
		}
					
	}
	
	public class level1Range
	{	
		private String shopId;//门店编码
		private String channelId;//渠道编码
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		
		
	}
	
	

}
