package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DistriOrderDetailQueryRes extends JsonBasicRes {

    private List<DCP_DistriOrderDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm{

        private String status;
        private String billNo;
        private String bDate;
        private String rDate;
        private String demandOrgNo;
        private String demandOrgName;
        private String memo;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String createOpId;
        private String createOpName;
        private String createDeptId;
        private String creatDeptName;
        private String createDateTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiDateTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDateTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDateTime;
        private List<DetailList> detailList;
    }

    @Data
    public class DetailList{

        private String item;
        private String demandOrgNo;
        private String demandOrgName;
        private String rDate;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String price;
        private String amt;
        private String distriPrice;
        private String distriAmt;
        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;

    }

}
