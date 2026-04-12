package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liyyd
 * @date 2025/02/21
 */
@Getter
@Setter
public class DCP_FeeEnableReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "费用编号")
        private String fee;
        @JSONFieldRequired(display = "操作类型 1-启用 2-禁用")
        private String oprType;
    }

}