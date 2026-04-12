package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComMomentData
 * 服务说明：朋友圈任务数据查询
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class DCP_ISVWeComMomentDataReq extends JsonBasicReq {
    private Request request;

    @Data
    public class Request{
        private String momentMsgId;
    }
}
