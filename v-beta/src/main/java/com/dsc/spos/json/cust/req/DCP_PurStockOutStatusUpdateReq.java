package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DCP_PurStockOutStatusUpdateReq extends JsonBasicReq {

    @Getter
    @Setter
    @JSONFieldRequired
    private DCP_PurStockOutStatusUpdateReq.Request request;

    @Data
    public class Request{

        @JSONFieldRequired(display = "退货出库单号")
        private String pStockOutNo;
        @JSONFieldRequired(display = "单据类型")
        private String billType;
        @JSONFieldRequired(display = "操作类型")
        private String opType;
        private String accountDate;
    }

}
