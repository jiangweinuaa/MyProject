package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DCP_ReceivingUpdateReq extends JsonBasicReq {

    private Request request;

    @Getter
    @Setter
    public class Request {

        @JSONFieldRequired
        private String orgNo;
        @JSONFieldRequired
        private String bDate;

        private String docType;
        @JSONFieldRequired
        private String receivingNo;

        @JSONFieldRequired
        private String receiptOrgNo;
        private String receiptAddress;

        private String loadDocType;

        private String loadDocNo;

        private String supplierNo;
        private String receiptDate;

        private String wareHouse;
        @JSONFieldRequired
        private String employeeID;
        @JSONFieldRequired
        private String departID;
        private String memo;
        private String deliveryNo;

        private String oType;
        private String ofNo;
        private String totPqty;
        private String totCqty;
        private String totDistriAmt;
        private String totAmt;

        @JSONFieldRequired
        private List<Receipt> dataList;


        private String payType;
        private String payOrgNo;
        private String payDateNo;
        private String billDateNo;
        private String invoiceCode;
        private String currency;
        private String payee;
        private String payer;

        private String customer;

        private String corp;
        private String receiptCorp;
        private String totPurAmt;

    }

    @Getter
    @Setter
    public class Receipt {
        @JSONFieldRequired
        private String item;

        private String oItem;
        private String oItem2;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String pluBarcode;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String distriPrice;
        @JSONFieldRequired
        private String distriAmt;
        private String price;
        private String amt;
        private String batchNo;
        private String prodDate;
        private String procRate;
        private String memo;
        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String isGift;
        private String oType;
        private String ofNo;
        private String unitRatio;
        private String baseQty;
        private String baseUnit;
        private String taxCalType;

        private String purPrice;
        private String purAmt;
        private String category;

        private String supPrice;
        private String supAmt;
    }

}
