package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VendorAdjQueryRes extends JsonRes {

    private List<DCP_VendorAdjQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String organizationNo;
        private String org_Name;
        private String adjustNO;
        private String bDate;
        private String otype;
        private String sStockInNo;
        private String supplier;
        private String supplierName;
        private String payDateNo;
        private String billDateNo;
        private String taxCode;
        private String taxRate;
        private String currency;
        private String currencyName;
        private String exRate;
        private String tot_Amt;
        private String memo;
        private String status;
    }
}
