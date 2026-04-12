package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockOutDownReq extends JsonBasicReq {
    private DCP_StockOutDownReq.levelElm request;

    @Data
    public class levelElm {
        private List<StockOutNoList> stockOutNoList;
    }

    @Data
    public class StockOutNoList{
        private String stockOutNo;
    }
}
