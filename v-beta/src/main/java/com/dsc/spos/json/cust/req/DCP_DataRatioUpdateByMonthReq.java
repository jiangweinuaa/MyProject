package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DataRatioUpdateByMonthReq extends JsonBasicReq {

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
        private String d_month;
        private String dataRatio;
        /**
         * 范围：1-全部门店 2-指定门店
         */
        private String rangeType;
        private List<LevelDataShop> shopList;

        public String getD_year() {
            return d_year;
        }

        public void setD_year(String d_year) {
            this.d_year = d_year;
        }

        public String getD_month() {
            return d_month;
        }

        public void setD_month(String d_month) {
            this.d_month = d_month;
        }

        public String getDataRatio() {
            return dataRatio;
        }

        public void setDataRatio(String dataRatio) {
            this.dataRatio = dataRatio;
        }

        public String getRangeType() {
            return rangeType;
        }

        public void setRangeType(String rangeType) {
            this.rangeType = rangeType;
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
