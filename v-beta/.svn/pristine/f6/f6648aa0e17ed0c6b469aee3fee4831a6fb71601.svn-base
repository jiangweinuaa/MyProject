package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurStockOutQueryReq extends JsonBasicReq {

    private DCP_PurStockOutQueryReq.levelElm request;

    @Data
    public class levelElm {

        private String status;
        private String orgNo;
        @JSONFieldRequired(display = "日期类型")
        private String dateType;
        @JSONFieldRequired(display = "开始日期")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期")
        private String endDate;
        private String keyTxt;
        @JSONFieldRequired(display = "单据类型")
        private String billType;
    }
}
