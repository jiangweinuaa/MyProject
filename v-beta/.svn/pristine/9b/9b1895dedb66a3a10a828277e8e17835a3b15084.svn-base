package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettBillQueryRes extends JsonRes {

    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private List<InterList> interList;
    }

    @NoArgsConstructor
    @Data
    public class InterList {
        private String bDate;
        private String settAccPeriod;
        private String billNo;
        private String status;
        private String corp;
        private String corpName;
        private String arAmt;
        private String arPostedAmt;
        private String arNo;
        private String apAmt;
        private String apPostedAmt;
        private String apNo;
    }
}

