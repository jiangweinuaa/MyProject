package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArWrtOffDocQueryRes extends JsonRes {


    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountId;
        private List<ArList> arList;
    }

    @NoArgsConstructor
    @Data
    public class ArList {
        private String taskId;
        private String arNo;
        private String pDate;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String arSubjectId;
        private String arSubjectName;
        private String unPaidAmt;
        private String status;
        private String memo;
    }

}
