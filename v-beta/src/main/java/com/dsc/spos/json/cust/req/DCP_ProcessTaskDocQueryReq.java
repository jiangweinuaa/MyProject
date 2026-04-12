package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProcessTaskDocQueryReq extends JsonBasicReq {

    private DCP_ProcessTaskDocQueryReq.levelElm request;

    @Data
    public class levelElm{
        private String status;
        private String dateType;
        private String beginDate;
        private String endDate;
        private String departId;
        private String keyTxt;
    }
}
