package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 会员体验券核销
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveCouponPay_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId; // 门店编号
        private String memberId; // 会员号
        private String couponCode; // 券号
        private String couponType; // 券类型
        private String quantity; // 使用张数，默认1
        private String reserveNo; // 预约单号
        private String opNo; // 项目老师编号
    }
}
