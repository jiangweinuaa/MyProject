package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WOReportDetailQueryReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_WOReportDetailQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String reportNo;


    }
}
