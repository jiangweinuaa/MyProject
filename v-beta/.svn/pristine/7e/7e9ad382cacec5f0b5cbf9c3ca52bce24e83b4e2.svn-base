package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_SingleStoreQueryRes extends JsonRes {
    private DCP_SingleStoreQueryRes.level1Elm datas;

    public DCP_SingleStoreQueryRes.level1Elm getDatas() {
        return datas;
    }

    public void setDatas(DCP_SingleStoreQueryRes.level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm
    {
        private List<Shop> shopList;

        public List<Shop> getShopList() {
            return shopList;
        }

        public void setShopList(List<Shop> shopList) {
            this.shopList = shopList;
        }
    }

    public static class Shop{

        private String shopId;

        private String shopName;

        private String enableSingleStore;

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getEnableSingleStore() {
            return enableSingleStore;
        }

        public void setEnableSingleStore(String enableSingleStore) {
            this.enableSingleStore = enableSingleStore;
        }
    }

}
