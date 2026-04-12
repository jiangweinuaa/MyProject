package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

public class DCP_ReceivingStatusUpdateReq extends JsonBasicReq {

    @Getter
    @Setter
    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{
        @JSONFieldRequired
        private String receivingNo;

        @JSONFieldRequired
        private String docType;  //枚举: 0-配送收货 2-统采直供(取消) 3-采购收货 4-采购收货入库

        @JSONFieldRequired
        private String opType;   //枚举: confirm：审核 unconfirm：取消审核 cancel：作废 close：结束收货
    }

}
