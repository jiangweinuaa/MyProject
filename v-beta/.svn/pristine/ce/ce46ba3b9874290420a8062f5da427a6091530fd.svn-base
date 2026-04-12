package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CFTemplateDeleteReq extends JsonBasicReq {


    @JSONFieldRequired
    private List<Request> request;

    @NoArgsConstructor
    @Data
    public  class Request {
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String cfCode;
    }
}
