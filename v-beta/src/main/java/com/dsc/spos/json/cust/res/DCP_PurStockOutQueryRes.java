package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurStockOutQueryRes  extends JsonRes {

    private List<DCP_PurStockOutQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String status;
        private String pStockOutNo;
        private String billType;
        private String orgNo;
        private String orgName;
        private String bDate;
        private String accountDate;
        private String supplierNo;
        private String supplierName;
        private String payType;
        private String payOrgNo;
        private String payOrgName;
        private String billDateNo;
        private String billDateDesc;
        private String payDateNo;
        private String payDateDesc;
        private String invoiceCode;
        private String invoiceName;
        private String currency;
        private String currencyName;
        private String sourceType;
        private String sourceBillNo;
        private String receivingNo;
        private String rDate;
        private String wareHouse;
        private String wareHouseName;
        private String totCqty;
        private String totPqty;
        private String totPurAmt;
        private String isLocation;
        private String employeeID;
        private String employeeName;
        private String departID;
        private String departName;
        private String createBy;
        private String createByName;
        private String createDateTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDateTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDateTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDateTime;
        private String ownOpID;
        private String ownOpName;
        private String ownDeptID;
        private String ownDeptName;
        private String accountBy;
        private String accountByName;
        private String accountDateTime;

    }
}
