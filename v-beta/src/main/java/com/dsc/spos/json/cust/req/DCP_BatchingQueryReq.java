package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_BatchingQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "开始日期(YYYYMMDD)")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期(YYYYMMDD)")
        private String endDate;
        private String keyTxt;

        @JSONFieldRequired
        private String dateType;
        private String status;
        private String departId;
    }

}
