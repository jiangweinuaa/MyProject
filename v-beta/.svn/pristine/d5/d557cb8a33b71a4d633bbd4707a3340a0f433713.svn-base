package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PurInReconDetailDeleterReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String purInvNo;
        @JSONFieldRequired
        private List<ItemList> itemList;
        private String corp;
    }

    @NoArgsConstructor
    @Data
    public class ItemList {
        @JSONFieldRequired
        private String item;
    }

}
