package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApEstProcessReq extends JsonBasicReq
{
    private DCP_ApEstProcessReq.levelRequest request;

    @Data
    public class levelRequest{

       //  "bdate": "string",
        //        "corp": "string",
        //        "corpName": "string",
        //        "apType": "01.采购暂估单,02.采退暂估单03.暂估差异"
        private String bdate;
        private String corp;
        private String corpName;
        private String apType;

    }

    @Data
    public class ApDoc{
        private String bDate;
        private String organizationNo;
        private String bizPartnerNo;
        private String payDateNo;
        private String taxCode;
        private String billNo;
    }

}
