package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;
/**
 * 服务函数：GroupChatList
 * 服务说明：获取客户群详情
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96338
 * @author jinzma
 * @since  2024-01-29
 */
@Data
public class GroupChatDetail {

    private String errcode;
    private String errmsg;
    private Group_chat group_chat;

    @Data
    public class Group_chat{
        private String chat_id;
        private String name;
        private String owner;
        private String create_time;
        private String notice;
        private List<Member> member_list;
        private List<Admin> admin_list;
        private String member_version;
    }
    @Data
    public class Member{
        private String userid;
        private String type;
        private String join_time;
        private String join_scene;
        private String unionid;
        private Invitor invitor;
        private String group_nickname;
        private String name;
    }
    @Data
    public class Invitor{
        private String userid;
    }
    @Data
    public class Admin{
        private String userid;
    }



}
