package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.math.RoundingMode;
import java.util.List;

@Data
public class DCP_ProcessTaskPendingMaterialRes extends JsonRes {

    private DCP_ProcessTaskPendingMaterialRes.level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<DataList> dataList;
    }

    @Data
    public class DataList{
        private String materialPluNo;
        private String materialPluName;
        private String spec;
        private String materialPQty;
        private String materialPUnit;
        private String materialPUName;
        private String materialPUdLength;
        private String isBuckle;
        private String adviceQty;
        private String baseUnit;
        private String pickUnit;
        private String pickUName;
        private String pickUdLength;
        private String pickUnitRatio;
        private RoundingMode pickRoundingMode;
        private String stockQty;
        private String pickMulQty;
        private String pickMinQty;
        private String price;
        private String distriPrice;
        private String pWarehouse;
        private String pWarehouseName;
        private String kWarehouse;
        private String kWarehouseName;
        private String oOType;
        private String oOfNo;
        private String oOItem;
        private String materialIsBatch;
        private String isLocation;
    }


}
