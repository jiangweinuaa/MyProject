package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProductInQueryReq extends JsonBasicReq {

    private DCP_ProductInQueryReq.levelElm request;

    @Data
    public class levelElm{
        private String beginDate;
        private String endDate;
        private String keyTxt;
    }
}
