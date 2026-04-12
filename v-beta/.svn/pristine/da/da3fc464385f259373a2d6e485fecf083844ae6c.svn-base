package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: 购物车商品删除
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartDeleteRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String totCqty;     // 总品种合计（选中商品的）
        private String totDistriAmt;// 总进货金额合计（选中商品的）
    }
}
