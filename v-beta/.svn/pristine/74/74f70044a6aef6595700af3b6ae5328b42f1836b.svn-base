package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_FinancialAuditReq extends JsonBasicReq
{

    private level1Elm request;

    @Data
    public  class level1Elm
    {
        private  String shopId;
        private  String bDate;//营业日期YYYY-MM-DD
        private  String oprType;//1-稽核 2-反稽核
        private  String auditOpno;
        private  String auditOpName;
        private List<level2Elm> payList;
    }

    @Data
    public  class level2Elm
    {
        private  String methodType;
        private  String payType;
        private  double totAmtAudit;
        private  double diffAmtAudit;
        private  String isNewPay;
    }

}
