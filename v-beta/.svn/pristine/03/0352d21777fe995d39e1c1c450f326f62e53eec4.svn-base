package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_TransApplyDeleteReq extends JsonBasicReq {

    private DCP_TransApplyDeleteReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired(display = "申请单号")
        private String billNo;
    }
}
