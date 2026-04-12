package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_InterSettSettingQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_InterSettSettingQueryReq.levelRequest request;

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