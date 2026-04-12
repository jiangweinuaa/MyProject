package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProcessTaskPendingMaterialReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ProcessTaskPendingMaterialReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String opType;
        @JSONFieldRequired
        private String isDeductBatchStock;
        private String toWarehouse;
        private List<levelElm2> datas;
    }

    @Data
    public class levelElm2{
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String punit;
        @JSONFieldRequired
        private String bomNo;
        @JSONFieldRequired
        private String versionNum;
        @JSONFieldRequired
        private String semiWoType;
        @JSONFieldRequired
        private String oOType;
        @JSONFieldRequired
        private String oOfNo;
        @JSONFieldRequired
        private String oOItem;
    }
}
