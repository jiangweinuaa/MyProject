package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_LightProGoodsCreateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private List<levelPlu> pluList;
        private List<levelShop> shopList;

        public List<levelPlu> getPluList() {
            return pluList;
        }

        public void setPluList(List<levelPlu> pluList) {
            this.pluList = pluList;
        }

        public List<levelShop> getShopList() {
            return shopList;
        }

        public void setShopList(List<levelShop> shopList) {
            this.shopList = shopList;
        }
    }
    public class levelPlu
    {
        private String pluNo;
        private String featureNo;
        private String unitNo;

        public String getPluNo() {
            return pluNo;
        }

        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }

        public String getFeatureNo() {
            return featureNo;
        }

        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }

        public String getUnitNo() {
            return unitNo;
        }

        public void setUnitNo(String unitNo) {
            this.unitNo = unitNo;
        }
    }
    public class levelShop
    {
        private String shopNo;

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }
    }
}
