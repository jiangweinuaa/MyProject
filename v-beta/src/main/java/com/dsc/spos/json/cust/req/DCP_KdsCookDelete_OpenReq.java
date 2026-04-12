package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 机器人删除
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_KdsCookDelete_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String cookId; // 机器人编号
    }
}
