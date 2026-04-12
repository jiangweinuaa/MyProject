package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TaxSubjectQueryRes  extends JsonRes {

    private List<DCP_TaxSubjectQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String accountId;
        private String coaRefId;
        private String status;
        private String accountName;

    }
}
