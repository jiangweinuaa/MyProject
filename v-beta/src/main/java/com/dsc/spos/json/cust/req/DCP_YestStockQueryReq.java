package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_YestStockQueryReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_YestStockQueryReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String warehouse;
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        private String pluNo;
        private String featureNo;
    }


}
