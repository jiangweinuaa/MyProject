package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DCP_PrintSettingReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PrintSettingReq.Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired
        private String modularNo;
        @JSONFieldRequired
        private String printType;
        @JSONFieldRequired
        private String defualtReportNo;

    }
}
