package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherTypeQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> voucherTypeList;
    }

    @Data
    public class level2Elm
    {
        private String voucherType;
        private String voucherName;
        private String memo;
    }


}
