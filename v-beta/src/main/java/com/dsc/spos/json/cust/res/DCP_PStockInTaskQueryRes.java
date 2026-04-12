package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PStockInTaskQueryRes extends JsonRes
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
        private String processTaskNo;
        private String memo;
        private String pTemplateNo;
        private String pDate;
        private String warehouse;
        private String materialWarehouseNo;
        private String dtNo;
        private String dtName;
        private String dtBeginTime;
        private String dtEndTime;
        private String processPlanNo;
        private String task0No;
        private String item;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String punitUdLength;
        private String pqty;
        private String price;
        private String distriPrice;
        private String mulQty;
        private String baseUnit;
        private String baseUnitUdLength;
        private String unitRatio;
        private String featureNo;
        private String featureName;
        private String pStockInQty;
        private String pStockInStatus;
    }

}
