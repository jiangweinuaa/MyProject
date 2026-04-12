package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约项目查询
 * @author: wangzyc
 * @create: 2021-07-28
 */
@Data
public class DCP_ReserveItemsQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;      // 所属门店
        private String keyTxt;      // 关键词，项目名称+编号模糊查询
        private String isCouponType;// 是否赠送体验券Y/N，空则查全部
        private String [] reserveType; // 支持预约方式 free：免费预约 coupon：用券预约 pay：支付后预约
    }
}
