package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务项目查询
 * @author: wangzyc
 * @create: 2021-07-21
 */
@Data
public class DCP_ServiceItemsQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String itemsType;            // 项目类型  套餐：PACKAGE 项目：ITEMS
        private String itemsNo;              // 项目编号
        private String itemsName;            // 项目名称
        private String serviceTime;          // 服务时长
        private String couponTypeId;         // 券类型编号
        private String qty;                 // 数量
        private String serviceIntroduction;  // 服务简介
        private String serviceNote;          // 服务介绍
        private String memo;                 // 备注
        private String status;               // 状态

        // 新增reserveType、price、vipPrice、cardPrice； By wangzyc
        private String reserveType;         // 支持预约方式 free：免费预约 coupon：用券预约 pay：支付后预约
        private String price;               // 售价
        private String vipPrice;            // 会员价
        private String cardPrice;           // 钻石卡价
    }
}
