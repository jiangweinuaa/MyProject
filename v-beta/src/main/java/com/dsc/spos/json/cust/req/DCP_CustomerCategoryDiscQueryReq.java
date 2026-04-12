package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/12
 */
@Getter
@Setter
public class DCP_CustomerCategoryDiscQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "客户类型 1-客户组 2-客户")
        private String customerType;
        private String customerNo;
//        @JSONFieldRequired(display = "编码/名称模糊搜索")
        private String keyTxt;
    }

}