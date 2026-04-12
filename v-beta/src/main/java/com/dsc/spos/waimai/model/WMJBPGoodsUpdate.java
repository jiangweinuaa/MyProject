package com.dsc.spos.waimai.model;

import java.util.List;
import com.sankuai.meituan.waimai.opensdk.vo.AvailableTimeParam;

public class WMJBPGoodsUpdate {

	private Float boxNum;//这些字段都是可传可不传，必须要用类型
	private Float boxPrice;//这些字段都是可传可不传，必须要用类型
	private String categoryName;
	private String description;
	private String dishName;
	private String EDishCode;
	private String ePoiId;
	
	private Integer isSoldOut;//这些字段都是可传可不传，必须要用类型
	private Integer minOrderCount;//这些字段都是可传可不传，必须要用类型 如果用int，即使不传，但是实体转json会默认这个节点有值=0
	private String picture;
	private Integer sequence;
	private String unit;
	private List<Skus> skus;

	public float getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(float boxNum) {
		this.boxNum = boxNum;
	}
	public float getBoxPrice() {
		return boxPrice;
	}
	public void setBoxPrice(float boxPrice) {
		this.boxPrice = boxPrice;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public String getEDishCode() {
		return EDishCode;
	}

	public void setEDishCode(String eDishCode) {
		EDishCode = eDishCode;
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

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<Skus> getSkus() {
		return skus;
	}

	public void setSkus(List<Skus> skus) {
		this.skus = skus;
	}

	public String getePoiId() {
	return ePoiId;
	}

	public void setePoiId(String ePoiId) {
	this.ePoiId = ePoiId;
	}

	public static class Skus {
		private AvailableTimeParam availableTimes;
		private String locationCode;
		private float price;
		private String skuId;
		private String spec;
		private int stock;
		private String upc;
		private String ladderPrice;
		private String ladderNum;
		private Float boxNum;
		private Float boxPrice;
		public AvailableTimeParam getAvailableTimes() {
			return availableTimes;
		}
		public void setAvailableTimes(AvailableTimeParam availableTimes) {
			this.availableTimes = availableTimes;
		}
		public String getLocationCode() {
			return locationCode;
		}
		public void setLocationCode(String locationCode) {
			this.locationCode = locationCode;
		}

		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public int getStock() {
			return stock;
		}
		public void setStock(int stock) {
			this.stock = stock;
		}
		public float getBoxNum() {
			return boxNum;
		}
		public void setBoxNum(float boxNum) {
			this.boxNum = boxNum;
		}
		public float getBoxPrice() {
			return boxPrice;
		}
		public void setBoxPrice(float boxPrice) {
			this.boxPrice = boxPrice;
		}
		public String getSkuId() {
			return skuId;
		}
		public void setSkuId(String skuId) {
			this.skuId = skuId;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getUpc() {
			return upc;
		}
		public void setUpc(String upc) {
			this.upc = upc;
		}
		public String getLadderPrice() {
			return ladderPrice;
		}
		public void setLadderPrice(String ladderPrice) {
			this.ladderPrice = ladderPrice;
		}
		public String getLadderNum() {
			return ladderNum;
		}
		public void setLadderNum(String ladderNum) {
			this.ladderNum = ladderNum;
		}

	}

}