package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_CostDomainOpenQryReq extends JsonBasicReq {

    private Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        private String orgNo;
        private String corp;
        private String keyTxt;
    }

}
