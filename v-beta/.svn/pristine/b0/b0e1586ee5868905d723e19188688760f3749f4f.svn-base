package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;
import lombok.Data;

/**
 * @description: 开票记录查询
 * @author: wangzyc
 * @create: 2022-03-15
 */
@Data
public class DCP_EInvoiceQueryReq extends JsonReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String invoiceBillNo;
        private String sourBillType;
        private String sourceBillNo;
        private String applyStatus;
        private String status;
        private String applyDateBegin;
        private String applyDateEnd;
        private String invoiceDateBegin;
        private String invoiceDateEnd;
    }
}
