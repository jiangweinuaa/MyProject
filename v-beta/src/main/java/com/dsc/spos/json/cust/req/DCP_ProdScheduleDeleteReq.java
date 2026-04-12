package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProdScheduleDeleteReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProdScheduleDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String billNo;
    }

}