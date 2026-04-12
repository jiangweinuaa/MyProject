package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 促销商品导入检测
 * @author: wangzyc
 * @create: 2021-06-24
 */
@Data
public class DCP_PromImportGoodsCheckRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm {
        private List<level2Elm> rightGoodsList;     // 通过检测的商品列表
        private List<level3Elm> errorGoodsList;     //
    }

    @Data
    public class level2Elm{
        private String code;                          // 资料编码
        private String codeName;                      // 资料名称
        private String unitId;                        // 单位编码
        private String unitName;                      // 单位名称
        private String originalPrice;                 // 原价，大于0
        private String specialPrice;                  // 特价，大于等于0
    }

    @Data
    public class level3Elm{
        private String code;                          // 资料编码
        private String codeName;                      // 资料名称
        private String unitId;                        // 单位编码
        private String unitName;                      // 单位名称
        private String originalPrice;                 // 原价，大于0
        private String specialPrice;                  // 特价，大于等于0
        private String errorDesc;                     // 错误描述：条码/编码不存在，名称为空，价格不规范等
    }
}
