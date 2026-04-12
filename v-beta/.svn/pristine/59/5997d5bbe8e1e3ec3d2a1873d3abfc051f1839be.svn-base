package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/16
 */
@Getter
@Setter
public class DCP_SStockInCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "入库单唯一标识（服务使用）")
        private String sStockInID;
        private String loadDocNo;
        private String memo;
        private String ofNo;
        private String oType;
        private String taxName;
        private double totCqty;
        private String payType;
        private String billDateNo;
        private String ooType;
        private String loadReceiptNo;

        private String supplier;
        @JSONFieldRequired(display = "申请部门")
        private String departId;
        private String currency;
        private double totDistriAmt;
        private String receipDate;
        private String orderOrgNo;
        private String receivingNo;
        private String docType;
        private String deliveryNo;
        @JSONFieldRequired(display = "申请人员")
        private String employeeId;
        private double totAmt;
        private String taxCode;
        @JSONFieldRequired(display = "收货仓库")
        private String warehouse;
        private String invoiceCode;
        private String loadDocType;
        private String pTemplateNo;
        @JSONFieldRequired(display = "入库类型")
        private String stockInType;
        private String payOrgNo;
        @JSONFieldRequired(display = "单据日期YYYYMMDD")
        private String bDate;
        private String originNo;
        private String buyerNo;
        private String buyerName;
        private String oofNo;
        private double totPqty;
        private String payDateNo;
        private String returnType;
        private String status;
        private String customer;
        private String purOrderNo;

        private String payee;//付款对象
        private String payer;//收款对象
        private String corp;
        private String bizOrgNo;
        private String bizCorp;

        private String purType;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;
        private String outputTaxCode;
        private String outputTaxRate;


        @JSONFieldRequired
        private List<Datas> datas;
    }


    @Getter
    @Setter
    public class Datas {
        private String receivingQty;
        @JSONFieldRequired(display = "商品编号")
        private String pluNo;

        private String pluBarcode;
        @JSONFieldRequired(display = "进货额")
        private String distriAmt;
        private String pluMemo;
        private String amt;
        @JSONFieldRequired(display = "基准数量")
        private String baseQty;
        private String ofNo;
        private String rItem;
        private String oType;
        private String expDate;
        @JSONFieldRequired(display = "基准单位")
        private String baseUnit;
        @JSONFieldRequired(display = "业务单位")
        private String punit;
        private String price;
        private String isGift;
        private String procRate;
        private String oItem;
        private String taxCalType;
        @JSONFieldRequired(display = "项次")
        private double item;
        private String batchNo;
        private String receivingNo;
        @JSONFieldRequired(display = "进货价")
        private double distriPrice;
        private String taxCode;
        private String warehouse;
        private String prodDate;
        private String location;
        private String taxRate;
        @JSONFieldRequired(display = "数量")
        private double pqty;
        private String pTemplateNo;
        private String inclTax;
        private String originNo;
        private String originItem;
        private String featureNo;
        private String punitUdLength;
        private String category;
        @JSONFieldRequired(display = "与基准单位转换率")
        private String unitRatio;

        private String ooItem;
        private String oofNo;
        private String ooType;

        private String purPrice;
        private String purAmt;

        private String custPrice;
        private String custAmt;

        private String preTaxAmt;
        private String taxAmt;

        private String sdPurPrice;//标准采购价 采购模板
        private String refCustPrice;
    }

}