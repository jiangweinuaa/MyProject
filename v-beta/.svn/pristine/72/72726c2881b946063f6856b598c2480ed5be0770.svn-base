package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettleDataGenerateRes extends JsonBasicRes {

    private List<SupPriceDetail> supPriceDetail;

    @NoArgsConstructor
    @Data
    public class SupPriceDetail {
        private String item;
        private String pluNo;
        private String featureNo;
        private String receiveOrgNo;
        private String supplyOrgNo;
        private String pUnit;
        private String receivePrice;
        private String supplyPrice;
    }
}
