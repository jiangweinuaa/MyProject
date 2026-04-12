package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_ShopArBillProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String corp;
        private String shopId;
        private String reconNo;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String eId;
    }
}
