package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/27
 */
@Getter
@Setter
public class DCP_WoProdRepStatDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "日期")
        private String wDate ;
        @JSONFieldRequired(display = "法人")
        private String corp;
        @JSONFieldRequired(display = "单据别")
        private String porderNo;
    }

}