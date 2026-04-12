package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/*
 * 服务函数：DCP_SubStockTakeRangeQuery
 * 服务说明：盘点子任务范围查询
 * @author jinzma
 * @since  2021-03-08
 */
public class DCP_SubStockTakeRangeQueryReq extends JsonBasicReq {
private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String subStockTakeNo;
        private String stockTakeNo;
        private String status;
        private String keyTxt;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }

        public String getStockTakeNo() {
            return stockTakeNo;
        }

        public void setStockTakeNo(String stockTakeNo) {
            this.stockTakeNo = stockTakeNo;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getKeyTxt() {
            return keyTxt;
        }

        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }
    }
}
