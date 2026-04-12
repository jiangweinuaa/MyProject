package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS退菜单据查询
 * @author: wangzyc
 * @create: 2021-10-11
 */
@Data
public class DCP_RefundDishQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String terminalType; // 设备模式，0配菜端 1制作端 2传菜端
        private String machineId; // 机台编号
    }
}
