package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_IntraCityLogisticsRes extends JsonBasicRes {

    private level1Elm datas;

    @Data
    public class level1Elm{
        private String longitude;
        private String latitude;
        private String riderName;
        private String riderPhone;
    }

}
