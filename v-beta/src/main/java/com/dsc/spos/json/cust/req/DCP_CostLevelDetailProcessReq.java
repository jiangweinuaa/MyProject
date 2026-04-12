package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/28
 */
@Getter
@Setter
public class DCP_CostLevelDetailProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "法人组织")
        private String corp;
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "账套编码")
        private String accountID;
        @JSONFieldRequired(display = "年")
        private String year;
        @JSONFieldRequired(display = "成本计算方式")
        private String cost_Calculation;
        @JSONFieldRequired(display = "法人名称")
        private String corp_Name;
        @JSONFieldRequired(display = "账套名称")
        private String account;
        @JSONFieldRequired(display = "")
        private String status;
    }

}