package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 末级商品分类查询
 * @author: wangzyc
 * @create: 2021-06-16
 */
@Data
public class DCP_FinalCategoryQueryRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String categoryName;    // 分类名称
        private String category;        // 分类编码
        private List<level2Elm> categoryName_lang;  // 分类名称多语言别
    }

    @Data
    public class level2Elm{
        private String langType;    // 语言别
        private String name;        // 分类名称
    }
}
