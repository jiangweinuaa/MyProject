package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 开票登记新增
 * @author: wangzyc
 * @create: 2022-03-15
 */
@Data
public class DCP_EInvoiceRegisterCreateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String billType;
        private String shopId;
        private String orderno;
        private String opNo;
        private String opName;
        private String taxCode;
        private String taxRate;
        private String projectName;
        private String isCheckLimitation;
    }
}
