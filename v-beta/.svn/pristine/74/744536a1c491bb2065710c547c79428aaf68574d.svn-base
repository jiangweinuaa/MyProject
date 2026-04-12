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
public class DCP_HrExpStatQueryReq extends JsonBasicReq {

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
        private String cost_Calculation;
        private String allocType;
        private String account;
        private String keyTxt;
    }

}