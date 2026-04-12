package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_STakeTemplateDetailQueryReq extends JsonBasicReq
{
    private DCP_STakeTemplateDetailQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private String templateNo;
    }


}
