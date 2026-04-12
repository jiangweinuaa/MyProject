package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProdScheduleStatusUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProdScheduleStatusUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String opType;
    }

}
