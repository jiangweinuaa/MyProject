package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagDetailQueryReq extends JsonBasicReq
{

	private levelRequest request;



	public levelRequest getRequest() {
		return request;
	}



	public void setRequest(levelRequest request) {
		this.request = request;
	}


	public class levelRequest
	{
		private String tagGroupType;//标签类型：SHOP-门店标签GOODS-商品标签GOODS_PROD-商品生产标签GOODS_DELIVERY-商品物流标签 CUST-客户标签
		private String tagNo;//标签编码
		private String status;//资料状态：-1未启用 100-已启用 0-已禁用

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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}



	}
}
