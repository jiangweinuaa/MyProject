package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/10
 */
@Getter
@Setter
public class DCP_CustomerPriceAdjustDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "定价申请单号")
        private String billNo;
    }

}