package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PStockOutUpdateReq extends JsonBasicReq {

    private DCP_PStockOutUpdateReq.levelElm request;

    @Data
    public class levelElm{
        private String pStockInNo;
        private String bDate;
        private String memo;
        private String pTemplateNo;
        private String warehouse;
        private String materialWarehouseNo;
        private String docType;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String totCqty;
        private String processPlanNo;
        private String task0No;
        private String employeeId;
        private String departId;
        private List<DCP_PStockOutUpdateReq.level1Elm> datas;

    }

    @Data
    public  class level1Elm {
        private String item;
        private String oItem;
        private String pluNo;
        private String punit;
        private String pqty;
        private String price;
        private String amt;
        private String scrapQty;
        private String taskQty;
        private String mulQty;
        private String bsNo;
        private String warehouse;
        private String batchNo;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String featureNo;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String memo;
        private String location;
        private String expDate;
        private List<DCP_PStockOutUpdateReq.level2Elm> material;
        private List<DCP_PStockOutUpdateReq.level3Elm> batchList;

        @JSONFieldRequired
        private String prodType;
        @JSONFieldRequired
        private String bomNo;
        @JSONFieldRequired
        private String versionNum;
    }

    @Data
    public class level3Elm{
        private String item;
        private String location;
        private String batchNo;
        private String prodDate;
        private String expDate;
        private String pUnit;
        private String pQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
    }

    @Data
    public  class level2Elm{
        private String mItem;
        private String material_item;
        private String material_warehouse;
        private String material_pluNo;
        private String material_punit;
        private String material_pqty;
        private String material_price;
        private String material_amt;
        private String material_finalProdBaseQty;    //成品基础量       从BOM服务中获取
        private String material_rawMaterialBaseQty;  //原料基础用量
        private String material_batchNo;
        private String material_prodDate;
        private String material_distriPrice;
        private String material_distriAmt;
        private String isBuckle;
        private String material_featureNo;
        private String material_baseUnit;
        private String material_baseQty;
        private String material_unitRatio;
        private String material_location;
        private String material_expDate;
        private String costRate;
    }


    @Data
    public class StockInfo{
        private String pluNo;
        private String featureNo;
        private String warehouse;
        private String batchNo;
        private String location;
        private String baseUnit;
        private String qty;
        private String lockQty;
        private String prodDate;
        private String validDate;
    }

}
