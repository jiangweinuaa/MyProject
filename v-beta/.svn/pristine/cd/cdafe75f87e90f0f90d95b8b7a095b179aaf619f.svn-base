package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_SingleStoreQueryReq extends JsonBasicReq {

    private DCP_SingleStoreQueryReq.levelRequest request;

    public DCP_SingleStoreQueryReq.levelRequest getRequest() {
        return request;
    }

    public void setRequest(DCP_SingleStoreQueryReq.levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private List<Shop> shopList;

        private String enableStatus;

        public List<Shop> getShopList() {
            return shopList;
        }

        public void setShopList(List<Shop> shopList) {
            this.shopList = shopList;
        }

        public String getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(String enableStatus) {
            this.enableStatus = enableStatus;
        }
    }

    public static class Shop{
        private String shopId;

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
    }

}
