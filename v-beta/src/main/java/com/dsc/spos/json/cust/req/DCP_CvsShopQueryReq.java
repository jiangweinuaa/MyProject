package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CvsShopQueryReq extends JsonBasicReq {

    private Level1Elm request;

    public Level1Elm getRequest() {
        return request;
    }
    public void setRequest(Level1Elm request) {
        this.request = request;
    }

    public class Level1Elm {
        private String orgNo;

        public String getOrgNo() {
            return orgNo;
        }
        public void setOrgNo(String orgNo) {
            this.orgNo = orgNo;
        }
    }
}
