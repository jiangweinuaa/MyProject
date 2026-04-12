package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS退菜单据查询
 * @author: wangzyc
 * @create: 2021-10-11
 */
@Data
public class DCP_RefundDishQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String billNo; // 来源单号
        private String oItem; // 来源项次
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarcode; // 商品条码
        private String qty; // 数量
        private String specAttrDetail; // 规格名称
        private String flavorstuffDetail; // 口味加料明细
        private String isPackage; // 是否套餐商品
        private String pGoodsDetail; // 是否套餐商品
        private String unitId; // 单位编码
        private String unitName; // 单位名称
        private String trNo; // 取餐号
        private String tableNo; // 桌号
        private String goodsStatus; // 菜品类型
        private String isUrge; // 是否催菜Y/N
        private String isOverTime; // 是否超时
        private String refundReasonName; // 退货原因名称
    }
}
