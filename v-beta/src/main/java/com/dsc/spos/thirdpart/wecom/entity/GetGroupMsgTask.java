package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：GetGroupMsgTask
 * 服务说明：获取群发成员发送任务列表
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96355
 * @author jinzma
 * @since  2024-02-29
 */
@Data
public class GetGroupMsgTask {
    private String errcode;
    private String errmsg;
    private String next_cursor;     //分页游标
    private List<Task> task_list;   //群发成员发送任务列表
    @Data
    public class Task {
        private String userid;     //企业服务人员的userid
        private String status;     //发送状态：0-未发送 2-已发送
        private String send_time;  //发送时间，未发送时不返回
    }
}
