package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ROrderQueryReq extends JsonBasicReq {

    private DCP_ROrderQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        private String keyTxt;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String status;
    }
}
