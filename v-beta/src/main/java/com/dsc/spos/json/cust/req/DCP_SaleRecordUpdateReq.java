package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_SaleRecordCreate
 *   說明 ：面销记录
 * @author yuanyy 
 * @since  2019-12-29
 */
public class DCP_SaleRecordUpdateReq extends JsonBasicReq{
	
	private level1Elm request;
//	private String timestamp;
	
	public level1Elm getRequest() {
		return request;
	}
	
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
//	public String getTimestamp() {
//		return timestamp;
//	}
//	
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
	
	public class level1Elm{
		
		private String salesRecordNo;
		private String customName;
		private String sex; //0：女    1：男   2：未知？    未知是什么鬼
		private String mobile;
		private String receptionTime;// 接待时间
		private String customType;// 1：新客    2：老客
		private String labels;
		private String remark;
		
		private List<level2Elm> intentionalProduct;

		public String getSalesRecordNo() {
			return salesRecordNo;
		}

		public void setSalesRecordNo(String salesRecordNo) {
			this.salesRecordNo = salesRecordNo;
		}

		public String getCustomName() {
			return customName;
		}

		public void setCustomName(String customName) {
			this.customName = customName;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getReceptionTime() {
			return receptionTime;
		}

		public void setReceptionTime(String receptionTime) {
			this.receptionTime = receptionTime;
		}

		public String getCustomType() {
			return customType;
		}

		public void setCustomType(String customType) {
			this.customType = customType;
		}

		public String getLabels() {
			return labels;
		}

		public void setLabels(String labels) {
			this.labels = labels;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public List<level2Elm> getIntentionalProduct() {
			return intentionalProduct;
		}

		public void setIntentionalProduct(List<level2Elm> intentionalProduct) {
			this.intentionalProduct = intentionalProduct;
		}

		
	}
	
	
	public class level2Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String categoryName;
		private String wunit;
		private String price ;
		private String originalPrice;
		private String imageFileName;
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getWunit() {
			return wunit;
		}
		public void setWunit(String wunit) {
			this.wunit = wunit;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getOriginalPrice() {
			return originalPrice;
		}
		public void setOriginalPrice(String originalPrice) {
			this.originalPrice = originalPrice;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			this.imageFileName = imageFileName;
		}
		
	}
	
	
}
