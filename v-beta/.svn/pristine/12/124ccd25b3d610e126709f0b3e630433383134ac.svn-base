package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/28
 */
@Getter
@Setter
public class DCP_PriceAdjustCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "模板编号：采购模板/零售价模板")
        private String templateNo;
        @JSONFieldRequired(display = "单据日期")
        private String bdate;
        private String isUpdateSdPrice;
        private String memo;
        @JSONFieldRequired(display = "调价人员")
        private String employeeID;
        private String invalidDate;
        @JSONFieldRequired(display = "商品明细")
        private List<PluList> pluList;
        @JSONFieldRequired(display = "服务唯一ID号")
        private String billno_ID;
        @JSONFieldRequired(display = "单据类型")
        private String bType;
        private String isUpdate_DefpurPrice;
        @JSONFieldRequired(display = "申请组织")
        private String orgNo;
        private String supplier;
        @JSONFieldRequired(display = "调价部门")
        private String departID;
        @JSONFieldRequired(display = "价格生效日")
        private String effectiveDate;
    }



    @Getter
    @Setter
    public class PluList {
        @JSONFieldRequired(display = "计价单位")
        private String priceUnit;
        @JSONFieldRequired(display = "项次")
        private String item;
        private String npurPrice;
        private String opurPriceType;
        private String opurPrice;
        @JSONFieldRequired(display = "商品编号")
        private String pluno;
        @JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        private String isDiscount;
        private String isProm;
        private List<NpriceList> npriceList;
        private String sdPrice;
        private String price;
        private String minPrice;
        private List<OpriceList> opriceList;
        private String npurPriceType;
        private String oPrice;
    }


    @Getter
    @Setter
    public class NpriceList {
        @JSONFieldRequired(display = "项序")
        private String item2;
        @JSONFieldRequired(display = "新采购单价")
        private String npurPrice;
        @JSONFieldRequired(display = "新起始数量")
        private String nbeginQty;
        @JSONFieldRequired(display = "新截止数量")
        private String nendQty;
    }

    @Getter
    @Setter
    public class OpriceList {
        @JSONFieldRequired(display = "项序")
        private String item2;
        @JSONFieldRequired(display = "原截止数量")
        private String oendQty;
        @JSONFieldRequired(display = "原采购单价")
        private String opurPrice;
        @JSONFieldRequired(display = "原起始数量")
        private String obeginQty;
    }

}