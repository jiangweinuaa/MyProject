package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_StockTaskProgressQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_StockTaskProgressQueryReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired(display = "任务单号")
        private String stockTaskNo;
    }
}
