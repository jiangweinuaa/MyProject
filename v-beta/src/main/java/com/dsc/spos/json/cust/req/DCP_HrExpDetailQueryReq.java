package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/10
 */
@Getter
@Setter
public class DCP_HrExpDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "账套编码")
        private String accountID;
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "成本计算方式")
        private String cost_Calculation;
        @JSONFieldRequired(display = "分摊类型")
        private String allocType;
        private String account;
        private String keyTxt;
    }

}