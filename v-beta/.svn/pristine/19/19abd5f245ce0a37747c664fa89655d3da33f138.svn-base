package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS配菜/制作完成单据查询
 * @author: wangzyc
 * @create: 2021-09-27
 */
@Data
public class DCP_KdsDishQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm
    {
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarcode; // 条码
        private String singleMakeQty; // 单次制作最大数量
        private String qty; // 数量
        private String specAttrDetail; // 规格+属性详细(中杯,正常糖,大份) 第3方外卖订单
        private String unitId; // 单位编码
        private String unitName; // 单位名称
        private String flavorstuffDetail; // 口味加料详细 【口味1, 口味2，加料1x1】
        private String isPackage; // 是否套餐商品Y/N
        private String pGoodsDetail; // 套餐子商品详细 【子商品1x2(口味, 加料1x1)，子商品2x1】
        private String repastType; // 就餐类型：0堂食1打包2外卖
        private String memo; // 备注，优先显示单品备注，若无显示整单备注，若无返空
        private String assortedTime; // 配菜完成时间
        private String madeTime; // 制作完成时间
        private List<level4Elm> itemList; // 项次组
        private String oChannelId; // 来源渠道编号
        private String oChannelName; // 来源渠道名称，仅显示“美团外卖”“饿了么”字符
        private String appName; // 应用类型名称
        private String loadDocType; // 订单渠道类型
        private String processTaskNo; // 加工任务单号
        private String billNo; // 来源单号
        private String trNo; // 取餐号
        private String tableNo; // 桌号
        private String adultCount; // 就餐人数
        private String isPrintCrossMenu; // 是否打印划菜单：空白、Y、N
        private String crossPrinter; // 划菜单打印机
        private List<level2Elm> goodsList; //
    }

    @Data
    public class level2Elm{
        private String item; // 项次
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarcode; // 商品条码
        private String qty; // 数量
        private String goodsStatus; // 制作状态 0-待配菜 1-待制作 2-待传菜/取餐 3-已完成
        private String isUrge; // 是否催菜
        private String isOverTime; // 是否超时
        private level3Elm timeInfo; // 时间信息
    }

    @Data
    public class level3Elm{
        private String orderTime; // 下单时间/配送/自提时间
        private String assortedTime; // 配菜完成时间
        private String madeTime; // 制作完成时间
        private String pickupTime; // 传菜/取餐时间
    }

    @Data
    public class level4Elm{
        private String item; // 项次
    }
}
