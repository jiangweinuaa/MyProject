package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComMomentCancel
 * 服务说明：朋友圈任务停发
 * @author jinzma
 * @since  2024-03-06
 */
@Data
public class DCP_ISVWeComMomentCancelReq extends JsonBasicReq {
    private Request request;

    @Data
    public class Request{
        private String momentMsgId;
    }
}
