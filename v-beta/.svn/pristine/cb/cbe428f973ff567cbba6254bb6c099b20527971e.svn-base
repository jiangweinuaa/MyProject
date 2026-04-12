package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WrtOffQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountId;
        private List<ApList> apList;
    }

    @NoArgsConstructor
    @Data
    public class ApList {
        private String taskId;
        private String apNo;
        private String pDate;
        private String bizPartnerNo;
        private String apSubjectId;
        private String unPaidAmt;
        private String status;
        private String memo;
    }
}

