package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReturnApplyProDetailQueryRes extends JsonRes {
    private List<DCP_ReturnApplyProDetailQueryRes.detail> datas;

    @Data
    public class detail{
        private String billNo;
        private String orgNo;
        private String orgName;
        private String bDate;
        private String item;
        private String approveStatus;
        private String pluNo;
        private String pluBarcode;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String approveQty;
        private String approvePrice;
        private String approveAmt;
        private String approveEmpId;
        private String approveEmpName;
        private String approveDeptId;
        private String approveDeptName;
        private String approveDate;
        private String stockOutNo;
        private String purType;
        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String returnType;
        private String receiptOrgNo;
        private String receiptOrgName;
        private String approveOrgNo;
        private String approveOrgName;
        private String reason;
        private String price;
        private String amt;
        private String distriPrice;
        private String distriAmt;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String batchNo;
        private String prodDate;
        private String expDate;

    }


}
