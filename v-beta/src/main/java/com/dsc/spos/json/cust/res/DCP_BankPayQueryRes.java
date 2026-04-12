package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BankPayQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String corp;
        private String corpName;
        private String organizationNo;
        private String organizationName;
        private String cmNo;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String sumFCYAmt;
        private String glNo;
    }
}
