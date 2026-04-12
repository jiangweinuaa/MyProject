package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DCP_WOReportProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_WOReportProcessReq.LevelRequest request;
    @NoArgsConstructor
    @Data
    public  class LevelRequest {
        private String reportNo;
        private String accountDate;
        private String oprType;
    }
}
