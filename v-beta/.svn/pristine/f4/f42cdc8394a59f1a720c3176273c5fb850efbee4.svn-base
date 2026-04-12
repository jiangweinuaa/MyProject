package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComGpMsgQuery
 * 服务说明：群发消息查询
 * @author jinzma
 * @since  2024-03-01
 */
@Data
public class DCP_ISVWeComGpMsgQueryReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String keyTxt;
        private String userId;
        private String type;
        private String status;
        private String chatId;
        private String tagId;
        private String beginDate;
        private String endDate;
    }
}
