package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DataRatioDeleteReq extends JsonBasicReq {

    private LevelRequest request;

    public LevelRequest getRequest() {
        return request;
    }

    public void setRequest(LevelRequest request) {
        this.request = request;
    }

    public class LevelRequest
    {
        private String d_year;
        private List<LevelDataShop> shopList;

        public String getD_year() {
            return d_year;
        }

        public void setD_year(String d_year) {
            this.d_year = d_year;
        }

        public List<LevelDataShop> getShopList() {
            return shopList;
        }

        public void setShopList(List<LevelDataShop> shopList) {
            this.shopList = shopList;
        }
    }

    public class LevelDataShop
    {
        private String shopId;
        private String shopName;

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
    }
}
