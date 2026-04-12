package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MStockOutDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MStockOutDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest {

        private List<Datas> datas;
    }

    @Data
    public class Datas{
        private String mStockOutNo;
    }
}
