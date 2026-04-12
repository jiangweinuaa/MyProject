package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ShopDateSaleQueryReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level2Elm> dataList;
        private List<levelPluElm> pluList;

    }

    @Data
    public class level2Elm
    {
        private String sDate;//日期（格式yyyymmdd）
    }

    @Data
    public class levelPluElm
    {
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String pUnitUdLength;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String mulQty;
    }


}
