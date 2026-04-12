package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_SortingAssignDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{
        @JSONFieldRequired(display = "派工编号")
        private String billNo;

    }

}
