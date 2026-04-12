package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ROrderUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ROrderUpdateReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String rOrderNo;
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String rDate;
        @JSONFieldRequired
        private String rDays;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        @JSONFieldRequired
        private String totCqty;
        @JSONFieldRequired
        private String totPqty;
        private String memo;
        @JSONFieldRequired
        private String status;
        private List<DCP_ROrderUpdateReq.Detail> detail;
    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluNo;
        private String pluBarcode;
        private String featureNo;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String baseUnit;
        @JSONFieldRequired
        private String baseQty;
        @JSONFieldRequired
        private String unitRatio;
        @JSONFieldRequired
        private String forecastQty;
        @JSONFieldRequired
        private String avgDeliverQty;
        @JSONFieldRequired
        private String dailyPqty;
        @JSONFieldRequired
        private String minQty;
        @JSONFieldRequired
        private String mulQty;
        @JSONFieldRequired
        private String safeQty;
        @JSONFieldRequired
        private String stockQty;
        @JSONFieldRequired
        private String status;
        private String memo;

    }

}
