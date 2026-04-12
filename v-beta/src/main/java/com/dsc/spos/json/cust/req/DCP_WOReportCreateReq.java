package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.JsonReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WOReportCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private LevelRequest request;
    @NoArgsConstructor
    @Data
    public  class LevelRequest {
        @JSONFieldRequired
        private String bDate;
        private String memo;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        @JSONFieldRequired
        private List<LevelElm2> datas;

    }


    @NoArgsConstructor
    @Data
    public  class LevelElm2 {
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String ofNo;
        @JSONFieldRequired
        private String oItem;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String equipNo;
        @JSONFieldRequired
        private String eQty;
        @JSONFieldRequired
        private String laborTime;
        @JSONFieldRequired
        private String machineTime;
        private String memo;
    }
}
