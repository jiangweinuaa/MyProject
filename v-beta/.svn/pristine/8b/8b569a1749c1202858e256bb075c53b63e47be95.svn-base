package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_BomVersionQueryReq extends JsonBasicReq {

    private DCP_BomVersionQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String bomNo;
        private String versionNum;
        private String beginDate;
        private String endDate;
    }
}
