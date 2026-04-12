package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProcessPOrderQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> dataList;

    }

    @Data
    public class level2Elm
    {
        private String pOrderNo;
        private String oShop;
        private String oShopName;
        private String rDate;
        private String pTemplateNo;
        private String pTemplateName;
        private String memo;
        private double totPQty;
        private double totCQty;
        private List<level3Elm> detailList;
    }

    @Data
    public class level3Elm
    {
        private String item;
        private String pluNo;
        private String pUnit;
        private String pUName;
        private String unitUdLength;
        private double pQty;
        private double productQty;
        private String pluName;
        private String spec;
        private String price;
        private String distriPrice;
        private  String baseUnit;
        private  String baseQty;
        private  String unitRatio;
        private  String mulQty;
        private String baseUnitUdLength;
        private  String featureNo;
        private  String featureName;


    }

}
