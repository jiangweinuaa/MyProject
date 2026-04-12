package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettleDataGenerateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String billType;
        @JSONFieldRequired
        private String billNo;
        private String supplyOrgNo;
        private List<Detail> detail;
        private String returnSupplyPrice;
    }

    @NoArgsConstructor
    @Data
    public class Detail {
        private String receiveOrgNo;
        private String sourceBillType;
        private String sourceBillNo;
        private String sourceItem;
        private String item;
        private String pluNo;
        private String featureNo;
        private String pUnit;
        private String pQty;
        private String receivePrice;
        private String receiveAmt;
        private String supplyPrice;
        private String supplyAmt;
    }

}
