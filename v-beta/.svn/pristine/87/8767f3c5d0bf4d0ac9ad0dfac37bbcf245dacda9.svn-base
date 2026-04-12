package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_OrderGoodsStatusUpdate_Open
 *   說明：订单商品状态修改
 * 服务说明：订单商品状态修改
 * @author wangzyc
 * @since  2021-4-20
 */
@Data
public class DCP_OrderGoodsStatusUpdate_OpenReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String goodsStatus;         // 操作商品状态 0.待制作 1.制作中 2.已完成 默认已制作
        private String opNo;                // 员工编号
        private String shopNo;              // 下订门店
        private List<level2Elm> goodsList;  // 商品列表
        private String stallId;             // 档口编号
    }

    @Data
    public class level2Elm{
        private String orderNo;             // 订单号
        private String oItem;               // 来源项次
        private String item;                // 项次
        private String pluNo;               // 商品编码
        private String pluBarcode;          // 商品条码
        private String qty;                 // 数量
    }
}
