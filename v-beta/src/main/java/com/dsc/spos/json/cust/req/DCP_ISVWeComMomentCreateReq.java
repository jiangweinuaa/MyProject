package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMomentCreate
 * 服务说明：朋友圈任务创建
 * @author jinzma
 * @since  2024-03-06
 */
@Data
public class DCP_ISVWeComMomentCreateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String name;
        private String type;
        private String remark;
        private String msg;

        private List<Tag> tagList;
        private List<User> userList;
        private List<Annex> annexList;
    }
    @Data
    public class Tag {
        private String tagId;
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
