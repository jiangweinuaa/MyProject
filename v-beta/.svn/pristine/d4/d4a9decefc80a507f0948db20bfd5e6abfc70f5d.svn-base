package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DCP_PrintTemplateQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_PrintTemplateQueryReq.Request request;

    @Getter
    @Setter
    public class Request {
        private String status;
        private String keyTxt;

    }

}