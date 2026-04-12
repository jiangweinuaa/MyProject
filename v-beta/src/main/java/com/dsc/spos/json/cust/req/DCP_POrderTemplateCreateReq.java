
package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
 

import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：POrderTemplateCreateDCP
 *   說明：要货模板新增
 * 服务说明：要货模板新增
 * @author panjing  20241111 01029 add orglist emplist
 * @since  2016-11-11
 */
public class DCP_POrderTemplateCreateReq extends JsonBasicReq
{
	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}
	@Getter
	@Setter
	public class levelRequest
	{
		private String templateID;
		private String templateName;
		
		private String preday;
		private String optionalTime;	
		private String timeType;
		private String timeValue;
		private String receiptOrgNo;
		private String hqPorder;
		private String status;
		private String shopType;//1-全部门店、2-指定门店
		private String rdate_Type;
		private String rdate_Add;
		private String rdate_Values;
		private String revoke_Day;
		private String revoke_Time;
		private String rdate_Times;
		private String isAddGoods;
		private String isShowHeadStockQty;
		private String supplierType;
		private String allotType;
		private String floatScale;
		private String supplier;

		private List<level1Elm> datas;
		private List<OrgList> orgList;
		private List<EmpList> empList;
		
		public String getTemplateID() {
			return templateID;
		}
		public void setTemplateID(String templateID) {
			this.templateID = templateID;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getPreday() {
			return preday;
		}
		public void setPreday(String preday) {
			this.preday = preday;
		}
		public String getOptionalTime() {
			return optionalTime;
		}
		public void setOptionalTime(String optionalTime) {
			this.optionalTime = optionalTime;
		}
		public String getTimeType() {
			return timeType;
		}
		public void setTimeType(String timeType) {
			this.timeType = timeType;
		}
		public String getTimeValue() {
			return timeValue;
		}
		public void setTimeValue(String timeValue) {
			this.timeValue = timeValue;
		}
		
		
		public String getReceiptOrgNo()
		{
			return receiptOrgNo;
		}
		public void setReceiptOrgNo(String receiptOrgNo)
		{
			this.receiptOrgNo = receiptOrgNo;
		}
		public String getHqPorder() {
			return hqPorder;
		}
		public void setHqPorder(String hqPorder) {
			this.hqPorder = hqPorder;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getShopType() {
			return shopType;
		}
		public void setShopType(String shopType) {
			this.shopType = shopType;
		}
		public String getRdate_Type() {
			return rdate_Type;
		}
		public void setRdate_Type(String rdate_Type) {
			this.rdate_Type = rdate_Type;
		}
		public String getRdate_Add() {
			return rdate_Add;
		}
		public void setRdate_Add(String rdate_Add) {
			this.rdate_Add = rdate_Add;
		}
		public String getRdate_Values() {
			return rdate_Values;
		}
		public void setRdate_Values(String rdate_Values) {
			this.rdate_Values = rdate_Values;
		}
		public String getRevoke_Day() {
			return revoke_Day;
		}
		public void setRevoke_Day(String revoke_Day) {
			this.revoke_Day = revoke_Day;
		}
		public String getRevoke_Time() {
			return revoke_Time;
		}
		public void setRevoke_Time(String revoke_Time) {
			this.revoke_Time = revoke_Time;
		}
		public String getRdate_Times() {
			return rdate_Times;
		}
		public void setRdate_Times(String rdate_Times) {
			this.rdate_Times = rdate_Times;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
		public String getIsAddGoods() {
			return isAddGoods;
		}
		public void setIsAddGoods(String isAddGoods) {
			this.isAddGoods = isAddGoods;
		}
		public String getIsShowHeadStockQty() {
			return isShowHeadStockQty;
		}
		public void setIsShowHeadStockQty(String isShowHeadStockQty) {
			this.isShowHeadStockQty = isShowHeadStockQty;
		}

	}
	@Getter
	@Setter
	public class EmpList {
		private String employeeNo;
		private String status;

	}

	@Getter
	@Setter
	public class OrgList {
		private String orgNo;
		private String status;
		private String isMustAllot;
		private String sortId;

	}
	public  class level1Elm
	{
		private String item;
		private String pluNo;
		private String punit;
		private String minQty;
		private String maxQty;
		private String mulQty;
		private String defQty;
		private String status;
		private String groupNo;//要货组别编号
		private String supplier;
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		public String getPunit()
		{
			return punit;
		}
		public void setPunit(String punit)
		{
			this.punit = punit;
		}
		public String getMinQty()
		{
			return minQty;
		}
		public void setMinQty(String minQty)
		{
			this.minQty = minQty;
		}
		public String getMaxQty()
		{
			return maxQty;
		}
		public void setMaxQty(String maxQty)
		{
			this.maxQty = maxQty;
		}
		public String getMulQty()
		{
			return mulQty;
		}
		public void setMulQty(String mulQty)
		{
			this.mulQty = mulQty;
		}
		public String getDefQty()
		{
			return defQty;
		}
		public void setDefQty(String defQty)
		{
			this.defQty = defQty;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getGroupNo()
		{
			return groupNo;
		}
		public void setGroupNo(String groupNo)
		{
			this.groupNo = groupNo;
		}
		public String getSupplier() {
			return supplier;
		}
		public void setSupplier(String supplier) {
			this.supplier = supplier;
		}
		
		
	
		

	}


}
