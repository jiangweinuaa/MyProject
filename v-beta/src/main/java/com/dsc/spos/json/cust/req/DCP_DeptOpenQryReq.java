package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DeptOpenQryReq extends JsonBasicReq {

    private DCP_DeptOpenQryReq.levelElm request;

    @Data
    public class levelElm{
        private String status;
        private String keyTxt;
        private String orgNo;
        private String isCostAlloc;
    }

}
