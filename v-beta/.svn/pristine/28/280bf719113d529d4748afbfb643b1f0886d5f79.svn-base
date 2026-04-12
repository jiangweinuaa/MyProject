package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 门店触屏菜单商品设置查询
 * @author: wangzyc
 * @create: 2021-06-17
 */
@Data
public class DCP_TouchMenuClassGoodsQueryRes extends JsonBasicRes {
    private level1Elm datas;

    private level1Elm request;

    @Data
    public class level1Elm{
        private String menuNo;    // 菜单编码
        private String menuName;  // 菜单名称
        private List<level2Elm> classList;
    }

    @Data
    public class level2Elm{
        private String item;            // 项次
        private String className;       // 分类名称
        private List<level3Elm> className_lang; // 分类名称多语言`
        private String classImage;      // 分类图片ID
        private String classImageUrl;   // 分类图片地址
        private String goodsSortType;   // 商品排序：1-默认顺序 3-价格升序
        private String remindType;      // 提醒类型，-1不提醒 0.必选 1.提醒
        private String labelName;       // 便签内容
        private String status;          // 100-有效0-无效
        private List<level4Elm> goodsList;  //
    }

    @Data
    public class level3Elm{
        private String langType;        // 语言类别 zh_CN：中文简体  zh_TW：繁体中文 en_US：英文
        private String name;            // 名称
    }

    @Data
    public class level4Elm {
        private String subItem;         // 子项次
        private String pluType;         // 商品类型：NORMAL-普通商品 FEATURE-多特征商品 PACKAGE-套餐商品  MULTISPEC-多规格商品
        private String pluNo;           // 商品编码
        private String dispName;        // 商品显示名称
        private List<level3Elm> dispName_lang;  // 商品显示名称多语言
        private String unitId;          // 销售单位编码
        private String unitName;        // 销售单位名称
        private String price;          // 销售单位价格
        private String remindType;          // -1：无提醒 0-提醒 1-必点
    }
}
