package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.JsonBasicRes;

public class DCP_GoodsCategoryTreeQueryRes extends JsonBasicRes {
    
    private data datas;
    
    public data getDatas() {
        return datas;
    }
    public void setDatas(data datas) {
        this.datas = datas;
    }
    
    public class data {
        private List<levelbrand> brand;
        private List<levelseries> series;
        private List<levelcategory> category;
        private List<levelattr> attr;
        private List<levelattrGroup> attrGroup;
        private List<leveltags> tags;
        
        public List<levelbrand> getBrand() {
            return brand;
        }
        public void setBrand(List<levelbrand> brand) {
            this.brand = brand;
        }
        public List<levelseries> getSeries() {
            return series;
        }
        public void setSeries(List<levelseries> series) {
            this.series = series;
        }
        public List<levelcategory> getCategory() {
            return category;
        }
        public void setCategory(List<levelcategory> category) {
            this.category = category;
        }
        public List<levelattr> getAttr() {
            return attr;
        }
        public void setAttr(List<levelattr> attr) {
            this.attr = attr;
        }
        public List<levelattrGroup> getAttrGroup() {
            return attrGroup;
        }
        public void setAttrGroup(List<levelattrGroup> attrGroup) {
            this.attrGroup = attrGroup;
        }
        public List<leveltags> getTags() {
            return tags;
        }
        public void setTags(List<leveltags> tags) {
            this.tags = tags;
        }
    }
    
    public class levelbrand {
        private String brandNo;
        private String brandName;
        
        public String getBrandNo() {
            return brandNo;
        }
        public void setBrandNo(String brandNo) {
            this.brandNo = brandNo;
        }
        public String getBrandName() {
            return brandName;
        }
        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }
    }
    
    public class levelseries {
        private String seriesNo;
        private String seriesName;
        
        public String getSeriesNo() {
            return seriesNo;
        }
        public void setSeriesNo(String seriesNo) {
            this.seriesNo = seriesNo;
        }
        public String getSeriesName() {
            return seriesName;
        }
        public void setSeriesName(String seriesName) {
            this.seriesName = seriesName;
        }
    }
    
    public class levelcategory{
        private String category;
        private String categoryName;
        private String upCategory;
        private String categoryLevel ;
        private List<levelcategory> child;
        private String categoryImageUrl; // 分类图片地址
        private String isAllowSelfBuilt;
        
        public String getCategoryImageUrl() {
            return categoryImageUrl;
        }
        public void setCategoryImageUrl(String categoryImageUrl) {
            this.categoryImageUrl = categoryImageUrl;
        }
        public String getCategoryLevel() {
            return categoryLevel;
        }
        public void setCategoryLevel(String categoryLevel) {
            this.categoryLevel = categoryLevel;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getCategoryName() {
            return categoryName;
        }
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        public String getUpCategory() {
            return upCategory;
        }
        public void setUpCategory(String upCategory) {
            this.upCategory = upCategory;
        }
        public List<levelcategory> getChild() {
            return child;
        }
        public void setChild(List<levelcategory> child) {
            this.child = child;
        }
        public String getIsAllowSelfBuilt() {
            return isAllowSelfBuilt;
        }
        public void setIsAllowSelfBuilt(String isAllowSelfBuilt) {
            this.isAllowSelfBuilt = isAllowSelfBuilt;
        }
    }
    
    public class levelattrGroup {
        private String attrGroupId;
        private String attrGroupName;
        
        public String getAttrGroupId() {
            return attrGroupId;
        }
        public void setAttrGroupId(String attrGroupId) {
            this.attrGroupId = attrGroupId;
        }
        public String getAttrGroupName() {
            return attrGroupName;
        }
        public void setAttrGroupName(String attrGroupName) {
            this.attrGroupName = attrGroupName;
        }
        
    }
    
    public class levelattr {
        private String attrId;
        private String attrName;
        private List<levelattrValue> attrValue;
        
        public String getAttrId() {
            return attrId;
        }
        public void setAttrId(String attrId) {
            this.attrId = attrId;
        }
        public String getAttrName() {
            return attrName;
        }
        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }
        public List<levelattrValue> getAttrValue() {
            return attrValue;
        }
        public void setAttrValue(List<levelattrValue> attrValue) {
            this.attrValue = attrValue;
        }
        
        
    }
    
    public class levelattrValue {
        private String attrValueId;
        private String attrValueName;
        
        public String getAttrValueId() {
            return attrValueId;
        }
        public void setAttrValueId(String attrValueId) {
            this.attrValueId = attrValueId;
        }
        public String getAttrValueName() {
            return attrValueName;
        }
        public void setAttrValueName(String attrValueName) {
            this.attrValueName = attrValueName;
        }
        
    }
    
    public class leveltags {
        private String tagNo;
        private String tagName;
        
        public String getTagNo() {
            return tagNo;
        }
        public void setTagNo(String tagNo) {
            this.tagNo = tagNo;
        }
        public String getTagName() {
            return tagName;
        }
        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }
    
    
}
