package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;


@Data
public class DCP_ExpenseQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;


    @Data
    public static class Request {

        private String bdate;
        private String beginDate;
        private String endDate;
        private String doc_Type;
        private String corp;
        private String corpName;
        private String supplierNo;
        private String supplierName;
        private String status;
    }
}
