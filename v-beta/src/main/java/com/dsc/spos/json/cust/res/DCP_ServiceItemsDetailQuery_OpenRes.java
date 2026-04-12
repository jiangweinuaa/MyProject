package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务项目详情查询
 * @author: wangzyc
 * @create: 2021-07-29
 */
@Data
public class DCP_ServiceItemsDetailQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String itemsNo;
        private String itemsName;
        private String pluType;     // 商品类型：NORMAL-普通商品 FEATURE-多特征商品 PACKAGE-套餐 多规格商品-MULTISPEC
        private String listImage;   // 商品图片url
        private String serviceTime; // 服务时长(分钟)
        private String couponType;  // 券类型编号
        private String qty;         // 赠送张数
        private String serviceIntroduction; // 服务简介
        private String serviceNote;  // 服务长说明
        private String memo;         // 备注
        private List<level2Elm> prodImage;  // 产品图列表
        private List<level3Elm> detailcomponents ; // 商品图文详情组件
        private String oriPrice;      // 一般零售价
//        private String price;         //  废弃(当前折扣价格)

        // 新增reserveType、price、vipPrice、cardPrice
        private String reserveType; // 支持预约方式 free：免费预约 coupon：用券预约 pay：支付后预约
        private String price; // 售价
        private String vipPrice; // 会员价
        private String cardPrice; // 钻石卡价
    }

    @Data
    public class level2Elm{
        private String prodImage;   // 产品图url
    }

    @Data
    public class level3Elm{
        private String item;        // 项次
        private String type;        // 	图文类型image/text
        private String content;     // 详情图片url 文本text
    }
}
