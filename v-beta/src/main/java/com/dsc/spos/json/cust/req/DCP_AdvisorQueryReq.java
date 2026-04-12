package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 顾问查询
 * @author: wangzyc
 * @create: 2021-07-19
 */
@Data
public class DCP_AdvisorQueryReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String keyTxt;
        private String shopId;
        private String status;
    }
}
