package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS配菜/制作完成单据查询
 * @author: wangzyc
 * @create: 2021-09-27
 */
@Data
public class DCP_KdsDishQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId; // 门店编号
        private String terminalType; // 机台类型 0-配菜端 1-制作端
        private String machineId; // 机台编号
        private String categoryId; // 分类编码
    }
}
