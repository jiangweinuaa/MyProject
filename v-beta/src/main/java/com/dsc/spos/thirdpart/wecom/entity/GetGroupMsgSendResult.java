package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：GetGroupMsgSendResult
 * 服务说明：获取企业群发成员执行结果
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96355
 * @author jinzma
 * @since  2024-02-29
 */
@Data
public class GetGroupMsgSendResult {
    private String errcode;
    private String errmsg;
    private String next_cursor;            //分页游标
    private List<Send> send_list;          //群成员发送结果列表

    @Data
    public class Send {
        private String external_userid;    //外部联系人userid，群发消息到企业的客户群不返回该字段
        private String chat_id;            //外部客户群id，群发消息到客户不返回该字段
        private String userid;             //企业服务人员的userid
        private String status;             //发送状态：0-未发送 1-已发送 2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败
        private String send_time;          //发送时间，发送状态为1时返回
    }
}
