package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/26
 */
@Getter
@Setter
public class DCP_SalePriceTemplateGoodsUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "商品模板")
        private String templateId;
        @JSONFieldRequired(display = "商品列表")
        private List<PluList> pluList;
    }

    @Getter
    @Setter
    public class PluList {
        @JSONFieldRequired(display = "生效日期YYYY-MM-DD")
        private String beginDate;
        @JSONFieldRequired(display = "序号")
        private String item;
        @JSONFieldRequired(display = "失效日期YYYY-MM-DD")
        private String endDate;
        @JSONFieldRequired(display = "售价")
        private String price;
        @JSONFieldRequired(display = "商品编码")
        private String pluNo;
        @JSONFieldRequired(display = "最低售价")
        private String minPrice;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
        @JSONFieldRequired(display = "是否允许折上折Y/N")
        private String isDiscount;
        @JSONFieldRequired(display = "是否参与促销Y/N")
        private String isProm;
        @JSONFieldRequired(display = "有效否：100-有效0-无效")
        private String status;
    }

}