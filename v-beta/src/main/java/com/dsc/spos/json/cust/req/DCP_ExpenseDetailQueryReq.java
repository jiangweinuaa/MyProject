package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;


@Data
public class DCP_ExpenseDetailQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;


    @Data
    public static class Request {
        private String bdate;
        private String doc_Type;
        @JSONFieldRequired(display = "法人")
        private String corp;
        private String corpName;
        @JSONFieldRequired(display = "结算对象")
        private String supplierNo;
        private String supplierName;
        @JSONFieldRequired
        private String bfeeNo;
        private String status;
    }
}
