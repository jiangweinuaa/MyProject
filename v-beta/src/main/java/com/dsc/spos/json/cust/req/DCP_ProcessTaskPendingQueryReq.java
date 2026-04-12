package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProcessTaskPendingQueryReq extends JsonBasicReq {

    private DCP_ProcessTaskPendingQueryReq.levelElm request;

    @Data
    public class levelElm{
        private String opType;
        private String prodType;
        private String dateType;
        private String beginDate;
        private String endDate;
        private String departId;
        private String pluNo;
    }
}
