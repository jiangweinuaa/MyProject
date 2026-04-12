package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurOrderQueryReq extends JsonBasicReq {

    private DCP_PurOrderQueryReq.levelElm request;

    @Data
    public class levelElm {

        private String status;
        private String purOrderNo;
        private String supplier;
        private String beginDate;
        private String endDate;
        private String receiveStatus;
        private String purType;
        private String isCheck_restrictGroup;
        private String restrictGroupType;
    }
}
