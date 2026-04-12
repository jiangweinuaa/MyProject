package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 购物车商品修改
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartUpdateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private List<level2Elm> pluList;    // 商品列表
    }

    @Data
    public class level2Elm{
        private String item;        // 项次（查询服务的item）
        private String pluNo;       // 品号
        private String featureNo;   // 特征码
        private String punit;       // 要货单位
        private String baseUnit;    // 基准单位
        private String unitRatio;   // 单位换算率
        private String pqty;        // 要货单位要货量
        private String baseQty;     // 基准单位要货量
        private String price;       // 零售价
        private String distriPrice; // 进货价
        private String selected;    // 选中状态（0未选中，1已选中）
    }
}
