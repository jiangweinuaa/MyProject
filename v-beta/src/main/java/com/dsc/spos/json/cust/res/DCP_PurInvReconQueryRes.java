package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PurInvReconQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String bDate;
        private String purInvNo;
        private String corp;
        private String corpName;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String invoiceCode;
        private String totInvFCYBTAmt;
        private String totInvFCYTAmt;
        private String totInvFCYATAmt;
        private String apNo;
    }
}
