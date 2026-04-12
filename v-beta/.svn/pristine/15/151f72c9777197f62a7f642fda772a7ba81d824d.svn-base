package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 购物车查询
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartDetailRes extends JsonBasicRes {
    
    private level1Elm datas;
    
    @Data
    public class level1Elm {
        private List<level2Elm> pluList;     // 商品列表
        private String totPqty;              // 总数量合计
        private String totCqty;              // 总品种合计
        private String totAmt;              // 总金额合计
        private String totDistriAmt;        // 总进货金额合计
        
        // Add invalidPluList By wangzyc   20210608
        private List<level3Elm> invalidPluList;  // 失效商品列表
    }
    
    @Data
    public class level2Elm {
        private String item;                // 项次
        private String listImage;           // 列表图
        
        // Add pluType By wangzyc   20210608
        private String pluType;             // 类型：NORMAL-普通商品 FEATURE-多特征商品 PACKAGE-套餐商品
        
        private String maxOrderSpec;        // 最大补货规格
        private String pluNo;               // 品号
        private String pluName;             // 品名
        private String featureNo;           // 特征码编码
        private String featureName;         // 特征码名称
        private String punit;               // 要货单位编码
        private String punitName;           // 要货单位名称
        private String pqty;                // 要货单位数量
        private String baseUnit;            // 基准单位编码
        private String baseUnitName	;       // 基准单位名称
        private String baseQty;             // 基准单位数量
        private String unitRatio;           // 单位换算率
        private String price;               // 零售价
        private String amt;                 // 零售总额
        private String distriPrice;         // 进货价
        private String distriAmt;           // 进货总额
        private String minQty;              // 最小要货量
        private String maxQty;              // 最大要货量
        private String mulQty;              // 要货倍量
        private String punitUdLength;       // punit单位对应的小数位数长度
        private String selected;            // 选中状态（0未选中，1已选中）
        private String isNewGoods;          // 是否新品（Y是，N否）
        private String isHotGoods;          // 是否爆品（Y是，N否）
        private String baseUnitUdLength;    // 基准单位对应的小数位数长度
        private String warningQty;
    }
    
    @Data
    public class level3Elm {
        private String item;                // 项次
        private String listImage;           // 列表图
        private String pluType;             // 类型：NORMAL-普通商品 FEATURE-多特征商品 PACKAGE-套餐商品
        private String maxOrderSpec;        // 最大补货规格
        private String pluNo;               // 品号
        private String pluName;             // 品名
        private String featureNo;           // 特征码编码
        private String featureName;         // 特征码名称
        private String punit;               // 要货单位编码
        private String punitName;           // 要货单位名称
        private String pqty;                // 要货单位数量
        private String baseUnit;            // 基准单位编码
        private String baseUnitName	;       // 基准单位名称
        private String baseQty;             // 基准单位数量
        private String unitRatio;           // 单位换算率
        private String price;               // 零售价
        private String amt;                 // 零售总额
        private String distriPrice;         // 进货价
        private String distriAmt;           // 进货总额
        private String minQty;              // 最小要货量
        private String maxQty;              // 最大要货量
        private String mulQty;              // 要货倍量
        private String punitUdLength;       // punit单位对应的小数位数长度
        private String selected;            // 选中状态（0未选中，1已选中）
        private String baseUnitUdLength;    // 基准单位对应的小数位数长度
        private String warningQty;
    }
}
