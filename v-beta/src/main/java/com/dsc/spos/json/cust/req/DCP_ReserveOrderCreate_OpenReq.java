package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约单创建
 * @author: wangzyc
 * @create: 2021-08-02
 */
@Data
public class DCP_ReserveOrderCreate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;  // 所属门店
        private String itemsNo; // 项目编号
        private String shopDistribution; // 是否到店分配Y/N，默认为N
        private String opNo; // 顾问编号
        private String couponCode; // 券号，POS必传，手机端使用时必传
        private String memberId; // 预约人，会员ID
        private String name; // 会员名称，若无则用微信昵称
        private String mobile; // 手机号
        private String date; // 预约日期
        private String time; // 预约时间段
        private String memo; // 捎话（备注）
        private String createOpId; // 创建人ID，若为商城渠道传openId
        private String createOpName; // 创建人姓名，会员名称若无则用微信昵称
    }
}
