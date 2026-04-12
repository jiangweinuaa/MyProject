package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VendorAdjCreateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_VendorAdjCreateReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String organizationNo;
        private String org_Name;
        private String adjustNO;
        @JSONFieldRequired
        private String otype;
        private String sStockInNo;
        @JSONFieldRequired
        private String supplierNo;
        private String supplierName;
        private String payDateNo;
        private String billDateNo;
        private String taxCode;
        private String taxRate;
        private String currency;
        private String currencyName;
        private String exRate;
        private String tot_Amt;
        private String meno;
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

        @JSONFieldRequired
        private List<DCP_VendorAdjCreateReq.AdjList> adjList;
    }

    @Data
    public class AdjList{
        @JSONFieldRequired
        private String organizationNo;
        private String org_Name;
        private String adjustNO;
        private String sStockInNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pqty;
        @JSONFieldRequired
        private String punit;
        private String receiving_Qty;
        private String proc_Rate;
        @JSONFieldRequired
        private String baseUnit;
        @JSONFieldRequired
        private String baseQty;
        @JSONFieldRequired
        private String unit_Ratio;
        private String poQty;
        private String stockIn_Qty;
        private String retwQty;
        private String purOrderNo;
        private String poItem;
        @JSONFieldRequired
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String location;
        private String batch_No;
        private String distriPrice;
        private String distriAmt;
        @JSONFieldRequired
        private String taxCode;
        @JSONFieldRequired
        private String taxRate;
        @JSONFieldRequired
        private String inclTax;
        private String amt;
        private String preTaxAmt;
        private String taxAmt;

        private String adjTaxAmt;
        private String adjAmtPreTax;
        private String adjAmt;

        private String adjPrice;
        private String adjTaxAmted;
        private String adjAmtPreTaxed;
        private String adjAmtTaxed;
        private String memo;
    }
}
