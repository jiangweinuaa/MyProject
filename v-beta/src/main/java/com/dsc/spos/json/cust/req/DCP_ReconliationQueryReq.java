package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReconliationQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ReconliationQueryReq.Level1Elm request;


    @Data
    public class Level1Elm{

        private String bdate;
        private String beginDate;
        private String endDate;
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String corpName;
        @JSONFieldRequired
        private String dataType;
        private String bizPartnerNo;
        private String status;

    }

}