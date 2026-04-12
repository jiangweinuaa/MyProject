package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagDetailAddReq extends JsonBasicReq
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
		private String tagGroupType;//标签类型：SHOP-门店标签GOODS-商品标签GOODS_PROD-商品生产标签GOODS_DELIVERY-商品物流标签 CUST-客户标签
		private String tagNo;//标签编码
		private String tagGroupNo;//标签组别
		private List<levelTypeDetail> idList;//资料列表
		
		
		public String getTagGroupNo() {
			return tagGroupNo;
		}
		public void setTagGroupNo(String tagGroupNo) {
			this.tagGroupNo = tagGroupNo;
		}
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
		public List<levelTypeDetail> getIdList() {
			return idList;
		}
		public void setIdList(List<levelTypeDetail> idList) {
			this.idList = idList;
		}		
	}
	
	public class levelTypeDetail
	{
		private String id;//资料编码
		private String name;//资料名称
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}	
		
	}
}
