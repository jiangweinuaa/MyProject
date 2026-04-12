package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/19
 */
@Getter
@Setter
public class DCP_GoodsDeliveryQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "开始日期yyyyMMdd")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期yyyyMMdd")
        private String endDate;
        @JSONFieldRequired(display = "商品列表")
        private List<PluList> pluList;
    }

    @Getter
    @Setter
    public class PluList {
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
    }

}