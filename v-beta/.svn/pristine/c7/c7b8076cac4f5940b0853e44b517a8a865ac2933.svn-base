package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ProductGroupCreateReq  extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProductGroupCreateReq.LevelRequest request;

    @Data
    public class LevelRequest {

        private String pGroupNo;
        private String pGroupName;
        private String departId;
        private String status;
        private String memo;
    }
}
