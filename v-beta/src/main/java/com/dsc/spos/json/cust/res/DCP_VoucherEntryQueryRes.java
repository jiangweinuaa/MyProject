package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherEntryQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> voucherEntryList;
    }

    @Data
    public class level2Elm
    {
        private String voucherType;
        private String entryType;
        private String entryId;
        private String entryName;
        private String debitOrCredit;
        private String subjectId;
        private String subjectName;
        private String memo;
    }

}
