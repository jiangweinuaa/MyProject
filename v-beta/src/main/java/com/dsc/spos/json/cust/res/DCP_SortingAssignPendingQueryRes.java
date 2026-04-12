package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class DCP_SortingAssignPendingQueryRes extends JsonRes {

    private List<Datas> datas;


    @Getter
    @Setter
    public class Datas {
        private String noticeNo;
        private String oType;
        private String ofNo;
        private String objectType;
        private String objectId;
        private String objectName;
        private String deliveryDate;
        private String rDate;
        private String warehouse;
        private String warehouseName;
        private String status;
        private String templateNo;
        private String templateName;
        private String totCqty;
        private String totPqty;

        private List<RouteList> routeList;
        private List<Detail> detail;
    }

    @Getter
    @Setter
    public class RouteList {

        private String routeNo;
        private String routeName;

    }

    @Getter
    @Setter
    public class Detail {
        private String rowNum;
        private String item;
        private String sourceType;
        private String sourceBillNo;
        private String oItem;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String category;
        private String categoryName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String poQty;
        private String stockOutQty;
        private String templateNo;
        private String templateName;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String status;

    }


}
