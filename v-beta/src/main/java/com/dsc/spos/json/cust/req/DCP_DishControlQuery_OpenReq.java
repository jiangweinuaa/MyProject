package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS菜品流程控制查询
 * @author: wangzyc
 * @create: 2021-09-13
 */
@Data
public class DCP_DishControlQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String keyTxt; // 编码/名称
    }
}
