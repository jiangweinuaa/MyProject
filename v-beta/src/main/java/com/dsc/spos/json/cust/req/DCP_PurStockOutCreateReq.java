package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_PurStockOutCreateReq extends JsonBasicReq {

    @Getter
    @Setter
    @JSONFieldRequired
    private DCP_PurStockOutCreateReq.Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "收货/入库组织")
        private String orgNo;
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
        @JSONFieldRequired(display = "扣账日期")
        private String accountDate;
        @JSONFieldRequired(display = "单据类型")
        private String billType;
        private String receivingNo;
        private String sourceType;
        private String sourceBillNo;
        @JSONFieldRequired(display = "供应商编码")
        private String supplierNo;
        private String payType;
        private String payOrgNo;
        private String billDateNo;
        private String payDateNo;
        private String invoiceCode;
        private String currency;
        private String purOrgNo;
        @JSONFieldRequired(display = "收货/入库仓库")
        private String wareHouse;
        @JSONFieldRequired(display = "申请人员ID")
        private String employeeID;
        @JSONFieldRequired(display = "申请部门ID")
        private String departID;
        private String memo;

        private String returnType;
        @JSONFieldRequired(display = "收货/入库明细")
        private List<DCP_PurStockOutCreateReq.Detail> dataList;
    }

    @Getter
    @Setter
    public class Detail {
        @JSONFieldRequired(display = "项次")
        private String item;
        private String rItem;
        private String oItem;
        private String purOrderNo;
        private String poItem;
        private String poItem2;
        @JSONFieldRequired(display = "商品编号")
        private String pluNo;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
        @JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        private String isFree;
        @JSONFieldRequired(display = "收货/入库单位")
        private String pUnit;
        @JSONFieldRequired(display = "收货/入库数量")
        private String pQty;
        private String purPrice;
        private String purAmt;
        private String preTaxAmt;
        private String taxAmt;
        @JSONFieldRequired(display = "收货/入库仓库")
        private String wareHouse;
        private String prodDate;
        private String expDate;
        //private String isQc;
        private String memo;
        private String taxCode;
        private String taxRate;
        private String inclTax;

        private String bsNo;
        private String purTemplateNo;

        private List<DCP_PurStockOutCreateReq.MulitiLosts> mulitiLotsList;

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
        private String oItem2;
    }


}
