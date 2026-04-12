package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_TagGroupQueryRes  extends JsonRes
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String tagGroupType;//标签类型：SHOP-门店标签GOODS-商品标签GOODS_PROD-商品生产标签GOODS_DELIVERY-商品物流标签 CUST-客户标签
		private String tagGroupNo;//标签分组编码
		private String tagGroupName;//标签分组名称
		private String sortId;//显示顺序
		private String exclusived;//组内标签是否互斥
				
		private List<levelGroup_lang> tagGroupName_lang;
		private List<levelTypeTag> subTagList;//标签列表
		public String getTagGroupType() {
			return tagGroupType;
		}
		public void setTagGroupType(String tagGroupType) {
			this.tagGroupType = tagGroupType;
		}
		public String getTagGroupNo() {
			return tagGroupNo;
		}
		public void setTagGroupNo(String tagGroupNo) {
			this.tagGroupNo = tagGroupNo;
		}
		public String getTagGroupName() {
			return tagGroupName;
		}
		public void setTagGroupName(String tagGroupName) {
			this.tagGroupName = tagGroupName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getExclusived() {
			return exclusived;
		}
		public void setExclusived(String exclusived) {
			this.exclusived = exclusived;
		}
		
		public List<levelGroup_lang> getTagGroupName_lang() {
			return tagGroupName_lang;
		}
		public void setTagGroupName_lang(List<levelGroup_lang> tagGroupName_lang) {
			this.tagGroupName_lang = tagGroupName_lang;
		}
		public List<levelTypeTag> getSubTagList() {
			return subTagList;
		}
		public void setSubTagList(List<levelTypeTag> subTagList) {
			this.subTagList = subTagList;
		}	
		
	}
	
	public class levelGroup_lang
	{
		private String langType ;
		private String name ;
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
	
	
	public class levelTypeTag
	{
		private String tagNo ;//标签编码
		private String tagName ;//标签名称
		private String sortId ;//显示顺序
		public String getTagNo() {
			return tagNo;
		}
		public void setTagNo(String tagNo) {
			this.tagNo = tagNo;
		}
		public String getTagName() {
			return tagName;
		}
		public void setTagName(String tagName) {
			this.tagName = tagName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		
		
		
	}

	


}
