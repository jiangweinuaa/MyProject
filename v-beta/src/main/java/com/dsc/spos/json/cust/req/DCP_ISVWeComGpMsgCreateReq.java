package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpMsgCreate
 * 服务说明：群发消息新增
 * @author jinzma
 * @since  2024-03-01
 */
@Data
public class DCP_ISVWeComGpMsgCreateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String name;
        private String msg;
        private String remark;
        private String type;            //类型: 1指定客户 2指定客户群
        private String sendType;        //类型: 0所有客户 1指定客户
        private List<Tag> tagList;
        private List<User> userList;
        private List<Chat> chatList;
        private List<Annex> annexList;
    }

    @Data
    public class User {
        private String userId;
    }
    @Data
    public class Chat{
        private String chatId;
    }
    @Data
    public class Tag{
        private String item;
        private String tagId;
    }
    @Data
    public class Annex{
        private String msgType;
        private String msgId;
    }
}
