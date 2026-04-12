package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPerInfoInPutReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String packerId;
        private String packerName;
        private String packerTelephone;
        private String delId;
        private String delName;
        private String delTelephone;
        private String[] orderList;

        public String getPackerId() {
            return packerId;
        }

        public void setPackerId(String packerId) {
            this.packerId = packerId;
        }

        public String getPackerName() {
            return packerName;
        }

        public void setPackerName(String packerName) {
            this.packerName = packerName;
        }

        public String getPackerTelephone() {
            return packerTelephone;
        }

        public void setPackerTelephone(String packerTelephone) {
            this.packerTelephone = packerTelephone;
        }

        public String getDelId() {
            return delId;
        }

        public void setDelId(String delId) {
            this.delId = delId;
        }

        public String getDelName() {
            return delName;
        }

        public void setDelName(String delName) {
            this.delName = delName;
        }

        public String getDelTelephone() {
            return delTelephone;
        }

        public void setDelTelephone(String delTelephone) {
            this.delTelephone = delTelephone;
        }

        public String[] getOrderList() {
            return orderList;
        }

        public void setOrderList(String[] orderList) {
            this.orderList = orderList;
        }
    }
}
