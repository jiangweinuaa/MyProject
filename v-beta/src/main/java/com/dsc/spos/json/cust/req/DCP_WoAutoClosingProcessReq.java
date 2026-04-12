package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WoAutoClosingProcessReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String corp_Name;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        @JSONFieldRequired
        private String costCloseDate;
        @JSONFieldRequired
        private String type;
    }
}
