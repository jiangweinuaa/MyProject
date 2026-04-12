package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 开票申请
 * @author: wangzyc
 * @create: 2022-03-11
 */
@Data
public class DCP_EInvoiceApply_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        // 发票参数模板id，按发票模板查询参数；
        private String templateid;

        // 开票项目id，为了取得税别和税率、项目名称等
        private String projectId;

        // 业务单据类型：Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值 ;鼎捷开票页面入口时必传；
        private String billType;

        /**
         * 合并开票订单，需符合以下条件：
         * 1、同一法人[系统内可以通过同一发票参数模板来判断]
         * 2、相同的税别【目前是简单的税别分类，仅区分商品税别、卡券税别】
         * 简单来说，就是商品和卡券要分开；
         */
        private List<level2Elm> ordernoList;

        // 发票类型：p-电子增值税普通发票，默认p
        private String invoiceKind;

        // 购方名称/抬头，个人或企业
        private String  buyerName;

        //购方税号，buyerTaxNum='个人'时可为空
        private String buyerTaxNum;

        // 开票项目名称，空时取默认项目名称
        private String projectName;

        // 购方地址
        private String buyerAddress;

        // 购方电话
        private String buyerTel;

        // 购方银行开户行地址
        private String buyerBank;

        // 购方银行账号
        private String buyerAccount;

        // 接收开票结果手机
        private String buyerPhone;

        // 接收开票结果邮箱
        private String buyerEmail;

        // 会员id
        private String memberId;

        // 会员名称
        private String memberName;

        // 用户id
        private String openId;

    }

    @Data
    public class level2Elm {
        // 业务单号，企业内唯一
        private String orderno;

        // 收款门店id
        private String shopid;
    }
}
