package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 开票申请试算
 * @author: wangzyc
 * @create: 2022-03-10
 */
@Data
public class DCP_EInvoicePre_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public static class level1Elm {
        // 发票参数模板id，按发票模板查询参数；
        private String templateid;

        // 开票项目id，为了取税率和税别
        private String projectId;

        // 业务单据类型：Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值 ;鼎捷开票页面入口时必传
        private String billType;

        /**
         * 合并开票订单，需符合以下条件：
         * 1、同一法人[系统内可以通过同一发票参数模板来判断]
         * 2、相同的税别【目前是简单的税别分类，仅区分商品税别、卡券税别】
         */
        private List<level2Elm> ordernoList;

        // 是否校验发票限额，Y/N,手工做开票登记的时候请传N
        private String isCheckLimitation;
    }

    @Data
    public static class level2Elm{
        // 业务单号，企业内唯一
        private String orderno;

        // 收款门店id
        private String shopid;

    }
}
