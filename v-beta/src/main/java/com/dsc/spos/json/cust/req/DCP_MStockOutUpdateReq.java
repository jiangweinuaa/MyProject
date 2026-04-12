package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MStockOutUpdateReq  extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MStockOutUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest {
        @JSONFieldRequired
        private String mStockOutNo;
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String docType;
        @JSONFieldRequired
        private String totPQty;
        @JSONFieldRequired
        private String totCQty;
        @JSONFieldRequired
        private String totAmt;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        private String adjustStatus;
        private String oMStockOutNo;
        private String oType;
        private String ofNo;
        private String oOType;
        private String oOfNo;
        private String memo;
        private String loadDocType;
        private String loadDocNo;
        @JSONFieldRequired
        private List<DCP_MStockOutUpdateReq.Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String warehouse;
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String featureNo;
        @JSONFieldRequired
        private String pUnit;
        private String oPQty;
        @JSONFieldRequired
        private String pQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        @JSONFieldRequired
        private String price;
        @JSONFieldRequired
        private String amt;
        @JSONFieldRequired
        private String distriPrice;
        @JSONFieldRequired
        private String distriAmt;
        private String batchNo;
        private String prodDate;
        private String loseDate;
        @JSONFieldRequired
        private String isBuckle;
        private String location;
        private String oType;
        private String ofNo;
        private String oItem;
        @JSONFieldRequired
        private String oOType;
        @JSONFieldRequired
        private String oOfNo;
        private String oOItem;
        private String loadDocItem;
        private String pItem;
        private String processNo;
        private String sItem;
        private String zItem;

    }

}
