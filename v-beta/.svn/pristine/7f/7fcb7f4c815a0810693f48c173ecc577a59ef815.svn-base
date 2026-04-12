package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_BomTreeQueryReq extends JsonBasicReq
{
    @JSONFieldRequired(display = "请求参数")
    private DCP_BomTreeQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        private String keyTxt;
        @JSONFieldRequired(display = "配方类型")
        private String bomType;
        @JSONFieldRequired(display = "适用组织")
        private String restrictShop;
    }
}
