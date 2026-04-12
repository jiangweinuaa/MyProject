package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 分类下属商品
 * @author: wangzyc
 * @create: 2021-06-16
 */
@Data
public class DCP_CategoryGoodsQueryRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String categoryName;    // 分类名称，取自入参
        private String category;        // 分类编码，取自入参
        private List<level2Elm> goodsList;  // 商品列表
    }

    @Data
    public class level2Elm{
        private String pluType;         // 商品类型
        private String pluNo;           // 商品编码
        private String pluName;         // 商品名称
        private List<level3Elm> pluName_lang;   // 商品名称多语言
        private String sUnit;           // 销售单位编码
        private String sUnitName;       // 销售单位名称
        private String price;           // 销售单位价格
    }

    @Data
    public class level3Elm{
        private String langType;        // 语言类别 zh_CN：中文简体  zh_TW：繁体中文 en_US：英文
        private String name;
    }
}
