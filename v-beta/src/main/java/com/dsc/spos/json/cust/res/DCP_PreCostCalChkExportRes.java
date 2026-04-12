package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PreCostCalChkExportRes extends JsonBasicRes {
    private List<DCP_PreCostCalChkProcessRes.CkList> ckList;

    @Data
    public class CkList{

        private String corp;
        private String corpName;
        private String year;
        private String period;
        private String accountID;
        private String account;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String bType;
        private String bNo;
        private String errorMessage;
    }
}
