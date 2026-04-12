package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/29
 */
@Getter
@Setter
public class DCP_CustomerPOrderUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "发货组织")
        private String deliverOrgNo;
        @JSONFieldRequired(display = "折扣金额合计")
        private String totDiscAmt;
        private String salesManName;
        private String memo;
        private String totTaxAmt;
        private String deliverWarehouse;
        @JSONFieldRequired(display = "结算方式 1统结 2分结")
        private String payType;
        @JSONFieldRequired(display = "结算条件")
        private String billDateNo;
        private String totPreTaxAmt;
        @JSONFieldRequired(display = "订单编号")
        private String pOrderNo;
        private String contact;
        @JSONFieldRequired(display = "申请部门")
        private String departId;
        @JSONFieldRequired(display = "交易币别")
        private String currency;
        @JSONFieldRequired(display = "总数量")
        private String tot_qty;
        private String eId;
        private String templateNo;
        private String address;
        private String bDate;
        @JSONFieldRequired(display = "需求日期，格式YYYYMMDD")
        private String rDate;
        @JSONFieldRequired(display = "列表")
        private List<Datas> datas;
        private String salesManNo;
        private String telephone;
        @JSONFieldRequired(display = "申请人员")
        private String employeeId;
        @JSONFieldRequired(display = "发票类型")
        private String invoiceCode;
        private String customerName;
        @JSONFieldRequired(display = "总金额")
        private String tot_amt;
        @JSONFieldRequired(display = "品种数")
        private String tot_cqty;
        @JSONFieldRequired(display = "整单折扣率")
        private String discRate;
        private String salesDepartId;
        @JSONFieldRequired(display = "结算组织")
        private String payOrgNo;
        @JSONFieldRequired(display = "付款条件")
        private String payDateNo;
        private String shopNo;
        @JSONFieldRequired(display = "大客户编号")
        private String customerNo;

        private String payer;
    }

    @Getter
    @Setter
    public class Datas {
        private String item;

        private String deliverOrgNo;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        @JSONFieldRequired(display = "交易金额(含税)")
        private String amt;
        private String baseQty;
        @JSONFieldRequired(display = "基准单位")
        private String baseUnit;
        private String preTaxAmt;
        private String deliverWarehouse;
        @JSONFieldRequired(display = "交易价")
        private String price;
        @JSONFieldRequired(display = "是否赠品  0否1是")
        private String isGift;
        @JSONFieldRequired(display = "原单价")
        private String oPrice;
        private String taxAmt;
//        @JSONFieldRequired(display = "计税方式 1一般 2运费/农产品")
        private String taxCalType;
        @JSONFieldRequired(display = "零售金额")
        private String retailAmt;
//        @JSONFieldRequired(display = "税别编码")
        private String taxCode;
        @JSONFieldRequired(display = "折扣金额")
        private String discAmt;
        @JSONFieldRequired(display = "折扣率(%)")
        private String discRate;
//        @JSONFieldRequired(display = "税率")
        private String taxRate;
        @JSONFieldRequired(display = "单位")
        private String unit;
//        @JSONFieldRequired(display = "单价含税否Y/N")
        private String inclTax;
        @JSONFieldRequired(display = "数量")
        private String qty;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
        @JSONFieldRequired(display = "商品分类")
        private String category;
        @JSONFieldRequired(display = "零售价")
        private String retailPrice;
        @JSONFieldRequired(display = "单位换算率")
        private String unitRatio;
    }

}