package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: 开票申请
 * @author: wangzyc
 * @create: 2022-03-11
 */
@Data
public class DCP_EInvoiceApply_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        // 开票申请单号YYYYMMDDHHMISSSSS
        private String invoiceBillNo;

        // 发票平台类型：#RUIHONG-瑞宏
        private String platformType;

        // 价税合计
        private String amt;

        // 税额
        private String taxAmt;

        // 未税金额
        private String exTaxAmt;
    }
}
