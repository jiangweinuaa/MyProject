package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProductGroupGoodsUpdateReq  extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProductGroupGoodsUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest {
        private String pGroupNo;
       private List<Datas> datas;
    }

    @Data
    public class Datas{
        private String pluNo;
    }
}
