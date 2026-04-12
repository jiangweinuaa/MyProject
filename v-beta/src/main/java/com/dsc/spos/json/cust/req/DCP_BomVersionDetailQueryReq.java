package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomVersionDetailQueryReq extends JsonBasicReq {

    private DCP_BomVersionDetailQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String bomNo;
        private List<versionList> versionList;

    }

    @Data
    public class versionList{
        private String versionNum;
    }

}
