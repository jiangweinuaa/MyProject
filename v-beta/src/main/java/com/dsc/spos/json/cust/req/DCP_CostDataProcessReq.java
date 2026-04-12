package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_CostDataProcessReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired(display = "账套编码")
        private String accountID;
        private String account;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
    }
}
