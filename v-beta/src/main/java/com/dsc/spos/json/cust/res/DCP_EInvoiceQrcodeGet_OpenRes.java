package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: 获取开票二维码
 * @author: wangzyc
 * @create: 2022-02-23
 */
@Data
public class DCP_EInvoiceQrcodeGet_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm {
        private String qrCode; // 开票二维码URL
        private String status;
        private String isApply;
    }

}
