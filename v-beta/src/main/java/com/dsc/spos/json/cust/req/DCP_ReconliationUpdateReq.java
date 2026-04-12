package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReconliationUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ReconliationUpdateReq.level1Elm request;

    @Data
    public class level1Elm {
        @JSONFieldRequired
        private String organizationNo;
        private String organizationName;
        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String reconNo;
        @JSONFieldRequired
        private String corp;
        private String corpName;
        @JSONFieldRequired
        private String dataType;
        @JSONFieldRequired
        private String bizPartnerNo;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String month;
        private String estReceExpDay;
        private String currReconAmt;
        private String currReconTaxAmt;
        private String currReconPretaxAmt;
        private String paidReceAmt;
        private String notPaidReceAmt;
        private String beginDate;
        private String endDate;
        private String currency;
        private String memo;
        private String isInvoiceIncl;
        private String payDateNo;
        @JSONFieldRequired
        private List<DCP_ReconliationUpdateReq.ReconList> reconList;

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
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;


    }

    @Data
    public class ReconList{
        //private String organizationNo;
        //private String organizationName;
        //private String corp;
        //private String corpName;
        //private String reconNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String sourceType;
        @JSONFieldRequired
        private String sourceNo;
        @JSONFieldRequired
        private String sourceNoSeq;
        @JSONFieldRequired
        private String rDate;
        private String fee;
        private String feeName;
        private String pluNo;
        private String pluName;
        private String currency;
        private String taxRate;
        @JSONFieldRequired
        private String direction;
        @JSONFieldRequired
        private String billQty;
        private String reconQty;
        @JSONFieldRequired
        private String billPrice;
        @JSONFieldRequired
        private String preTaxAmt;
        @JSONFieldRequired
        private String amt;
        private String reconAmt;
        private String unPaidAmt;
        @JSONFieldRequired
        private String currReconAmt;
        private String departId;
        private String cateGory;
        private String isInvoiceIncl;
        private String taxCode;
    }

}
