package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComCustomTagUpdate
 * 服务说明：批量企微客户打标签
 * @author jinzma
 * @since  2024-01-25
 */
@Data
public class DCP_ISVWeComCustomTagUpdateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String type;         //add添加 remove删除
        private List<ExternalUserId> externalUserIdList;
        private List<Tag> tagList;
        private List<Chat> chatIdList;
    }
    @Data
    public class ExternalUserId {
        private String externalUserId;
    }
    @Data
    public class Tag {
        private String tagId;
    }
    @Data
    public class Chat {
        private String chatId;
    }
}
