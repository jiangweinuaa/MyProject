package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_InterSettlementQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public  class Request {
        private String corp;
        private String corpName;
        private String year;
        private String month;
        private String demandCorp;
        private String supplyOrg;
        private String dataType;
        private String billNo;
        private String pluNo;
        private String pluName;
        private String fee;
        private String feeName;
    }
}
