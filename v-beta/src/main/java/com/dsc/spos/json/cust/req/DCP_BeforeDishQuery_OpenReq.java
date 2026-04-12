package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: KDS预制菜品查询
 * @author: wangzyc
 * @create: 2021-10-15
 */
@Data
public class DCP_BeforeDishQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String terminalType; // 机台类型 0-配菜端 1-制作端 2-传菜端
        private String isStock; // 是否仅查询剩余可用数量 Y查大于0的  N查等于0的 不传查所有的
        private String category; //末级类别编码
        private String keyTxt; //商品名称或编码查询


    }
}
