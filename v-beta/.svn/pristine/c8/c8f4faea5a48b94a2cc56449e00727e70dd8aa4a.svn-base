package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约项目查询外部接口
 * @author: wangzyc
 * @create: 2021-07-28
 */
@Data
public class DCP_ReserveItemsQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm {
        private String itemsNo;
        private String itemsName;
        private String imageUrl;
        private String serviceTime;
        private String serviceIntroduction;
        private String shopDistribution; // 预约到店分配Y1/N
        private String couponType; // 赠送券类型
        private String qty; // 赠送张数

        // 新增reserveType、price、vipPrice、cardPrice
        private String reserveType; // 支持预约方式 free：免费预约 coupon：用券预约 pay：支付后预约
        private String price; // 售价
        private String vipPrice; // 会员价
        private String cardPrice; // 钻石卡价

    }
}
