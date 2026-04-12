package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ReserveTimeQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId;  // 所属门店
        private String status;  // 状态
    }
}
