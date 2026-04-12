package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CostDomainOpenQryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String corp;
        private String corpName;
        private List<OrgList> orgList;
    }

    @NoArgsConstructor
    @Data
    public class OrgList {
        private String orgNo;
        private String orgName;
    }
}

