package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_HolidayGoodsQueryReq extends JsonBasicReq {
    private level1Elm request;

    public level1Elm getRequest() {
        return request;
    }

    public void setRequest(level1Elm request) {
        this.request = request;
    }

    public class level1Elm {
        private  String billNo;
        private  String keyTxt;
        private  String status;
        private  String execStatus;

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

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

        public String getExecStatus() {
            return execStatus;
        }

        public void setExecStatus(String execStatus) {
            this.execStatus = execStatus;
        }
    }
}
