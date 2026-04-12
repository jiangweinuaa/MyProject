package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 订单假删除（C端隐藏）
 * @author: wangzyc
 * @create: 2021-07-16
 */
@Data
public class DCP_OrderDeleteUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String orderNo;
        private String loadDocType;
    }
}
