package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{

        private List<String> status;
        private String warehouse;
        @JSONFieldRequired(display = "开始日期yyyyMMdd")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期yyyyMMdd")
        private String endDate;
        private String keyTxt;

    }

}
