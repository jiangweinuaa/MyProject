package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

/**
 * 服务函数：GetMomentTaskResult
 * 服务说明：获取客户朋友圈任务创建结果
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96351
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class GetMomentTaskResult {
    private String errcode;
    private String errmsg;
    private String status;      //任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成
    private String type;        //操作类型，字节串，此处固定为add_moment_task
    private Result result;      //详细的处理结果。当任务完成后此字段有效

    @Data
    public class Result {
        private String errcode;
        private String errmsg;
        private String moment_id;  //朋友圈id，可通过获取客户朋友圈企业发表的列表接口获取朋友圈企业发表的列表
        private InvalidSender invalid_sender_list;  //不合法的执行者列表，包括不存在的id以及不在应用可见范围内的部门或者成员
        private InvalidExternalContact invalid_external_contact_list;
    }

    @Data
    public class InvalidSender {
        private String[] user_list;
        private Integer[] department_list;
    }

    @Data
    public class InvalidExternalContact {
        private String[] tag_list;
    }
}
