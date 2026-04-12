package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/04
 */
@NoArgsConstructor
@Data
@Getter
@Setter
public class DCP_CostDomainQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String corp;
        private String corpName;
        private String CostDomainTYPE;
        private String cost_Calculation;
        private List<Detail> datas;
    }

    @NoArgsConstructor
    @Data
    public class Detail {
        private String costDomainid;
        private String costDomain;
        private String memo;
        private String status;
    }
}
