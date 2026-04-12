package com.dsc.spos.waimai.model;

import java.util.List;

import com.sankuai.meituan.waimai.opensdk.vo.AvailableTimeParam;

public class WMJBPQueryListByEPoiId {

	private List<Data> data;
	public void setData(List<Data> data) {
		this.data = data;
	}
	public List<Data> getData() {
		return data;
	}

	public class Data {
		private String boxNum;
		private String boxPrice;
		private String categoryName;
		private String description;
		private String dishName;
		private String eDishCode;
		private int isSoldOut;
		private int minOrderCount;
		private String picture;
		private float price;
		private int sequence;
		private List<Skus> skus;
		private String unit;

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getCategoryName() {
			return categoryName;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}

		public void setDishName(String dishName) {
			this.dishName = dishName;
		}
		public String getDishName() {
			return dishName;
		}

		public void setEDishCode(String eDishCode) {
			this.eDishCode = eDishCode;
		}
		public String getEDishCode() {
			return eDishCode;
		}
		public void setPicture(String picture) {
			this.picture = picture;
		}
		public String getPicture() {
			return picture;
		}

		public String getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}
	public String getBoxPrice() {
		return boxPrice;
	}
	public void setBoxPrice(String boxPrice) {
		this.boxPrice = boxPrice;
	}
	public String geteDishCode() {
		return eDishCode;
	}
	public void seteDishCode(String eDishCode) {
		this.eDishCode = eDishCode;
	}
	
	public int getIsSoldOut() {
		return isSoldOut;
	}
	public void setIsSoldOut(int isSoldOut) {
		this.isSoldOut = isSoldOut;
	}
	public int getMinOrderCount() {
		return minOrderCount;
	}
	public void setMinOrderCount(int minOrderCount) {
		this.minOrderCount = minOrderCount;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public void setSkus(List<Skus> skus) {
			this.skus = skus;
		}
		public List<Skus> getSkus() {
			return skus;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnit() {
			return unit;
		}
	}
	public class Skus {
		private String locationCode;
		private String price;
		private String ladderPrice;
		private String ladderNum;
		private String skuId;
		private String spec;
		private String stock;
		private String upc;
		public void setLocationCode(String locationCode) {
			this.locationCode = locationCode;
		}
		public String getLocationCode() {
			return locationCode;
		}

		public void setPrice(String price) {
			this.price = price;
		}
		public String getPrice() {
			return price;
		}

		public void setLadderPrice(String ladderPrice) {
			this.ladderPrice = ladderPrice;
		}
		public String getLadderPrice() {
			return ladderPrice;
		}

		public void setLadderNum(String ladderNum) {
			this.ladderNum = ladderNum;
		}
		public String getLadderNum() {
			return ladderNum;
		}

		public void setSkuId(String skuId) {
			this.skuId = skuId;
		}
		public String getSkuId() {
			return skuId;
		}

		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getSpec() {
			return spec;
		}

		public void setStock(String stock) {
			this.stock = stock;
		}
		public String getStock() {
			return stock;
		}

		public void setUpc(String upc) {
			this.upc = upc;
		}
		public String getUpc() {
			return upc;
		}

	}












}
