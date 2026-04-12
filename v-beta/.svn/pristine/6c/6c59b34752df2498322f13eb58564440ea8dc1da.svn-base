package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpMsgDelete
 * 服务说明：群发消息删除（批量）
 * @author jinzma
 * @since  2024-03-04
 */
@Data
public class DCP_ISVWeComGpMsgDeleteReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private List<GpMsg> gpMsgList;
    }
    @Data
    public class GpMsg {
        private String gpMsgId;
    }
}
