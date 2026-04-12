package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 客显设置启用禁用
 * @author: wangzyc
 * @create: 2022-02-07
 */
@Data
public class DCP_DualPlayEnableReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String oprType; // 操作类型：1-启用 2-禁用
        private String dualPlayID; // 客显ID
    }
}
