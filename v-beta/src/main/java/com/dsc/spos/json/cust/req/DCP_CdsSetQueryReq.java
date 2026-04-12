package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: CDS叫号屏设置查询
 * @author: wangzyc
 * @create: 2022-05-25
 */
@Data
public class DCP_CdsSetQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{

    }
}
