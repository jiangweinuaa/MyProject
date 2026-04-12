package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/26
 */
@Getter
@Setter
public class DCP_SalePriceTemplateGoodsQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        private String effectStatus;
        private String effectDate;
        private String ePrice;
        @JSONFieldRequired(display = "显示顺序：1-添加顺序降序2-商品编码升序")
        private String sortType;
        private String templateId;
        private String sPrice;
        private String category;
        private String keyTxt;
        private String redisUpdateSuccess;
    }

}