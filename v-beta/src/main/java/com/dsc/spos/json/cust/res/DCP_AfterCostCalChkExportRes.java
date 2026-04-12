package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_AfterCostCalChkExportRes extends JsonBasicRes {

    private List<ChkList> chkList;

    @NoArgsConstructor
    @Data
    public static class ChkList {
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
