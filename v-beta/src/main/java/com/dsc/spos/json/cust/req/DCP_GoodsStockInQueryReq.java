package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * @apiNote 商品配送收货查询
 * @since 2021-04-20
 * @author jinzma
 */
public class DCP_GoodsStockInQueryReq extends JsonBasicReq {
    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String keyTxt;
        private String status;
        private String dateType;
        private String beginDate;
        private String endDate;

        public String getKeyTxt() {
            return keyTxt;
        }

        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDateType() {
            return dateType;
        }

        public void setDateType(String dateType) {
            this.dateType = dateType;
        }
    }
}
