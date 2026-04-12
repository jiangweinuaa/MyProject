package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CurInvCostAdjQueryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String dataSource;
        private String referenceNo;
    }
}
