package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_AcctSetOpenQryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String accountID;
        private String account;
        private String acctType;
        private String corp;
        private String orgName;
        private String costCalculation;
        private String costDomain;
    }
}
