package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/29
 */
@Getter
@Setter
public class DCP_CustomerPOrderDetailReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        private String eId;
        @JSONFieldRequired(display = "订单号")
        private String pOrderNo;
        private String searchScope;
        private String shopId;
    }

}