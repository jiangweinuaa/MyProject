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
public class DCP_SalePriceTemplateGoodsAddReq extends JsonBasicReq {

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
        @JSONFieldRequired(display = "最低价折扣率：存储100=100%")
        private String minPriceDiscRate;
        @JSONFieldRequired(display = "失效日期YYYY-MM-DD")
        private String endDate;
        @JSONFieldRequired(display = "售价折扣率：存储100=100%")
        private String priceDiscRate;
        @JSONFieldRequired(display = "编码")
        private String id;
        @JSONFieldRequired(display = "是否允许折上折Y/N")
        private String isDiscount;
        @JSONFieldRequired(display = "CATEGORY-商品分类；GOODS-商品；")
        private String type;
        @JSONFieldRequired(display = "是否参与促销Y/N")
        private String isProm;
    }

}