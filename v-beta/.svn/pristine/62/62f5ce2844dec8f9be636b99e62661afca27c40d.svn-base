package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_N_OrderParaQuery
 * 服务说明：N_订单参数查询 代码全部继承DCP_OrderParaQueryReq
 * @author jinzma
 * @since  2024-05-23
 */
@Data
public class DCP_N_OrderParaQueryReq extends JsonBasicReq {

    private levelRequest request;

    @Data
    public class levelRequest {
        private String keyTxt;
        private String businessType; //业务类型 0：外卖饿了么服务商门店绑定,其他业务可不传
    }

}
