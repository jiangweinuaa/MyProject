package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DCP_DemandPreAllotCalculateRes extends JsonRes {

    private DCP_DemandPreAllotCalculateRes.level1Elm datas;

    @Data
    public class level1Elm{

        private List<PluList> pluList;
        private List<ObjectList> objectList;
    }

    @Data
    public class PluList{
        private String item;
        private String pluNo;
        private String pluName;
        private String pluBarCode;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private Double totPqty;
        private Double totStockOutNoQty;
        private Double totProcessQty;
        private Double totAllotQty;
        private Double totDiffQty;
        private String wareHouse;
        private String wareHouseName;
        private Double availableStockQty;
        private Integer totCqty;

        private List<ObjectListDetail> objectList;
    }

    @Data
    public class PluAngle{
        private String pluNo;
        private String featureNo;
        private String warehouse;
        private String baseUnit;
        private BigDecimal stockQty;

        private List<String> templateNos;//品号存在哪些模板
    }

    @Data
    public class ObjectList{
        private String item;
        private String objectType;
        private String objectId;
        private String objectName;
        private String orderNo;
        private String orderType;
        private String bDate;
        private String rDate;
        private String submitDateTime;
        private String isUrgent;
        private String isAdd;
        private String isMustAllot;
        private String sortId;
        private String distriOrgNo;
        private String distriOrgName;
        private Double totPqty;
        private Double totStockOutNoQty;
        private Double totProcessQty;
        private Double totAllotQty;
        private Double totDiffQty;
        private String templateNo;
        private String templateName;
        private Integer totCqty;


        private List<PluListDetail> pluList;
    }

    @Data
    public class ObjectListDetail{

        private String item;
        private String objectType;
        private String objectId;
        private String objectName;
        private String orderNo;
        private String orderType;
        private String orderItem;
        private String templateNo;
        private String templateName;
        private String isUrgent;
        private String isAdd;
        private Double pQty;
        private Double stockOutNoQty;
        private Double processQty;
        private Double allotQty;
        private Double diffQty;
        private String isMustAllot;
        private String sortId;
        private String submitDateTime;
        private String bDate;
        private String rDate;
        private String distriPrice;
        private String retailPrice;
        private String floatScale;
    }

    @Data
    public class PluListDetail{
        private String orderItem;
        private String item;
        private String pluNo;
        private String pluName;
        private String pluBarCode;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private Double pQty;
        private Double stockOutNoQty;
        private Double processQty;
        private Double allotQty;
        private Double diffQty;
        private Double availableStockQty;
        private String wareHouse;
        private String wareHouseName;
        private String distriPrice;
        private String retailPrice;

    }

    @Data
    public class DemandDetail{
        private String item;
        private String orderItem;
        private String objectType;
        private String objectId;
        private String objectName;
        private String orderNo;
        private String orderType;
        private String bDate;
        private String rDate;
        private String submitDateTime;
        private String isUrgent;
        private String isAdd;
        private String isMustAllot;
        private String sortId;
        private String distriOrgNo;
        private String distriOrgName;
        private BigDecimal floatScale;
        private String pluNo;
        private String pluName;
        private String pluBarCode;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pUnit;//需求单位
        private String pUnitName;
        private String udLegth;
        private String unitRatio;
        private BigDecimal pQty;//需求量
        private BigDecimal stockOutNoQty;//通知出货量
        private BigDecimal processQty;//待配量=需求量-转配量
        private BigDecimal processQtyBase;
        private BigDecimal allotQty;//本次分配量
        private BigDecimal allotQtyBase;
        private BigDecimal diffQty;//差异量
        private String wareHouse;
        private String wareHouseName;
        private String distriPrice;
        private String retailPrice;
        private String baseUnit;
        private String templateNo;
        private String templateName;

    }
}
