package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_StockInDetailQueryReq extends JsonBasicReq {

    private DCP_StockInDetailQueryReq.LevelReq request;

    @Data
    public class LevelReq{

        private String docType;
        private String stockInNo;
        private String status;
        private String ofNo;
    }
}
