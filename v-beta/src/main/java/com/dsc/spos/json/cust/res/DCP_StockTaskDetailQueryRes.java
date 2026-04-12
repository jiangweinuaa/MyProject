package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockTaskDetailQueryRes extends JsonRes {

    private List<DCP_StockTaskDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String stockTaskNo;
        private String stockTaskID;
        private String sDate;
        private String bDate;
        private String docType;
        private String pTemplateNo;
        private String pTemplateName;
        private String createType;
        private String taskWay;
        private String isBTake;
        private String status;
        private String isAdjustStock;
        private String notGoodsMode;
        private String totCqty;
        private String totSubTaskQty;
        private String warehouseType;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String memo;
        private String createDate;
        private String createTime;
        private String createBy;
        private String createByName;
        private String createDeptId;
        private String createDeptName;
        private String modifyDate;
        private String modifyTime;
        private String modifyBy;
        private String modifyByName;
        private String confirmBy;
        private String confirmByName;
        private String confirmDate;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDate;
        private String cancelTime;
        private List<Detail> detail;
        private List<OrgList> orgList;

    }

    @Data
    public class Detail{
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String spec;
        private String sdPrice;
        private String category;
        private String categoryName;
    }

    @Data
    public class OrgList{
        private String organizationNo;
        private String organizationName;
        private String warehouse;
        private String warehouseName;
    }

}
