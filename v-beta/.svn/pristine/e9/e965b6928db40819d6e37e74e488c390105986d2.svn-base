package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/10
 */
@Getter
@Setter
public class DCP_CustomerPriceAdjustCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "生效客户")
        private List<CustList> custList;
        @JSONFieldRequired(display = "价格生效日，格式YYYYMMDD")
        private String beginDate;
        private String totCqty;
        @JSONFieldRequired(display = "单据日期，格式YYYYMMDD")
        private String bDate;
        @JSONFieldRequired(display = "价格截止日，格式YYYYMMDD")
        private String endDate;
        @JSONFieldRequired(display = "定价部门")
        private String departId;
        private String memo;
        @JSONFieldRequired(display = "定价人员")
        private String employeeId;
        @JSONFieldRequired(display = "商品明细")
        private List<Detail> detail;
        private String status;
    }

    @Getter
    @Setter
    public class CustList {
        @JSONFieldRequired(display = "项次")
        private String item;
        @JSONFieldRequired(display = "客户类型 1客户组 2客户")
        private String customerType;
        @JSONFieldRequired(display = "客户组/客户编号")
        private String customerNo;
    }

    @Getter
    @Setter
    public class Detail {
        @JSONFieldRequired(display = "项次")
        private String item;
        @JSONFieldRequired(display = "计价单位")
        private String unit;
        @JSONFieldRequired(display = "价格")
        private String price;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        @JSONFieldRequired(display = "商品分类")
        private String category;
        @JSONFieldRequired(display = "零售价")
        private String retailPrice;
        @JSONFieldRequired(display = "折扣率")
        private String discRate;
    }

}