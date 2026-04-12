package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_StockAdjustProcess
 * 服务说明：库存调整单确认
 * @author jinzma
 * @since  2024-08-08
 */
@Data
public class DCP_StockAdjustProcessReq extends JsonBasicReq {
    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        @JSONFieldRequired
        private String adjustNo;
        @JSONFieldRequired
        private String oprType;
        private String accountDate;
    }
}
