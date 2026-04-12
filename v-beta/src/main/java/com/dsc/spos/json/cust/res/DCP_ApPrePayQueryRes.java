package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApPrePayQueryRes extends JsonRes
{
    private DCP_ApPrePayQueryRes.level1Elm datas;

    @Data
    public class level1Elm{

        private String accountId;
        private String account;
        private String taskId;
        private String totfCYTATAmt;
        private String totfCYRevAmt;
        private String totunPaidAmt;

        private List<ApList> apList;
    }

    @Data
    public class ApList{
        private String bizPartnerNo;
        private String bizPartnerName;
        private String apNo;
        private String pDate;
        private String fCYTATAmt;
        private String fCYRevAmt;
        private String unPaidAmt;
        private String status;
        private String sourceNo;
    }

}
