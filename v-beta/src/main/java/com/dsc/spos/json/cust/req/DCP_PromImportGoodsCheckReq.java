package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 促销商品导入检测
 * @author: wangzyc
 * @create: 2021-06-24
 */
@Data
public class DCP_PromImportGoodsCheckReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<level2Elm> goodsList;
        private String codeTypeNo;          // 资料类型0-条码3-编码，特价作业为编码
        private String promCategory;        // 促销类型：
    }

    @Data
    public class level2Elm{
        private String code;                // 资料编码
        private String codeName;            // 资料名称
        private String unitId;              // 商品单位，特价时必须
        private String originalPrice;       // 原价，特价时需传入，大于0
        private String specialPrice;       // 特价，特价时需传入，大于等于0
    }

}
