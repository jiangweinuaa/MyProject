package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DataRatioUpdateByShopReq extends JsonBasicReq {

    private LevelRequest request;

    public LevelRequest getRequest() {
        return request;
    }

    public void setRequest(LevelRequest request) {
        this.request = request;
    }

    public class LevelRequest
    {
        private String shopId;
        private String d_year;
        private List<LevelDataRatio> dataRatioList;

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getD_year() {
            return d_year;
        }

        public void setD_year(String d_year) {
            this.d_year = d_year;
        }

        public List<LevelDataRatio> getDataRatioList() {
            return dataRatioList;
        }

        public void setDataRatioList(List<LevelDataRatio> dataRatioList) {
            this.dataRatioList = dataRatioList;
        }
    }

    public class LevelDataRatio
    {
        private String d_month;
        private String dataRatio;

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
    }
}
