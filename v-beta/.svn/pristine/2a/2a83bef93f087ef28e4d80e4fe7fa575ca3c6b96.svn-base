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
public class DCP_CostDomainEnableReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "成本域编号")
        private String costDomainId;
        @JSONFieldRequired(display = "法人编码")
        private String corp;
        @JSONFieldRequired(display = "操作类型 1-启用 2-禁用")
        private String oprType;
    }

}