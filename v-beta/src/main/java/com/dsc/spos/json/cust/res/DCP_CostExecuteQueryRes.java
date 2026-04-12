package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_CostExecuteQueryRes extends JsonRes {

    private Datas datas;

    @Data
    public class Datas {
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String mainTaskId;
        private String inputPrg;
        private String impStateInfo;
        private String memo;
        private List<CostList> costList;
    }

    @Data
    public class CostList {
        private String item;
        private String subtaskId;
        private String mainTaskId;
        private String type;
        private String inputPrg;
        private String impStateInfo;
        private String memo;
    }

}
