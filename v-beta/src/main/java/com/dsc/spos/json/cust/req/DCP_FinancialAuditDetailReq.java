package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_FinancialAuditDetailReq extends JsonBasicReq
{

    private level1Elm request;

    @Data
    public class  level1Elm
    {
        private String shopId;//
        private String bDate;//营业日期YYYY-MM-DD
    }

}
