package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_InterSettSettingVQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_InterSettSettingVQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        //private String status;
        //private String processNo;
        //private String businessType;
        private String supplyObject;
        private String demandObject;
        private String relationship;
    }

}