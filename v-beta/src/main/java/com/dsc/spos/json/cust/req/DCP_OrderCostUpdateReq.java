package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_OrderCostUpdate
 * 服务说明：订单超区费，配送费，加急费修改
 * @author jinzma
 * @since  2023-12-06
 */
@Data
public class DCP_OrderCostUpdateReq extends JsonBasicReq {
    private Request request;
    
    @Data
    public class Request {
        private String orderNo;
        private String deliveryMoney;
        private String superZoneMoney;
        private String urgentMoney;
    }
}
