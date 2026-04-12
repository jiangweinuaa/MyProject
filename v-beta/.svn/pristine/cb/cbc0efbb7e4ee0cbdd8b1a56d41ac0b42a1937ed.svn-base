package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DCP_ApBillProcessReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_ApBillProcessReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String eId;
        @JSONFieldRequired
        private String corp;

        @JSONFieldRequired
        public String bDate;
        public String isInvoiceIncl;

        private List<NoList> noList;

        private List<SupplierList> supplierList;
    }

    @Data
    public class NoList{
        public String billNo;
        public String purInvNo;


    }

    @Data
    public class SupplierList{
        public String bizPartnerNo;
    }


    @Data
    public class czgList{
        private String apNo;//暂估单的单号

        //冲暂估的应付单 金额
        private BigDecimal fcyBtAmt;
        private BigDecimal lcyBtAmt;
    }

    @Data
    public class ApBillDetailList{
        private String accountId;
        private String organizationNo;
        private String apNo;
        private String item;
        private String bizPartnerNo;
        private String receiver;
        private String sourceType;
        private String sourceNo;
        private String sourceItem;
        private String sourceOrg;
        private String pluNo;
        private String spec;
        private String priceUnit;
        private String qty;
        private String billPrice;
        private String fee;
        private String oofNo;
        private String ooItem;
        private String departId;
        private String cateGory;
        private String isGift;
        private String bsNo;
        private String taxRate;
        private String feeSubjectId;
        private String taxSubjectId;
        private String direction;
        private String isRevEst;
        private String apSubjectId;
        private String payDateNo;
        private String employeeNo;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String memo;
        private String currency;
        private String fCYPrice;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String fCYStdCostAmt;
        private String fCYActCostAmt;
        private String lCYPrice;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYStdCostAmt;
        private String lCYActCostAmt;
        private String purOrderNo;
    }

    @Data
    public class ReconDetail{
        private String reconNo;
        private String item;
        private String apNo;

        private String sourceNo;
        private String sourceItem;
        private String qty;
        private String amt;
    }


}
