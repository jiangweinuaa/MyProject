package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComJsApiTicketQuery
 * 服务说明：查询 jsapi_ticket
 * @author jinzma
 * @since  2024-03-18
 */
@Data
public class DCP_ISVWeComJsApiTicketQueryRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas {
        private String jsApiTicket;
        private String agentId;
    }
}
