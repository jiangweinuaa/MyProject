package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/20
 */
@Getter
@Setter
public class DCP_CostAllocDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "帐套编码")
        private String accountID;
        private String allocSource;
        @JSONFieldRequired(display = "期别")
        private String period;
//        @JSONFieldRequired(display = " 项次")
        private String item;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "分摊类型")
        private String allocType;
    }

}