package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_DeliveryManQuery
 *    說明：配送员查询
 * 服务说明：配送员查询
 * @author wangzyc
 * @since  2021/4/23
 */
@Data
public class DCP_DeliveryManQueryReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String keyTxt;  // 配送员编号/名称/手机号
        private String  org;    // 所属组织
        private String status;  // 状态
    }
}
