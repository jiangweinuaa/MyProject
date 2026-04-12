package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagDeleteReq extends JsonBasicReq
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
		private List<tagInfo> tagList;

		public List<tagInfo> getTagList()
		{
			return tagList;
		}

		public void setTagList(List<tagInfo> tagList)
		{
			this.tagList = tagList;
		}
		
	}
	public class tagInfo
	{

		private String tagGroupType;//标签类型：SHOP-门店标签GOODS-商品标签GOODS_PROD-商品生产标签GOODS_DELIVERY-商品物流标签 CUST-客户标签
		private String tagNo;//标签编码
		public String getTagGroupType() {
			return tagGroupType;
		}
		public void setTagGroupType(String tagGroupType) {
			this.tagGroupType = tagGroupType;
		}
		public String getTagNo() {
			return tagNo;
		}
		public void setTagNo(String tagNo) {
			this.tagNo = tagNo;
		}		
	
	}
}
