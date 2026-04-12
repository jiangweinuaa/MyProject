package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_BatchToMStockOutReq extends JsonBasicReq {

    private DCP_BatchToMStockOutReq.Level1Elm request;

    @Data
    public class  Level1Elm{
        private String batchNo;
    }
}
