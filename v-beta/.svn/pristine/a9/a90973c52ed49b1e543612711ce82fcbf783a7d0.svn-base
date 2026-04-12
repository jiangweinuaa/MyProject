package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_Blob1CreateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_Blob1CreateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private String content;
        private String no;
    }
}
