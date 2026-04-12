package com.dsc.spos.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 通过订单查询开票信息返回参数
 * @author: wangzyc
 * @create: 2022-02-24
 */
@Data
public class EInvoiceResponse implements Serializable {

        private String orderno; // 业务单号，20位；=入参orderNo； 此单号含在orderlist里
        private String platformtype; // 发票平台；取发票模板对应的发票平台，
        private String invoicebillno; // 开票单号： 诺诺V1版本：开票单号=业务单号；
        private String ordertotal; // 可开票金额，精确到小数点 后面两位；=明细含税金额合计
        private String taxtotal; // 税额，精确到小数点 后面两位；=明细中 税额合计
        private String bhtaxtotal; // 未税金额，精确到小数点 后面两位；=明细不含税金额合计
        private String kptype; // 开票类型：1-正数发票 2负数发票，默认1

        // 暂时不用
        private String fpdm; // 发票代码
        private String fphm; // 发票号码
        private String tsfs; // 推送方式

        private String qdbz; // 清单标志
        private String qdxmmc; // 清单项目名称:打印清单时对 应发票票面项 目名称
        private String invoicedate; // 时间datetime

        private String message; // 红票 需包含“对应正数发票 代 码 :XXXXXXXXX 号 码:YYYYYYYY”字样，其 中“X”为发票代码，“Y” 为发票号码
        private String clerk; // 开票员
        private String payee; // 收票人
        private String checker; // 复核人

        private String deptid; // 部门门店(诺 诺系统中的 id）
        private String clerkid; // 开票员 id（诺 诺系统中的 id）

        private String cpybz; // 成品油标志：0  非成品油， 1  成品油，默认 为 0

        private List<level1Elm> detail; // 零售单：取自DCP_SALE_PAY，排除订金冲销【idorderpay='Y'】 订单：  商城订单：CRM_ORDERPAY  非商城订单且渠道上开启开发票：DCP_ORDER_PAY_DETAIL

        /**
         * 抬头自定义： 固定为1-是；
         * 如果是三方的开票页面，则购方信息由三方采集，估清为1；
         */
        private String self_flag;

        /**
         * 销方地址，取自发票参数
         */
        private String saleaddress;

        /**
         * 销方税号，取自发票参数
         */
        private String saletaxnum;

        /**
         * 销方电话 ，取自发票参数
         */
        private String salephone;

        /**
         * 销方银行帐号，取自发票参数
         */
        private String saleaccount;


        /**
         *
         */
        private List<level2Elm> orderlist;

    @Data
    public class level1Elm{
        private String goodsname; // 商品名称，固定为发票参数中的项目名称

        private String shopId; // 门店id
        private String orderNo;

        // 数量，16，数量；数量、单 价必 须都不填，或 者都必填，不可只填 一个； 当数量、单 价都不填 时，
        // 不含 税金额、税 额、含 税金额都必填，固定为1
        private String num;

        // 单价含税标志，0:不 含税,1:含税，固定为1
        private String hsbz;

        // 16，单价；数量、单 价必 须都不填，或 者都必 填，不可只 填一个； 当数量、 单价都不填 时，不 含税
        // 金额、税 额、 含税金额都必填，固定为可开票支付方式金额合计【不含找零和溢收】
        private String price;

        // 税率，固定取发票参数中的税率/100
        private String taxrate;

        // 商品税别编码，固定为发票参数中的税别编码
        private String spbm;

        // 发票行性质:0, 正常行 ( 默 认);1,折扣行;2, 被折扣行；固定为0
        private String fphxz;

        // 优 惠 政 策 标 识:0,不使用;1, 使用，固定为0
        private String yhzcbs;

        // 增值税特殊管理，固定为空
        private String zzstsgl;

        // 零税率标识 : 空,非零税率;1, 免 税 ;2, 不 征 税;3,普通零税 率，固定为空
        private String lslbs;

        /**
         * 含税金额，否，精确到小数点后面 两位，红票为负。不含 税金额、税额、含税金 额任何一个不传时
         * 会 根据传入的单价，数量 进行计算，可能和实际 数值存在误差，建议都 传入；
         * 零售单算法：
         * =支付方式中可开票金额合计【不含找零，不含溢收】
         */
        private String taxamt;

        /**
         * 税额，否，精确到小数点后面 两位，红票为负。不含 税金额、税额、含税金 额任何一个不传时，会 根
         * 据传入的单价，数量 进行计算，可能和实际 数值存在误差，建议都 传入；
         * =round((含税金额)*税率/(1+税率)),2)；
         */
        private String tax;

        /**
         * 不含税金额，精确到小数点后面两 位，红票为负。不含税 金额、税额、含税金额 任何一个不传时，
         * 会根 据传入的单价，数量进 行计算，可能和实际数 值存在误差，建议都传 入；含税金额-税额
         */
        private String taxfreeamt;

    }

    @Data
    private class level2Elm{
        private String orderno;
        private String billtype; // 业务单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值 ;鼎捷开票页面入口时必传；
        private String apptype;   // 渠道类型编码
        private String channelid; // 渠道编码
        private String shopid; // 收款门店id
    }

}
