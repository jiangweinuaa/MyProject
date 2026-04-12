package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DCP_BatchingDocProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_BatchingDocProcessReq.levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        private String batchNo;
        private String accountDate;

    }
}
