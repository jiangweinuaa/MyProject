package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasic;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BatchStockQueryReq extends JsonBasicReq {

    private DCP_BatchStockQueryReq.Level1Elm request;

    @Data
    public class  Level1Elm{
        @JSONFieldRequired(display = "组织编号")
        private String orgNo;
        private String wareHouse;
        private String location;

        private List<String> batchList;

        @JSONFieldRequired(display = "商品列表")
        private List<PluList> pluList;
    }

    @Data
    public class PluList{

        @JSONFieldRequired(display = "商品编号")
        private String pluNo;
        private String featureNo;
        private String pUnit;
    }

}
