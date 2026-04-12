package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProdTemplateQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ProdTemplateQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        private String restrictOrg;
        private String keyTxt;
    }

}
