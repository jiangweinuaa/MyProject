package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReturnApplyProQueryRes extends JsonRes {
    private List<DCP_ReturnApplyProQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String billNo;
        private String bDate;
        private String orgNo;
        private String orgName;
        private String totCqty;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private List<StatusCount> statusCount;
    }

    @Data
    public class StatusCount{

        private String appStatus;
        private String qty;

    }
}
