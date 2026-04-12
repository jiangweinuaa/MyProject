package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_ClassQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
		
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{

		private String classType;
		private String classNo;
		private String className;
		private String displayName;
		private String levelId;
		private String upClassNo;
		private String upClassName;
		private String subClassCount;//下级分类数量
		private String status;
		
		private String sortId;//显示顺序，排序第一列
		private String memo;

		// 新增分组图片
		private String classImageUrl; // 分组图片地址
		private String remind; // 是否开启点单提醒Y/N
		private String remindType; // 提醒类型，0.必须 1.提醒
		private String label; // 推荐标签Y/N
		private String labelName; // 标签内容

        // 新增是否可分享 2021/6/18 wangzyc
        private String isShare; // 是否可分享：0.否 1.是

		private String beginDate;//生效日期YYYY-MM-DD
		private String endDate;//失效日期YYYY-MM-DD
		private String goodsSortType;//商品排序：1-默认顺序 2-销量降序 3-价格升序 4-价格降序 5-上架时间降序
		/*private String restrictCompany;
		private String restrictShop;
		private String restrictChannel;
		private String restrictAppType;*/
		
		private String createtime;
		private String createopid;//创建人编号
		private String createopname;
		private String lastmoditime;
		private String lastmodiopid;//最后修改人编码
		private String lastmodiname;
		
		//private List<className> className_lang;
		//private List<displayName> displayName_lang;
		private List<level1Elm> subClassList;


        public String getIsShare() {
            return isShare;
        }

        public void setIsShare(String isShare) {
            this.isShare = isShare;
        }

        public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getUpClassName() {
			return upClassName;
		}
		public void setUpClassName(String upClassName) {
			this.upClassName = upClassName;
		}
		public String getClassType() {
			return classType;
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
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}		
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
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
		public String getSubClassCount() {
			return subClassCount;
		}
		public void setSubClassCount(String subClassCount) {
			this.subClassCount = subClassCount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getCreateopid() {
			return createopid;
		}
		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}
		public String getCreateopname() {
			return createopname;
		}
		public void setCreateopname(String createopname) {
			this.createopname = createopname;
		}
		public String getLastmoditime() {
			return lastmoditime;
		}
		public void setLastmoditime(String lastmoditime) {
			this.lastmoditime = lastmoditime;
		}
		public String getLastmodiopid() {
			return lastmodiopid;
		}
		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}
		public String getLastmodiname() {
			return lastmodiname;
		}
		public void setLastmodiname(String lastmodiname) {
			this.lastmodiname = lastmodiname;
		}
	/*	public List<className> getClassName_lang() {
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
		}*/

        public String getClassImageUrl() {
            return classImageUrl;
        }

        public void setClassImageUrl(String classImageUrl) {
            this.classImageUrl = classImageUrl;
        }

        public List<level1Elm> getSubClassList() {
			return subClassList;
		}
		public void setSubClassList(List<level1Elm> subClassList) {
			this.subClassList = subClassList;
		}

		public String getRemind() {
			return remind;
		}

		public void setRemind(String remind) {
			this.remind = remind;
		}

		public String getRemindType() {
			return remindType;
		}

		public void setRemindType(String remindType) {
			this.remindType = remindType;
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
			
	}

}
