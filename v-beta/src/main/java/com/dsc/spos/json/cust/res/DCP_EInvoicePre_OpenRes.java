package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: 开票申请试算
 * @author: wangzyc
 * @create: 2022-03-10
 */
@Data
public class DCP_EInvoicePre_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        // 价税合计
        private String amt;

        // 税额
        private String taxAmt;

        // 未税金额
        private String exTaxAmt;

        // 发票限额，默认1000元【指的是未税金额不超过1000元】
        private String limitation;

        // 开票项目编码
        private String projectId;

        // 开票项目名称
        private String projectName;

        // 税率
        private String taxRate;

        // 税别编码
        private String taxCode;
    }
}
