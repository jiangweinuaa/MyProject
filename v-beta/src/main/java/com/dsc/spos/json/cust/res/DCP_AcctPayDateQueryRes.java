package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DCP_AcctPayDateQueryRes extends JsonRes {


    private List<DCP_AcctPayDateQueryRes.Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String paydateType;
        private String paydateNo;
        private String paydateBase;
        private String pSeasons;
        private String pMonths;
        private String pDays;
        private String duedateBase;
        private String dSeasons;
        private String dMonths;
        private String dDays;
        private String estReceExpDay;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;
    }
}
