package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ApBillEstQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountId;
        private String apType;
        private String corp;
        private String taskId;
        private String status;

        private String createBy;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;

        private List<EstList> estList;
    }

    @NoArgsConstructor
    @Data
    public class EstList {
        private String accountID;
        private String sourceOrg;
        private String item;
        private String wrtOffType;
        private String estBillNo;
        private String period;
        private String wrtOffAPSubject;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYPrcDiffAmt;
    }
}

