package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MaterialReplaceQueryRes extends JsonRes {

    private List<DCP_MaterialReplaceQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {

        private String replaceType;
        private String organizationNo;
        private String organizationName;
        private String materialPluNo;
        private String materialPluName;
        private String materialQty;
        private String materialUnit;
        private String materialUName;
        private String replacePluNo;
        private String replacePluName;
        private String replaceQty;
        private String replaceUnit;
        private String replaceUName;
        private String replaceBDate;
        private String replaceEDate;
        private String status;
        private String priority;
        private String memo;
    }
}
