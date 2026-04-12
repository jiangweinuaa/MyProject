package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class ISV_WMClientQueryReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String clientNo;

        public String getClientNo() {
            return clientNo;
        }

        public void setClientNo(String clientNo) {
            this.clientNo = clientNo;
        }
    }
}
