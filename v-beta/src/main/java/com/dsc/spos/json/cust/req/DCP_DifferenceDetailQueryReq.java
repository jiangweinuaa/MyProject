package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DifferenceDetailQueryReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_DifferenceDetailQueryReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired(display = "差异申诉单号")
        private String differenceNo;
    }

}
