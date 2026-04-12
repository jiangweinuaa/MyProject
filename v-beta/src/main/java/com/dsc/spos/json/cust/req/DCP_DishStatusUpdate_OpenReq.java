package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS菜品状态变更
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_DishStatusUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class  level1Elm{

        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String terminalType; // 机台类型  0-配菜端 1-制作端 2-传菜端
        private String userId; // 用户编号
        private String goodsStatus; // 制作状态 0-待配菜 1-待制作 2-待传菜/取餐 3-已完成
        private String updateStatus; // 变更状态0-待配菜1-待制作2-待传菜/取餐3-已完成
        private List<level2Elm> goodsList; // 商品列表
        private String cookId;//机器人
    }

    @Data
    public class level2Elm
    {
        private String processTaskNo; // 加工任务单号
        private String billNo; // 来源单号
        private String qty; // 数量
        private String pluNo; // 商品编码
        private String [] itemList; // 项次组
        private String oItem; // 来源项次
        private String pluBarCode; // 商品条码
    }
}
