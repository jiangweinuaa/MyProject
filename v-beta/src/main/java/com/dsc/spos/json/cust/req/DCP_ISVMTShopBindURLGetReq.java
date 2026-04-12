package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ISVMTShopBindURLGetReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String eId;
        private String shopNo;
        private String shopName;

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }
}
