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
public class DCP_CustomerCategoryDiscUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "客户类型")
        private String customerType;
        @JSONFieldRequired(display = "品类折扣明细")
        private List<CateDiscList> cateDiscList;
        @JSONFieldRequired(display = "客户组/客户编码")
        private String customerNo;
    }

    @Getter
    @Setter
    public class CateDiscList {
        @JSONFieldRequired(display = "商品分类编码")
        private String category;
        @JSONFieldRequired(display = "折扣率：5=5%")
        private String discRate;
    }

}