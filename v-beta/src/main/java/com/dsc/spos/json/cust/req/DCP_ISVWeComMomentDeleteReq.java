package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMomentDelete
 * 服务说明：朋友圈任务删除
 * @author jinzma
 * @since  2024-03-06
 */
@Data
public class DCP_ISVWeComMomentDeleteReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
       private List<MomentMsg> momentMsgList;
    }
    @Data
    public class MomentMsg {
        private String momentMsgId;
    }
}
