package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArBillQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountId;
        private String accountName;
        private String taskId;
        private String totfCYTATAmt;
        private String totfCYRevAmt;
        private String totunPaidAmt;
        private List<ArList> arList;
        private List<TotList> totList;
    }

    @NoArgsConstructor
    @Data
    public class ArList {
        private String accountId;
        private String accountName;
        private String taskId;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String arNo;
        private String pDate;
        private String fCYTATAmt;
        private String fCYRevAmt;
        private String unPaidAmt;
        private String status;
    }

    @NoArgsConstructor
    @Data
    public class TotList {
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
    }

}
