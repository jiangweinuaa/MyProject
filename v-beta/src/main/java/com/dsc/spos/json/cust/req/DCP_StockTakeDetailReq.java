package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_StockTakeDetail
 * 服务说明：库存盘点明细查询
 * @author jinzma
 * @since  2021-03-02
 */
public class DCP_StockTakeDetailReq extends JsonBasicReq {
    private levelElm request;
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    public class levelElm{
        private String ofNo;
        private String stockTakeNo;
        public String getOfNo() {
            return ofNo;
        }
        public void setOfNo(String ofNo) {
            this.ofNo = ofNo;
        }
        public String getStockTakeNo() {
            return stockTakeNo;
        }
        public void setStockTakeNo(String stockTakeNo) {
            this.stockTakeNo = stockTakeNo;
        }
    }
}
