package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGroupChatDetail_Open
 * 服务说明：查询企微客户群详情_外部
 * @author jinzma
 * @since  2024-03-18
 */
@Data
public class DCP_ISVWeComGroupChatDetail_OpenRes extends JsonRes {
    private Datas datas;

    @Data
    public class Datas {
        private String chatId;
        private String name;
        private String status;
        private String userId;
        private String userName;
        private String memberCnt;
        private String inCnt;
        private String outCnt;
        private String notice;
        private String memberNum;
        private String nonMemberNum;
        private String memberRatio;
        private String nonMemberRatio;
        private String staffNum;
        private String customerNum;
        private String staffRatio;
        private String customerRatio;
        private String createTime;
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
