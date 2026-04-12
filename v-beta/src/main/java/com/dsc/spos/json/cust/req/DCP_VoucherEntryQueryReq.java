package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VoucherEntryQueryReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
       private String voucherType;//凭证类型：1-销售凭证

    }


}
