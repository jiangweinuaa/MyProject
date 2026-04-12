package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_BankReceipetStatusUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired(display = "收支单号编号")
        private String cmNo;
        private String status;
        @JSONFieldRequired(display = "操作类型")
        private String opType;
        private String eId;
        private String memo;
    }
}
