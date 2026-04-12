package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurOrderBookingQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PurOrderBookingQueryReq.Request request;

    @Data
    public class Request{
        @JSONFieldRequired
        private String dateType;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String supplier;
        private String purOrderNo;
        private String receiptOrgNo;
        private String status;

    }

}
