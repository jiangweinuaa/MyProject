package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

@Data
public class DCP_ExpenseCopyReq extends JsonBasicReq {

    private Request request;

    @Data
    public class Request {
        @JSONFieldRequired
        private String targetBfeeNo;
        @JSONFieldRequired
        private String sourceBfeeNo;
        @JSONFieldRequired
        private List<BfeeNo> bfeeNo;

    }

    @Data
    public class BfeeNo {
        private String organizationNo;
        private String org_Name;
        private String bdate;
        private String doc_Type;
        private String corp;
        private String corpName;
        private String supplierNo;
        private String supplierName;
        private String settMode;
        private String settType;
        private String tot_Amt;
        private String bfeeNo;
        private String sourceType;
        private String sourceNo;
        private String employeeNo;
        private String name;
        private String departNo;
        private String departName;
        private String currency;
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
