package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_ExpenseCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        @JSONFieldRequired(display = "组织编号")
        private String organizationNo;
        @JSONFieldRequired(display = "组织名称")
        private String org_Name;
        @JSONFieldRequired(display = "单据日期")
        private String bdate;
        @JSONFieldRequired(display = "单据类型")
        private String doc_Type;
        @JSONFieldRequired(display = "法人")
        private String corp;
        @JSONFieldRequired(display = "法人名称")
        private String corpName;
        @JSONFieldRequired(display = "结算对象")
        private String supplierNo;
        @JSONFieldRequired(display = "结算对象名称")
        private String supplierName;
        @JSONFieldRequired(display = "结算方式")
        private String settMode;
        @JSONFieldRequired(display = "结算类型")
        private String settType;
        @JSONFieldRequired
        private String billDateNo;
        @JSONFieldRequired
        private String payDateNo;
        private String tot_Amt;
        private String bfeeNo;
        @JSONFieldRequired(display = "来源类型(枚举值:1 ：手工维护；2：其他费用单 ，3：返利费用)")
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

        @JSONFieldRequired
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
