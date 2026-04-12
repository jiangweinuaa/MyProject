package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS基础设置查询
 * @author: wangzyc
 * @create: 2021-09-18
 */
@Data
public class DCP_KdsBaseSetQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
    }
}
