package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/27
 */
@Getter
@Setter
public class DCP_GoodsCustomerPriceDiscQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "客户编号")
        private String customerNo;

        @JSONFieldRequired(display = "商品列表")
        private List<PluList> pluList;
    }

    @Getter
    @Setter
    public class PluList {
        @JSONFieldRequired(display = "单位")
        private String unit;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
    }

}