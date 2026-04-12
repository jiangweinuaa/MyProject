package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS传菜/出餐单据查询
 * @author: wangzyc
 * @create: 2021-09-28
 */
@Data
public class DCP_CdsDishQuery_OpenRes extends JsonRes {
    private level1Elm datas;

    @Data
    public class level1Elm {
      private String orderNum; // 待传菜订单合计
      private String goodsNum; // 待传菜商品合计
      private List<level2Elm> orderList; // 订单信息
    }

    @Data
    public class level2Elm{
        private String oChannelId; // 来源渠道编号
        private String oChannelName; // 来源渠道名称，仅显示“美团外卖”“饿了么”字符
        private String appName; // 应用类型
        private String loadDocType; // 订单渠道类型
        private String billNo; // 来源单号
        private String trNo; // 取餐号
        private String productStatus; // 制作状态，默认为 0,0-待配菜1-待制作2-待取餐3-已取餐
        private String tableNo; // 桌号
        private String adultCount; // 就餐人数
        private String repastType; // 就餐类型
        private String memo; // 整单备注
        private String isOverTime; // 是否超时Y/N
        private String shipType; // 外卖取货方式 6.同城配送 3.顾客自提
        private String tableWareQty; // 参数数量
        private level3Elm timeInfo; // 时间信息
        private List<level4Elm> goodsList; // 商品列表
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
        private String qty; // 数量
        private String specAttrDetail; // 规格+属性详细(中杯,正常糖,大份)
        private String unitId; // 单位编码
        private String unitName; // 单位名称
        private String flavorstuffDetail; // 口味加料详细 【口味1, 口味2，加料1x1】
        private String isPackage; // 是否套餐商品 Y/N
        private String pGoodsDetail; // 套餐子商品详细 【子商品1x2(口味, 加料1x1)，子商品2x1】
        private String repastType; // 就餐类型：0堂食1打包2外卖
        private String goodsStatus; // 制作状态
        private String memo; // 单品备注
        private String isUrge; // 是否催菜Y/N
        private String isRefundOrder; // 是否退货0.整单退 1.部分退
        private String refundQty; // 退货数量
        private level5Elm timeInfo; // 是时间信息
        private List<level4Elm> pGoodsList; // 套餐子商品列表
    }

    @Data
    public class level5Elm{
        private String orderTime; // 下单时间/配送/自提时间
        private String assortedTime; // 配菜完成时间
        private String madeTime; // 制作完成时间
        private String pickupTime; // 传菜/取餐时间
    }
}
