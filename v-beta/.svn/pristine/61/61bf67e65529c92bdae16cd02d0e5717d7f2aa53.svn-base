package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：GetMomentTask
 * 服务说明：获取客户朋友圈企业发表的列表
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96352
 * @author jinzma
 * @since  2024-03-05
 */
@Data
public class GetMomentTask {
    private String errcode;
    private String errmsg;
    private String next_cursor;
    private List<Task> task_list;       //发表任务列表

    @Data
    public class Task {
        private String userid;          //发表成员用户userid
        private String publish_status;  //成员发表状态。0:未发表 1：已发表
    }

}
