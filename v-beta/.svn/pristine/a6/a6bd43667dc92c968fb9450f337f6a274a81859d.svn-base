package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_SettleDataNoQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_SettleDataNoQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        private String beginDate;
        private String endDate;
        private String organizationNo;
        @JSONFieldRequired
        private String payOrgNo;
        private String payOrgName;
        private String keyTxt;
        private String bizPartnerNo;
        private String bizPartnerName;
        @JSONFieldRequired
        private String bizType;
        private String status;
    }

}
