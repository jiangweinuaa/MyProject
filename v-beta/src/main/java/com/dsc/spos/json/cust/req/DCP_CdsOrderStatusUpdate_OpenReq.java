package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: CDS叫号/取餐
 * @author: wangzyc
 * @create: 2021-10-09
 */
@Data
public class DCP_CdsOrderStatusUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;
        private String machineId; // 机台编号
        private String handleType; // 操作类型 0.叫号 1.取餐/传菜 2.全部取餐
        private String businessType; // 点单类型：0-触屏先结 2-触屏后结桌台管理 3-触屏先结桌台管理
        private String isOrder; // 是否外卖订单
        private String billNo; // 单号
        private List<level2Elm> goodsList;
    }

    @Data
    public class level2Elm{
        private String billNo; // 来源单号
        private String pluNo; // 商品编号
        private String oItem; // 来源项次
        private String pluBarCode; // 商品编码
    }
}
