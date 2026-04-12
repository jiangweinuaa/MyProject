package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/04
 */
@Getter
@Setter
public class DCP_CostDomainDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "成本域编码")
        private String costDomainId;
        @JSONFieldRequired(display = "法人编码")
        private String corp;
    }

}