package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReconliationProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ReconliationProcessReq.level1Elm request;

    @Data
    public class level1Elm {
        @JSONFieldRequired
        private String organizationNo;
        private String organizationName;
        @JSONFieldRequired
        private String startDate;
        @JSONFieldRequired
        private String endDate;
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String dataType;
        private String bizPartnerNo;
        //@JSONFieldRequired
        private String bDate;
    }
}
