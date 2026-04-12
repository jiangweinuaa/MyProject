package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_DeliveryBind_Open
 *    說明：配送员绑定
 * 服务说明配送员绑定
 * @author wangzyc
 * @since  2021/5/19
 */
@Data
public class DCP_DeliveryBind_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String opNo;          // 配送员编号
        private String openId;        // 微信ID
        private String shopId;        // 最近一次登录门店
    }
}
