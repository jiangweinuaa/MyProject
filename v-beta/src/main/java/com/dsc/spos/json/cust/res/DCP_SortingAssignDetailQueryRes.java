package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignDetailQueryRes extends JsonRes {

    private List<Datas> datas;

    @Getter
    @Setter
    public class Datas {
        private String billNo;
        private String oType;
        private String bDate;
        private String warehouse;
        private String warehouseName;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String status;
        private String totOrgCnt;
        private String totCqty;
        private String totPqty;
        private String createOpId;
        private String createOpName;
        private String createDeptId;
        private String createDeptName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String closeBy;
        private String closeByName;
        private String closeTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;

        private List<Detail> detail;
    }

    @Getter
    @Setter
    public class Detail {

        private String objectType;
        private String objectId;
        private String objectName;
        private String rDate;
        private String deliveryDate;
        private String ofNo;
        private String orderNo;
        private String routeNo;
        private String routeName;
        private String pTemplateNo;
        private String pTemplateName;
        private String totCqty;
        private String totPqty;
        private String totRecords;
        private String isAdditional;
        private String addTime;
        private String addBy;
        private String addByName;
        private String addIsDispatch;

        private List<DataList> dataList;
    }

    @Getter
    @Setter
    public class DataList {
        private String rowNum;
        private String oItem;
        private String orderItem;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String category;
        private String categoryName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String noQty;
        private String poQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;

    }

}
