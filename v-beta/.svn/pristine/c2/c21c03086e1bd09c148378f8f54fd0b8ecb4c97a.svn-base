package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_DeliveryPasswordUpdate
 *    說明：配送员重置密码
 * 服务说明：配送员重置密码
 * @author wangzyc
 * @since  2021/4/23
 */
@Data
public class DCP_DeliveryPasswordUpdateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String opNo;    // 配送员编号
        private String newPassword;    // 新密码
    }
}
