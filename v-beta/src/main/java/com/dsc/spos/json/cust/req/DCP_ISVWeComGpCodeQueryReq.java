package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComGpCodeQuery
 * 服务说明：社群活码查询
 * @author jinzma
 * @since  2024-02-28
 */
@Data
public class DCP_ISVWeComGpCodeQueryReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private String keyTxt;
        private String shopId;
        private String beginDate;
        private String endDate;
        private String chatId;
    }
}
