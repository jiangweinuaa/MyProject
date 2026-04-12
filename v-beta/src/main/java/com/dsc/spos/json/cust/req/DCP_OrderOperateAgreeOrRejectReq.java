package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_OrderOperateAgreeOrRejectReq extends JsonBasicReq {


    private levReq request;

    public levReq getRequest() {
        return request;
    }

    public void setRequest(levReq request) {
        this.request = request;
    }

    public class levReq{
        private String eId ;
        private String shopId;
        private String opNo;
        private String opName;
        private String operationType;//操作类型 1表示同意修改订单，2表示同意退单
        List<OrderList> orderList ;

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getOpNo() {
            return opNo;
        }

        public void setOpNo(String opNo) {
            this.opNo = opNo;
        }

        public String getOpName() {
            return opName;
        }

        public void setOpName(String opName) {
            this.opName = opName;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public List<OrderList> getOrderList() {
            return orderList;
        }

        public void setOrderList(List<OrderList> orderList) {
            this.orderList = orderList;
        }
    }

    public class OrderList{
        private String orderNo;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

    }
}
