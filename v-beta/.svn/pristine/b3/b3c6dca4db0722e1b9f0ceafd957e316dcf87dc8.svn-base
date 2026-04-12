package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdScheduleDetailQueryRes extends JsonRes {

    private List<DCP_ProdScheduleDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String bDate;
        private String billNo;
        private String beginDate;
        private String endDate;
        private String semiWOGenType;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String totCqty;
        private String totPqty;
        private String totWOQty;
        private String memo;
        private String status;
        private String createBy;
        private String createByName;
        private String createTime;
        private String createDeptId;
        private String createDeptName;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;
        private String closeBy;
        private String closeByName;
        private String closeTime;
        private List<Detail> detail;
        private List<GenList> genList;

    }

    @Data
    public class Detail{
        private String item;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String upPluNo;
        private String upPluName;
        private String rDate;
        private String pGroupNo;
        private String pGroupName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String poQty;
        private String stockQty;
        private String shortQty;
        private String adviceQty;
        private String minQty;
        private String mulQty;
        private String remainType;
        private String preDays;
        private String bomNo;
        private String versionNum;
        private String gItem;
        private String sourceType;
        private String memo;
        private String oddValue;
        private List<SourceList> sourceList;

    }

    @Data
    public class SourceList{
        private String item;
        private String oItem;
        private String orderType;
        private String orderNo;
        private String orderItem;
        private String objectType;
        private String objectId;
        private String objectName;
        private String rUnit;
        private String rUnitName;
        private String rQty;
        private String poQty;
        private String pTemplateNo;
        private String pTemplateName;
    }

    @Data
    public class GenList{
        private String item;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String pGroupNo;
        private String pGroupName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String unitRatio;
        private String rDate;
        private String preDays;
        private String beginDate;
        private String endDate;
        private String toWOQty;
        private String bomNo;
        private String verisonNum;
        private String prodType;
        private String upPluNo;
        private String upPluName;
        private List<UpPluList> upPluList;
        private List<WoList> woList;

    }

    @Data
    public class UpPluList{
        private String oItem;
        private String upPluNo;
        private String upPluName;
        private String rDate;
        private String upPUnit;
        private String upPUnitName;
        private String upPQty;
        private String pQty;
    }

    @Data
    public class WoList{
        private String mONo;
        private String mOItem;
        private String mOBeginDate;
        private String mOEndDate;
        private String wOQty;
        private String disPatchQty;
        private String disPatchStatus;
    }
}
