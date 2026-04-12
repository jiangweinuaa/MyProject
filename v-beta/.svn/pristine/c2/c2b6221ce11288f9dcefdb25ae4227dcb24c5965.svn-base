package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_DataRatioQueryRes extends JsonRes {

    private List<level1Elm> datas;

    public List<level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level1Elm
    {
        private String shopId;
        private String shopName;
        private List<level2Elm> dataRatioList;

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

        public List<level2Elm> getDataRatioList() {
            return dataRatioList;
        }

        public void setDataRatioList(List<level2Elm> dataRatioList) {
            this.dataRatioList = dataRatioList;
        }
    }

    public class level2Elm
    {
        private String d_year;
        private String d_month;
        private String dataRatio;

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
    }
}
