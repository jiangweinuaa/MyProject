package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_OrderGoodsStatusDeal_Open
 *   說明：订单商品待确认处理
 * 服务说明：订单商品待确认处理
 * @author wangzyc
 * @since  2021-4-28
 */
@Data
public class DCP_OrderGoodsStatusDeal_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String oprType;     // 操作类型：1确认退单提醒 2确认配送时间修改提醒
        private String shopNo;      // 下订门店
        private List<String> orderList;  // 商品列表
    }

}
