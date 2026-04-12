package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PosFuncTemplateDetailRes extends JsonRes
{	

	private level1Elm datas;


	

	public level1Elm getDatas()
	{
		return datas;
	}

	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}

	public class level1Elm
	{

		private String templateId ;//模板编码
		private String templateName ;//模板编码
		private String pageType ;//页面类型：SETTLEFIRSTSALE-先结算零售，DISHORDER-点菜，TABLEDETAIL-桌台详情，INTEGRATEDSALE-综合零售
		private String pageName ;
		
		private String restrictShop ;//适用门店：0-所有门店1-指定门店2-排除门店
	
		private String memo ;//备注
		private String status;//状态：-1未启用100已启用0已禁用		
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS		
		private String createopid;//创建人编号		
		private String createopname;//创建人名称	
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS		
		private String lastmodiopid;//最后修改人编码		
		private String lastmodiname;//最后修改人名称	

		private List<levelTemplateName> templateName_lang;		
		private List<levelFuncGroup> funcList;		
		private List<levelRange> rangeList;
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
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
		public List<levelRange> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<levelRange> rangeList) {
			this.rangeList = rangeList;
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
		public List<levelTemplateName> getTemplateName_lang() {
			return templateName_lang;
		}
		public void setTemplateName_lang(List<levelTemplateName> templateName_lang) {
			this.templateName_lang = templateName_lang;
		}
		public List<levelFuncGroup> getFuncList() {
			return funcList;
		}
		public void setFuncList(List<levelFuncGroup> funcList) {
			this.funcList = funcList;
		}
		public void setLastmodiname(String lastmodiname) {
			this.lastmodiname = lastmodiname;
		}
			

		
	}

	public class levelTemplateName
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

	public class levelFuncGroup
	{
		private String funcGroup;
		private String sortId;
		private String funcGroupName;
		private String memo;
		
		private List<funcNo> funcNoList;
		
		public String getFuncGroup() {
			return funcGroup;
		}
		public void setFuncGroup(String funcGroup) {
			this.funcGroup = funcGroup;
		}
		public List<funcNo> getFuncNoList() {
			return funcNoList;
		}
		public void setFuncNoList(List<funcNo> funcNoList) {
			this.funcNoList = funcNoList;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getFuncGroupName()
		{
			return funcGroupName;
		}
		public void setFuncGroupName(String funcGroupName)
		{
			this.funcGroupName = funcGroupName;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
			
		
		 
	}
	
	public class funcNo
	{
		private String funcNo;
		private String funcName;
		private String sortId;
		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}		
		
	}

	public class levelRange
	{
		private String shopId ;//门店编码，排序第一列
		private String shopName ;//编号
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
			

	}


}
