package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgDelete
 * 服务说明：个人欢迎语删除
 * @author jinzma
 * @since  2024-02-20
 */
@Data
public class DCP_ISVWeComWelcomeMsgDeleteReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request {
        List<WelMsg> welMsgList;
    }
    @Data
    public class WelMsg {
        private String welMsgId;
    }
}
