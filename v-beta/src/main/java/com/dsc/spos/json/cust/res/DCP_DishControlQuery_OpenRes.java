package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS菜品流程控制查询
 * @author: wangzyc
 * @create: 2021-09-13
 */
@Data
public class DCP_DishControlQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String categoryId; // 商品分类编码
        private String categoryName; // 商品分类名称
        private List<level2Elm> goodsList; //
    }

    @Data
    public class level2Elm{
        private String categoryId; // 商品分类编码
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String unSide; // 商免配菜Y/N
        private String unCook; // 免制作Y/N
        private String unCall; // 免传菜Y/N
    }


}
