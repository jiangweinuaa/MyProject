package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApPrepayProcessReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ApPrepayProcessReq.LevelRequest request;

    @Data
    public class LevelRequest {

        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String taskId;
        private String purOrderNo;
    }
}
