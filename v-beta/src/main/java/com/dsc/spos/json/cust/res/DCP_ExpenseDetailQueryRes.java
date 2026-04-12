package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ExpenseDetailQueryRes extends JsonRes {

    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String organizationNo;
        private String org_Name;
        private String bdate;
        private String doc_Type;
        private String corp;
        private String corpName;
        private String supplierNo;
        private String supplierName;
        private String settMode;
        private String billDateNo;
        private String billDateName;
        private String payDateNo;
        private String payDateName;
        private String settType;
        private String tot_Amt;
        private String bfeeNo;
        private String sourceType;
        private String employeeNo;
        private String name;
        private String departNo;
        private String departName;
        private String currency;
        private String currencyName;
        private List<Detail> datas;

        private String createBy;
        private String createByName;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modifyByName;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirmByName;
        private String confirm_Date;
        private String confirm_Time;
        private String submitBy;
        private String submitByName;
        private String submit_Date;
        private String submit_Time;
        private String cancelBy;
        private String cancelByName;
        private String cancel_Date;
        private String cancel_Time;

        private String status;
    }

    @NoArgsConstructor
    @Data
    public class Detail {
        private String bfeeNo;
        private String item;
        private String fee;
        private String feeName;
        private String feeNature;
        private String amt;
        private String meno;
        private String taxCode;
        private String taxName;
        private String taxCodeInfo;
        private String taxRate;
        private String organizationNo;
        private String org_Name;
        private String priceCategory;
        private String currency;
        private String feeType;
        private String startDate;
        private String endDate;
        private String settAccPeriod;
        private String category;
        private String departNo;
        private String departName;
        private String isInSettDoc;
        private String expAttriType;
        private String expAttriOrg;
        private String expAttriOrgName;
        private String pluNo;
        private String pluName;
        private String isInvoiceIncl;
        private String sourceNo;
        private String sourceNoSeq;
    }
}
