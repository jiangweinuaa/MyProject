package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WoStatusUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        @JSONFieldRequired
        private List<WostatusList> wostatusList;
    }

    @NoArgsConstructor
    @Data
    public class WostatusList {
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String taskId;
        private String productStatus;
        private String costCloseDate;
    }
}

