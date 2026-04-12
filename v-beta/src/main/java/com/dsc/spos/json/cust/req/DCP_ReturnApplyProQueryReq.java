package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReturnApplyProQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyProQueryReq.level1Elm request;

    @Data
    public class level1Elm{
        private String approveStatus;
        private String approveOrgNo;
        private String beginDate;
        private String endDate;
        private String returnType;
        private String keyTxt;
        private String getType;

    }
}
