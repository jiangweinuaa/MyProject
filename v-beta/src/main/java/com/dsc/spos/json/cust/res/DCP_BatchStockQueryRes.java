package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BatchStockQueryRes extends JsonRes {

    private List<LevelItem> batchList;

    @Data
    public class LevelItem{

        private String orgNo;
        private String orgName;
        private String wareHouse;
        private String wareHouseName;
        private String location;
        private String locationName;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String batchNo;
        private String prodDate;
        private String expDate;
        private String wUnit;
        private String wUnitName;
        private String stockQty;
        private String onlineQty;
        private String lockQty;
        private String availableStockQty;
        private String availablePQty;

        private String pStockQty;
        private String pAvailableStockQty;

        private List<UnitStock> unitStock;

    }

    @Data
    public class UnitStock{
        private String pUnit;
        private String pUnitName;
        private String pStockQty;
        private String pAvailableStockQty;
    }
}
