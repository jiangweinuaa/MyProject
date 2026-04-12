package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurchaseApplyTrackRes extends JsonRes {

    private List<DCP_PurchaseApplyTrackRes.Level1Elm> datas;


    @Data
    public class Level1Elm {
        private String billNo;
        private String totCqty;
        private String totPqty;
        private String totPurQty;
        private String totReceiveQty;
        private String totStockInQty;
        private List<Detail> detail;
    }

    @Data
    public class PurOrder{
        private String supplier;
        private String supplierName;
        private String purOrderNo;
        private String purOrderStatus;
        private String arrivalDate;
        private String purUnit;
        private String purUnitName;
        private String purQty;
        private String receiveQty;
        private String stockInQty;

    }

    @Data
    public class Detail{
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private List<PurOrder> purOrder;
    }

}
