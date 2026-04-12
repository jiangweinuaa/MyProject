package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomProcessInitReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_BomProcessInitReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private List<BomList> bomList;
    }

    @Data
    public class BomList{
        @JSONFieldRequired
        private String bomNo;
    }

}
