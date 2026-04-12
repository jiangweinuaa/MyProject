package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;


@Data
public class DCP_ExpenseUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        @JSONFieldRequired
        private String organizationNo;
        private String org_Name;
        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String doc_Type;
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String supplierNo;
        private String supplierName;
        @JSONFieldRequired
        private String settMode;
        @JSONFieldRequired
        private String billDateNo;
        @JSONFieldRequired
        private String payDateNo;
        @JSONFieldRequired
        private String settType;
        private String tot_Amt;
        @JSONFieldRequired
        private String bfeeNo;
        @JSONFieldRequired
        private String sourceType;

        private String sourceNo;
        private String employeeNo;
        private String name;
        private String departNo;
        private String departName;
        @JSONFieldRequired
        private String currency;
        @JSONFieldRequired
        private String status;
        private String createBy;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirm_Date;
        private String confirm_Time;
        private String submitBy;
        private String submit_Date;
        private String submit_Time;
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;
        private List<Datas> datas;
    }

    @Data
    public class Datas {
        private String shopId;
        private String organizationNo;
        private String org_Name;
        private String bfeeNo;
        private String item;
        private String fee;
        private String feeName;
        private String amt;
        private String meno;
        private String taxCode;
        private String taxRate;
        private String priceCategory;
        private String currency;
        private String taxCodeInfo;
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
        private String pluNo;
        private String pluName;
        private String isInvoiceIncl;
        private String sourceNo;
        private String sourceNoSeq;
    }

}
