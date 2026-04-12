package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 会员体验券查询
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveCouponQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String memberId; // 会员号，手机端必传
        private String shopId; // 门店编码，POS必传
        private String status; // 券状态 1=已使用 2=未使用(已开始-未过期) 9=已失效(已过期)
        private String couponCode; // 券号
        private String keyTxt; // 关键词，会员手机号+ID+名称
    }
}
