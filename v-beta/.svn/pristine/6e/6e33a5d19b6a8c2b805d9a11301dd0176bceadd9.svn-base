package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DCP_WOReportUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_WOReportUpdateReq.LevelRequest request;
    @NoArgsConstructor
    @Data
    public  class LevelRequest {

        @JSONFieldRequired
        private String reportNo;
        @JSONFieldRequired
        private String bDate;
        private String memo;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        @JSONFieldRequired
        private List<DCP_WOReportUpdateReq.LevelElm2> datas;

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


