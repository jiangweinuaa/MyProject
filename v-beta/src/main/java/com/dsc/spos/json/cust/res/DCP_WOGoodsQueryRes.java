package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WOGoodsQueryRes extends JsonRes {


    private List<DatasLevel> datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String oOType;
        private String oOfNo;
        private String oOItem;

        private String bomNo;
        private String versionNum;

        private String pluNo;
        private String pluName;
        private String pDate;
        private String pQty;
        private String pUnit;
        private String pUName;
        private String prodQty;
    }
}
