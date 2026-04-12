package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ExpenseDeleteReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String doc_Type;
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String supplierNo;
        private String supplierName;
        @JSONFieldRequired
        private String bfeeNo;
        private String item;
        @JSONFieldRequired(display = "删除条件,1：结算法人+单据编号 2：结算法人+单据编号+项次")
        private String delete_Type;
        private String status;
    }
}
