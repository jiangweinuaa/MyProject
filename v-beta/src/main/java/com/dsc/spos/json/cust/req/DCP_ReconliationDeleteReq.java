package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReconliationDeleteReq extends JsonBasicReq {


    private DCP_ReconliationDeleteReq.level1Elm request;

    @Data
    public class level1Elm {
        private String organizationNo;
        private String organizationName;
        private String bdate;
        private String corp;
        private String corpName;
        private String dataType;
        private String bizPartnerNo;
        private String reconNo;
    }
}
