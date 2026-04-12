package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReturnApplyProDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyProDetailQueryReq.level1Elm request;

    @Data
    public class level1Elm{
        private String approveStatus;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        //@JSONFieldRequired
        private String returnType;
        private String billNo;
        private String keyTxt;
        private String getType;
    }
}
