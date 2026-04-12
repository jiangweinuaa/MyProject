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
public class DCP_SalePriceTemplateCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "适用对象：COMPANY-公司 SHOP-门店")
        private String templateType;
        @JSONFieldRequired(display = "适用公司或门店")
        private List<RangeList> rangeList;
        private String memo;
        @JSONFieldRequired(display = "自建门店")
        private String selfBuiltShopId;

        private String templateId;
        @JSONFieldRequired(display = "模板名称")
        private List<TemplateName_Lang> templateName_lang;
        @JSONFieldRequired(display = "适用渠道：0-所有渠道1-指定渠道")
        private String restrictChannel;
        private String channelId;
        @JSONFieldRequired(display = "状态：-1未启用100已启用0已禁用")
        private String status;
    }



    @Getter
    @Setter
    public class RangeList {
        @JSONFieldRequired(display = "名称")
        private String name;
        @JSONFieldRequired(display = "编号")
        private String id;
    }


    @Getter
    @Setter
    public class TemplateName_Lang {
        @JSONFieldRequired(display = "")
        private String name;
        @JSONFieldRequired(display = "")
        private String langType;
    }

}