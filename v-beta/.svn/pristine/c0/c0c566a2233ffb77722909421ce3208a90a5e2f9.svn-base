package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApBillDeleteReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_ApBillDeleteReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String apNo;
        private List<String> itemList;

    }

}
