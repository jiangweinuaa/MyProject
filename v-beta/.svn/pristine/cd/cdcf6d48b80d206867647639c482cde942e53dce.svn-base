package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

public class DCP_SalePriceTemplateQueryRes extends JsonRes {
	
	private List<level1Elm> datas ;
	private String isSelfBuiltTemplate;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public String getIsSelfBuiltTemplate() {
		return isSelfBuiltTemplate;
	}
	public void setIsSelfBuiltTemplate(String isSelfBuiltTemplate) {
		this.isSelfBuiltTemplate = isSelfBuiltTemplate;
	}
	
	public class level1Elm {
		private String templateId;//模板编码
		private String templateName;//模板名称
		private String templateType;//适用对象：COMPANY-公司 SHOP-门店
		private String restrictChannel;//适用渠道：适用渠道：0-所有渠道1-指定渠道		
		private String channelId;//渠道编码
		private String channelName;//渠道名称
		private String memo;//备注
		private String status;//状态：-1未启用100已启用0已禁用
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS，降序第一列
		private String createopid;//创建人编号
		private String createopname;//创建人名称
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS
		private String lastmodiopid;//最后修改人编码
		private String lastmodiname;//最后修改人名称
		private String redisUpdateSuccess;//同步缓存是否成功Y/N
		private List<range> rangeList;//适用范围
		private List<levelTemplate> templateName_lang;
		private String selfBuiltShopId;
		
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getTemplateType() {
			return templateType;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		public String getRestrictChannel() {
			return restrictChannel;
		}
		public void setRestrictChannel(String restrictChannel) {
			this.restrictChannel = restrictChannel;
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
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public String getRedisUpdateSuccess() {
			return redisUpdateSuccess;
		}
		public void setRedisUpdateSuccess(String redisUpdateSuccess) {
			this.redisUpdateSuccess = redisUpdateSuccess;
		}
		public List<range> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<range> rangeList) {
			this.rangeList = rangeList;
		}
		public List<levelTemplate> getTemplateName_lang() {
			return templateName_lang;
		}
		public void setTemplateName_lang(List<levelTemplate> templateName_lang) {
			this.templateName_lang = templateName_lang;
		}
		public String getSelfBuiltShopId() {
			return selfBuiltShopId;
		}
		public void setSelfBuiltShopId(String selfBuiltShopId) {
			this.selfBuiltShopId = selfBuiltShopId;
		}
	}
	public class levelTemplate {
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
	public class range {
		private String id;//编号
		private String name;//名称
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
