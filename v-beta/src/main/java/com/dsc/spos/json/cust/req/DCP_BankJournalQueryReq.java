package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_BankJournalQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired(display = "银行账户")
        private String accountCode;
        @JSONFieldRequired(display = "开始日期")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期")
        private String endDate;
    }
}
