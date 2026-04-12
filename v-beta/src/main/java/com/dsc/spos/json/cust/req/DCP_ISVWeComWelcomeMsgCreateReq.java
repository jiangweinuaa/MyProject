package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgCreate
 * 服务说明：个人欢迎语创建
 * @author jinzma
 * @since  2024-02-20
 */
@Data
public class DCP_ISVWeComWelcomeMsgCreateReq extends JsonBasicReq {
    private Request request;

    @Data
    public class Request{
        private String name;
        private String remark;
        private String msg;
        private String restrictStaff;
        private List<User> userList;
        private List<Annex> annexList;
    }
    @Data
    public class User {
        private String userId;
    }
    @Data
    public class Annex {
        private String msgType;
        private String msgId;
    }

}
