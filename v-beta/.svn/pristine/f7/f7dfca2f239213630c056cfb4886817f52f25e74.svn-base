package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/29
 */
@Getter
@Setter
public class DCP_CustomerPOrderQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "开始日期，格式YYYYMMDD")
        private String beginDate;
        private String eId;
        private String dateType;
        @JSONFieldRequired(display = "结束日期，格式YYYYMMDD")
        private String endDate;
        private String searchScope;
        private String shopId;
        private String status;
        private String keyTxt;
    }

}