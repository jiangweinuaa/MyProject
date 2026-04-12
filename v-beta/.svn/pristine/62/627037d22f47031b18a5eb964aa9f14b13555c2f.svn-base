package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/*
 * 服务函数：DCP_SubStockTakeLoadProcess
 * 服务说明：盘点子任务导入
 * @author jinzma
 * @since  2021-03-09
 */
public class DCP_SubStockTakeLoadProcessReq extends JsonBasicReq {
    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String stockTakeNo;
        private String stockType;

        public String getStockTakeNo() {
            return stockTakeNo;
        }

        public void setStockTakeNo(String stockTakeNo) {
            this.stockTakeNo = stockTakeNo;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }
    }
}
