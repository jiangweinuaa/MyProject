package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_StockOutRefundDeleteReq extends JsonBasicReq
{

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String stockOutNo;
        private String stockOutNo_origin;


        public String getStockOutNo_origin()
        {
            return stockOutNo_origin;
        }

        public void setStockOutNo_origin(String stockOutNo_origin)
        {
            this.stockOutNo_origin = stockOutNo_origin;
        }

        public String getStockOutNo() {
            return stockOutNo;
        }
        public void setStockOutNo(String stockOutNo) {
            this.stockOutNo = stockOutNo;
        }

    }


}
