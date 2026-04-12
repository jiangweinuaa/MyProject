package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgQuery
 * 服务说明：个人欢迎语查询
 * @author jinzma
 * @since  2024-02-20
 */
@Data
public class DCP_ISVWeComWelcomeMsgQueryReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String keyTxt;
        private String userId;
        private String beginDate;
        private String endDate;
    }
}
