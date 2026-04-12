package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DCP_PurReceiveDetailQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        private String status;
        private String billNo;
        private String receivingNo;
//        1.仅查询待入库量>0明细
        private String searchScope;
    }

}
