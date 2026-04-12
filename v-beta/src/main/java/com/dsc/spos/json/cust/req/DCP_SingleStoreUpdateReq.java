package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_SingleStoreUpdateReq extends JsonBasicReq {

    private DCP_SingleStoreUpdateReq.levelRequest request;

    public DCP_SingleStoreUpdateReq.levelRequest getRequest() {
        return request;
    }

    public void setRequest(DCP_SingleStoreUpdateReq.levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private List<DCP_SingleStoreUpdateReq.Shop> shopList;

        private String enableSingleStore;

        public List<DCP_SingleStoreUpdateReq.Shop> getShopList() {
            return shopList;
        }

        public void setShopList(List<DCP_SingleStoreUpdateReq.Shop> shopList) {
            this.shopList = shopList;
        }

        public String getEnableSingleStore() {
            return enableSingleStore;
        }

        public void setEnableSingleStore(String enableSingleStore) {
            this.enableSingleStore = enableSingleStore;
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
