package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/21
 */
@Getter
@Setter
public class DCP_IniInvCostOpnDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "账套编号 ")
        private String accountID;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "类型")
        private String type;
        @JSONFieldRequired(display = "账套")
        private String account;
        private String item;
    }

}