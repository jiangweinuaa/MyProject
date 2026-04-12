package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: 会员体验券核销
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveCouponPay_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String orderNo; // 消费单号，商户唯一订单号
        private String trade_no; // 消费单交易单号，会员系统生成的单号
    }
}
