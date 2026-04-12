package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 购物车商品修改
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartUpdateRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm {
        private List<level2Elm> pluList;    // 商品列表
        private String totCqty;             // 总品种合计（选中商品的）
        private String totDistriAmt;        // 总进货金额合计（选中商品的）
    }

    @Data
    public class level2Elm{
        private String item;                // 项次
        private String pqty;                // 要货单位数量
        private String baseQty;             // 基准单位数量
        private String selected;            // 选中状态（0未选中，1已选中）
    }
}
