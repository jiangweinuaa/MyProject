package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TransApplyDetailQueryRes extends JsonRes {

    private List<DCP_TransApplyDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{
        private String status;
        private String bDate;
        private String billNo;
        private String transType;
        private String transOutOrgNo;
        private String transOutOrgName;
        private String transOutWarehouse;
        private String transOutWarehouseName;
        private String transInOrgNo;
        private String transInOrgName;
        private String transInWarehouse;
        private String transInWarehouseName;
        private String isTranInConfirm;
        private String rDate;
        private String pTemplateNo;
        private String pTemplateName;
        private String reason;
        private String totCqty;
        private String totPoQty;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String memo;
        private String createBy;
        private String createByName;
        private String createDeptId;
        private String createDeptName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String submitBy;
        private String submitByName;
        private String submitTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;
        private String closeBy;
        private String closeByName;
        private String closeTime;
        private String applyType;

        private List<DCP_TransApplyDetailQueryRes.Detail> detail;
        private List<DCP_TransApplyDetailQueryRes.Source> source;

    }

    @Data
    public class Detail{
        private String item;
        private String listImage;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String poQty;
        private String pQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String price;
        private String amt;
        private String distriPrice;
        private String distriAmt;
        private String memo;
        private String status;
        private String reason;
        private String stockOutQty;
        private String stockInQty;
        private String pickMinQty;
        private String pickMulQty;
    }

    @Data
    public class Source{
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String oType;
        private String ofNo;
        private String oItem;
        private String pQty;
        private String pUnit;
        private String pUnitName;
    }
}