package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_DemandToProdQueryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public  class Datas {
        private String rDate;
        private String orderType;
        private String orderNo;
        private String objectType;
        private String objectId;
        private String objectName;
    }
}
