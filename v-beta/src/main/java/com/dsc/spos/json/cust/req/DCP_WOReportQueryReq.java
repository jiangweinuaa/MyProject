package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WOReportQueryReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_WOReportQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String dateType;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String keyTxt;
        private String status;
        private String departId;

    }
}
