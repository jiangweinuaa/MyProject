package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * DCP_OrderStatusLogCreate_Open
 * 供CRM调用  同步操作日志
 */
@Data
public class DCP_OrderStatusLogCreate_OpenReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm {
        private String eId;         // 企业编号
        private String orderNo;     // 订单号
        private String opNo;        // 操作人编码
        private String opName;      // 操作人名称
        private String description;      // 操作说明
        private String statusType;      // 订单状态
        private String status;      // 订单状态
    }
}
