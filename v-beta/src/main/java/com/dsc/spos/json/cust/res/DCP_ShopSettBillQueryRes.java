package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ShopSettBillQueryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String bDate;
        private String reconNo;
        private String shopId;
        private String sourceType;
        private String bizPartnerNo;
        private String tot_Amt;
        private String payAmt;
        private String arNo;
        private String arNo2;
    }
}
