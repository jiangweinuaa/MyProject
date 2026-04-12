package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PStockInTaskQueryReq extends JsonBasicReq
{
    private levelElm request;

    @Data
    public class levelElm
    {
        private String keyTxt;
        private String pDate;
        private String dtNo;
        private String isPStockIn;
    }

}
