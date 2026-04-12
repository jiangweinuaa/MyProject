package com.dsc.spos.json.cust.req;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ExpSheetStatusUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;


    @Data
    public static class Request {
        @JSONFieldRequired
        private String doc_Type;
        @JSONFieldRequired
        private String bfeeNo;
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }
}
