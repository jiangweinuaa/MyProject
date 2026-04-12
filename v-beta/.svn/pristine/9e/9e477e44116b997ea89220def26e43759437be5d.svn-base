package com.dsc.spos.model;

import lombok.Data;

import java.util.List;


/**
 * @description: 电子发票请求结构
 * @author: wangzyc
 * @create: 2022-02-24
 */
@Data
public class EInvoiceRequest {

    //  通过订单查询开票信息
    private String eid; // 企业编号
    private String operater; // 操作类型
    private String orderno; // 业务单号，企业内唯一； operater=queryInfoByOrderId时必传；
    private String projectId; // 开票项目id，为了取税率和税别

    private String billtype; // 业务单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值 ;鼎捷开票页面入口时必传；
    private String shopid; // 收款门店id
    private List<level1Elm> ordernolist; // 合并开票订单号；  operater=queryInfoByOrderList时必传；
    private String templateid; // 发票参数模板id，鼎捷开票入口时必传，用于查询开票参数
    private String querytype; // 查询类型：0-开票数据试算【不验证 退单、已开票等信息】 1-查可开票信息 2-申请开票，默认1
    private String langType; // 语言类型

    private String ischecklimitation; // 是否校验发票限额Y/N,默认N

    private level2Elm content; //


    private String apply; // 是否申请开票
    // 开票信息
    private String invoiceKind; // 发票类型：p-电子增值税普通发票，默认p
    private String buyerName; // 购方名称/抬头，个人或企业
    private String buyerTaxNum; // 购方税号，buyerTaxNum='个人'时可为空
    private String projectName; // 开票项目名称，空时取默认项目名称
    private String buyerAddress; // 购方地址;
    private String buyerTel; // 购方电话
    private String buyerBank; // 购方银行开户行地址;
    private String buyerAccount; // 购方银行账号
    private String buyerPhone; // 接收开票结果手机
    private String buyerEmaill; // 接收开票结果邮箱

    private String memberId;
    private String memberName;
    private String openId;

    @Data
    public static class level1Elm{
        private String orderno; // 业务单号，企业内唯一；
        private String shopid; // 收款门店id
    }

    @Data
    public static class level2Elm{
        private String c_orderno; // 订单号
        private String c_kprq; // 开票日期，时间戳格式, 1490867741000
        private String c_fpdm; // 发票代码，12 位，长度固定
        private String c_fphm; // 发发票号码，8 位，长度固定
        private String c_hjse; // 价税税额，保留小数点后 2 位
        private String c_bhsje; // 不含税金额，保留小数点后 2 位
        private String c_url; // pdf发票地址
        private String c_fpqqlsh; // 发票流水号，生成发票唯一标识
        private String qrCode; // 二维码地址
        private String c_jpg_url; // 发票图片地址
        private String email; // 购方邮箱
        private String phone; // 购方手机
        private String checkCode; // 校验码
        private String machineCode; // 税控设备号（机器编 码）
        private String cipherText; // 发票密文

        private String c_errorMessage; //
        private String c_saletaxnum;
        private String c_status;
    }
}
