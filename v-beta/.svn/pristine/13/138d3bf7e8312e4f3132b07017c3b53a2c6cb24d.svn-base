package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DemandToWOQueryReq extends JsonBasicReq {

    @JSONFieldRequired(display = "请求参数")
    private DCP_DemandToWOQueryReq.LevelRequest request;

    @Data
    public class LevelRequest {

//        @JSONFieldRequired(display = "需求类型")
        private String orderType;
        private String keyTxt;

        private String beginDate;
        private String endDate;
        private String prodDepartId;
        @JSONFieldRequired(display = "扣减可用库存")
        private String isDeductStock;
        @JSONFieldRequired
        private List<OrderList> orderList;

    }
    @Data
    public class OrderList {
        @JSONFieldRequired
        private String orderNo;

    }

}
