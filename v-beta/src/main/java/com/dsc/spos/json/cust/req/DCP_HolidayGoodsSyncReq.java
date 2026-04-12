package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_HolidayGoodsSyncReq extends JsonBasicReq
{
    private level1Elm request;

    public level1Elm getRequest() {
        return request;
    }

    public void setRequest(level1Elm request) {
        this.request = request;
    }

    public class level1Elm {
        private String billNo;
        private String eId;

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }
    }


}
