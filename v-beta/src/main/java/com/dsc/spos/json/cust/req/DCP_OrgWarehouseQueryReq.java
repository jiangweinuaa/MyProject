package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_OrgWarehouseQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        private String keyTxt;
        private List<OrgList> orgList;
        private String getType;

    }

    @NoArgsConstructor
    @Data
    public class OrgList {
        private String orgNo;
    }
}
