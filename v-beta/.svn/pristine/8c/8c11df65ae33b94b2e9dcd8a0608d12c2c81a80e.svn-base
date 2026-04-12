package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DCP_PurStockInCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired
        private String orgNo;
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String billType;
        private String receivingNo;
        private String sourceType;
        private String sourceBillNo;
        @JSONFieldRequired
        private String supplierNo;
        private String payType;
        private String payOrgNo;
        private String billDateNo;
        private String payDateNo;
        private String invoiceCode;
        private String currency;
        private String purOrgNo;
        @JSONFieldRequired
        private String wareHouse;
        @JSONFieldRequired
        private String employeeID;
        @JSONFieldRequired
        private String departID;
        private String memo;
        private String deliveryFee;
        private String bsNo;
        private String returnType;
        @JSONFieldRequired
        private List<Detail> dataList;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = {"pluNo","featureNo"}) //去重需要重写equals
    public class Detail {
        @JSONFieldRequired
        private String item;
        private String rItem;
        private String oItem;
        private String purOrderNo;
        private String poItem;
        private String poItem2;
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String featureNo;
        @JSONFieldRequired
        private String pluBarcode;
        private String isFree;
        private String pUnit;
        private String pQty;
        private String purPrice;
        private String purAmt;
        private String preTaxAmt;
        private String taxAmt;
        @JSONFieldRequired
        private String wareHouse;
        private String prodDate;
        private String expDate;
        private String isQc;
        private String memo;
        private String taxCode;
        private String taxRate;
        private String inclTax;
        private List<MulitiLosts> mulitiLotsList;

    }

    @Getter
    @Setter
    public class MulitiLosts {
        private String item2;
        private String location;
        private String batchNo;
        private String prodDate;
        private String expDate;
        private String pQty;
    }


}
