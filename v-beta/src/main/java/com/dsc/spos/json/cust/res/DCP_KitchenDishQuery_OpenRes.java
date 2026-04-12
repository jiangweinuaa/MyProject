package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS待生产单据查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_KitchenDishQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String pluNo; // 商品编码
        private String pluName; // 商品名称
        private String pluBarcode; // 条码
        private String singleMakeQty; // 单词制作最大数量
        private String qty; // 数量
        private String specAttrDetail; // 规格+属性详细(中杯,正常糖,大份) 第3方外卖订单
        private String unitId; // 单位编码
        private String unitName; // 单位名称
        private String flavorstuffDetail; // 口味加料详细 【口味1, 口味2，加料1x1】
        private String isPackage; // 是否套餐商品
        private String pGoodsDetail; // 套餐子商品详情 【子商品1x2(口味, 加料1x1)，子商品2x1】
//        private String repastType; // 就餐类型：0堂食1打包2外卖
        private String goodsStatus; // 制作状态 0-待配菜 1-待制作 2-待传菜/取餐 3-已完成
        private String memo; // 备注
        private String someTime; // 当前距离最早下单时间(分钟)
        private String cookId; // 机器人编号
        private String cookName; // 机器人名称
        private String cookStatus; // 炒菜状态，0未炒菜，1炒菜中
        private String trNo; // 取餐号，需拼接A120/美团外卖20/大厅A12/A166...
        private String repastNum; // 就餐类型数量，需拼接 例如，堂食x1,打包x2,外卖x1,预制x1...
        private String isPrintCrossMenu; // 是否打印划菜单：空白、Y、N
        private String crossPrinter; // 划菜单打印机
        private List<level2Elm> orderList; // 单据信息
        private String sorttime; //排序时间

    }

    @Data
    public class level2Elm
    {
        private String oChannelId; //来源渠道编号
        private String oChannelName; // 来源渠道名称，仅显示“美团外卖”“饿了么”字符
        private String appName; // 应用类型 POS/POSANDROID：统称"POS" SCAN/WAIMAI：统称"小程序" ELEME：饿了么 MEITUAN：美团
        private String loadDocType; // 订单渠道类型
        private String processTaskNo; // 加工任务单号
        private String billNo; // 来源单号
        private String trNo; // 取餐号
        private String qty; // 本单占比数量
        private String tableNo; // 桌台
        private String adultCount; // 就餐人数
        private String repastType; // 就餐类型 0堂食 1打包 2外卖
        private String memo; // 整单备注
        private String isUrge; // 是否催菜Y/N
        private String isOverTime; // 是否超时
        private String isBefore; // 是否预制菜Y/N
        private level3Elm timeInfo; // 时间信息
        private List<level4Elm> itemList; // 项次组
    }

    @Data
    public class level3Elm{
        private String orderTime; // 下单时间/配送/自提时间
        private String someTime; // 当前距离下单时间(分钟)
        private String madeTime; // 制作完成时间
    }

    @Data
    public class level4Elm{
        private String item; // 项次
    }
}
