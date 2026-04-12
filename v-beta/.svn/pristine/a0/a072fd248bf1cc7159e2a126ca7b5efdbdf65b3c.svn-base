package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomEnableReq extends JsonBasicReq
{

    private DCP_BomEnableReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private String opType;
        private List<DCP_BomEnableReq.BomList> bomList;
    }

    @Data
    public class BomList{
        private String bomNo;
    }
}
