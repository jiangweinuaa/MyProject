package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

import java.util.List;

/**
 * 服务函数：UserBehaviorData
 * 服务说明：获取「联系客户统计」数据
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96358
 * @author jinzma
 * @since  2024-02-23
 */
@Data
public class UserBehaviorData {
    private String errcode;
    private String errmsg;
    private List<Behavior> behavior_data;

    @Data
    public class Behavior{
        private String stat_time;
        private String chat_cnt;
        private String message_cnt;
        private String reply_percentage;
        private String avg_reply_time;
        private String negative_feedback_cnt;
        private String new_apply_cnt;
        private String new_contact_cnt;
    }
}
