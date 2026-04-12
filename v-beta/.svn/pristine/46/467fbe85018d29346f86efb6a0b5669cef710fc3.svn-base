package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGroupChatDetail
 * 服务说明：查询企微客户群详情
 * @author jinzma
 * @since  2024-01-15
 */
@Data
public class DCP_ISVWeComGroupChatDetailRes extends JsonRes {

    private Datas datas;

    @Data
    public class Datas {
        private String chatId;
        private String name;
        private String status;
        private String userId;
        private String userName;
        private String memberCnt;
        private String createTime;
        private String notice;

        private List<Admin> adminList;
        private List<Member> memberList;
    }
    @Data
    public class Admin {
        private String userId;
        private String userName;
    }
    @Data
    public class Member {
        private String userId;
        private String userName;
        private String groupNickName;
        private String userType;
        private String joinScene;
        private String joinTime;
    }
}
