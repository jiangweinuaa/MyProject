package com.dsc.spos.json.cust.req;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_ShopSettBillDelectReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String reconNo;
        private String eId;
        private String shopId;
    }
}
