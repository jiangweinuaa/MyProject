package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_CostDataStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public  class Request {
        @JSONFieldRequired(display = "底稿单号")
        private String costNo;
        private String status;
        @JSONFieldRequired(display = "操作类型")
        private String opType;
        private String eId;
    }
}
