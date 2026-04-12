package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/11
 */
@Getter
@Setter
public class DCP_CustGroupQueryReq extends JsonBasicReq {

    private Request request;

    @Getter
    @Setter
    public class Request {
        private String status;
        private String keyTxt;
    }

}