package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockTakeUpdate
 *   說明：盘点模板修改
 * 服务说明：盘点模板修改
 * @author panjing 
 * @since  2016-11-11
 */
public class DCP_SStockTemplateUpdateReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() 
	{
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest {
		private String templateNo;
		private String templateName;
		private String timeType;
		private String timeValue;
		private String status;
		private String supplier;
		private String shopType;//1-全部门店、2-指定门店
		private List<level1Elm> datas;

		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
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
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
		public String getTemplateNo()
		{
			return templateNo;
		}
		public void setTemplateNo(String templateNo)
		{
			this.templateNo = templateNo;
		}
		public String getSupplier() {
			return supplier;
		}
		public void setSupplier(String supplier) {
			this.supplier = supplier;
		}
		public String getShopType() {
			return shopType;
		}
		public void setShopType(String shopType) {
			this.shopType = shopType;
		}
	}



	public  class level1Elm {
		private String item;
		private String pluNo;
		private String punit;
		private String status;
		private String price;

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
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
	}
}
