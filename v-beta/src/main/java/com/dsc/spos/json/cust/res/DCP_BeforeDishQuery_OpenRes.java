package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS预制菜品查询
 * @author: wangzyc
 * @create: 2021-10-15
 */
@Data
public class DCP_BeforeDishQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String categoryId; // 商品分类编码
        private String categoryName; // 商品分类名称
        private List<level2Elm> goodsList; // 商品列表
    }

    @Data
    public class level2Elm {
        private String categoryId; // 商品分类编码
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarCode; // 商品条码
        private String remainQty; // 剩余数量
        private String distriPrice; // 进货价
        private String price; // 零售价
        private String baseUnit; // 基准单位
        private String baseQty; // 基准数量
        private String unitRatio; // 单位转化率
        private String featureNo; // 特征码
        private String featureName; // 特征码名称
        private String unitId; // 销售单位编码
        private String unitName; //销售单位名称
    }
}
