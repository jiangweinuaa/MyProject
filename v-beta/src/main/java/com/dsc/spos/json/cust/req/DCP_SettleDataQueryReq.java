package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_SettleDataQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_SettleDataQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        private String bizType;
        private String beginDate;
        private String endDate;
        private String organizationNo;
        @JSONFieldRequired
        private String payOrgNo;
        private String keyTxt;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String status;
        private String billNo;
        private String bType;

    }

}
