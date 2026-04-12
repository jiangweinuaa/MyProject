package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherEntryUpdateReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level1Elm> voucherEntryList;
    }

    @Data
    public class level1Elm
    {
        private String voucherType;
        private String entryType;
        private String entryId;
        private String debitOrCredit;
        private String subjectId;
        private String subjectName;
        private String memo;
    }

}
