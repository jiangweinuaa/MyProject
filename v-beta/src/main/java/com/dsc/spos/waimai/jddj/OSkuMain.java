package com.dsc.spos.waimai.jddj;

import java.util.Date;
import java.util.List;

/**
* Auto-generated: 2019-02-01 16:31:52
*
* @author bejson.com (i@bejson.com)
* @website http://www.bejson.com/java2pojo/
*/
public class OSkuMain {

   private long skuId;
   private String outSkuId;
   private long orgCode;
   private int categoryId;
   private int brandId;
   private String skuName;
   private int skuPrice;
   private double weight;
   private Date fixedUpTime;
   private int fixedStatus;
   private List<Long> shopCategories;
   private List<Integer> sellCities;
   private String slogan;
   public void setSkuId(long skuId) {
        this.skuId = skuId;
    }
    public long getSkuId() {
        return skuId;
    }

   public void setOutSkuId(String outSkuId) {
        this.outSkuId = outSkuId;
    }
    public String getOutSkuId() {
        return outSkuId;
    }

   public void setOrgCode(long orgCode) {
        this.orgCode = orgCode;
    }
    public long getOrgCode() {
        return orgCode;
    }

   public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getCategoryId() {
        return categoryId;
    }

   public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
    public int getBrandId() {
        return brandId;
    }

   public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
    public String getSkuName() {
        return skuName;
    }

   public void setSkuPrice(int skuPrice) {
        this.skuPrice = skuPrice;
    }
    public int getSkuPrice() {
        return skuPrice;
    }

   public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getWeight() {
        return weight;
    }

   public void setFixedUpTime(Date fixedUpTime) {
        this.fixedUpTime = fixedUpTime;
    }
    public Date getFixedUpTime() {
        return fixedUpTime;
    }

   public void setFixedStatus(int fixedStatus) {
        this.fixedStatus = fixedStatus;
    }
    public int getFixedStatus() {
        return fixedStatus;
    }

   public void setShopCategories(List<Long> shopCategories) {
        this.shopCategories = shopCategories;
    }
    public List<Long> getShopCategories() {
        return shopCategories;
    }

   public void setSellCities(List<Integer> sellCities) {
        this.sellCities = sellCities;
    }
    public List<Integer> getSellCities() {
        return sellCities;
    }
		public String getSlogan() {
			return slogan;
		}
		public void setSlogan(String slogan) {
			this.slogan = slogan;
		}

}