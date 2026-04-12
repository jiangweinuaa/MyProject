package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS退单商品待确认处理
 * @author: wangzyc
 * @create: 2021-09-18
 */
@Data
public class DCP_RefundStatusDeal_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String terminalType; // 设备模式，0配菜端 1制作端 2传菜端
        private List<String> orderList; // 订单列表
    }
}
