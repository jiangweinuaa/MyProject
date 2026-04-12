package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS机器人新增
 * @author: wangzyc
 * @create: 2021-09-13
 */
@Data
public class DCP_KdsCookCreate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String sortId; // 顺序
        private String cookId; // 机器人编号
        private String cookName; // 机器人名称
        private String status; // 启用状态Y/N
    }
}
