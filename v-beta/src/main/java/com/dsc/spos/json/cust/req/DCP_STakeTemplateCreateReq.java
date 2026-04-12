package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateDeleteReq.levelRequest;
import lombok.Data;

/**
 * 服務函數：STakeTemplateCreateDCP
 *   說明：盘点模板新建
 * 服务说明：盘点模板新建
 * @author panjing 
 * @since  2016-11-11
 */
public class DCP_STakeTemplateCreateReq extends JsonBasicReq
{
	private levelRequest request;

	public levelRequest getRequest() 
	{
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String templateID;
		private String templateName;
		private String taskWay;
		private String isBtake;
		private String timeType;
		private String timeValue;
		private String status;
		private String stockTakeCheck;
		private String shopType;//1-全部门店、2-指定门店
		private String isAdjustStock;
		private String rangeWay;
		private String isShowZStock;

		private List<level1Elm> datas;

        private List<OrgList> orgList;

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
		public String getIsBtake() {
			return isBtake;
		}
		public void setIsBtake(String isBtake) {
			this.isBtake = isBtake;
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
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
		public String getTaskWay() {
			return taskWay;
		}
		public void setTaskWay(String taskWay) {
			this.taskWay = taskWay;
		}
		public String getStockTakeCheck() {
			return stockTakeCheck;
		}
		public void setStockTakeCheck(String stockTakeCheck) {
			this.stockTakeCheck = stockTakeCheck;
		}
		public String getIsAdjustStock() {
			return isAdjustStock;
		}
		public void setIsAdjustStock(String isAdjustStock) {
			this.isAdjustStock = isAdjustStock;
		}
		public String getRangeWay() {
			return rangeWay;
		}
		public void setRangeWay(String rangeWay) {
			this.rangeWay = rangeWay;
		}
		public String getIsShowZStock() {
			return isShowZStock;
		}
		public void setIsShowZStock(String isShowZStock) {
			this.isShowZStock = isShowZStock;
		}

        public List<OrgList> getOrgList() {
            return orgList;
        }

        public void setOrgList(List<OrgList> orgList) {
            this.orgList = orgList;
        }
    }



	public  class level1Elm
	{

		private String item;
		private String pluNo;
		private String punit;
		private String status;
		private String categoryNo;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
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
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCategoryNo() {
			return categoryNo;
		}
		public void setCategoryNo(String categoryNo) {
			this.categoryNo = categoryNo;
		}

	}

    @Data
    public class OrgList{
        private String organizationNo;
        private String warehouse;
        private String status;
    }


}


