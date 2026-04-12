package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_LStockOutRefundProcessReq extends JsonBasicReq
{

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String lStockOutNo;
        private String shopId;
        private String opType;

        public String getShopId() {
            return shopId;
        }
        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
        public String getlStockOutNo() {
            return lStockOutNo;
        }
        public void setlStockOutNo(String lStockOutNo) {
            this.lStockOutNo = lStockOutNo;
        }

        public String getOpType() {
            return opType;
        }

        public void setOpType(String opType) {
            this.opType = opType;
        }
    }


}
