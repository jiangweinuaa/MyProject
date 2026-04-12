package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

@Data
public class DCP_CostExecuteCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private String type;
        private String accountID;
        private String account;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        @JSONFieldRequired
        private String mainTaskId;
        private String inputPrg;
        private String impStateInfo;
        private String memo;

        private List<Executelist> executelist;
    }


    @Data
    public class Executelist {
        @JSONFieldRequired
        private String subtaskId;
        @JSONFieldRequired
        private String mainTaskId;
        @JSONFieldRequired
        private String type;
        @JSONFieldRequired
        private String inputPrg;
        @JSONFieldRequired
        private String impStateInfo;
        private String memo;
        @JSONFieldRequired
        private String item;
    }

}
