package com.dsc.spos.thirdpart.duandian;

import lombok.Data;

@Data
public class marketingVirgoChangeReq {
    /**
     * 手机号(手机号和渠道会员编码2选一，手机号优先)
     */
    private String phone;
    /**
     * 渠道会员编码(手机号和渠道会员编码2选一，手机号优先)
     */
    private String memberNo;
    /**
     * 渠道类型
     */
    private String channelType = "POS";
    /**
     * 业务单据(全局唯一)
     */
    private String bizNo;
    /**
     * 调整类型(枚举 增加：UP、扣减：DOWN)
     */
    private String changeType;
    /**
     * 积分过期时间
     */
    private String expireTime;
    /**
     * 事件类型(枚举，默认营促销活动：PROMOTION)
     */
    private String eventType = "PROMOTION";
    /**
     * 调整积分值(积分增加或者扣减的值)
     */
    private double changePoint;
    /**
     * 变更原因
     */
    private String reason;
}

