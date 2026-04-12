package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FixedValueQueryReq extends JsonBasicReq {
    private Level1Elm request;

    public Level1Elm getRequest() {
        return request;
    }

    public void setRequest(Level1Elm request) {
        this.request = request;
    }

    public static class Level1Elm {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
