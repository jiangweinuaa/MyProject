package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 获取开票二维码
 * @author: wangzyc
 * @create: 2022-02-23
 */
@Data
public class DCP_EInvoiceQrcodeGet_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String invoiceQrCodeType; // 0-发票平台二维码 1-鼎捷发票二维码，默认0
        private String orderNo; // 业务单号，企业内唯一
        private String billType; // 业务单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值
        private String shopId; // 开票门店id
        private String templateId; // 发票参数模板id
    }
}
