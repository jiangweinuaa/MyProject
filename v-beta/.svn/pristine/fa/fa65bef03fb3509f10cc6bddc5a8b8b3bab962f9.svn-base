package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS机器人查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_KdsCookQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号，不传则查门店所有机器人 前期先不传
    }
}
