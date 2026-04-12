package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_OrderStatusUpdate_Open
 *   說明：订单状态变更
 * 服务说明：订单状态变更
 * @author wangzyc
 * @since  2021-5-10
 */
@Data
public class DCP_OrderStatusUpdate_OpenReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm {
        private String eId;         // 企业编号
        private String shopId;      // 门店编号
        private String opNo;        // 操作人编码
        private String opName;      // 操作人名称
        private String orderNo;     // 订单号
        private String loadDocType; // 来源类型
        private String status;      // 订单状态
        private String productStatus;// 生产状态
        private String deliveryStatus;  // 物流状态
        private String delId;       // 配送员 ID
        private String delName;     // 配送员 名称
        private String delTelephone;// 配送员 电话
        private String deliveryType;// 物流类型
        private String deliveryNo;  // 物流单号
    }
}
