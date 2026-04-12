package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/28
 */
@Getter
@Setter
public class DCP_PriceAdjustQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "开始日期,格式YYYY-MM-DD")
        private String beginDate;
        @JSONFieldRequired(display = "日期类型")
        private String dateType;
        @JSONFieldRequired(display = "结束日期,格式YYYY-MM-DD")
        private String endDate;
        private String billNo;
        private String status;
        private String keyTxt;
        @JSONFieldRequired(display = "单据类型 1-采购价 2-零售价")
        private String bType;

    }

}