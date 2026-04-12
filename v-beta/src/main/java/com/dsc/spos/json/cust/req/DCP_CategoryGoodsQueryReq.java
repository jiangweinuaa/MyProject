package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 分类下属商品
 * @author: wangzyc
 * @create: 2021-06-16
 */
@Data
public class DCP_CategoryGoodsQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<level2Elm> categoryList;   // 商品分类列表
    }

    @Data
    public class level2Elm{
        private String category;                // 分类编码
        private String categoryName;            // 分类名称

    }

}
