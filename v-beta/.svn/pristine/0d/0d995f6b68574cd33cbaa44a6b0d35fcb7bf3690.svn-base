package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_ClassUpdateReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String classType;
		private String classNo;
		private String levelId;
		private String upClassNo;
		private String status;
		
		private String sortId;
		private String memo;

		// 新增分组图片名称 2021/3/5 wangzyc
		private String classImage; // 分组图片名称
		private String remind; // 是否开启点单提醒Y/N
		private String remindType; // 提醒类型，0.必须 1.提醒
		private String label; // 推荐标签Y/N
		private String labelName; // 标签内容

        // 新增是否可分享 2021/6/18 wangzyc
        private String isShare; // 是否可分享：0.否 1.是

		private String beginDate;
		private String endDate;
		private String goodsSortType;

		private String restrictShop;
		private String restrictChannel;
		private String restrictAppType;
		private String restrictPeriod;
		
		
		private List<className> className_lang;
		private List<displayName> displayName_lang;
		private List<range> rangeList;
		
		public String getClassType() {
			return classType;
		}

        public String getIsShare() {
            return isShare;
        }

        public void setIsShare(String isShare) {
            this.isShare = isShare;
        }

        public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getLevelId() {
			return levelId;
		}
		public void setLevelId(String levelId) {
			this.levelId = levelId;
		}
		public String getUpClassNo() {
			return upClassNo;
		}
		public void setUpClassNo(String upClassNo) {
			this.upClassNo = upClassNo;
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
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getGoodsSortType() {
			return goodsSortType;
		}
		public void setGoodsSortType(String goodsSortType) {
			this.goodsSortType = goodsSortType;
		}
		
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public String getRestrictChannel() {
			return restrictChannel;
		}
		public void setRestrictChannel(String restrictChannel) {
			this.restrictChannel = restrictChannel;
		}
		public String getRestrictAppType() {
			return restrictAppType;
		}
		public void setRestrictAppType(String restrictAppType) {
			this.restrictAppType = restrictAppType;
		}
		public List<className> getClassName_lang() {
			return className_lang;
		}
		public void setClassName_lang(List<className> className_lang) {
			this.className_lang = className_lang;
		}
		public List<displayName> getDisplayName_lang() {
			return displayName_lang;
		}
		public void setDisplayName_lang(List<displayName> displayName_lang) {
			this.displayName_lang = displayName_lang;
		}
		public List<range> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<range> rangeList) {
			this.rangeList = rangeList;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRestrictPeriod()
		{
			return restrictPeriod;
		}

        public String getClassImage() {
            return classImage;
        }

        public void setClassImage(String classImage) {
            this.classImage = classImage;
        }

        public void setRestrictPeriod(String restrictPeriod)
		{
			this.restrictPeriod = restrictPeriod;
		}

		public String getRemindType() {
			return remindType;
		}

		public void setRemindType(String remindType) {
			this.remindType = remindType;
		}

		public String getRemind() {
			return remind;
		}

		public void setRemind(String remind) {
			this.remind = remind;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getLabelName() {
			return labelName;
		}

		public void setLabelName(String labelName) {
			this.labelName = labelName;
		}
	}
	
	public class className {
		private String langType;
		private String name;
		
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
	
	public class displayName {
		private String langType;
		private String name;
		
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
	
	public class range 
	{
		private String rangeType;
		private String id;
		private String name;
		
		public String getRangeType() {
			return rangeType;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}
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
