package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArBillDeleteReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        @JSONFieldRequired
        private String accountId;
        private String taskId;
        @JSONFieldRequired
        private String arNo;
        private List<ItemList> itemList;
        private String bizPartnerNo;
    }

    @NoArgsConstructor
    @Data
    public class ItemList {
        private String item;
    }
}

