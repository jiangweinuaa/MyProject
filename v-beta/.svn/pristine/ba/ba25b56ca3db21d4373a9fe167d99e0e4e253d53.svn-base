package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockAdjustCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_StockAdjustCreateReq.Request request;

    @Data
    public class Request {
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String docType;
        private String accountDate;
        private String oType;
        private String ofNo;
        private String loadDocType;
        private String loadDocNo;
        @JSONFieldRequired
        private String warehouse;
        @JSONFieldRequired
        private String totCqty;
        @JSONFieldRequired
        private String totPqty;
        @JSONFieldRequired
        private String totAmt;
        @JSONFieldRequired
        private String totDistriAmt;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        private String memo;
        @JSONFieldRequired
        private List<Detail> detail;
    }

    @Data
    public class Detail {
        @JSONFieldRequired
        private String item;
        private String oItem;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        private String location;
        private String batchNo;
        private String prodDate;
        private String expDate;
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
        private String refWqty;
        @JSONFieldRequired
        private String price;
        @JSONFieldRequired
        private String amt;
        @JSONFieldRequired
        private String distriPrice;
        @JSONFieldRequired
        private String distriAmt;
        private String memo;


    }
}
