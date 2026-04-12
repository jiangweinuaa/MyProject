package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：GroupChatList
 * 服务说明：获取客户群列表
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96337
 * @author jinzma
 * @since  2024-01-29
 */
@Data
public class GroupChatList {
    private String errcode;
    private String errmsg;
    private String next_cursor;
    private List<Group_chat> group_chat_list;

    @Data
    public class Group_chat{
        private String chat_id;
        private String status;
    }

}
