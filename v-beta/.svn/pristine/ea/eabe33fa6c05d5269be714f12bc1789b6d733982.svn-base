package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DistriOrderDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_DistriOrderDetailQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired(display = "单据编号")
        private String billNo;
    }

}
