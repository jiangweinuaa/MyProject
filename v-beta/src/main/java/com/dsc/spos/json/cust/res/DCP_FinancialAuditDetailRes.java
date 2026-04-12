package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_FinancialAuditDetailRes extends JsonBasicRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private String isAudit;
        private String auditOpno;
        private String auditOpName;
        private List<level2Elm> payList;
    }


    @Data
    public class level2Elm
    {
        private String methodType;//
        private String payType;
        private String payName;
        private double totAmt;
        private double extraAmt;
        private double tcAmt;
        private double diffAmt;
        private double merreceive;
        private double totAmtAudit_ref ;
        private double totAmtAudit;
        private double diffAmtAudit;
    }


}
