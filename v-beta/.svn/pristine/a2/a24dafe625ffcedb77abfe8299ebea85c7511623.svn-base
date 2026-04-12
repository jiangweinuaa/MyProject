package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_SupPriceTemplateQueryRes extends JsonRes {

	private List<level1Elm> datas ;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String templateId;//模板编码
		private String templateName;//模板名称
		private List<TemplateLang> templateName_lang;//
		private String supplierType;//供货机构类型：1-公司，2-供应商
		private String supplierId;//供应机构编码
		private String supplierName;//供应机构名称
		private String receiverType;//收货机构类型：1-公司，2-门店
		private String receiverIdRange;//收货机构范围i：0-全部机构1-指定机构
		private List<Range> rangeList;//收货机构适用范围
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
		public List<TemplateLang> getTemplateName_lang()
		{
			return templateName_lang;
		}
		public void setTemplateName_lang(List<TemplateLang> templateName_lang)
		{
			this.templateName_lang = templateName_lang;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getSupplierType() {
			return supplierType;
		}
		public void setSupplierType(String supplierType) {
			this.supplierType = supplierType;
		}
		public String getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(String supplierId) {
			this.supplierId = supplierId;
		}
		public String getSupplierName()
		{
			return supplierName;
		}
		public void setSupplierName(String supplierName)
		{
			this.supplierName = supplierName;
		}
		public String getReceiverType() {
			return receiverType;
		}
		public void setReceiverType(String receiverType) {
			this.receiverType = receiverType;
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
		public String getReceiverIdRange()
		{
			return receiverIdRange;
		}
		public void setReceiverIdRange(String receiverIdRange)
		{
			this.receiverIdRange = receiverIdRange;
		}
		public List<Range> getRangeList()
		{
			return rangeList;
		}
		public void setRangeList(List<Range> rangeList)
		{
			this.rangeList = rangeList;
		}
	}
	
	public class TemplateLang {
		private String langType;//
		private String name;//
		public String getLangType()
		{
			return langType;
		}
		public void setLangType(String langType)
		{
			this.langType = langType;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}		
	}
	
	
	public class Range {
		private String id;//编码
		private String name;//名称
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}		
	}
	
}
