package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProdTemplateDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ProdTemplateDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        @JSONFieldRequired
        private String templateId;
    }

}

