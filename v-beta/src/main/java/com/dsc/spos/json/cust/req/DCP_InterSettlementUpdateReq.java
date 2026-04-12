package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettlementUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public  class Request {
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String month;
        @JSONFieldRequired
        private String dataType;
        @JSONFieldRequired
        private List<InterList> interList;
    }

    @NoArgsConstructor
    @Data
    public class InterList {
        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String item;
        private String interTradeType;
        @JSONFieldRequired
        private String status;
        private String unsettAmt;
        private String settledAmt;
        private String unpostedAmt;
        private String postedAmt;
        private String invoiceQty;
        private String invoiceAmt;
    }

}
