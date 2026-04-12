package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VendorAdjDetailQueryRes extends JsonRes {

    private List<DCP_VendorAdjDetailQueryRes.level1Elm> datas;

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
        private String taxName;
        private String taxRate;
        private String currency;
        private String currencyName;
        private String exRate;
        private String tot_Amt;
        private String memo;
        private String status;
        private List<AdjList> adjList;
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
        private String cancelBy;
        private String cancelByName;
        private String cancel_Date;
        private String cancel_Time;

    }

    @Data
    public class AdjList{
        private String organizationNo;
        private String adjustNO;
        private String sStockInNo;
        private String item;
        private String pqty;
        private String punit;
        private String punitName;
        private String baseUnitName;
        private String receiving_Qty;
        private String proc_Rate;
        private String baseUnit;
        private String baseQty;
        private String unit_Ratio;
        private String poQty;
        private String stockIn_Qty;
        private String retwQty;
        private String purOrderNo;
        private String poItem;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String location;
        private String batch_No;
        private String distriPrice;
        private String distriAmt;
        private String taxCode;
        private String taxName;
        private String locationName;
        private String taxRate;
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
