package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_ReceivingQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;


    @Getter
    @Setter
    public class Request {
        private String status;
        private String supplierNo;
        @JSONFieldRequired(display = "日期类型")
        private String dateType;
        @JSONFieldRequired(display = "开始日期，格式:YYYYMMDD")
        private String beginDate;
        @JSONFieldRequired(display = "截止日期，格式:YYYYMMDD")
        private String endDate;
        private String keyTxt;
        private String getType;
        @JSONFieldRequired(display = "单据类型")
        private List<String> docType;

    }

}
