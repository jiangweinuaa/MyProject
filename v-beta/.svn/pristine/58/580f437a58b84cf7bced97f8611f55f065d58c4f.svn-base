package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WOReportDeleteReq  extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_WOReportDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String reportNo;
    }

}
