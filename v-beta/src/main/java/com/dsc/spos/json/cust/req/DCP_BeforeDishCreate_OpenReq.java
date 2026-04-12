package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS预制菜品创建
 * @author: wangzyc
 * @create: 2021-10-08
 */
@Data
public class DCP_BeforeDishCreate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId; // 门店编号
        private String userId; // 员工编号
        private String userName; // 员工名称
        private List<level2Elm> goodsList; //
    }

    @Data
    public class level2Elm {
        private String categoryId; // 分类编码
        private String pluNo; // 商品编码
        private String pluBarCode; // 商品条码
        private String unitId; // 单位编码
        private String qty; // 制作数量
    }
}
