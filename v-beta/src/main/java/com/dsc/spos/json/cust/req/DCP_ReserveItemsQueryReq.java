package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约项目查询
 * @author: wangzyc
 * @create: 2021-07-21
 */
@Data
public class DCP_ReserveItemsQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;
        private String keyTxt;
        private String status;
    }
}
