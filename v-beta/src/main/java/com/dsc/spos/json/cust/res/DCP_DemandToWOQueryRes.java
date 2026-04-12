package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DemandToWOQueryRes  extends JsonRes
{
    private List<DCP_DemandToWOQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm{
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String rDate;
        private String prodUnit;
        private String prodUnitName;
        private String prodQty;
        private String stockQty;
        private String shortQty;
        private String baseUnit;
        private String unitRatio;
        private String minQty;
        private String mulQty;
        private String remainType;
        private String preDays;
        private String oddValue;
        private String pGroupNo;
        private String bomNo;
        private String versionNum;
        private List<Detail> detail;
    }


    @Data
    public class Detail{
        private String orderNo;
        private String orderItem;
        private String orderType;
        private String objectType;
        private String objectId;
        private String objectName;
        private String pUnit;
        private String pQty;
        private String prodUnit;
        private String prodQty;
        private String pTemplateNo;
        private String pTemplateName;
    }

}
