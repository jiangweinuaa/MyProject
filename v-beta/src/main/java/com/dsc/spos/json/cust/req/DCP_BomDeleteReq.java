package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomDeleteReq extends JsonBasicReq
{

    private DCP_BomDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private List<BomList> bomList;
    }

    @Data
    public class BomList{
        private String bomNo;
    }
}
