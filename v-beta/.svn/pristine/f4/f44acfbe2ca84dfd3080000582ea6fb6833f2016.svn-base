package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/25
 */
@Getter
@Setter
public class DCP_PurReceiveCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "收货通知单号")
        private String receivingNo;
        private String memo;
        @JSONFieldRequired(display = "申请人员ID")
        private String employeeID;
        private String purOrgNo;
        private String invoiceCode;
        @JSONFieldRequired(display = "收货仓库")
        private String wareHouse;
        @JSONFieldRequired(display = "采购单号")
        private String purOrderNo;
        private String payType;
        private String billDateNo;
        private String deliveryFee;
        @JSONFieldRequired(display = "收货组织")
        private String orgNo;
        private String payOrgNo;
        @JSONFieldRequired(display = "单据日期 格式:YYYYMMDD")
        private String bDate;
        @JSONFieldRequired(display = "供应商编号")
        private String supplier;
        @JSONFieldRequired(display = "收货明细")
        private List<DataList> dataList;
        @JSONFieldRequired(display = "申请部门ID")
        private String departID;
        private String currency;
        private String payDateNo;
        private String payee;

        private String corp;
        private String bizOrgNo;
        private String bizCorp;

        private String purType;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;

    }

    @Getter
    @Setter
    public class DataList {
        @JSONFieldRequired(display = "项次")
        private String item;
        private String qcStatus;
        private String receivingNo;
        @JSONFieldRequired(display = "收货单位")
        private String pUnit;
        @JSONFieldRequired(display = "商品编号")
        private String pluNo;
        private String pluName;
        private String pluBarcode;
        private String memo;
        private String baseQty;
        private String rItem;
        private String taxCode;
        private String expDate;
        private String poItem;
        private String wareHouse;
        private String prodDate;
        private String purOrderNo;
        private String taxRate;
        private String baseUnit;
        @JSONFieldRequired(display = "收货数量")
        private String pQty;
        private String inclTax;
        private String featureNo;
        private String isGift;
        private String poItem2;
        private String unitRatio;
  
        private String batchNo;
        private String taxCalType;

        private String receivePrice;
        private String receiveAmt;
        private String supPrice;
        private String supAmt;

        private String procRate;
    }

}