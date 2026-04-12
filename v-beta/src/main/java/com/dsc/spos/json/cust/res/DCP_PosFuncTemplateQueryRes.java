package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_PosFuncTemplateQueryRes extends JsonRes
{
	
	private List<level1Elm> datas ;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}



	public class level1Elm
	{
		private String templateId;//模板编码
		private String templateName;//模板名称
		private String pageType;//页面类型：SETTLEFIRSTSALE-先结算零售，DISHORDER-点菜，TABLEDETAIL-桌台详情，INTEGRATEDSALE-综合零售
		private String pageName;
		private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
	
		private String memo;//备注
		private String status;//状态：-1未启用100已启用0已禁用
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS，降序第一列
		private String createopid;//创建人编号
		private String createopname;//创建人名称
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS
		private String lastmodiopid;//最后修改人编码
		private String lastmodiname;//最后修改人名称
		
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
		public String getPageType() {
			return pageType;
		}
		public void setPageType(String pageType) {
			this.pageType = pageType;
		}
		public String getPageName() {
			return pageName;
		}
		public void setPageName(String pageName) {
			this.pageName = pageName;
		}
		public String getRestrictShop() {
				return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
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
		
	}

}
