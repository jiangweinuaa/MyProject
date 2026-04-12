package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_StockOutRefundProcessReq extends JsonBasicReq
{

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm {
        private String stockOutNo;
        private String docType;
        private String ofNo;
        private String status;
        //钉钉审批
        private String oEId;
        private String oShopId;
        private String o_createBy;
        private String o_inv_cost_warehouse;
        private String o_langType;
        public String getStockOutNo() {
            return stockOutNo;
        }
        public void setStockOutNo(String stockOutNo) {
            this.stockOutNo = stockOutNo;
        }
        public String getDocType() {
            return docType;
        }
        public void setDocType(String docType) {
            this.docType = docType;
        }
        public String getOfNo() {
            return ofNo;
        }
        public void setOfNo(String ofNo) {
            this.ofNo = ofNo;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getoEId() {
            return oEId;
        }
        public void setoEId(String oEId) {
            this.oEId = oEId;
        }
        public String getoShopId() {
            return oShopId;
        }
        public void setoShopId(String oShopId) {
            this.oShopId = oShopId;
        }
        public String getO_createBy() {
            return o_createBy;
        }
        public void setO_createBy(String o_createBy) {
            this.o_createBy = o_createBy;
        }
        public String getO_inv_cost_warehouse() {
            return o_inv_cost_warehouse;
        }
        public void setO_inv_cost_warehouse(String o_inv_cost_warehouse) {
            this.o_inv_cost_warehouse = o_inv_cost_warehouse;
        }
        public String getO_langType() {
            return o_langType;
        }
        public void setO_langType(String o_langType) {
            this.o_langType = o_langType;
        }



    }

}

