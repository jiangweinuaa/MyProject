package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherOrgUpdateReq extends JsonBasicReq
{
    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level1Elm> orgList;
    }

    @Data
    public class level1Elm
    {
        private String orgId;
        private String orgName;
        private String orgIdOut;
        private String orgNameOut;
        private String compIdOut;
        private String compNameOut;
    }

}
