package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/03
 */
@Getter
@Setter
public class DCP_CostLevelCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "截止成本阶")
        private double end_CostLevel;
        @JSONFieldRequired(display = "起始成本阶")
        private double start_CostLevel;
        @JSONFieldRequired(display = "成本分群编码")
        private String costGroupingId;
        @JSONFieldRequired(display = "成本分群说明")
        private String costGroupingId_Name;
        @JSONFieldRequired(display = "")
        private String status;
    }

    @Getter
    @Setter
    public class Datas {
        @JSONFieldRequired(display = "")
        private boolean typecnfflg;
        @JSONFieldRequired(display = "成本分群说明")
        private String costGroupingId_Name;
        @JSONFieldRequired(display = "语言别")
        private String langType;
        @JSONFieldRequired(display = "状态")
        private String status;
    }

}