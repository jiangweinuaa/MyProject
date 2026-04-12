package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS待生产单据查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_KitchenDishQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private List<String> goodsStatus; // 制作状态 0-待配菜 1-待制作
        private String queryType; // 显示模式，0.待分配 1.待制作(机器人模式)，默认0
        private String categoryQuery; // 末级商品分类
    }
}
