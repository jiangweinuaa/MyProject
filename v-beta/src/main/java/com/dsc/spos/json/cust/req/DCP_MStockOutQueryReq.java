package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MStockOutQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_MStockOutQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String dateType;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String keyTxt;
        private String status;
        private String departId;
    }

}
