package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_DeliveryLoginStatusQuery_Open
 *    說明：查询配送员登录状态
 * 服务说明 查询配送员登录状态
 * @author wangzyc
 * @since  2021/5/19
 */
@Data
public class DCP_DeliveryLoginStatusQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String openId;  // 微信用户Id
    }
}
