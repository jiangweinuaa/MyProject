package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_DataRatioQueryReq extends JsonBasicReq {

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
        private String[] shopList;

        public String getD_year() {
            return d_year;
        }

        public void setD_year(String d_year) {
            this.d_year = d_year;
        }

        public String[] getShopList() {
            return shopList;
        }

        public void setShopList(String[] shopList) {
            this.shopList = shopList;
        }
    }
}
