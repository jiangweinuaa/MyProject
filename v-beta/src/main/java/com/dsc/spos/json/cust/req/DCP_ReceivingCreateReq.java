package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：ReceivingCreateReq
 *   說明：收货通知单新增
 * 服务说明：收货通知单新增
 * @author chensong
 * @since  2016-10-12
 */


@Setter
@Getter
public class DCP_ReceivingCreateReq extends JsonBasicReq{

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {

        private String autoConfirm;

		@JSONFieldRequired
        private String orgNo;
		@JSONFieldRequired
        private String bDate;
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


        private String docType;
        private String customer;

		@JSONFieldRequired
        private List<Detail> dataList;

        private String payType;
        private String payOrgNo;
        private String payDateNo;
        private String billDateNo;
        private String invoiceCode;
        private String currency;
        private String payee;
        private String payer;

        private String corp;
        private String receiptCorp;
        private String totPurAmt;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = {"featureNo","pluNo"})
    public class Detail {
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

        private String wareHouse;
        //@JSONFieldRequired(display = "进货单价")
        private String distriPrice;
        //@JSONFieldRequired(display = "进货金额(含税)")
        private String distriAmt;
        private String price;
        private String amt;
        private String batchNo;
        private String prodDate;
        private String procRate;
        private String memo;
        private String ofNo;
        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String isGift;
        private String oType;
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
