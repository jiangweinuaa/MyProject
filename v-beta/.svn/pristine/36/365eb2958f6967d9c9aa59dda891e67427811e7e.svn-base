package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherEmpQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> empList;
    }

    @Data
    public class level2Elm
    {
        private String opNo;
        private String opName;
        private String opNoOut;
    }
}
