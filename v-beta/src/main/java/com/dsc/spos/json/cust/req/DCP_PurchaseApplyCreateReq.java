package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurchaseApplyCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PurchaseApplyCreateReq.LevelRequest request;

    @Data
    public class LevelRequest {
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String rDate;
        @JSONFieldRequired
        private String billNo_ID;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        private String memo;
        @JSONFieldRequired
        private String totCqty;
        @JSONFieldRequired
        private String totPqty;
        @JSONFieldRequired
        private String totAmt;
        @JSONFieldRequired
        private String totPurAmt;

        private String receiptCorp;
        private String corp;

        @JSONFieldRequired
        private List<Detail> detail;

    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluNo;
        private String pluBarcode;
        private String featureNo;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String baseUnit;
        @JSONFieldRequired
        private String baseQty;
        @JSONFieldRequired
        private String unitRatio;
        @JSONFieldRequired
        private String propQty;
        private String refWqty;
        @JSONFieldRequired
        private String minQty;
        @JSONFieldRequired
        private String mulQty;
        @JSONFieldRequired
        private String price;
        @JSONFieldRequired
        private String amt;
        @JSONFieldRequired
        private String purPrice;
        @JSONFieldRequired
        private String purAmt;
        private String memo;
        //@JSONFieldRequired
        private String warehouse;

        private String supplier;
        private String templateNo;
        private String purType;
        private String preDays;
        private String arrivalDate;
        private String purDate;

    }
}
