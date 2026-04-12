package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PStockOutQueryReq extends JsonBasicReq {

    private DCP_PStockOutQueryReq.levelElm request;

    @Data
    public class levelElm{
        private String status;
        private String keyTxt;
        private String dateType;
        private String docType;
        private String beginDate;
        private String endDate;

    }


}

