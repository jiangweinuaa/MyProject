package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: CDS叫号/取餐
 * @author: wangzyc
 * @create: 2021-10-09
 */
@Data
public class DCP_CdsOrderStatusUpdate_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String oChannelName; // 来源渠道名称，仅显示“美团外卖”“饿了么”字符
        private String appName; // 应用类型名称
        private String billNo; // 来源单号
        private String trNo; // 取餐号
        private String memo; // 账单备注
        private String totQty; // 总数量 不含套餐子商品
        private String totDisc; // 总折扣
        private String totAmt; // 总金额
        private level2Elm shipInfo; // 收货/自提信息
        private level3Elm timeInfo; // 时间信息
        private List<level4Elm> goodsList; // 商品列表
    }

    @Data
    public class level2Elm {
        private String getMan; // 收货/自提人
        private String getManTel; // 收货/自提人电话
        private String address; // 配送地址
    }

    @Data
    public class level3Elm{
        private String orderTime; // 下单时间/配送/自提时间
        private String someTime; // 当前距离下单时间(分钟)
        private String madeTime; // 制作完成时间
    }

    @Data
    public class level4Elm{
        private String oItem; // 来源项次
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarcode; // 商品条码
        private String price; // 零售价
        private String qty; // 数量
        private String disc; // 折扣金额
        private String amt; // 小计金额
        private String specAttrDetail; // 属性规格名称
        private String unitId; // 单位编码
        private String unitName; // 单位名称
        private String flavorstuffDetail; // 口味加料详细
        private String isPackage; // 是否套餐商品
        private String pGoodsDetail; // 套餐子商品详情
        private level5Elm timeInfo; // 时间信息
    }

    @Data
    public class level5Elm{
        private String orderTime; // 下单时间/配送/自提时间
        private String assortedTime; // 配菜完成时间
        private String madeTime; // 制作完成时间
        private String pickupTime; // 传菜/取餐时间
    }
}
