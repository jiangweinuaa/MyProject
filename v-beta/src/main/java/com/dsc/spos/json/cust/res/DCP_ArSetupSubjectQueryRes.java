package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ArSetupSubjectQueryRes extends JsonRes {

    private List<DCP_ArSetupSubjectQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String accountId;
        private String accountName;
        private String setupType;
        private String status;
        private String coaRefID;

    }
}
