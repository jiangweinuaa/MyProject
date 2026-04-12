package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ShopDateSaleQueryRes extends JsonRes
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
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String averageQty;
        private String baseUnit;
        private String baseUnitName;
        private String adviceQty;
        private String pUnit;
        private String pUnitName;
        private String earliestSaleTime;
        private String lastestSaleTime;

        private List<level3Elm> saleList;
    }

    @Data
    public class level3Elm
    {
        private String sDate;
        private String dayEarliestSaleTime;
        private String dayLastestSaleTime;
        private String saleQty;
    }

}
