package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockTaskProgressQueryRes extends JsonRes {

    private List<DCP_StockTaskProgressQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm{
        private String stockTaskNo;
        private String totCqty;
        private String totTaskQty;
        private String totPendingTaskQty;
        private String totProcessTaskQty;
        private String totCompleteTaskQty;
        private String completeRate;
        private List<Detail> detail;
    }

    @Data
    public class Detail{
        private String organizationNo;
        private String organizationName;
        private String totSubTaskQty;
        private String completeRate;
        private List<WarehouseList> warehouseList;
    }

    @Data
    public class WarehouseList{
        private String subTaskNo;
        private String stockTakeNo;
        private String status;
        private String warehouse;
        private String warehouseName;
        private String sumQty;
        private String sumAmt;
        private String sumDistriAmt;
    }
}
