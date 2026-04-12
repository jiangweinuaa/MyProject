package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_LightProGoodsDeleteReq extends JsonBasicReq {

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

        public List<levelPlu> getPluList() {
            return pluList;
        }

        public void setPluList(List<levelPlu> pluList) {
            this.pluList = pluList;
        }
    }
    public class levelPlu
    {
        private String pluNo;
        private String featureNo;
        private String shopNo;

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

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }
    }

}
