package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProdTemplateEnableReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProdTemplateEnableReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String templateId;
        @JSONFieldRequired
        private String opType;
    }

}
