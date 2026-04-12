package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApBillQueryRes extends JsonRes
{
    private DCP_ApBillQueryRes.level1Elm datas;

    @Data
    public class level1Elm{
        private String accountId;
        private String accountName;
        //private String taskId;
        private String totfCYTATAmt;
        private String totfCYRevAmt;
        private String totunPaidAmt;

        private List<ToList> totList;
        private List<ApList> apList;
    }

    @Data
    public class ApList{
        private String accountId;
        private String accountName;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String apNo;
        private String pDate;
        private String fCYTATAmt;
        private String fCYRevAmt;
        private String unPaidAmt;
        private String status;
        private String taskId;
        private String glNo;
    }

    @Data
    public class ToList{
        private String accountId;
        private String accountName;
        private String totFCYATAmt;
        private String totFCYRevAmt;
        private String totUnPaidAmt;
        private String yTDFCYATAmt;
        private String yTDFCYRevAmt;
        private String yTDUnPaidAmt;
        private String totConfirm;
        private String totInsert;
        private String ungenVoucher;
        private String totCreateAP;

    }

}
