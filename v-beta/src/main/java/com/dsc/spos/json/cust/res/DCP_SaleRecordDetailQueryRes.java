package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 面销记录
 * @author 2019-12-31
 *
 */
public class DCP_SaleRecordDetailQueryRes extends JsonRes {
	
	private List<level1Elm> intentionalProduct;
	
	public List<level1Elm> getIntentionalProduct() {
		return intentionalProduct;
	}
	
	public void setIntentionalProduct(List<level1Elm> intentionalProduct) {
		this.intentionalProduct = intentionalProduct;
	}

	public class level1Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String categoryName;
		private String wunit;
		private String wunitName;
		private String price;
		private String imageFileName;
		private String originalPrice;
		
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
		
		public String getWunitName() {
			return wunitName;
		}
		public void setWunitName(String wunitName) {
			this.wunitName = wunitName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			this.imageFileName = imageFileName;
		}
		public String getOriginalPrice() {
			return originalPrice;
		}
		public void setOriginalPrice(String originalPrice) {
			this.originalPrice = originalPrice;
		}
		
	}
	
	
}
