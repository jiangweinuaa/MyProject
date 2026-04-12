package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 会员体验券核销撤销
 * @author: wangzyc
 * @create: 2022-01-07
 */
@Data
public class DCP_ReserveCouponPayReverse_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String memberId; // 会员号
        private String orderNo; // 消费单号商户唯一单号
        private String trade_no; // 消费单交易号 会员系统生成的单号
        private String reserveNo; // 预约单号
    }

}
