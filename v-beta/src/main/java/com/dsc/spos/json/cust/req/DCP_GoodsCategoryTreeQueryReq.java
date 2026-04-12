package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsCategoryTreeQueryReq extends JsonBasicReq {
    
    private levelRequest request;
    
    public levelRequest getRequest() {
        return request;
    }
    public void setRequest(levelRequest request) {
        this.request = request;
    }
    
    public class levelRequest {
        private String category;
        private String brand;
        private String series;
        private String attrGroup;
        private String attr;
        private String isOneLevel;
        private String tag;
        
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getBrand() {
            return brand;
        }
        public void setBrand(String brand) {
            this.brand = brand;
        }
        public String getSeries() {
            return series;
        }
        public void setSeries(String series) {
            this.series = series;
        }
        public String getAttrGroup() {
            return attrGroup;
        }
        public void setAttrGroup(String attrGroup) {
            this.attrGroup = attrGroup;
        }
        public String getAttr() {
            return attr;
        }
        public void setAttr(String attr) {
            this.attr = attr;
        }
        public String getIsOneLevel() {
            return isOneLevel;
        }
        public void setIsOneLevel(String isOneLevel) {
            this.isOneLevel = isOneLevel;
        }
        public String getTag() {
            return tag;
        }
        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
