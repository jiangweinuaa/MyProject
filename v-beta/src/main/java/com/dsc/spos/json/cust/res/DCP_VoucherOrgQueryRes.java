package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherOrgQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> orgList;
    }

    @Data
    public class level2Elm
    {
        private String orgId;
        private String orgName;
        private String orgIdOut;
        private String orgNameOut;
        private String compIdOut;
        private String compNameOut;
    }

}
