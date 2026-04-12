package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PrintRPQueryRes extends JsonRes {

    private List<DCP_PrintRPQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String modularNo;
        private List<Detail> detail;
    }

    @Data
    public class Detail{
        private String printNo;
        private String printName;
        private String proName;
        private String parameter;
        private String isStandard;
        private String isDefault;
        private String printType;
    }
}
