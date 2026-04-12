package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DCP_TopupSetUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String topupOrg;

        private List<SetList> setList;
    }

    @NoArgsConstructor
    @Data
    public class SetList {
        private String topupProdID;
        private String topupPayType;
        private String consPayType;
        private String consProdID;
    }

}
