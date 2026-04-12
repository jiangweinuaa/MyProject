package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_FinancialAuditExportGet_u8cReq extends JsonBasicReq
{
    private level1Elm request;

    @Data
    public class  level1Elm
    {
        private String bDate;//起始营业日期YYYY-MM-DD
        private String opNo;
        private List<level2Elm> shopList;


    }
    @Data
    public class  level2Elm
    {
        private String shopId;
    }


}
