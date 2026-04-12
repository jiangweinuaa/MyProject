package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 会员体验券查询
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveCouponQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String couponCode; // 券号
        private String couponName; // 券名称
        private String description; // 简要说明
        private String description2; // 详细说明
        private String couponType; // 券类型
        private String beginDate; // 有效期开始日期
        private String endDate; // 有效截止日期
        private String picUrl; // 券面图片
        private String status; // 券状态 1=已使用 2=未使用(已开始-未过期) 9=已失效(已过期)
        private String allowGift; // 允许转赠
        private String couponEverytime; // 单笔消费可用张数
        private String goodsId; // 对应的商品ID
        private List<level2Elm> itemList; // 限制服务项目
        private String memberId; // 会员编号
        private String name; // 会员姓名
        private String mobile; // 手机号
    }

    @Data
    public class level2Elm{
        private String itemsNo; // 项目编号
        private String itemsName; // 项目名称
        private String imageUrl; // 商品图片
        private String serviceTime; // 服务时长(分钟)
        private String serviceIntroduction; // 服务介绍
        private List<level3Elm> shopList; // 所属门店
    }

    @Data
    public class level3Elm{
        private String shopId; // 支持的门店编号
        private String shopDistribution; // 预约到店分配Y/N
    }
}
