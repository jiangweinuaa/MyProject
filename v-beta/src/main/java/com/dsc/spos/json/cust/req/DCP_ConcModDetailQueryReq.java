package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ConcModDetailQueryReq extends JsonBasicReq
{
    private LevelElm request;
    @Data
    public class LevelElm{
        private String rFuncNo;
    }
}
