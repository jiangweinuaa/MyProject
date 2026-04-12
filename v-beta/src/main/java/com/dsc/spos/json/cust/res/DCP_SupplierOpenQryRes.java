package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_SupplierOpenQryRes extends JsonRes {
    private List<DCP_SupplierOpenQryRes.level1Elm> datas;

    @Data
    public class level1Elm {

        private String bizPartnerNo;
        private String sName;
        private String fullName;
        private String mainContact;
        private String mainConMan;
        private String telephone;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String taxCalType;
        private String payType;
        private String billDateNo;
        private String billDate_desc;
        private String payDateNo;
        private String payDate_desc;
        private String payCenter;
        private String payCenterName;
        private String invoiceCode;
        private String invoiceName;
        private String currency;
        private String currencyName;
        private String payee;
        private String payeeName;
        private String payer;
        private String payerName;

    }

}
