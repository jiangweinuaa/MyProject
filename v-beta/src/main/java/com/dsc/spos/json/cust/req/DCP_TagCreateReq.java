package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagCreateReq extends JsonBasicReq
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
		private String tagGroupNo;//标签分组编码
		private String sortId;//显示顺序
		private String memo ;
		private String status;

		private String isSingleProduce; // 是否启用单份生产Y/N  仅限生产标签使用
		
		private List<levelTagTypeLang> tagName_lang;//标签名称多语言

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

		public String getTagGroupNo() {
			return tagGroupNo;
		}

		public void setTagGroupNo(String tagGroupNo) {
			this.tagGroupNo = tagGroupNo;
		}

		public String getSortId() {
			return sortId;
		}

		public void setSortId(String sortId) {
			this.sortId = sortId;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<levelTagTypeLang> getTagName_lang() {
			return tagName_lang;
		}

		public void setTagName_lang(List<levelTagTypeLang> tagName_lang) {
			this.tagName_lang = tagName_lang;
		}

        public String getIsSingleProduce() {
            return isSingleProduce;
        }

        public void setIsSingleProduce(String isSingleProduce) {
            this.isSingleProduce = isSingleProduce;
        }
    }
	
	public class levelTagTypeLang
	{
		private String langType;//
		private String name;//
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
		
	}
	
}
